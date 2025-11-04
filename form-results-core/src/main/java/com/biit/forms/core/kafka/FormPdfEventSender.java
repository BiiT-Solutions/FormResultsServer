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

    @Value("${spring.kafka.send.pdf.topic:}")
    private String sendTopic;

    private final PdfReportEventConverter pdfReportEventConverter;

    private final KafkaEventTemplate kafkaTemplate;

    public FormPdfEventSender() {
        this.pdfReportEventConverter = null;
        this.kafkaTemplate = null;
    }

    @Autowired(required = false)
    public FormPdfEventSender(PdfReportEventConverter pdfReportEventConverter,
                              KafkaEventTemplate kafkaTemplate) {
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
