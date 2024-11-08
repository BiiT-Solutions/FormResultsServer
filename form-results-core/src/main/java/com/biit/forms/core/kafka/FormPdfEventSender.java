package com.biit.forms.core.kafka;

import com.biit.form.result.FormResult;
import com.biit.forms.core.kafka.converter.PdfReportEventConverter;
import com.biit.kafka.events.KafkaEventTemplate;
import com.biit.kafka.logger.EventsLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnExpression("${spring.kafka.enabled:false}")
public class FormPdfEventSender {

    @Value("${spring.kafka.send.topic:}")
    private String sendTopic;

    private final PdfReportEventConverter pdfReportEventConverter;

    private final KafkaEventTemplate kafkaTemplate;

    public FormPdfEventSender() {
        this.pdfReportEventConverter = null;
        this.kafkaTemplate = null;
    }

    public FormPdfEventSender(@Autowired(required = false) PdfReportEventConverter pdfReportEventConverter,
                              @Autowired(required = false) KafkaEventTemplate kafkaTemplate) {
        this.pdfReportEventConverter = pdfReportEventConverter;
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendPdfForm(byte[] pdfForm, FormResult formResult, UUID sessionId, String organization) {
        EventsLogger.debug(this.getClass().getName(), "Preparing for sending events...");
        if (kafkaTemplate != null && sendTopic != null && !sendTopic.isEmpty()) {
            kafkaTemplate.send(sendTopic, pdfReportEventConverter.getPdfEvent(pdfForm, formResult, sessionId, organization));
            EventsLogger.debug(this.getClass().getName(), "Event with pdf from '{}' and version '{}' send!",
                    formResult.getLabel(), formResult.getVersion());
        }
    }
}
