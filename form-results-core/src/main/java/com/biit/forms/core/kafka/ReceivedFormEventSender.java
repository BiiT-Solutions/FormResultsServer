package com.biit.forms.core.kafka;


import com.biit.forms.core.models.ReceivedFormDTO;
import com.biit.kafka.events.EventSender;
import com.biit.kafka.events.KafkaEventTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Sends an event when the controller of MyEntity performs any CRUD operation.
 * EVENT_TYPE will define the FACT_TYPE custom property on the event.
 */
@Component
public class ReceivedFormEventSender extends EventSender<ReceivedFormDTO> {

    private static final String MY_ENTITY_EVENT_TYPE = "myEntity";

    public static final String TAG = "MyApplication";

    public ReceivedFormEventSender(@Autowired(required = false) KafkaEventTemplate kafkaTemplate) {
        super(kafkaTemplate, TAG, MY_ENTITY_EVENT_TYPE);
    }
}
