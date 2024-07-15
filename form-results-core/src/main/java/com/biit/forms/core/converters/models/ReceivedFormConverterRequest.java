package com.biit.forms.core.converters.models;

import com.biit.server.converters.models.ConverterRequest;
import com.biit.forms.persistence.entities.ReceivedForm;

public class ReceivedFormConverterRequest extends ConverterRequest<ReceivedForm> {
    public ReceivedFormConverterRequest(ReceivedForm receivedForm) {
        super(receivedForm);
    }
}
