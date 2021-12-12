package com.systemdesign;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class MainApp {

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();

        LocalDateTime startTime = LocalDateTime.now().plus(2, ChronoUnit.HOURS);
        LocalDateTime endTime = startTime.plus(30, ChronoUnit.MINUTES);
        String meetingId = scheduler.book(1, startTime, endTime);
        scheduler.book(1, startTime.plusMinutes(15), endTime);
        scheduler.cancel(1, meetingId);
    }
}
