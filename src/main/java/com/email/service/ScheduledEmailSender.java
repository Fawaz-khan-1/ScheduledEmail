package com.email.service;

import com.email.entity.User;
import com.email.repo.UserRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ScheduledEmailSender {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender emailSender;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void sendEmailAtThreePM() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            sendEmail(user);
        }
    }

    private void sendEmail(User user) {
        String recipientEmail = user.getEmail();
        String recipientName = user.getName();
        int mailCount = user.getMailCount() + 1;
        user.setMailCount(mailCount);
        userRepository.save(user);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject("StartupIndia - Profile Creation");

            Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
            configuration.setClassForTemplateLoading(this.getClass(), "/templates");
            Template template = configuration.getTemplate("emailTemplate.ftl");

            Map<String, Object> model = new HashMap<>();
            model.put("recipientName", recipientName);
            model.put("recipientEmail", recipientEmail);
            model.put("mailCount", mailCount);

            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            helper.setText(content, true);

            ClassPathResource resource = new ClassPathResource("static.images/congrats.png");
            helper.addInline("image", resource);


        } catch (MessagingException | IOException | TemplateException e) {
            e.printStackTrace();
        }
        emailSender.send(message);
    }
}
