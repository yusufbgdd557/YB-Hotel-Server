package dev.yusuf.bookingProject.business.abstracts;

import dev.yusuf.bookingProject.model.BookedRoom;

import java.util.List;

public interface BookedRoomService {
    List<BookedRoom> getAllBookedRoomsByRoomId(Long roomId);
}
