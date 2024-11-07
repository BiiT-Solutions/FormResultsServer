package com.biit.forms.core.kafka;

import com.biit.form.result.FormResult;
import com.biit.forms.core.kafka.converter.EventConverter;
import com.biit.kafka.events.KafkaEventTemplate;
import com.biit.kafka.logger.EventsLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FormPdfEventSender {

    @Value("${spring.kafka.send.topic:}")
    private String sendTopic;

    private final EventConverter eventConverter;

    private final KafkaEventTemplate kafkaTemplate;

    public FormPdfEventSender(EventConverter eventConverter, KafkaEventTemplate kafkaTemplate) {
        this.eventConverter = eventConverter;

        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendPdfForm(byte[] pdfForm, FormResult formResult, UUID sessionId, String organization) {
        EventsLogger.debug(this.getClass().getName(), "Preparing for sending events...");
        if (kafkaTemplate != null && sendTopic != null && !sendTopic.isEmpty()) {
            kafkaTemplate.send(sendTopic, eventConverter.getPdfEvent(pdfForm, formResult, sessionId, organization));
            EventsLogger.debug(this.getClass().getName(), "Event with pdf from '{}' and version '{}' send!",
                    formResult.getLabel(), formResult.getVersion());
        }
    }
}
