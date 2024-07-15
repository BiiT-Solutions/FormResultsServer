package com.biit.forms.core.kafka;

import com.biit.form.result.FormResult;
import com.biit.forms.core.controllers.ReceivedFormController;
import com.biit.forms.core.converters.EventConverter;
import com.biit.forms.core.email.ServerEmailService;
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


/**
 * Subscribes to the EventListener to get any event, and handles it.
 */
@ConditionalOnExpression("${spring.kafka.enabled:false}")
@Controller
public class EventController {

    private final EventConverter eventConverter;
    private final ReceivedFormRepository receivedFormRepository;
    private final ReceivedFormController receivedFormController;
    private final ServerEmailService serverEmailService;


    public EventController(@Autowired(required = false) EventConsumerListener eventListener, EventConverter eventConverter,
                           ReceivedFormRepository receivedFormRepository, ReceivedFormController receivedFormController,
                           ServerEmailService serverEmailService) {
        this.eventConverter = eventConverter;
        this.receivedFormRepository = receivedFormRepository;
        this.receivedFormController = receivedFormController;
        this.serverEmailService = serverEmailService;

        //Listen to a topic
        if (eventListener != null) {
            eventListener.addListener((event, offset, groupId, key, partition, topic, timeStamp) ->
                    eventHandler(event, key, partition, topic, timeStamp));
        }
    }

    public void eventHandler(Event event, String key, int partition, String topic, long timeStamp) {
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
            //Generate PDF and send by email
            sendFormByMail(formResult);
        } catch (Exception e) {
            EventsLogger.severe(this.getClass(), "Invalid event received!!\n" + event);
        }
    }


    private FormResult getFomResult(Event event, String createdBy) {
        final FormResult formResult = event.getEntity(FormResult.class);
        formResult.setSubmittedBy(createdBy);
        return formResult;
    }


    private void sendFormByMail(FormResult formResult) {
        try {
            final byte[] pdfForm = receivedFormController.convertToPdf(formResult);
            serverEmailService.sendPdfForm(formResult.getSubmittedBy(), formResult.getName(), pdfForm);
        } catch (Exception e) {
            FormResultsLogger.errorMessage(this.getClass(), e);
        }
    }
}
