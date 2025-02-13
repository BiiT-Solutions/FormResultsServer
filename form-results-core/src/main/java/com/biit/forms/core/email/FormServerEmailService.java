package com.biit.forms.core.email;

import com.biit.logger.mail.SendEmail;
import com.biit.logger.mail.exceptions.EmailNotSentException;
import com.biit.logger.mail.exceptions.InvalidEmailAddressException;
import com.biit.server.email.EmailSendPool;
import com.biit.server.email.ServerEmailService;
import com.biit.server.logger.EmailServiceLogger;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.usermanager.client.providers.UserManagerClient;
import com.biit.utils.file.FileReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class FormServerEmailService extends ServerEmailService {

    private static final String USER_ACCESS_EMAIL_TEMPLATE = "email-templates/parchment.html";

    @Value("#{new Boolean('${mail.server.enabled:true}')}")
    private boolean mailEnabled;

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

    @Value("${mail.hidden.copy.address:#{null}}")
    private String mailHiddenCopy;

    @Value("${mail.to.replace.address:#{null}}")
    private String mailToForcedAddress;

    @Value("${forms.not.sent.by.mail:}")
    private List<String> formsIgnoredNames;

    private final MessageSource messageSource;

    private final Locale locale = Locale.ENGLISH;

    private final UserManagerClient userManagerClient;

    public FormServerEmailService(Optional<EmailSendPool> emailSendPool, MessageSource messageSource, UserManagerClient userManagerClient) {
        super(emailSendPool, messageSource);
        this.messageSource = messageSource;
        this.userManagerClient = userManagerClient;
    }


    public void sendPdfForm(String username, String formName, byte[] pdfForm) throws EmailNotSentException, InvalidEmailAddressException,
            FileNotFoundException {
        if (!mailEnabled) {
            EmailServiceLogger.debug(this.getClass(), "Emails are disabled!");
            return;
        }
        if (formsIgnoredNames.contains(formName)) {
            EmailServiceLogger.warning(this.getClass(), "Form '{}' is marked as ignorable. Email will not be sent.", formName);
            return;
        }

        String mailTo = mailToForcedAddress;
        if (mailToForcedAddress == null || mailToForcedAddress.isBlank()) {
            final Optional<IAuthenticatedUser> user = userManagerClient.findByUsername(username);
            if (user.isPresent()) {
                mailTo = user.get().getEmailAddress();
            } else {
                EmailServiceLogger.warning(this.getClass(), "User '" + username + "' not found. Email not sent.");
                return;
            }
        }

        if (smtpServer != null && emailUser != null) {
            EmailServiceLogger.info(this.getClass(), "Sending form '{}' to email '{}' by '{}'.", formName, mailTo, username);
            final String emailTemplate = populateUserAccessMailFields(FileReader.getResource(USER_ACCESS_EMAIL_TEMPLATE, StandardCharsets.UTF_8),
                    new String[]{username}, locale);
            sendTemplate(mailTo, getMessage("pdf.form.mail.subject", null, locale),
                    emailTemplate, getMessage("pdf.form.mail.text", new String[]{username}, locale), pdfForm, formName + ".pdf");
        } else {
            EmailServiceLogger.debug(this.getClass(), "Email settings not set. Emails will be ignored.");
            EmailServiceLogger.debug(this.getClass(), "Values are smtpServer '{}', emailUser '{}'.",
                    smtpServer, emailUser);
            throw new EmailNotSentException("Email settings not set. Emails will be ignored.");
        }
    }


    private void sendTemplate(String email, String mailSubject, String emailTemplate, String plainText, byte[] pdfForm, String attachmentName)
            throws EmailNotSentException, InvalidEmailAddressException {
        if (!mailEnabled) {
            return;
        }
        if (smtpServer != null && emailUser != null) {
            SendEmail.sendEmail(smtpServer, smtpPort, emailUser, emailPassword, emailSender, email, mailCopy, mailHiddenCopy,
                    mailSubject, emailTemplate, plainText, pdfForm, MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE, attachmentName);
            EmailServiceLogger.info(this.getClass(), "Email sent!");
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
