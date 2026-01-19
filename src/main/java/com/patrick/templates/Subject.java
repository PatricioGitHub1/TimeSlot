package com.patrick.templates;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String subjectId;
    private List<Group> groupList;

    public Subject() {
        groupList = new ArrayList<>();
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "subjectId='" + subjectId + '\'' +
                ", groupList=" + groupList +
                '}';
    }
}
