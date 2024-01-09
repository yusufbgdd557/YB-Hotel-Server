package dev.yusuf.bookingProject.exception;

public class RoleAlreadyExistsException extends RuntimeException {
    public RoleAlreadyExistsException(String message) {
        super(message);
    }
}
