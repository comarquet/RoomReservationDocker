package com.roomreservation.service;

import com.roomreservation.mapper.RoomMapper;
import com.roomreservation.model.RoomEntity;
import com.roomreservation.record.BookingRecord;
import com.roomreservation.record.RoomRecord;
import com.roomreservation.repository.BookingDao;
import com.roomreservation.repository.RoomDao;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class managing room-related operations.
 * Handles room creation, updates, deletions, and availability checks.
 */
@Service
public class RoomService {
  
  private final RoomDao roomDao;
  private final BookingDao bookingDao;
  
  /**
   * Constructs a RoomService with required dependencies.
   * @param roomDao Data access object for room operations
   * @param bookingDao Data access object for booking operations
   */
  public RoomService(RoomDao roomDao, BookingDao bookingDao) {
    this.roomDao = roomDao;
    this.bookingDao = bookingDao;
  }
  
  /**
   * Retrieves all rooms in the system.
   * @return List of RoomRecord objects representing all rooms
   */
  public List<RoomRecord> getAllRooms() {
    return roomDao.findAll().stream()
      .map(RoomMapper::of)
      .collect(Collectors.toList());
  }
  
  /**
   * Retrieves a room by its ID.
   * @param id The ID of the room to retrieve
   * @return RoomRecord representing the requested room
   * @throws RuntimeException if room is not found
   */
  public RoomRecord getRoomById(Long id) {
    RoomEntity roomEntity = roomDao.findById(id)
      .orElseThrow(() -> new RuntimeException("RoomEntity not found"));
    return RoomMapper.of(roomEntity);
  }
  
  /**
   * Creates a new room.
   * @param roomRecord Room details including name and capacity
   * @return RoomRecord representing the created room
   */
  public RoomRecord createRoom(RoomRecord roomRecord) {
    RoomEntity roomEntity = new RoomEntity();
    roomEntity.setName(roomRecord.name());
    roomEntity.setCapacity(roomRecord.capacity());
    RoomEntity savedRoomEntity = roomDao.save(roomEntity);
    return RoomMapper.of(savedRoomEntity);
  }
  
  public RoomRecord updateRoom(Long id, RoomRecord roomRecord) {
    RoomEntity roomEntity = roomDao.findById(id)
      .orElseThrow(() -> new RuntimeException("RoomEntity not found"));
    roomEntity.setName(roomRecord.name());
    roomEntity.setCapacity(roomRecord.capacity());
    RoomEntity updatedRoomEntity = roomDao.save(roomEntity);
    return RoomMapper.of(updatedRoomEntity);
  }
  
  public void deleteRoom(Long id) {
    roomDao.deleteById(id);
  }
  
  /**
   * Checks room availability for a given time period.
   * @param startTime Start of the time period to check
   * @param endTime End of the time period to check
   * @return List of RoomRecord objects representing available rooms
   */
  public List<RoomRecord> getAvailableRooms(LocalDateTime startTime, LocalDateTime endTime) {
    List<RoomEntity> allRooms = roomDao.findAll();
    return allRooms.stream()
      .filter(room -> isRoomAvailable(room, startTime, endTime))
      .map(RoomMapper::of)
      .collect(Collectors.toList());
  }
  
  private boolean isRoomAvailable(RoomEntity room, LocalDateTime startTime, LocalDateTime endTime) {
    List<BookingRecord> bookings = room.getBookingEntities();
    return bookings.stream()
      .noneMatch(booking ->
        (startTime.isBefore(booking.endTime()) || startTime.isEqual(booking.endTime())) &&
          (endTime.isAfter(booking.startTime()) || endTime.isEqual(booking.startTime()))
      );
  }
}

