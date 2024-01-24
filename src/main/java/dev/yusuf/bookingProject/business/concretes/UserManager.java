package dev.yusuf.bookingProject.business.concretes;

import dev.yusuf.bookingProject.business.abstracts.UserService;
import dev.yusuf.bookingProject.exception.UserAlreadyExistsException;
import dev.yusuf.bookingProject.model.Role;
import dev.yusuf.bookingProject.model.User;
import dev.yusuf.bookingProject.repository.RoleRepository;
import dev.yusuf.bookingProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserManager implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final RoleRepository roleRepository;


//    @Override
//    public User registerUser(User user) {
//
//        if (userRepository.existsByEmail(user.getEmail())){
//            throw new UserAlreadyExistsException(user.getEmail() + "Already exists");
//        }
//        System.out.println("Before setting password. Password is: " + user.getPassword());
//
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//        System.out.println("After setting password. Encoded password is: " + user.getPassword());
////        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        Role userRole = roleRepository.findByName("ROLE_USER").get();
//        user.setRoles(Collections.singletonList(userRole));
//        return userRepository.save(user);
//
//    }

    @Override
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(user.getEmail() + "already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role 'ROLE_USER' not found"));

        user.setRoles(Collections.singletonList(userRole));
        return userRepository.save(user);
    }


    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(String email) {
        User theUser = getUser(email);
        if (theUser != null) {
            this.userRepository.deleteByEmail(email);
        }

    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found!"));
    }


//    @Override
//    @TransactionalnewPassword
//    public void updatePassword(String username, String ) {
//        User theUser = userRepository.findByEmail(username)
//                .orElseThrow(() -> new ResourceNotFoundException("Person not found with username: " + username));
//
//        // Update the password
//        theUser.setPassword(newPassword);
//        userRepository.save(theUser);
//    }

    @Override
    public void changePassword(String email, String currentPassword, String newPassword) {
        // Retrieve user from the database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with the following email: " + email));

        // Validate the current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid current password");
        }

        // Update the password with the new one
        user.setPassword(passwordEncoder.encode(newPassword));

        // Save the updated user
        userRepository.save(user);
    }


}
