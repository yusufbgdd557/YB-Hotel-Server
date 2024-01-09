package dev.yusuf.bookingProject.business.abstracts;

import dev.yusuf.bookingProject.model.User;

import java.util.List;

public interface UserService {

    User registerUser(User user);

    List<User> getUsers();

    void deleteUser(String email);

    User getUser(String email);

}
