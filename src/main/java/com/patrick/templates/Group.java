package com.patrick.templates;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String subjectId;
    private String groupId;
    private List<GroupClass> groupClassList;

    public Group() {
        groupClassList = new ArrayList<>();
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public List<GroupClass> getGroupClassList() {
        return groupClassList;
    }

    public void setGroupClassList(List<GroupClass> groupClassList) {
        this.groupClassList = groupClassList;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "Group{" +
                "subjectId='" + subjectId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", groupClassList=" + groupClassList +
                '}';
    }
}
