package com.biit.forms.core.controllers;


import com.biit.form.result.FormResult;
import com.biit.form.result.pdf.FormAsPdf;
import com.biit.form.result.pdf.exceptions.EmptyPdfBodyException;
import com.biit.form.result.pdf.exceptions.InvalidElementException;
import com.biit.forms.core.converters.ReceivedFormConverter;
import com.biit.forms.core.converters.models.ReceivedFormConverterRequest;
import com.biit.forms.core.exceptions.ReceivedFormNotFoundException;
import com.biit.forms.core.kafka.ReceivedFormEventSender;
import com.biit.forms.core.models.ReceivedFormDTO;
import com.biit.forms.core.providers.ReceivedFormProvider;
import com.biit.forms.persistence.entities.ReceivedForm;
import com.biit.forms.persistence.repositories.ReceivedFormRepository;
import com.biit.kafka.config.ObjectMapperFactory;
import com.biit.kafka.controllers.KafkaElementController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ReceivedFormController extends KafkaElementController<ReceivedForm, Long, ReceivedFormDTO, ReceivedFormRepository,
        ReceivedFormProvider, ReceivedFormConverterRequest, ReceivedFormConverter> {

    @Autowired
    protected ReceivedFormController(ReceivedFormProvider provider, ReceivedFormConverter converter, ReceivedFormEventSender eventSender) {
        super(provider, converter, eventSender);
    }

    @Override
    protected ReceivedFormConverterRequest createConverterRequest(ReceivedForm receivedForm) {
        return new ReceivedFormConverterRequest(receivedForm);
    }

    public ReceivedFormDTO getByName(String name) {
        return getConverter().convert(new ReceivedFormConverterRequest(getProvider().findByName(name).orElseThrow(() ->
                new ReceivedFormNotFoundException(this.getClass(),
                        "No MyEntity with name '" + name + "' found on the system."))));
    }

    public byte[] convertToPdf(ReceivedFormDTO receivedFormDTO, String submittedBy) throws EmptyPdfBodyException, DocumentException, InvalidElementException,
            JsonProcessingException {
        return convertToPdf(ObjectMapperFactory.getObjectMapper().readValue(receivedFormDTO.getForm(), FormResult.class), submittedBy);
    }

    public byte[] convertToPdf(FormResult formResult) throws EmptyPdfBodyException, DocumentException, InvalidElementException {
        return convertToPdf(formResult, formResult.getName());
    }

    public byte[] convertToPdf(FormResult formResult, String footer) throws EmptyPdfBodyException, DocumentException, InvalidElementException {
        // Convert to pdf.
        final FormAsPdf pdfDocument = new FormAsPdf(formResult, footer);
        return pdfDocument.generate();
    }
}
