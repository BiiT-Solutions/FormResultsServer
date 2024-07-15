package com.biit.forms.core.converters;

import com.biit.form.result.FormResult;
import com.biit.forms.persistence.entities.ReceivedForm;
import com.biit.kafka.events.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventConverter {

    @Value("${spring.application.name:#{null}}")
    private String applicationName;

    public ReceivedForm getReceivedForm(Event event, FormResult formResult) {
        final ReceivedForm receivedForm = new ReceivedForm();
        receivedForm.setForm(event.getPayload());
        receivedForm.setCreatedBy(event.getCreatedBy());
        receivedForm.setFormName(formResult.getName());
        receivedForm.setFormVersion(formResult.getVersion());
        receivedForm.setOrganization(formResult.getOrganization());
        return receivedForm;
    }
}
