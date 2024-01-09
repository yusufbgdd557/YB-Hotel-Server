package dev.yusuf.bookingProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users = new HashSet<>();

    public Role(String name) {
        this.name = name;
    }

    public void assignRoleToUser(User user) {
        user.getRoles().add(this);
        this.getUsers().add(user);
    }

    public void removeUserFromRoles(User user) {
        user.getRoles().remove(this);
        this.getUsers().remove(user);
    }

    public void removeAllUsersFromRole() {
        if (this.getUsers() != null) {
            List<User> roleUsers =this.getUsers().stream().toList();
            roleUsers.forEach(this::removeUserFromRoles);
        }
    }

    public String getName() {
        return name != null? name : "";
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", users=" + users +
                '}';
    }
}
