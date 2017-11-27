package com.yukaiwen.Back;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {
	
	public void sendEmail(String email, String url) {
		String subject = "Confirmation email";
		
		String body = "<h4>One conversion has been done.</h4>" +
			"<div>You can download your video <a href=\"+url+\">here</a></div>";
		
		try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            String message = "Corps du message";

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("amelieykw1991@gmail.com", "Administrateur"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(email));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
	}
}
