package com.biit.forms.core.kafka;

/*-
 * #%L
 * Form Results Server (Core)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import com.biit.forms.core.models.ReceivedFormDTO;
import com.biit.kafka.events.EventSender;
import com.biit.kafka.events.KafkaEventTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * Sends an event when the controller of MyEntity performs any CRUD operation.
 * EVENT_TYPE will define the FACT_TYPE custom property on the event.
 */
@Component
@ConditionalOnExpression("${spring.kafka.enabled:false}")
public class ReceivedFormEventSender extends EventSender<ReceivedFormDTO> {

    private static final String MY_ENTITY_EVENT_TYPE = "myEntity";

    public static final String TAG = "MyApplication";

    public ReceivedFormEventSender() {
        super(null, TAG, MY_ENTITY_EVENT_TYPE);
    }

    public ReceivedFormEventSender(@Autowired(required = false) KafkaEventTemplate kafkaTemplate) {
        super(kafkaTemplate, TAG, MY_ENTITY_EVENT_TYPE);
    }
}
