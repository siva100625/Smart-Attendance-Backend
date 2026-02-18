package com.example.smart_attendance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendLowAttendanceMail(String toEmail, String studentName, double percentage) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Low Attendance Warning");

        message.setText("Dear " + studentName + ",\n\n"
                + "Your attendance is below 75%.\n"
                + "Current Attendance: " + percentage + "%\n\n"
                + "Please attend classes regularly.\n\n"
                + "Smart Attendance System");

        mailSender.send(message);
    }
}
