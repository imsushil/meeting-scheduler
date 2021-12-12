package com.systemdesign.employee;

import com.systemdesign.meeting.Meeting;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
public class Employee {
    private int id;
    private String name;

    private NavigableSet<Meeting> meetings = new TreeSet<>();

    public Employee(){}
    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    boolean hasAnyMeetingWithMeetingId(String meetingId) {
        return meetings.stream()
                       .anyMatch(meeting -> meeting.getMeetingId().equals(meetingId));
    }

    boolean hasAnotherMeeting(LocalDateTime startTime, LocalDateTime endTime) {
        Optional<Meeting> meeting = meetings.stream().filter(m -> checkOverlappingCondition(m, startTime, endTime)).findFirst();
        return meeting.isPresent();
    }

    private boolean checkOverlappingCondition(Meeting meeting, LocalDateTime startTime, LocalDateTime endTime) {
        return endTime.isAfter(meeting.getStartTime()) || meeting.getEndTime().isAfter(startTime);
    }

    boolean addMeeting(Meeting m) {
        return meetings.add(m);
    }

    boolean cancelMeeting(Meeting m) {
        return meetings.remove(m);
    }
}