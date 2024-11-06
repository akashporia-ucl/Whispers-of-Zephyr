package com.whispers_of_zephyr.notification_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class NotificationService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final String textMessage = """
            Welcome to Whispers of Zephyr!

            We\u2019re thrilled to have you with us. Here, we believe in creating unforgettable experiences, whether you\u2019re here to dive into captivating stories, explore inspiring insights, or simply enjoy a moment of serenity.
            Take your time to browse through our latest blogs, discover hidden gems, and connect with a community that shares your curiosity. We hope you enjoy your journey with us and find plenty of inspiration along the way!

            What to Explore:
            \t- Engaging Stories: Dive into tales that transport you to other worlds.
            \t- Thoughtful Articles: Find insights on a variety of topics, from personal growth to philosophy.
            \t- Serene Spaces: Enjoy peaceful moments, meditative practices, and calming visuals.

            Join the community:
            \t- Share your thoughts and ideas with us in the comments.
            \t- Follow us on social media to stay connected with the latest updates.

            We\u2019re excited to have you with us on this journey. Let's explore the world together!

            Regards,
            Team Whispers of Zephyr
            """;

    @Autowired
    private final JavaMailSender javaMailSender;

    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendWelcomeEmail(String emailId) {
        log.info("Seting up welcome email to " + emailId);
        SimpleMailMessage messgae = new SimpleMailMessage();
        messgae.setFrom(fromEmail);
        messgae.setTo(emailId);
        messgae.setSubject("Welcome to Whispers of Zephyr");
        messgae.setText(textMessage);
        javaMailSender.send(messgae);
        log.info(emailId + " received welcome email successfully");
    }

}
