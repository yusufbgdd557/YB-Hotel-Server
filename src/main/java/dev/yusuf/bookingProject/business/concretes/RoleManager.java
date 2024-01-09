package dev.yusuf.bookingProject.business.concretes;

import dev.yusuf.bookingProject.business.abstracts.RoleService;
import dev.yusuf.bookingProject.business.abstracts.UserService;
import dev.yusuf.bookingProject.exception.RoleAlreadyExistsException;
import dev.yusuf.bookingProject.exception.UserAlreadyExistsException;
import dev.yusuf.bookingProject.model.Role;
import dev.yusuf.bookingProject.model.User;
import dev.yusuf.bookingProject.repository.RoleRepository;

import dev.yusuf.bookingProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleManager implements RoleService {

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final UserService userService;

    @Autowired
    private final UserRepository userRepository;


    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role createRole(Role theRole) {
        String roleName = "ROLE_" + theRole.getName().toUpperCase();
        Role role = new Role(roleName);
        if (roleRepository.existsByName(roleName)) {
            throw new RoleAlreadyExistsException(theRole.getName() + "role already exists");
        }
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long roleId) {
        this.removeAllUsersFromRole(roleId);
        roleRepository.deleteById(roleId);
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name).get();
    }

    @Override
    public User removeUserFromRole(Long userId, Long roleId) {
        Optional <User> user = userRepository.findById(userId);
        Optional<Role> role = roleRepository.findById(roleId);
        if (role.isPresent() && role.get().getUsers().contains(user.get())) {
            role.get().removeUserFromRoles(user.get());
            roleRepository.save(role.get());
            return user.get();
        }
        throw new UsernameNotFoundException("User not found!");
    }

    @Override
    public User assignRoleToUser(Long userId, Long roleId) {
        Optional <User> user = userRepository.findById(userId);
        Optional<Role> role = roleRepository.findById(roleId);
        if (user.isPresent() && user.get().getRoles().contains(role.get())) {
            throw new UserAlreadyExistsException(user.get().getFirstName() + " is already assigned to the " + role.get().getName() + " role");
        }
        if (role.isPresent()) {
            role.get().assignRoleToUser(user.get());
            roleRepository.save(role.get());
        }
        return user.get();
    }

    @Override
    public Role removeAllUsersFromRole(Long roleId) {

        Optional<Role> role = roleRepository.findById(roleId);
        role.get().removeAllUsersFromRole();
        return roleRepository.save(role.get());

    }
}
