package com.systemdesign;

import com.systemdesign.employee.EmployeeService;
import com.systemdesign.meeting.MeetingService;
import com.systemdesign.room.RoomService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SchedulerTest {
    private Scheduler scheduler;

    @Before
    public void setUp() throws Exception {
        scheduler = new Scheduler();
        setStaticFinalInstanceToNull(RoomService.class.getDeclaredField("INSTANCE"), null);
        setStaticFinalInstanceToNull(EmployeeService.class.getDeclaredField("INSTANCE"), null);
        setStaticFinalInstanceToNull(MeetingService.class.getDeclaredField("INSTANCE"), null);

    }

    void setStaticFinalInstanceToNull(Field field, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    @Test
    public void whenBookingAMeetingRoomAndRoomIsAvailable_thenSuccess() {
        LocalDateTime startTime = LocalDateTime.now().plus(2, ChronoUnit.HOURS);
        LocalDateTime endTime = startTime.plus(30, ChronoUnit.MINUTES);
        String meetingId = scheduler.book(1, startTime, endTime);
        Assert.assertEquals("Should get an ID of length 36.", 36, meetingId.length());
    }

    @Test
    public void whenBookingAMeetingRoomAndRoomIsAvailableButConflictingMeeting_thenShowConflictingErrorMsg() {
        LocalDateTime startTime = LocalDateTime.now().plus(2, ChronoUnit.HOURS);
        LocalDateTime endTime = startTime.plus(30, ChronoUnit.MINUTES);
        String meetingId = scheduler.book(1, startTime, endTime);

        LocalDateTime startTime1 = LocalDateTime.now().plus(1, ChronoUnit.HOURS);
        LocalDateTime endTime1 = startTime.plus(90, ChronoUnit.MINUTES);
        String response = scheduler.book(1, startTime, endTime);

        String expectedMsg = "Conflicting with another meeting.";
        Assert.assertTrue("Message contains '" + expectedMsg + "'", response.contains(expectedMsg));
    }

    @Test
    public void whenMeetingIsScheduledByTheOrganizer_thenCancelMeetingSuccess() {
        LocalDateTime startTime = LocalDateTime.now().plus(6, ChronoUnit.HOURS);
        LocalDateTime endTime = startTime.plus(30, ChronoUnit.MINUTES);
        String meetingId = scheduler.book(1, startTime, endTime);

        String response = scheduler.cancel(1, meetingId);
        Assert.assertEquals("Success", response);
    }

    @Test
    public void whenMeetingIsNotScheduledByTheOrganizer_thenCancelMeetingFails() {
        LocalDateTime startTime = LocalDateTime.now().plus(6, ChronoUnit.HOURS);
        LocalDateTime endTime = startTime.plus(30, ChronoUnit.MINUTES);
        String meetingId = scheduler.book(2, startTime, endTime);

        String response = scheduler.cancel(1, meetingId);
        Assert.assertEquals("Fail", response);
    }
}