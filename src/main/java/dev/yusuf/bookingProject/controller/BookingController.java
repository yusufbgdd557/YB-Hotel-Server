package dev.yusuf.bookingProject.controller;

import dev.yusuf.bookingProject.business.abstracts.BookingService;
import dev.yusuf.bookingProject.business.abstracts.RoomService;
import dev.yusuf.bookingProject.dto.responses.BookingResponse;
import dev.yusuf.bookingProject.dto.responses.RoomResponse;
import dev.yusuf.bookingProject.exception.InvalidBookingRequestException;
import dev.yusuf.bookingProject.exception.ResourceNotFoundException;
import dev.yusuf.bookingProject.model.BookedRoom;
import dev.yusuf.bookingProject.model.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
@CrossOrigin(origins = "http://localhost:5173")
public class BookingController {

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private final RoomService roomService;


    @GetMapping("/all-bookings")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookedRoom> bookedRooms = bookingService.getAllBookings();
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (BookedRoom bookedRoom : bookedRooms) {
            BookingResponse bookingResponse = getBookingResponse(bookedRoom);
            bookingResponses.add(bookingResponse);
        }
        return  ResponseEntity.ok(bookingResponses);
    }

    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        try {
            BookedRoom bookedRoom = bookingService.findByBookingConfirmationCode(confirmationCode);
            BookingResponse bookingResponse = getBookingResponse(bookedRoom);
            return ResponseEntity.ok(bookingResponse);

        } catch (ResourceNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
                                         @RequestBody BookedRoom bookingRequest) {
        try {
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok("Room booked successfully! Your booking confirmation code is :" + confirmationCode);
        } catch (InvalidBookingRequestException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public void CancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
    }

    @GetMapping("/user/{email}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserEmail(@PathVariable String email) {
        List<BookedRoom> bookings = bookingService.getBookingsByUserEmail(email);
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (BookedRoom booking : bookings) {
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }



    private BookingResponse getBookingResponse(BookedRoom bookedRoom) {
        Room theRoom = roomService.getRoomById(bookedRoom.getRoom().getId()).get();
        RoomResponse roomResponse = new RoomResponse(
                theRoom.getId(),
                theRoom.getRoomType(),
                theRoom.getRoomPrice());
        return new BookingResponse(
                bookedRoom.getBookingId(),bookedRoom.getCheckInDate(),
                bookedRoom.getCheckOutDate(), bookedRoom.getGuestFullName(),
                bookedRoom.getGuestEmail(), bookedRoom.getNumberOfAdults(),
                bookedRoom.getNumberOfChildren(), bookedRoom.getTotalNumberOfGuests(),
                bookedRoom.getBookingConfirmationCode(), roomResponse);
    }
}
