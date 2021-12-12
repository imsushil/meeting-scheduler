package com.systemdesign.room;

import com.systemdesign.meeting.Meeting;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;

@Getter
public class Room {
    private final int id;
    private final String name;
    private NavigableSet<Meeting> allMeetings = new TreeSet<>();
    private Meeting conflictingMeeting;

    public Room(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean book(Meeting meeting) {
        return allMeetings.add(meeting);
    }

    public void cancel(Meeting meeting) {
        allMeetings. remove(meeting);
    }

    boolean hasAnyConflictingMeeting(int organizerId, LocalDateTime startTime, LocalDateTime endTime) {
        Meeting potentialMeeting = new Meeting(null, id, organizerId, startTime, endTime);
        Meeting meetingBeforeThisTime = allMeetings.floor(potentialMeeting);
        Meeting meetingAfterThisTime = allMeetings.ceiling(potentialMeeting);
        return checkOverlappingCondition(potentialMeeting, meetingBeforeThisTime, meetingAfterThisTime);
    }

    private boolean checkOverlappingCondition(Meeting potentialMeeting, Meeting meetingBeforeThisTime, Meeting meetingAfterThisTime) {
        if(Objects.nonNull(meetingBeforeThisTime) && meetingBeforeThisTime.getEndTime().isAfter(potentialMeeting.getStartTime())) {
            conflictingMeeting = meetingBeforeThisTime;
            return true;
        }
        if(Objects.nonNull(meetingAfterThisTime) && potentialMeeting.getEndTime().isAfter(meetingAfterThisTime.getStartTime())) {
            conflictingMeeting = meetingAfterThisTime;
            return true;
        }
        return false;
    }
}