package com.biit.forms.core.converters;

import com.biit.server.controller.converters.ElementConverter;
import com.biit.forms.core.converters.models.ReceivedFormConverterRequest;
import com.biit.forms.core.models.ReceivedFormDTO;
import com.biit.forms.persistence.entities.ReceivedForm;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ReceivedFormConverter extends ElementConverter<ReceivedForm, ReceivedFormDTO, ReceivedFormConverterRequest> {


    @Override
    protected ReceivedFormDTO convertElement(ReceivedFormConverterRequest from) {
        final ReceivedFormDTO receivedFormDTO = new ReceivedFormDTO();
        BeanUtils.copyProperties(from.getEntity(), receivedFormDTO);
        return receivedFormDTO;
    }

    @Override
    public ReceivedForm reverse(ReceivedFormDTO to) {
        if (to == null) {
            return null;
        }
        final ReceivedForm receivedForm = new ReceivedForm();
        BeanUtils.copyProperties(to, receivedForm);
        return receivedForm;
    }
}
