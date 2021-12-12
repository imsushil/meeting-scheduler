package com.systemdesign.employee;

import com.systemdesign.config.Configuration;
import com.systemdesign.exceptions.InvalidConfigurationException;
import com.systemdesign.meeting.Meeting;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.systemdesign.util.ConfigConstants.EMPLOYEES;

public class EmployeeService {
    private static volatile EmployeeService INSTANCE;

    private int totalEmployees;
    private Map<Integer, Employee> employeeIdToEmployeeMap;

    private EmployeeService() {
        init();
    }

    private void init() {
        employeeIdToEmployeeMap = getEmployeesAsMap();

        String result = Configuration.get(EMPLOYEES);
        if(result == null) throw new InvalidConfigurationException("app.properties should have property '" + EMPLOYEES + "'");
        totalEmployees = Integer.parseInt(result);
    }

    private Map<Integer, Employee> getEmployeesAsMap() {
        return IntStream.rangeClosed(1, totalEmployees)
                        .mapToObj(i -> new Employee(i, ""))
                        .collect(Collectors.toMap(Employee::getId, Function.identity()));
    }

    public boolean isEmployeeIdInvalid(int employeeId) {
        return !employeeIdToEmployeeMap.containsKey(employeeId);
    }

    public static EmployeeService getInstance() {
        if(INSTANCE == null) {
            synchronized (EmployeeService.class) {
                if(INSTANCE == null) INSTANCE = new EmployeeService();
            }
        }
        return INSTANCE;
    }

    public void addMeeting(Meeting meeting) {
        employeeIdToEmployeeMap.get(meeting.getOrganizerId()).addMeeting(meeting);
    }

    public void cancelMeeting(Meeting meeting) {
        employeeIdToEmployeeMap.get(meeting.getOrganizerId()).cancelMeeting(meeting);
    }

    public boolean hasConflictingMeeting(Integer organizerId, LocalDateTime startTime, LocalDateTime endTime) {
        return employeeIdToEmployeeMap.get(organizerId).hasAnotherMeeting(startTime, endTime);
    }

    public boolean hasAnyMeetingWithMeetingId(Integer organizerId, String meetingId) {
        return employeeIdToEmployeeMap.get(organizerId).hasAnyMeetingWithMeetingId(meetingId);
    }

}