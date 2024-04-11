package com.example.md05_project.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Override
    public String sendMail(String email,String subject,String text) {
        try{
            SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
            simpleMailMessage.setFrom("jav230630@gmail.com");
            simpleMailMessage.setTo(email);
            simpleMailMessage.setText(text);
            simpleMailMessage.setSubject(subject);
            javaMailSender.send(simpleMailMessage);
            return "Sent";
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
