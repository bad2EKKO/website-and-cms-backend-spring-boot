package com.MOF_ITMD.DepWeb.service;

import com.MOF_ITMD.DepWeb.models.InquiriesForm;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class InquiriesService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendInquiries(InquiriesForm inquiriesForm) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo("xxxxxxxx@xxxxx.xxx");
            helper.setSubject("Inquiry from " + inquiriesForm.getFirstName());
            helper.setText("Name: " + inquiriesForm.getFirstName() + " " + inquiriesForm.getLastName() + "\nContact Number: " + inquiriesForm.getContactNumber() + "\nEmail: " + inquiriesForm.getEmail() + "\nMessage: " + inquiriesForm.getMessage());

            javaMailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();

        }

    }

    public void SendReplyEmail(InquiriesForm inquiriesForm) {
        try {
            MimeMessage rmessage = javaMailSender.createMimeMessage();
            MimeMessageHelper rhelper = new MimeMessageHelper(rmessage, true);

            rhelper.setTo(inquiriesForm.getEmail());
            rhelper.setSubject("Reply for your Inquiries");
            rhelper.setText("Dear " + inquiriesForm.getFirstName() + "," + "\n\nThank you for your inquiry.");

            javaMailSender.send(rmessage);
        } catch (MessagingException e) {
            e.printStackTrace();

        }
    }

}
