package com.systemdesign.meeting;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MeetingService {
    private static volatile MeetingService INSTANCE;

    private Map<String, Meeting> meetingIdToMeetingMap = new HashMap<>();

    private Meeting createMeeting(int organizerId, int roomId, LocalDateTime startTime, LocalDateTime endTime) {
        String meetingId = UUID.randomUUID().toString();
        return new Meeting(meetingId, roomId, organizerId, startTime, endTime);
    }

    public Meeting addMeeting(Integer organizerId, Integer roomId, LocalDateTime startTime, LocalDateTime endTime) {
        Meeting meeting = createMeeting(organizerId, roomId, startTime, endTime);
        meetingIdToMeetingMap.put(meeting.getMeetingId(), meeting);
        return meeting;
    }

    public boolean removeMeeting(Meeting meeting) {
        Meeting result = meetingIdToMeetingMap.remove(meeting.getMeetingId());
        return result != null;
    }

    public boolean hasMeetingWithId(String meetingId) {
        return meetingIdToMeetingMap.containsKey(meetingId);
    }

    public Meeting get(String meetingId) {
        return meetingIdToMeetingMap.get(meetingId);
    }

    public static MeetingService getInstance() {
        if(INSTANCE == null) {
            synchronized (MeetingService.class) {
                if(INSTANCE == null) INSTANCE = new MeetingService();
            }
        }
        return INSTANCE;
    }


}
