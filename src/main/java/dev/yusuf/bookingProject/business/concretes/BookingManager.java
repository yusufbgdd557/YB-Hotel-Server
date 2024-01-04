package dev.yusuf.bookingProject.business.concretes;

import dev.yusuf.bookingProject.business.abstracts.BookingService;
import dev.yusuf.bookingProject.business.abstracts.RoomService;
import dev.yusuf.bookingProject.exception.InvalidBookingRequestException;
import dev.yusuf.bookingProject.model.BookedRoom;
import dev.yusuf.bookingProject.model.Room;
import dev.yusuf.bookingProject.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingManager implements BookingService {

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final RoomService roomService;


    @Override
    public List<BookedRoom> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<BookedRoom> getAllBookedRoomsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    @Override
    public void cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {

        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new InvalidBookingRequestException("Check-in date must come before check-out date!");
        }
        Room room = roomService.getRoomById(roomId).get();
        List<BookedRoom> existingBookings = room.getBookings();
        boolean isRoomAvailable = isRoomAvailable(bookingRequest, existingBookings);
        if (isRoomAvailable) {
            room.addBooking(bookingRequest);
            bookingRepository.save(bookingRequest);
        } else {
            throw new InvalidBookingRequestException("Sorry, this room is not available for the selected dates!");
        }
        return bookingRequest.getBookingConfirmationCode();
    }

    @Override
    public BookedRoom findByBookingConfirmationCode(String bookingConfirmationCode) {
        return bookingRepository.findByBookingConfirmationCode(bookingConfirmationCode);
    }
    
    private boolean isRoomAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        return existingBookings.stream().noneMatch(currentBooking ->
                (bookingRequest.getCheckInDate().isBefore(currentBooking.getCheckOutDate()) ||
                        bookingRequest.getCheckInDate().equals(currentBooking.getCheckOutDate())) &&
                        (bookingRequest.getCheckOutDate().isAfter(currentBooking.getCheckInDate()) ||
                                bookingRequest.getCheckOutDate().equals(currentBooking.getCheckInDate()))
        );
    }


}
