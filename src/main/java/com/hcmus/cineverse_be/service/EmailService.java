package com.hcmus.cineverse_be.service;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        //message.setFrom("webdevelopmentadvanced2425@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        System.out.println("sender: " + mailSender);
        try {
            mailSender.send(message);
            log.info("Mail sent to {}", to);
        } catch (Exception e) {
            log.info("Failed to send email to {}", to + ", error: " + e.getMessage());
        }
    }
    /*@Value("${resend.api.key}")
    String resendApiKey;

    Resend resend;

    @PostConstruct
    public void init() {
        resend = new Resend(resendApiKey);
    }

    @Bean
    public Resend getResendInstance() {
        if (resend == null) {
            return new Resend(resendApiKey);
        }
        return resend;
    }

    public void sendEmail(@NonNull final CreateEmailOptions sendEmailRequest) {
        try {
            System.out.println(resendApiKey);
            CreateEmailResponse data = resend.emails().send(sendEmailRequest);
            System.out.println(data.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email.");
        }
    }*/
}
