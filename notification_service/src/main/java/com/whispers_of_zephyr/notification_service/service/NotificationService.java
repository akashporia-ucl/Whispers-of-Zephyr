package com.whispers_of_zephyr.notification_service.service;

import java.time.Year;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.whispers_of_zephyr.notification_service.component.BlogEvent;
import com.whispers_of_zephyr.notification_service.component.NewUserEvent;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class NotificationService {

    private final int currentYear = Year.now().getValue();

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private final JavaMailSender javaMailSender;

    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    private void sendWelcomeEmail(String emailId) throws RuntimeException {
        log.info("Seting up welcome email to " + emailId);
        String htmlContent = "<!DOCTYPE html>"
                + "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1'>"
                + "<style>"
                + "@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }"
                + "@keyframes buttonHover { from { transform: scale(1); } to { transform: scale(1.05); } }"
                + "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f7f7f7; color: #333; }"
                + ".container { width: 100%; max-width: 700px; margin: 40px auto; background-color: #ffffff; padding: 30px; border-radius: 12px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); animation: fadeIn 1s ease-in; }"
                + ".header { background-color: #003366; color: white; padding: 20px; text-align: center; font-size: 28px; border-radius: 12px 12px 0 0; font-weight: bold; animation: fadeIn 1s ease-in-out; }"
                + ".content { padding: 20px; text-align: left; color: #333; line-height: 1.8; font-size: 16px; animation: fadeIn 1.2s ease-in-out; }"
                + ".cta-button { display: block; width: 80%; max-width: 250px; margin: 30px auto; padding: 14px; background-color: #0056b3; color: white; text-align: center; text-decoration: none; font-size: 18px; border-radius: 8px; font-weight: bold; transition: transform 0.3s ease-in-out; }"
                + ".cta-button:hover { animation: buttonHover 0.3s forwards; background-color: #004085; }"
                + ".footer { background-color: #f7f7f7; color: #666; text-align: center; padding: 15px; border-radius: 0 0 12px 12px; font-size: 14px; animation: fadeIn 1.4s ease-in-out; }"
                + "h3 { color: #003366; font-size: 20px; margin-top: 20px; font-weight: bold; }"
                + "ul { padding-left: 20px; margin-top: 10px; }"
                + "ul li { margin-bottom: 8px; }"
                + ".highlight { font-weight: bold; color: #0056b3; }"
                + "</style></head><body>"
                + "<div class='container'>"
                + "<div class='header'>You're Invited to Interview with Whispers of Zephyr</div>"
                + "<div class='content'>"
                + "<p>Dear Applicant,</p>"
                + "<p><strong>We are excited to move forward with your application!</strong></p>"
                + "<p>After reviewing your application, we’re thrilled to invite you to the next stage of our hiring process: the job interview. This is an exciting opportunity to learn more about your experience, skills, and passion to work with us at <strong>Whispers of Zephyr</strong>.</p>"
                + "<p>We believe in the power of collaboration and creativity, and we're looking for individuals who are not only skilled but also enthusiastic about joining a dynamic team dedicated to producing extraordinary content.</p>"
                + "<p><strong>Interview Details:</strong></p>"
                + "<ul>"
                + "<li><strong>Position:</strong> Software Engineer, London</li>"
                + "<li><strong>Salary:</strong> £ 100,000</li>"
                + "<li><strong>Interview Date:</strong> February 20, 2025</li>"
                + "<li><strong>Time:</strong> 10:00 AM (GMT)</li>"
                + "<li><strong>Location:</strong> Virtual (via Zoom - Link will be provided closer to the date)</li>"
                + "</ul>"
                + "<p>During the interview, we’ll discuss your previous experience, explore your creative ideas, and get to know you better as a potential member of our team. We’re looking forward to hearing about the unique perspective you would bring to Whispers of Zephyr.</p>"
                + "<h3>What to Prepare:</h3>"
                + "<ul>"
                + "<li>Your updated CV and portfolio (if applicable)</li>"
                + "<li>A brief summary of why you’re interested in this role and what you can offer</li>"
                + "<li>A few examples of your past work or projects</li>"
                + "<li>Data structure and algorithms</li>"
                + "</ul>"
                + "<p>If you have any questions or need further details, please don't hesitate to reach out to our HR team at <span class='highlight'>recruiting@whispersofzephyr.com</span> or reply to this email.</p>"
                + "<p><strong>We’re excited to meet you!</strong> Let’s make this interview a meaningful step towards a rewarding career with us.</p>"
                + "<p><strong>Please reply to this email to confirm your availability</strong><p>"
                + "<p>Looking forward to our conversation!</p>"
                + "<p>Best regards,<br><strong>Team Whispers of Zephyr</strong></p>"
                + "</div>"
                + "<div class='footer'>&copy; " + currentYear + " Whispers of Zephyr. All rights reserved.</div>"
                + "<div style='text-align: center; margin-top: 20px;'>"
                // + "<img src='/resources/static/interview.gif' alt='Interview Animation'
                // width='100' style='animation: fadeIn 1.5s ease-in-out;'>"
                + "</div>"
                + "</div></body></html>";

        // String htmlContent = "<!DOCTYPE html>"
        // + "<html><head><meta charset='UTF-8'><meta name='viewport'
        // content='width=device-width, initial-scale=1'>"
        // + "<style>"
        // + "@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }"
        // + "@keyframes buttonHover { from { transform: scale(1); } to { transform:
        // scale(1.05); } }"
        // + "body { font-family: Arial, sans-serif; margin: 0; padding: 0;
        // background-color: #f4f4f4; }"
        // + ".container { width: 100%; max-width: 600px; margin: 20px auto;
        // background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0
        // 10px rgba(0, 0, 0, 0.1); animation: fadeIn 1s ease-in; }"
        // + ".header { background-color: #4CAF50; color: white; padding: 15px;
        // text-align: center; font-size: 24px; border-radius: 8px 8px 0 0; font-weight:
        // bold; animation: fadeIn 1s ease-in-out; }"
        // + ".content { padding: 20px; text-align: left; color: #333333; line-height:
        // 1.6; font-size: 16px; animation: fadeIn 1.2s ease-in-out; }"
        // + ".cta-button { display: block; width: 80%; max-width: 250px; margin: 20px
        // auto; padding: 12px; background-color: #4CAF50; color: white; text-align:
        // center; text-decoration: none; font-size: 18px; border-radius: 5px;
        // font-weight: bold; transition: transform 0.2s ease-in-out; }"
        // + ".cta-button:hover { animation: buttonHover 0.3s forwards; }"
        // + ".footer { background-color: #f4f4f4; color: #888888; text-align: center;
        // padding: 15px; border-radius: 0 0 8px 8px; font-size: 14px; animation: fadeIn
        // 1.4s ease-in-out; }"
        // + "</style></head><body>"
        // + "<div class='container'>"
        // + "<div class='header'>Welcome to Whispers of Zephyr</div>"
        // + "<div class='content'>"
        // + "<p>Hello,</p>"
        // + "<p><strong>We’re absolutely delighted to have you here!</strong></p>"
        // + "<p>Life is a collection of stories, ideas, and fleeting moments of
        // inspiration. At <strong>Whispers of Zephyr</strong>, we believe in capturing
        // these moments—turning them into meaningful experiences that stay with
        // you.</p>"
        // + "<p>Whether you're here to escape into captivating narratives, explore deep
        // and thought-provoking ideas, or simply enjoy a peaceful moment of reflection,
        // you’ll find something that speaks to you.</p>"
        // + "<p><strong>So, what’s waiting for you?</strong></p>"
        // + "<h3>🌟 Discover What’s Inside:</h3>"
        // + "<ul>"
        // + "<li><strong>Engaging Stories:</strong> Lose yourself in tales that
        // transport you beyond the ordinary.</li>"
        // + "<li><strong>Thoughtful Articles:</strong> Explore reflections on life,
        // philosophy, creativity, and self-growth.</li>"
        // + "<li><strong>Serene Spaces:</strong> Find moments of tranquility through
        // mindfulness, meditative practices, and visual inspirations.</li>"
        // + "<li><strong>Exclusive Insights:</strong> Get access to handpicked content
        // curated just for you.</li>"
        // + "</ul>"
        // + "<p>We’re building something special here—<strong>a place where curiosity
        // thrives, and ideas flourish</strong>. We invite you to be a part of this
        // journey.</p>"
        // + "<h3>💬 Join the Conversation:</h3>"
        // + "<ul>"
        // + "<li>Comment on articles and share your thoughts—your voice matters.</li>"
        // + "<li>Connect with like-minded individuals who share your interests.</li>"
        // + "<li>Follow us on social media and stay up-to-date with fresh content and
        // exciting updates.</li>"
        // + "</ul>"
        // + "<p><strong>The best journeys begin with a single step.</strong> Let’s
        // embark on this together.</p>"
        // + "<a href='#' class='cta-button'>Start Exploring</a>"
        // + "<p>Looking forward to sharing this adventure with you!</p>"
        // + "<p>Best wishes,<br><strong>Team Whispers of Zephyr</strong></p>"
        // + "</div>"
        // + "<div class='footer'>&copy; " + currentYear + " Whispers of Zephyr. All
        // rights reserved.</div>"
        // + "<div style='text-align: center; margin-top: 20px;'>"
        // // + "<img src='/resources/static/welcome.gif' alt='Loading Animation'
        // // width='100' style='animation: fadeIn 1.5s ease-in-out;'>"
        // + "</div>"
        // + "</div></body></html>";

        String subject = "Welcome to Whispers of Zephyr";
        try {
            sendEmail(emailId, subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to send welcome email to " + emailId);
            throw new RuntimeException("Failed to send welcome email");
        }
        log.info(emailId + " received welcome email successfully");
    }

    public void sendOTP(String emailId, String otp) {
        log.info("Sending OTP to " + emailId);
        String subject = "Your OTP Code";
        String htmlContent = "<!DOCTYPE html>"
                + "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1'>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }"
                + ".container { width: 100%; max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }"
                + ".header { background-color: #4CAF50; color: white; padding: 15px; text-align: center; font-size: 24px; border-radius: 8px 8px 0 0; font-weight: bold; }"
                + ".content { padding: 20px; text-align: left; color: #333333; line-height: 1.6; font-size: 16px; }"
                + ".otp-box { display: block; font-size: 24px; font-weight: bold; color: #ffffff; background-color: #4CAF50; padding: 15px; text-align: center; border-radius: 5px; margin: 20px auto; width: 60%; }"
                + ".cta-button { display: block; width: 80%; max-width: 250px; margin: 20px auto; padding: 12px; background-color: #4CAF50; color: white; text-align: center; text-decoration: none; font-size: 18px; border-radius: 5px; font-weight: bold; }"
                + ".footer { background-color: #f4f4f4; color: #888888; text-align: center; padding: 15px; border-radius: 0 0 8px 8px; font-size: 14px; }"
                + "</style></head><body>"
                + "<div class='container'>"
                + "<div class='header'>Your Secure OTP Code</div>"
                + "<div class='content'>"
                + "<p>Hello,</p>"
                + "<p>You're receiving this email because a one-time password (<strong>OTP</strong>) was requested for your account verification.</p>"
                + "<p><strong>Your OTP Code:</strong></p>"
                + "<div class='otp-box'>" + otp + "</div>"
                + "<p>Please enter this OTP to complete your verification. This code is valid for <strong>10 minutes</strong>.</p>"
                + "<h3>🔒 Why This Matters?</h3>"
                + "<ul>"
                + "<li>Ensures secure access to your account.</li>"
                + "<li>Protects your personal information.</li>"
                + "<li>Prevents unauthorised access attempts.</li>"
                + "</ul>"
                + "<p>If you <strong>did not request this OTP</strong>, please ignore this email or contact support.</p>"
                + "<p>Stay secure and enjoy your experience with <strong>Whispers of Zephyr</strong>!</p>"
                + "<p>Best regards,<br><strong>Team Whispers of Zephyr</strong></p>"
                + "</div>"
                + "<div class='footer'>&copy; " + currentYear + " Whispers of Zephyr. All rights reserved.</div>"
                + "</div></body></html>";

        try {
            sendEmail(emailId, subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to send OTP email to " + emailId);
            throw new RuntimeException("Failed to send OTP email");
        }
        log.info(emailId + " received OTP email successfully");
    }

    private void sendEmail(String emailId, String subject, String htmlContent) throws Exception {
        log.info("Sending email to " + emailId);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromEmail);
        helper.setTo(emailId);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        message.setHeader("Importance", "High");
        javaMailSender.send(message);
    }

    @RabbitListener(queues = "notification-service.notify.updates")
    public void handleNewBlogCreatedEvent(BlogEvent blogEvent) {
        log.info("Received NewBlogCreatedEvent: Blog ID = {}, Title = {}, Author ID = {}, Created At = {}",
                blogEvent.getBlogId(), blogEvent.getBlogTitle(), blogEvent.getBlogAuthor(),
                blogEvent.getBlogCreatedTime());
    }

    // @RabbitListener(queues = "user.created.queue")
    // public void handleNewUserCreatedEvent(NewUserEvent newUserEvent) {
    // log.info("Received NewUserCreatedEvent: User ID = {}",
    // newUserEvent.getUserId());
    // try {
    // sendWelcomeEmail(newUserEvent.getEmail());
    // } catch (RuntimeException e) {
    // log.error("Failed to send welcome email to " + newUserEvent.getEmail());
    // }
    // }

}
