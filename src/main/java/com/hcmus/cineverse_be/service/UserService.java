package com.hcmus.cineverse_be.service;

import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.cloud.firestore.Firestore;
import com.hcmus.cineverse_be.exception.ValidationException;
import com.hcmus.cineverse_be.validation.UserValidation;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final FirebaseAuth firebaseAuth;
    private final Firestore firestore;
    private final UserValidation userValidation;

    public UserService(FirebaseAuth firebaseAuth, Firestore firestore, UserValidation userValidation) {
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;
        this.userValidation = userValidation;
    }

    public void create(String username, String email, String password) {
        Map<String, String> errors = new HashMap<>();

        // Validate username
        String usernameError = userValidation.checkUsernameEmpty(username);
        if (usernameError != null) {
            errors.put("username", usernameError);
        } else {
            boolean hasUsernameExists = checkUsernameExists(username);
            if (hasUsernameExists) {
                errors.put("username", "Username already exists");
            }
        }

        // Validate email
        String emailError = userValidation.checkEmailEmpty(email);
        if (emailError != null) {
            errors.put("email", emailError);
        } else {
            String emailFormatError = userValidation.checkEmailFormat(email);
            if (emailFormatError != null) {
                errors.put("email", emailFormatError);
            } else {
                boolean hasEmailExists = checkEmailExists(email);
                if (hasEmailExists) {
                    errors.put("email", "Email already exists");
                }
            }
        }

        // Validate password
        String passwordError = userValidation.checkPasswordEmpty(password);
        if (passwordError != null) {
            errors.put("password", passwordError);
        } else {
            String passwordLengthError = userValidation.checkPasswordLength(password);
            if (passwordLengthError != null) {
                errors.put("password", passwordLengthError);
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("User validation error", errors);
        }

        // Create user in Firebase
        UserRecord.CreateRequest request = new UserRecord.CreateRequest();
        request.setEmail(email);
        request.setPassword(password);
        request.setEmailVerified(Boolean.TRUE);
        request.setDisplayName(username);

        try {
            UserRecord userRecord = firebaseAuth.createUser(request);
            String uid = userRecord.getUid();

            Map<String, Object> userData = new HashMap<>();
            userData.put("uid", uid);
            userData.put("username", username);

            firestore.collection("user").document(uid)
                    .set(userData)
                    .get();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create user in Firebase", e);
        }
    }

    private boolean checkUsernameExists(String username) {
        try {
            Query query = firestore.collection("user").whereEqualTo("username", username);
            QuerySnapshot snapshot = query.get().get();
            return !snapshot.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkEmailExists(String email) {
        try {
            firebaseAuth.getUserByEmail(email);
            return true;
        } catch (FirebaseAuthException e) {
            if ("USER_NOT_FOUND".equals(e.getAuthErrorCode().toString())) {
                return false;
            }

            throw new RuntimeException("Error when checking email", e);
        }
    }
}
