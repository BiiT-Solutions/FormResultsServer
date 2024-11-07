package com.biit.forms.core.kafka.converter;

import com.biit.form.result.FormResult;
import com.biit.kafka.events.Event;
import com.biit.kafka.events.EventCustomProperties;
import com.biit.kafka.events.EventSubject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class EventConverter {
    private static final String FORM_RESULT_EVENT_TYPE = "PdfReport";

    @Value("${spring.application.name:#{null}}")
    private String applicationName;

    public PdfFormPayload generatePayload(byte[] pdfForm, FormResult formResult) {
        final PdfFormPayload infographicPayload = new PdfFormPayload();
        infographicPayload.setFormName(formResult.getLabel());
        infographicPayload.setFormVersion(formResult.getVersion());
        infographicPayload.setPdfContent(pdfForm);
        return infographicPayload;
    }

    public Event getPdfEvent(byte[] pdfForm, FormResult formResult, UUID sessionId, String organization) {
        final PdfFormPayload eventPayload = generatePayload(pdfForm, formResult);
        final Event event = new Event(eventPayload);
        event.setCreatedBy(formResult.getSubmittedBy());
        event.setMessageId(UUID.randomUUID());
        event.setSubject(EventSubject.REPORT.toString());
        event.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        event.setCreatedAt(LocalDateTime.now());
        event.setReplyTo(applicationName);
        event.setTag(formResult.getLabel());
        event.setSessionId(sessionId);
        event.setCustomProperty(EventCustomProperties.FACT_TYPE, FORM_RESULT_EVENT_TYPE);
        event.setOrganization(organization);
        return event;
    }
}
