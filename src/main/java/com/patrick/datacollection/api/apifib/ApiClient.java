package com.patrick.datacollection.api.apifib;
import com.patrick.datacollection.api.apifib.templates.GroupClassFIB;
import com.patrick.datacollection.api.apifib.templates.Quatrimestre;
import com.patrick.templates.Group;
import com.patrick.templates.GroupClass;
import com.patrick.templates.Subject;

import org.apache.hc.core5.net.URIBuilder;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

import static com.patrick.utils.JSONwrapper.*;

public class ApiClient {
    private String clientIdParamName = "client_id";
    private String codiAssigParamName = "codi_assig";
    private String clientId = null;
    private final String BASE_URL = "https://api.fib.upc.edu/v2/";
    private final String CURRENT_QUATRIMESTRE = BASE_URL + "quadrimestres/actual-horaris/";

    private final HttpClient httpClient;

    public ApiClient(String clientId) {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        this.clientId =clientId;
    }

    List<GroupClass> transformGroupClassFIBtoGroupClass(List<GroupClassFIB> groupClassFIBList){
        List<GroupClass> resultList = new ArrayList<>();

        for (GroupClassFIB gcFIB : groupClassFIBList) {
            GroupClass gc = new GroupClass();

            gc.setSubjectId(gcFIB.getCodi_assig());

            // the class id is an integer where the first digit is the group
            // and the last digit the subgroup
            // e.g : 42 -> main group 40, subgroup 42
            gc.setClassId(gcFIB.getGrup());
            String mainGroup = String.valueOf(Integer.parseInt(gcFIB.getGrup()) / 10 * 10);
            gc.setGroupId(mainGroup);

            // get day of the week
            gc.setDayOfWeek(DayOfWeek.of(gcFIB.getDia_setmana()));

            // set times
            gc.setStartingTime(gcFIB.getInici());
            gc.setEndingTime(gcFIB.getInici().plusHours(gcFIB.getDurada()));

            resultList.add(gc);
        }

        return resultList;
    }

    /**
     * Given a URL and list of subjects, query the data for the given subjects
     * applying a filter. If list is empty it searches for all available subjects.
     *
     * @param subjectsURL
     * @param subjects
     * @return
     */
    public List<Subject> getSubjectsData(String subjectsURL, List<String> subjects) throws IOException, InterruptedException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(subjectsURL);

        for (String subj : subjects) {
            uriBuilder.addParameter(codiAssigParamName,subj);
        }

        uriBuilder.setParameter(clientIdParamName,clientId);
        String finalURL = uriBuilder.build().toString();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(finalURL))
                .header("Content-Type", "application/json")
                .header("Accept","application/json")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        JsonNode jsonNode = getJsonNode(response.body());

        // when parsing response if anything fails return empty List
        if (response.statusCode() != 200 || jsonNode == null) {
            return new ArrayList<Subject>();
        }

        List<GroupClassFIB> groupClassFIBList = convertToList(GroupClassFIB.class, jsonNode, "results");
        if (groupClassFIBList == null) {
            return new ArrayList<Subject>();
        }

        List<GroupClass> groupClassList = transformGroupClassFIBtoGroupClass(groupClassFIBList);

        // map to store and fins classes given their subject name and group they belong to
        Map<String, Map<String, List<GroupClass>>> classesByGroupBySubject = new HashMap<>();

        for (GroupClass gc : groupClassList) {
            classesByGroupBySubject.putIfAbsent(gc.getSubjectId(), new HashMap<>());
            classesByGroupBySubject.get(gc.getSubjectId()).putIfAbsent(gc.getGroupId(), new ArrayList<>());
            classesByGroupBySubject.get(gc.getSubjectId()).get(gc.getGroupId()).add(gc);
        }

        // prepare to construct subjects with all their groups and classes
        List<Subject> subjectList = new ArrayList<>();

        for (Map.Entry<String, Map<String, List<GroupClass>>> entry1 : classesByGroupBySubject.entrySet()) {
            Subject subject = new Subject();
            subject.setSubjectId(entry1.getKey());

            for (Map.Entry<String, List<GroupClass>> entry2 : classesByGroupBySubject.get(entry1.getKey()).entrySet()) {
                for (int i = 1; i < 9; i++) {
                    List<GroupClass> gcList = new ArrayList<>();
                    int subgroupID = Integer.parseInt(entry2.getKey()) + i;
                    String strID = String.valueOf(subgroupID);

                    Group group = new Group();
                    group.setSubjectId(entry1.getKey());
                    group.setGroupId(entry2.getKey());

                    boolean valid = false;
                    for (GroupClass gc : entry2.getValue()) {
                        if (gc.getClassId().equals(gc.getGroupId())) {
                            gcList.add(gc);
                        }

                        if (gc.getClassId().equals(strID)) {
                            valid = true;
                            gcList.add(gc);
                        }
                    }

                    if (valid) {
                        group.setGroupClassList(gcList);
                        subject.getGroupList().add(group);
                    }

                }

            }

            subjectList.add(subject);
        }

        return subjectList;
    }

    /**
     * Return list with ID's of all available subjects available on the current quatri
     *
     * @return list of Strings representing subject's ID's
     * @throws IOException
     * @throws InterruptedException
     */
    public List<String> getCurrentSubjects(String subjectsURL) throws IOException, InterruptedException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(subjectsURL);
        uriBuilder.setParameter(clientIdParamName,clientId);
        String finalURL = uriBuilder.build().toString();

        // inside current quatri we extract link to all available subjects
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(finalURL))
                .header("Content-Type", "application/json")
                .header("Accept","application/json")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        JsonNode jsonNode = getJsonNode(response.body());

        // when parsing info, if anything fails return empty List
        if (response.statusCode() != 200 || jsonNode == null) {
            return new ArrayList<String>();
        }

        List<String> subjects = convertToList(String.class, jsonNode, "results");

        if (subjects == null) {
            return new ArrayList<String>();
        }

        return subjects;
    }

    /**
     * Returns Quatrimestre object holding information of the current Quatrimestre,
     * on any error during the mapping returns NULL
     *
     * @return Quatrimestre current
     * @throws IOException
     * @throws InterruptedException
     */
    public Quatrimestre getCurrentQuatrimestre() throws IOException, InterruptedException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(CURRENT_QUATRIMESTRE);
        uriBuilder.setParameter(clientIdParamName,clientId);
        String finalURL = uriBuilder.build().toString();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(finalURL))
                .header("Content-Type", "application/json")
                .header("Accept","application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        return convertToObject(Quatrimestre.class, response.body());
    }

    /**
     * Function to test API connection, it tries to communicate with the
     * main page
     * @return boolean indicating if resources were reachable (status 200)
     */
    public boolean testConnection() throws IOException, InterruptedException, URISyntaxException {
        if (clientId == null) {
            System.out.println("ERROR: clientId is null");
            return false;
        }

        URIBuilder uriBuilder = new URIBuilder(CURRENT_QUATRIMESTRE);
        uriBuilder.setParameter(clientIdParamName,clientId);
        String finalURL = uriBuilder.build().toString();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(finalURL))
                .header("Content-Type", "application/json")
                .header("Accept","application/json")
                .GET()
                .build();


        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200;
    }
}
