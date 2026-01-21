package com.patrick.generator;

import com.patrick.generator.exception.ScheduleGeneratorException;
import com.patrick.templates.Group;
import com.patrick.templates.GroupClass;
import com.patrick.templates.Subject;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;

public class ScheduleGenerator {
    private final Set<DayOfWeek> excludedDaysOfWeek;
    private final Map<DayOfWeek,Pair<LocalTime,LocalTime>> timeLimits;
    private final List<Subject> subjectList;
    private final int maxOptions;
    private boolean shouldContinue;

    ScheduleGenerator(Builder builder) {
        this.excludedDaysOfWeek = builder.excludedDaysOfWeek;
        this.timeLimits = builder.timeLimits;
        this.subjectList = builder.subjectList;
        this.maxOptions = builder.maxOptions;
        this.shouldContinue = true;
    }

    boolean isValidGroup(Group group) {
        for (GroupClass gc : group.getGroupClassList()) {
            if (excludedDaysOfWeek.contains(gc.getDayOfWeek())) {
                return false;
            }
        }

        return true;
    }

    private void privateCompute(List<List<Group>> result, boolean[] visitedSubject, List<Group> partial) {
        // base case: if the number of groups is equal to the subjects
        // we have finished, save valid schedule
        if (partial.size() == this.subjectList.size()) {
            result.add(new ArrayList<>(partial));

            if (result.size() == this.maxOptions) {
                this.shouldContinue = false;
            }

            return;
        }

        for (int i = 0; i < subjectList.size() && shouldContinue; i++) {
            if (!visitedSubject[i]) {
                visitedSubject[i] = true;

                for (Group group : subjectList.get(i).getGroupList()) {
                    if (isValidGroup((group))) {
                        partial.add(group);

                        privateCompute(result,visitedSubject,partial);

                        partial.removeLast();
                    }

                    // force exit
                    if (!shouldContinue) break;
                }

                visitedSubject[i] = false;
            }
        }
    }

    public List<List<Group>> compute() {
        List<List<Group>> result = new ArrayList<>();
        boolean[] visitedSubject = new boolean[subjectList.size()];
        privateCompute(result, visitedSubject, new ArrayList<>());
        return result;
    }

    public static class Builder {
        private Set<DayOfWeek> excludedDaysOfWeek;
        private Map<DayOfWeek,Pair<LocalTime,LocalTime>> timeLimits;
        private List<Subject> subjectList;
        private int maxOptions;

        public Builder() {
            this.excludedDaysOfWeek = new HashSet<>();
            this.timeLimits = new HashMap<>();
            maxOptions = 1000;
        }

        public Builder setMaxOptions(int i) {
            if (1 <= i) {
                this.maxOptions = i;
            }
            return this;
        }

        public Builder excludeDay(DayOfWeek dayofweek) {
            this.excludedDaysOfWeek.add(dayofweek);
            return this;
        }

        public Builder acceptDay(DayOfWeek dayofweek){
            this.excludedDaysOfWeek.remove(dayofweek);
            return this;
        }

        public Builder setDayLowerLimit(DayOfWeek day, LocalTime lowerLimit) {
            this.timeLimits.putIfAbsent(day, new MutablePair<>());
            this.timeLimits.compute(day, (k, pair) -> new MutablePair<>(lowerLimit, pair.getRight()));
            return this;
        }

        public Builder setDayUpperLimit(DayOfWeek day, LocalTime upperLimit) {
            this.timeLimits.putIfAbsent(day, new MutablePair<>());
            this.timeLimits.compute(day, (k, pair) -> new MutablePair<>(pair.getLeft(), upperLimit));
            return this;
        }

        public Builder setLowerLimit(LocalTime lowerLimit) {
            for (DayOfWeek day : DayOfWeek.values()) {
                setDayLowerLimit(day,lowerLimit);
            }
            return this;
        }

        public Builder setUpperLimit(LocalTime upperLimit) {
            for (DayOfWeek day : DayOfWeek.values()) {
                setDayUpperLimit(day, upperLimit);
            }
            return this;
        }

        public Builder removeDayLimit(DayOfWeek day) {
            this.timeLimits.remove(day);
            return this;
        }

        public Builder removeAllDayLimits() {
            for (DayOfWeek day : DayOfWeek.values()) {
                removeDayLimit(day);
            }
            return this;
        }

        public Builder setSubjectList(List<Subject> subjectList) {
            this.subjectList = subjectList;
            return this;
        }

        public Builder clearSubjectList() {
            this.subjectList.clear();
            return this;
        }

        public ScheduleGenerator build() {
            // max options has to be natural greater or equal than 1
            if (maxOptions < 1) {
                String errorMessage = "maxOptions(%d) builder parameter " +
                        "has to be a natural number bigger than 0";

                errorMessage = String.format(
                        errorMessage,
                        maxOptions
                );
                throw new ScheduleGeneratorException(errorMessage);
            }

            // cant generate a schedule without subjects
            if (this.subjectList == null || this.subjectList.isEmpty()) {
                throw new ScheduleGeneratorException("subjectList builder parameter can't be null or empty");
            }

            // check time constraints are valid
            for (DayOfWeek day : timeLimits.keySet()) {
                // if any of the values is null automatically correct
                // otherwise check there are no impossible bounds
                LocalTime lower = timeLimits.get(day).getLeft();
                LocalTime upper = timeLimits.get(day).getRight();
                if (lower != null && upper != null) {
                    if (lower.isAfter(upper)) {
                        String errorMessage = "Day %s has an illegal time constraint. " +
                                "Time bounds have to be lower <= upper : lower(%s) upper(%s)";
                        errorMessage = String.format(
                                errorMessage,
                                day.getDisplayName(TextStyle.FULL,Locale.ENGLISH),
                                lower,
                                upper
                                );

                        throw new ScheduleGeneratorException(errorMessage);
                    }
                }

            }

            // remove time constraints from excluded days
            // to simplify comparisons
            for (DayOfWeek day : excludedDaysOfWeek) {
                timeLimits.remove(day);
            }

            return new ScheduleGenerator((this));
        }
    }
}
