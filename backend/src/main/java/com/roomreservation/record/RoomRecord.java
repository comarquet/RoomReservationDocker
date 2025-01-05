package com.roomreservation.record;

import java.util.List;

public record RoomRecord(
  Long id,
  String name,
  int capacity,
  List<BookingRecord> bookings
) {}
