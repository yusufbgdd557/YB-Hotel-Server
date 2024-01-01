package dev.yusuf.bookingProject.controller;

import dev.yusuf.bookingProject.business.abstracts.BookedRoomService;
import dev.yusuf.bookingProject.business.abstracts.RoomService;
import dev.yusuf.bookingProject.dto.responses.BookedRoomResponse;
import dev.yusuf.bookingProject.dto.responses.RoomResponse;
import dev.yusuf.bookingProject.exception.PhotoRetrivalException;
import dev.yusuf.bookingProject.model.BookedRoom;
import dev.yusuf.bookingProject.model.Room;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private final RoomService roomService;

    @Autowired
    private final BookedRoomService bookedRoomService;

    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom (
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice")BigDecimal roomPrice) throws SQLException, IOException {

        Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
        RoomResponse roomResponse = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(), savedRoom.getRoomPrice());
        return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("/room/types")
    public List<String> getRoomTypes() {
        return roomService.getAllRoomTypes();
    }

    @GetMapping("/all-rooms")
    public  ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : rooms) {
            byte [] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0) {
                String base64Photo = Base64.encodeBase64String(photoBytes);
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(base64Photo);
                roomResponses.add(roomResponse);
            }
        }
    return ResponseEntity.ok(roomResponses);
    }

    private RoomResponse getRoomResponse(Room room) {
        List<BookedRoom> bookedRooms = getAllBookedRoomsByRoomId(room.getId());
//        List<BookedRoomResponse> bookedRoomResponses = bookedRooms
//                .stream()
//                .map(bookedRoom -> new BookedRoomResponse(bookedRoom.getBookingId(),
//                        bookedRoom.getCheckInDate(),
//                        bookedRoom.getCheckOutDate(),
//                        bookedRoom.getBookingConfirmationCode())).toList();
        byte [] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        if (photoBlob != null) {
            try {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            } catch (SQLException e) {
                throw new PhotoRetrivalException("Error retrieving photo!");
            }
        }
        return new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(), room.getIsBooked(), photoBytes);
    }

    private List<BookedRoom> getAllBookedRoomsByRoomId(Long roomId) {
        return bookedRoomService.getAllBookedRoomsByRoomId(roomId);
    }
    @DeleteMapping("/delete/room/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("roomId") Long roomId) {
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
