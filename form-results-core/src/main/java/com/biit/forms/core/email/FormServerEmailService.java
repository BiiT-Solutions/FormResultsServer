package com.biit.forms.core.email;

import com.biit.logger.mail.SendEmail;
import com.biit.logger.mail.exceptions.EmailNotSentException;
import com.biit.logger.mail.exceptions.InvalidEmailAddressException;
import com.biit.server.logger.EmailServiceLogger;
import com.biit.utils.file.FileReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Locale;

@Service
public class FormServerEmailService {

    private static final String USER_ACCESS_EMAIL_TEMPLATE = "email-templates/cauldron.html";

    protected static final String EMAIL_TITLE_TAG = "EMAIL:TITLE";
    protected static final String EMAIL_SUBTITLE_TAG = "EMAIL:SUBTITLE";
    protected static final String EMAIL_BODY_TAG = "EMAIL:BODY";
    protected static final String EMAIL_FOOTER_TAG = "EMAIL:FOOTER";

    @Value("${mail.server.smtp.server:#{null}}")
    private String smtpServer;

    @Value("${mail.server.smtp.port:587}")
    private String smtpPort;

    @Value("${mail.server.smtp.username:#{null}}")
    private String emailUser;

    @Value("${mail.server.smtp.password:#{null}}")
    private String emailPassword;

    @Value("${mail.sender:#{null}}")
    private String emailSender;

    @Value("${mail.copy.address:#{null}}")
    private String mailCopy;

    @Value("${mail.to.address:#{null}}")
    private String mailTo;

    private final MessageSource messageSource;

    private final Locale locale = Locale.ENGLISH;

    public FormServerEmailService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    public void sendPdfForm(String submittedBy, String formName, byte[] pdfForm) throws EmailNotSentException, InvalidEmailAddressException,
            FileNotFoundException {
        if (mailTo != null) {
            if (smtpServer != null && emailUser != null) {
                final String emailTemplate = populateUserAccessMailFields(FileReader.getResource(USER_ACCESS_EMAIL_TEMPLATE, StandardCharsets.UTF_8),
                        new String[]{submittedBy}, locale);
                sendTemplate(mailTo, getMessage("pdf.form.mail.subject", null, locale),
                        emailTemplate, getMessage("pdf.form.mail.text", new String[]{submittedBy}, locale), pdfForm, formName + ".pdf");
                EmailServiceLogger.info(this.getClass(), "Sending form '{}' to email '{}' by ''.", formName, mailTo, submittedBy);
            } else {
                EmailServiceLogger.debug(this.getClass(), "Email settings not set. Emails will be ignored.");
                EmailServiceLogger.debug(this.getClass(), "Values are smtpServer '{}', emailUser '{}'.",
                        smtpServer, emailUser);
                throw new EmailNotSentException("Email settings not set. Emails will be ignored.");
            }
        } else {
            EmailServiceLogger.warning(this.getClass(), "No emailTo property set. Ignoring emails.");
        }
    }


    protected void sendTemplate(String email, String mailSubject, String emailTemplate, String plainText, byte[] pdfForm, String attachmentName)
            throws EmailNotSentException, InvalidEmailAddressException {
        if (smtpServer != null && emailUser != null) {
            SendEmail.sendEmail(smtpServer, smtpPort, emailUser, emailPassword, emailSender, Collections.singletonList(email), null,
                    mailCopy != null ? Collections.singletonList(mailCopy) : null, mailSubject,
                    emailTemplate, plainText, pdfForm, MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE, attachmentName);
        } else {
            EmailServiceLogger.warning(this.getClass(), "Email settings not set. Emails will be ignored.");
            EmailServiceLogger.debug(this.getClass(), "Values are smtpServer '{}', emailUser '{}'.",
                    smtpServer, emailUser);
            throw new EmailNotSentException("Email settings not set. Emails will be ignored.");
        }
    }


    private String populateUserAccessMailFields(String html, Object[] args, Locale locale) {
        return html.replace(EMAIL_TITLE_TAG, getMessage("pdf.form.mail.title", null, locale))
                .replace(EMAIL_SUBTITLE_TAG, getMessage("pdf.form.mail.subtitle", args, locale))
                .replace(EMAIL_BODY_TAG, getMessage("pdf.form.mail.body", args, locale))
                .replace(EMAIL_FOOTER_TAG, getMessage("pdf.form.mail.footer", null, locale));
    }


    protected String getMessage(String key, Object[] args, Locale locale) {
        try {
            return messageSource.getMessage(key, args, locale);
        } catch (Exception e) {
            EmailServiceLogger.severe(this.getClass(), e.getMessage());
            return key;
        }
    }
}
