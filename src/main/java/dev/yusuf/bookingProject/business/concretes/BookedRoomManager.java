package dev.yusuf.bookingProject.business.concretes;

import dev.yusuf.bookingProject.business.abstracts.BookedRoomService;
import dev.yusuf.bookingProject.model.BookedRoom;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookedRoomManager implements BookedRoomService {
    @Override
    public List<BookedRoom> getAllBookedRoomsByRoomId(Long roomId) {
        return null;
    }
}
