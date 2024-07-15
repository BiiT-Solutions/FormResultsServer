package com.biit.forms.core.kafka;

import com.biit.kafka.events.Event;
import com.biit.kafka.logger.EventsLogger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;


/**
 * Subscribes to the EventListener to obtain any event, and handles it.
 */
@ConditionalOnExpression("${spring.kafka.enabled:false}")
@Controller
public class EventController {


    public EventController(@Autowired(required = false) EventConsumerListener eventListener) {

        //Listen to topic
        if (eventListener != null) {
            eventListener.addListener((event, offset, groupId, key, partition, topic, timeStamp) ->
                    eventHandler(event, key, partition, topic, timeStamp));
        }
    }

    public void eventHandler(Event event, String key, int partition, String topic, long timeStamp) {
        EventsLogger.debug(this.getClass(), "Received event '{}' on topic '{}', key '{}', partition '{}' at '{}'",
                event, topic, key, partition, LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),
                        TimeZone.getDefault().toZoneId()));

//        final String createdBy = event.getCustomProperties().get(EventCustomProperties.ISSUER.getTag()) != null
//                ? event.getCustomProperties().get(EventCustomProperties.ISSUER.getTag())
//                : event.getCreatedBy();
    }
}
