package com.patrick.templates;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

public class GroupClass {
    private String subjectId;
    private String groupId;
    private String classId;
    private DayOfWeek dayOfWeek;
    private LocalTime startingTime;
    private LocalTime endingTime;
    int durationMinutes;

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(LocalTime endingTime) {
        // if starting time is set, check no impossible values are used, calculate time diff (duration)
        if (this.startingTime != null) {
            Duration duration = Duration.between(this.startingTime, endingTime);
            if (duration.isNegative()) {
                throw new DateTimeException("StartingTime takes place after endingTime");
            } else {
                this.endingTime = endingTime;
                this.durationMinutes = (int) duration.toMinutes();
            }

            // if starting time is null, allow new time and set default duration
        } else {
            this.endingTime = endingTime;
            this.durationMinutes = 0;
        }
    }

    public LocalTime getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(LocalTime startingTime) {
        // if ending time is set, check no impossible values are used, calculate time diff (duration)
        if (this.endingTime != null) {
            Duration duration = Duration.between(startingTime, this.endingTime);
            if (duration.isNegative()) {
                throw new DateTimeException("StartingTime takes place after endingTime");
            } else {
                this.startingTime = startingTime;
                this.durationMinutes = (int) duration.toMinutes();
            }

        // if ending time is null, allow new time and set default duration
        } else {
            this.startingTime = startingTime;
            this.durationMinutes = 0;
        }
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    @Override
    public String toString() {
        return "GroupClass{" +
                "subjectId='" + subjectId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", classId='" + classId + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                ", startingTime=" + startingTime +
                ", endingTime=" + endingTime +
                ", durationMinutes=" + durationMinutes +
                '}';
    }
}
