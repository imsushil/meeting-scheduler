package com.systemdesign.room;

import com.systemdesign.config.Configuration;
import com.systemdesign.exceptions.ConflictingMeetingException;
import com.systemdesign.exceptions.InvalidConfigurationException;
import com.systemdesign.meeting.Meeting;
import com.systemdesign.util.ConfigConstants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.systemdesign.util.ConfigConstants.EMPLOYEES;
import static com.systemdesign.util.ConfigConstants.MEETING_ROOMS;
import static com.systemdesign.util.MessageConstants.ALL_ROOMS_BUSY_MSG;

public class RoomService {
    private static volatile RoomService INSTANCE;
    private int totalRooms;

    private Map<Integer, Room> roomIdToRoomMap;

    private RoomService() {
        init();

    }

    private void init() {
        roomIdToRoomMap = getRoomIdToRoomMap();

        String totalRoomsInString = Configuration.get(EMPLOYEES);
        if(totalRoomsInString == null) throw new InvalidConfigurationException("app.properties should have property '" + MEETING_ROOMS + "'");
        totalRooms = Integer.parseInt(totalRoomsInString);
    }

    private Map<Integer, Room> getRoomIdToRoomMap() {
        return getRooms().stream().collect(Collectors.toMap(Room::getId, Function.identity()));
    }
    public List<Room> getRooms() {
        return IntStream.rangeClosed(1, totalRooms)
                        .mapToObj(i -> new Room(i, "Room"+i))
                        .collect(Collectors.toList());
    }

    public Integer getAnyAvailableRoom(int organizerId, LocalDateTime startTime, LocalDateTime endTime) throws ConflictingMeetingException {
        return roomIdToRoomMap.values().stream()
                .filter(room -> !room.hasAnyConflictingMeeting(organizerId, startTime, endTime))
                .map(Room::getId)
                .findFirst()
                .orElseThrow(() -> new ConflictingMeetingException(ALL_ROOMS_BUSY_MSG));
    }

    public List<Integer> getAllAvailableRooms(int organizerId, LocalDateTime startTime, LocalDateTime endTime) throws ConflictingMeetingException {
        List<Integer> availableRooms = roomIdToRoomMap.values().stream()
                .filter(room -> !room.hasAnyConflictingMeeting(organizerId, startTime, endTime))
                .map(Room::getId)
                .collect(Collectors.toList());
        if( availableRooms.isEmpty()) {
            throw new ConflictingMeetingException(ALL_ROOMS_BUSY_MSG);
        }
        return availableRooms;
    }

    public boolean book(Meeting meeting) {
        return roomIdToRoomMap.get(meeting.getRoomId()).book(meeting);
    }

    public void cancel(Meeting meeting) {
        roomIdToRoomMap.get(meeting.getRoomId()).cancel(meeting);
    }

    public static RoomService getInstance() {
        if (INSTANCE != null) return INSTANCE;
        synchronized (RoomService.class) {
            if(INSTANCE == null) INSTANCE = new RoomService();
        }
        return INSTANCE;
    }
}
