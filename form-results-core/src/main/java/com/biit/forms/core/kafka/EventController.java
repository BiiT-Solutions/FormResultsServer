package com.biit.forms.core.kafka;

import com.biit.form.result.FormResult;
import com.biit.forms.core.controllers.ReceivedFormController;
import com.biit.forms.core.converters.EventConverter;
import com.biit.forms.core.email.FormServerEmailService;
import com.biit.forms.logger.FormResultsLogger;
import com.biit.forms.persistence.repositories.ReceivedFormRepository;
import com.biit.kafka.events.Event;
import com.biit.kafka.events.EventCustomProperties;
import com.biit.kafka.logger.EventsLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.UUID;


/**
 * Subscribes to the EventListener to get any event, and handles it.
 */
@ConditionalOnExpression("${spring.kafka.enabled:false}")
@Controller
public class EventController {

    private final EventConverter eventConverter;
    private final ReceivedFormRepository receivedFormRepository;
    private final ReceivedFormController receivedFormController;
    private final FormServerEmailService formServerEmailService;
    private final FormPdfEventSender formPdfEventSender;

    private EventController() {
        this.eventConverter = null;
        this.receivedFormRepository = null;
        this.receivedFormController = null;
        this.formServerEmailService = null;
        this.formPdfEventSender = null;
    }

    @Autowired(required = false)
    public EventController(EventConsumerListener eventListener, EventConverter eventConverter,
                           ReceivedFormRepository receivedFormRepository, ReceivedFormController receivedFormController,
                           FormServerEmailService formServerEmailService, FormPdfEventSender formPdfEventSender) {
        this.eventConverter = eventConverter;
        this.receivedFormRepository = receivedFormRepository;
        this.receivedFormController = receivedFormController;
        this.formServerEmailService = formServerEmailService;
        this.formPdfEventSender = formPdfEventSender;

        //Listen to a topic
        if (eventListener != null) {
            eventListener.addListener((event, offset, groupId, key, partition, topic, timeStamp) ->
                    eventHandler(event, key, partition, topic, timeStamp));
        }
    }

    public synchronized void eventHandler(Event event, String key, int partition, String topic, long timeStamp) {
        EventsLogger.debug(this.getClass(), "Received event '{}' on topic '{}', key '{}', partition '{}' at '{}'",
                event, topic, key, partition, LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),
                        TimeZone.getDefault().toZoneId()));

        final String createdBy = event.getCustomProperties().get(EventCustomProperties.ISSUER.getTag()) != null
                ? event.getCustomProperties().get(EventCustomProperties.ISSUER.getTag())
                : event.getCreatedBy();

        try {
            //Event is a FormResult
            final FormResult formResult = getFomResult(event, createdBy);
            EventsLogger.info(this.getClass(), "Received Form Result '{}'.", formResult.getLabel());
            EventsLogger.debug(this.getClass(), "With content:\n '{}'.", formResult.toJson());
            receivedFormRepository.save(eventConverter.getReceivedForm(event, formResult));
            EventsLogger.debug(this.getClass(), "Form '{}' saved.", formResult.getLabel());
            final byte[] pdfForm = receivedFormController.convertToPdf(formResult, formResult.getSubmittedBy());
            //Generate PDF and send by email
            sendFormByMail(pdfForm, formResult.getLabel(), formResult.getSubmittedBy());
            //Generate PDF and send as event
            sendFormByEvent(pdfForm, formResult, event.getSessionId(), event.getOrganization());
        } catch (Exception e) {
            EventsLogger.severe(this.getClass(), "Invalid event received!!\n" + event);
        }
    }


    private FormResult getFomResult(Event event, String createdBy) {
        final FormResult formResult = event.getEntity(FormResult.class);
        formResult.setSubmittedBy(createdBy);
        return formResult;
    }


    private void sendFormByMail(byte[] pdfForm, String formLabel, String submittedBy) {
        try {
            formServerEmailService.sendPdfForm(submittedBy, formLabel, pdfForm);
        } catch (Exception e) {
            FormResultsLogger.errorMessage(this.getClass(), e);
        }
    }

    private void sendFormByEvent(byte[] pdfForm, FormResult formResult, UUID sessionId, String organization) {
        formPdfEventSender.sendPdfForm(pdfForm, formResult, sessionId, organization);
    }
}
