package com.hcmus.cineverse_be.validation;

import org.springframework.stereotype.Component;

@Component
public class UserValidation {

    public String checkUsernameEmpty(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty";
        }
        return null;
    }

    public String checkEmailEmpty(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty";
        }
        return null;
    }

    public String checkPasswordEmpty(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty";
        }
        return null;
    }

    public String checkEmailFormat(String email) {
        // A simple email format validation
        String emailRegex = "^[a-z][a-z0-9_.]{0,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,}){1,3}$";
        if (email != null && !email.matches(emailRegex)) {
            return "Invalid email format";
        }
        return null;
    }

    public String checkPasswordLength(String password) {
        if (password.length() < 6) {
            return "Password must be at least 6 characters long";
        }
        return null;
    }
}
