package dev.yusuf.bookingProject.business.concretes;

import dev.yusuf.bookingProject.business.abstracts.RoomService;
import dev.yusuf.bookingProject.model.Room;
import dev.yusuf.bookingProject.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

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

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }
}