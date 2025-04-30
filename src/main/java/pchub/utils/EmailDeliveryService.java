package pchub.utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailDeliveryService {
    private String smtpHost;
    private int smtpPort;
    private String username;
    private String password;
    
    public EmailDeliveryService(String smtpHost, int smtpPort, String username, String password) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.username = username;
        this.password = password;
    }
    
    public void sendOTP(String email, String otp) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Your OTP Code");
            message.setText("Your OTP code is: " + otp + "\nThis code will expire in " + 
                    GenerateOTP.OTP_EXPIRY_MINUTES + " minutes.");
            
            Transport.send(message);
            
            System.out.println("OTP sent to " + email);
        } catch (MessagingException e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage(), e);
        }
    }
} 