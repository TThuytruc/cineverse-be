package com.hcmus.cineverse_be.service;

import com.google.firebase.auth.*;
import com.hcmus.cineverse_be.client.FirebaseAuthClient;
import com.hcmus.cineverse_be.entity.User;
import com.hcmus.cineverse_be.exception.FirebaseAuthenticationException;
import com.hcmus.cineverse_be.exception.ValidationException;
import com.hcmus.cineverse_be.response.auth.RefreshTokenResponse;
import com.hcmus.cineverse_be.validation.UserValidation;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final FirebaseAuth firebaseAuth;
    private final UserValidation userValidation;
    private final FirebaseAuthClient firebaseAuthClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    public UserService(FirebaseAuth firebaseAuth, UserValidation userValidation, FirebaseAuthClient firebaseAuthClient) {
        this.firebaseAuth = firebaseAuth;
        this.userValidation = userValidation;
        this.firebaseAuthClient = firebaseAuthClient;
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

            User user = new User();
            user.setUid(uid);
            user.setUsername(username);

            mongoTemplate.save(user);

        } catch (Exception e) {
            throw new RuntimeException("An error occurred while creating user.", e);
        }
    }

    private boolean checkUsernameExists(String username) {
        try {
            Query query = new Query(Criteria.where("username").is(username));
            return mongoTemplate.exists(query, User.class);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while checking username.", e);
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

            throw new RuntimeException("An error occurred while checking email.", e);
        }
    }

    private String getUsernameByUid(String uid) {
        try {
            Query query = new Query(Criteria.where("uid").is(uid));
            User user = mongoTemplate.findOne(query, User.class);
            return user != null ? user.getUsername() : null;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while getting username.", e);
        }
    }


    public RefreshTokenResponse refreshAccessToken(@NonNull final String refreshToken) {
        return firebaseAuthClient.refreshAccessToken(refreshToken);
    }

    public boolean isGoogleUser() {
        String uid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserRecord userRecord = null;
        try {
            userRecord = FirebaseAuth.getInstance().getUser(uid);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("An error occurred while getting user record.", e);
        }

        for (UserInfo provider : userRecord.getProviderData()) {
            if ("google.com".equals(provider.getProviderId())) {
                return true;
            }
        }

        return false;
    }

    public String getUserName() {

        String uid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserRecord userRecord = null;
        try {
            userRecord = FirebaseAuth.getInstance().getUser(uid);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("An error occurred while getting user record.", e);
        }

        boolean isGoogleUser = false;
        for (UserInfo provider : userRecord.getProviderData()) {
            if ("google.com".equals(provider.getProviderId())) {
                isGoogleUser = true;
                break;
            }
        }

        if (isGoogleUser) {
            return userRecord.getDisplayName();
        } else {
            return getUsernameByUid(uid);
        }
    }
}
