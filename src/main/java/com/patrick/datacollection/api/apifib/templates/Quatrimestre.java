package com.patrick.datacollection.api.apifib.templates;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Quatrimestre {
    private String id;
    private String url;
    private String actual;
    @JsonProperty("actual_horaris")
    private String actualHoraris;
    @JsonProperty("classes")
    private String classesURL;
    @JsonProperty("examens")
    private String examensURL;
    @JsonProperty("assignatures")
    private String assignaturesURL;

    public boolean isCurrent() {
        return actual != null && actual.equals("S");
    }

    public boolean isCurrentHoraris() {
        return actualHoraris != null && actualHoraris.equals("S");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getAssignaturesURL() {
        return assignaturesURL;
    }

    public void setAssignaturesURL(String assignaturesURL) {
        this.assignaturesURL = assignaturesURL;
    }

    public String getExamensURL() {
        return examensURL;
    }

    public void setExamensURL(String examensURL) {
        this.examensURL = examensURL;
    }

    public String getActualHoraris() {
        return actualHoraris;
    }

    public void setActualHoraris(String actualHoraris) {
        this.actualHoraris = actualHoraris;
    }

    public String getClassesURL() {
        return classesURL;
    }

    public void setClassesURL(String classesURL) {
        this.classesURL = classesURL;
    }

    @Override
    public String toString() {
        return "Quatrimestre{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", actual='" + actual + '\'' +
                ", actualHoraris='" + actualHoraris + '\'' +
                ", classesURL='" + classesURL + '\'' +
                ", examensURL='" + examensURL + '\'' +
                ", assignaturesURL='" + assignaturesURL + '\'' +
                '}';
    }
}
