package com.systemdesign.meeting;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class Meeting implements Comparable<Meeting> {
    private String meetingId;
    private int roomId;
    private int organizerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Override
    public int compareTo(Meeting meeting) {
        return startTime.compareTo(meeting.startTime);
    }

}
