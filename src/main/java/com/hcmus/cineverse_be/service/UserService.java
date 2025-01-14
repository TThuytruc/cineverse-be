package com.hcmus.cineverse_be.service;

import com.google.firebase.auth.*;
import com.hcmus.cineverse_be.client.FirebaseAuthClient;
import com.hcmus.cineverse_be.entity.User;
import com.hcmus.cineverse_be.exception.FirebaseAuthenticationException;
import com.hcmus.cineverse_be.exception.ValidationException;
import com.hcmus.cineverse_be.response.auth.ProfileInformationResponse;
import com.hcmus.cineverse_be.response.auth.RefreshTokenResponse;
import com.hcmus.cineverse_be.validation.UserValidation;
import com.resend.*;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import com.resend.services.emails.model.Email;
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
    private final EmailService emailService;

    @Autowired
    private MongoTemplate mongoTemplate;

    public UserService(FirebaseAuth firebaseAuth, UserValidation userValidation, FirebaseAuthClient firebaseAuthClient, EmailService emailService) {
        this.firebaseAuth = firebaseAuth;
        this.userValidation = userValidation;
        this.firebaseAuthClient = firebaseAuthClient;
        this.emailService = emailService;
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
        request.setEmailVerified(Boolean.FALSE);
        request.setDisplayName(username);

        try {
            UserRecord userRecord = firebaseAuth.createUser(request);
            String uid = userRecord.getUid();

            User user = new User();
            user.setUid(uid);
            user.setUsername(username);

            sendEmailVerification(uid);

            mongoTemplate.save(user);



        } catch (Exception e) {
            throw new RuntimeException("An error occurred while creating user.", e);
        }
    }

    public void sendEmailVerification(String uid) {
        try {
            UserRecord userRecord = firebaseAuth.getUser(uid);
            String email = userRecord.getEmail();

            if (email != null) {

                String link = firebaseAuthClient.generateEmailVerification(email);

                System.out.println("generate email to " + email + " with link: " + link);
                // For example, using JavaMailSender or any other email service
                sendEmail(email, link);
            }
        } catch (Exception e) {
            throw new FirebaseAuthenticationException("Failed to send email verification link." + e);
        }
    }

    private void sendEmail(String email, String link)  {
        try {

            //using JavaMailSender or any other email service
            emailService.sendMail(email, "Verify your email", "Congrats on sending your confirmation link: " + link);


            System.out.println("Sending email to " + email + " with link: " + link);

            /*CreateEmailOptions sendEmailRequest = CreateEmailOptions.builder()
                    .from("awd24.example.com")
                    //.from("webdevelopmentadvanced2425@gmail.com")
                    .to(email)
                    .subject("Verify your email")
                    .html("<p>Congrats on sending your confirmation link: <strong>"+link+"</strong>!</p>")
                    .build();

            emailService.sendEmail(sendEmailRequest);*/

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email verification link: " + e);
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

    public void verifyEmailCallback(String oobCode) {
        try {
            System.out.println("oobCode: " + oobCode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify email.");
        }
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

    public ProfileInformationResponse getUserInformation(@NonNull final String idToken) {
        ProfileInformationResponse profileInformationResponse = firebaseAuthClient.getUserEmailAndUidFromIdToken(idToken);

        UserRecord userRecord = null;
        try {
            userRecord = FirebaseAuth.getInstance().getUser(profileInformationResponse.getUid());
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
            profileInformationResponse.setUsername(userRecord.getDisplayName());
        } else {
            profileInformationResponse.setUsername(getUsernameByUid(profileInformationResponse.getUid()));
        }

        return profileInformationResponse;
    }
}
