package com.biit.forms.core.controllers;

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
import com.biit.server.security.IUserOrganizationProvider;
import com.biit.server.security.model.IUserOrganization;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Locale;

@Controller
public class ReceivedFormController extends KafkaElementController<ReceivedForm, Long, ReceivedFormDTO, ReceivedFormRepository,
        ReceivedFormProvider, ReceivedFormConverterRequest, ReceivedFormConverter> {

    @Value("${show.technical.names:true}")
    private boolean showTechnicalNames;

    @Value("${disable.translations:false}")
    private boolean disableTranslations;

    protected ReceivedFormController(ReceivedFormProvider provider, ReceivedFormConverter converter,
                                     @Autowired(required = false) ReceivedFormEventSender eventSender,
                                     List<IUserOrganizationProvider<? extends IUserOrganization>> userOrganizationProvider) {
        super(provider, converter, eventSender, userOrganizationProvider);
    }

    @Override
    protected ReceivedFormConverterRequest createConverterRequest(ReceivedForm receivedForm) {
        return new ReceivedFormConverterRequest(receivedForm);
    }

    public ReceivedFormDTO getByName(String name) {
        return getConverter().convert(new ReceivedFormConverterRequest(getProvider().findByName(name).orElseThrow(() ->
                new ReceivedFormNotFoundException(this.getClass(),
                        "No Document with name '" + name + "' found on the system."))));
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
        final FormAsPdf pdfDocument = new FormAsPdf(formResult, footer, showTechnicalNames, disableTranslations);
        return pdfDocument.generate();
    }

    public byte[] convertToPdf(FormResult formResult, String footer, Locale locale) throws EmptyPdfBodyException, DocumentException, InvalidElementException {
        // Convert to pdf.
        final FormAsPdf pdfDocument = new FormAsPdf(formResult, footer, locale, showTechnicalNames, disableTranslations);
        return pdfDocument.generate();
    }

    public ReceivedFormDTO findBy(String name, int version, String createdBy, String organization) {
        return getConverter().convert(new ReceivedFormConverterRequest(getProvider().findBy(name, version, createdBy, organization).orElseThrow(() ->
                new ReceivedFormNotFoundException(this.getClass(),
                        "No Document with name '" + name + "' found on the system."))));
    }
}
