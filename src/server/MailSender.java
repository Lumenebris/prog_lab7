package server;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailSender {

    private static MailSender instance;
    private final ExecutorService executor;
    private final Properties mailProps;
    private final String login;
    private final String password;

    public static MailSender getInstance() {
        if (instance == null) {
            instance = new MailSender();
        }
        return instance;
    }

    private MailSender() {
        this.executor = Executors.newWorkStealingPool();
        this.mailProps = new Properties();
        this.login = "lena_laba7@mail.ru";
        this.password = "matbax-sapgik-3wEvve";

        mailProps.put("mail.smtp.user", login);
        mailProps.put("mail.smtp.password", password);
        mailProps.put("mail.smtp.host", "smtp.mail.ru");
        mailProps.put("mail.smtp.port", "465");
        mailProps.put("mail.smtp.auth", "true");
        mailProps.put("mail.smtp.ssl.enable", "true");
        mailProps.put("mail.debug", "true");
    }

    public void sendMail(String recipient, String subject, String body) {
        executor.submit(() -> {
            Session session = Session.getInstance(mailProps,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(login, password);
                        }
                    });
            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(login));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(recipient));
                message.setSubject(subject);
                message.setText(body);
                Transport.send(message);
            } catch (MessagingException e) {
                System.out.println("Не удалось отправить почту: " + e);
            }
        });
    }
}
