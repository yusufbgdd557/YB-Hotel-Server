package dev.yusuf.bookingProject.business.abstracts;

import dev.yusuf.bookingProject.model.BookedRoom;

import java.util.List;

public interface BookingService {
    List<BookedRoom> getAllBookedRoomsByRoomId(Long roomId);

    void cancelBooking(Long bookingId);

    String saveBooking(Long roomId, BookedRoom bookingRequest);

    BookedRoom findByBookingConfirmationCode(String bookingConfirmationCode);

    List<BookedRoom> getAllBookings();

    List<BookedRoom> getBookingsByUserEmail(String email);
}
