package dev.yusuf.bookingProject.business.concretes;

import dev.yusuf.bookingProject.business.abstracts.RoomService;
import dev.yusuf.bookingProject.exception.ResourceNotFoundException;
import dev.yusuf.bookingProject.model.Room;
import dev.yusuf.bookingProject.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomManager implements RoomService {

    @Autowired
    private final RoomRepository roomRepository;

    @Override
    public Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice) throws IOException, SQLException {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);

        if (!file.isEmpty()) {
            byte[] photoBytes = file.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            room.setPhoto(photoBlob);
        }

        return roomRepository.save(room);
    }

//    @Override
//    public Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice) throws IOException, SQLException {
//        Room room = new Room();
//        room.setRoomType(roomType);
//        room.setRoomPrice(roomPrice);
//
//        if (!file.isEmpty()) {
//            try {
//                byte[] photoBytes = file.getBytes();
//                Blob photoBlob = new SerialBlob(photoBytes);
//                room.setPhoto(photoBlob);
//            } catch (IOException | SQLException e) {
//
//                throw new RuntimeException("Error adding room with photo", e);
//            }
//        }
//
//        return roomRepository.save(room);
//    }


    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
        Optional<Room> theRoom = roomRepository.findById(roomId);
        if (theRoom.isEmpty()) {
            throw new ResourceNotFoundException("Sorry, room not found!");
        }
        Blob photoBlob = theRoom.get().getPhoto();
        if (photoBlob != null) {
            return photoBlob.getBytes(1, (int) photoBlob.length());
        }
        return null;
    }


    private byte[] readBlob(Blob blob) throws IOException, SQLException {
        try (InputStream inputStream = blob.getBinaryStream()) {
            return IOUtils.toByteArray(inputStream);
        }
    }

    @Override
    public void deleteRoom(Long roomId) {
        Optional<Room> theRoom = roomRepository.findById(roomId);
        if (theRoom.isPresent()) {
            roomRepository.deleteById(roomId);
        }
    }
}
