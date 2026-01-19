package com.patrick;

import com.patrick.datacollection.api.apifib.ApiClient;
import com.patrick.datacollection.api.apifib.templates.Quatrimestre;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        ApiClient apiClient = new ApiClient("");
        try {

            Quatrimestre quatrimestre = apiClient.getCurrentQuatrimestre();
            //System.out.println(apiClient.getSubjectsData(quatrimestre.getClassesURL(),apiClient.getCurrentSubjects(quatrimestre.getAssignaturesURL())));
            System.out.println(apiClient.getSubjectsData(quatrimestre.getClassesURL(),new ArrayList<>(Arrays.asList("EDA"))));
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}