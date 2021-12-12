package com.systemdesign;

import com.systemdesign.employee.EmployeeService;
import com.systemdesign.exceptions.ConflictingMeetingException;
import com.systemdesign.exceptions.InvalidEmployeeIdException;
import com.systemdesign.exceptions.InvalidMeetingException;
import com.systemdesign.meeting.Meeting;
import com.systemdesign.meeting.MeetingService;
import com.systemdesign.room.RoomService;

import java.time.LocalDateTime;

import static com.systemdesign.util.MessageConstants.*;

public class Scheduler {
    private final RoomService roomService = RoomService.getInstance();
    private final EmployeeService employeeService = EmployeeService.getInstance();
    private final MeetingService meetingService = MeetingService.getInstance();

    private Meeting tryBook(int organizerId, LocalDateTime startTime, LocalDateTime endTime) throws ConflictingMeetingException, InvalidEmployeeIdException {
        if (employeeService.isEmployeeIdInvalid(organizerId)) {
            throw new InvalidEmployeeIdException("Employee with id = " + organizerId + " does not exist.");
        }
        if (employeeService.hasConflictingMeeting(organizerId, startTime, endTime)) {
            throw new ConflictingMeetingException("Conflicting with another meeting.");
        }

        Integer roomId = roomService.getAnyAvailableRoom(organizerId, startTime, endTime);
        Meeting meeting = meetingService.addMeeting(organizerId, roomId, startTime, endTime);
        roomService.book(meeting);
        employeeService.addMeeting(meeting);
        return meeting;
    }

    private void tryCancel(int employeeId, String meetingId) throws ConflictingMeetingException, InvalidMeetingException {
        if (!meetingService.hasMeetingWithId(meetingId))
            throw new InvalidMeetingException("Meeting with id = " + meetingId + " does not exist.");
        if (employeeService.isEmployeeIdInvalid(employeeId))
            throw new IllegalArgumentException("Employee with id = " + employeeId + " does not exist.");
        if (!employeeService.hasAnyMeetingWithMeetingId(employeeId, meetingId))
            throw new ConflictingMeetingException("You are not the organizer of this meeting.");
        Meeting meeting = meetingService.get(meetingId);
        roomService.cancel(meeting);
        employeeService.cancelMeeting(meeting);
        meetingService.removeMeeting(meeting);
    }

    public String book(int organizerId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            Meeting meeting = tryBook(organizerId, startTime, endTime);
            System.out.printf(ROOM_BOOKED_MSG, meeting.getRoomId(), meeting.getMeetingId());
            return meeting.getMeetingId();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    public String cancel(int employeeId, String meetingId) {
        try {
            tryCancel(employeeId, meetingId);
            System.out.println(CANCEL_SUCCESS_MSG);
            return CANCEL_SUCCESS_MSG;
        } catch (ConflictingMeetingException e) {
            System.out.println(CANCEL_FAILED_MSG);
            return CANCEL_FAILED_MSG;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ex.getMessage();
        }
    }
}