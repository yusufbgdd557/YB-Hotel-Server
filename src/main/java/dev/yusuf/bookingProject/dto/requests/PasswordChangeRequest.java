package dev.yusuf.bookingProject.dto.requests;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PasswordChangeRequest {

    private String email;
    private String currentPassword;
    private String newPassword;

}
