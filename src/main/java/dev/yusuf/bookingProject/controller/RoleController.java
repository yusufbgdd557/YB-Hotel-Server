package dev.yusuf.bookingProject.controller;

import dev.yusuf.bookingProject.business.abstracts.RoleService;
import dev.yusuf.bookingProject.exception.RoleAlreadyExistsException;
import dev.yusuf.bookingProject.model.Role;
import dev.yusuf.bookingProject.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class RoleController {

    @Autowired
    private final RoleService roleService;

    @GetMapping("/all-roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return new ResponseEntity<>(roleService.getRoles(), HttpStatus.FOUND);
    }

//    @PostMapping("/create-new-role")
//    public ResponseEntity<String> createRole(@RequestBody Role theRole) {
//        try {
//            roleService.createRole(theRole);
//            return ResponseEntity.ok("New role created successfully!");
//        } catch (RoleAlreadyExistsException exception) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
//        }
//    }

    @PostMapping("/create-new-role")
    public ResponseEntity<String> createRole(@RequestBody Role theRole, Authentication authentication) {
        // Check if the user has the "ADMIN" role
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            try {
                roleService.createRole(theRole);
                return ResponseEntity.ok("New role created successfully!");
            } catch (RoleAlreadyExistsException exception) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to create a new role.");
        }
    }

    @DeleteMapping("/delete/{roleId}")
    public void deleteRole(@PathVariable("roleId") Long roleId) {
        this.roleService.deleteRole(roleId);
    }

    @PostMapping("/remove-all-users-from-role/{roleId}")
    public Role removeAllUsersFromRole(@PathVariable("roleId") Long roleId) {
        return this.roleService.removeAllUsersFromRole(roleId);
    }

    @PostMapping("/remove-user-from-role")
    public User removeUser(@RequestParam("userId") Long userId,
                           @RequestParam("roleId") Long roleId) {
        return this.roleService.removeUserFromRole(userId,roleId);
    }

    @PostMapping("/assign-role-to-user")
    public User assignRoleToUser(@RequestParam("userId") Long userId,
                                 @RequestParam("roleId") Long roleId) {
        return this.roleService.assignRoleToUser(userId, roleId);
    }

}
