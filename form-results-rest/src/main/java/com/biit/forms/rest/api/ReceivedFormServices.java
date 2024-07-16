package com.biit.forms.rest.api;

import com.biit.forms.core.controllers.ReceivedFormController;
import com.biit.forms.core.converters.ReceivedFormConverter;
import com.biit.forms.core.converters.models.ReceivedFormConverterRequest;
import com.biit.forms.core.email.FormServerEmailService;
import com.biit.forms.core.models.ReceivedFormDTO;
import com.biit.forms.core.providers.ReceivedFormProvider;
import com.biit.forms.logger.FormResultsLogger;
import com.biit.forms.persistence.entities.ReceivedForm;
import com.biit.forms.persistence.repositories.ReceivedFormRepository;
import com.biit.server.exceptions.BadRequestException;
import com.biit.server.rest.ElementServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/received")
public class ReceivedFormServices extends ElementServices<ReceivedForm, Long, ReceivedFormDTO, ReceivedFormRepository,
        ReceivedFormProvider, ReceivedFormConverterRequest, ReceivedFormConverter, ReceivedFormController> {

    private final FormServerEmailService formServerEmailService;

    public ReceivedFormServices(ReceivedFormController controller, FormServerEmailService formServerEmailService) {
        super(controller);
        this.formServerEmailService = formServerEmailService;
    }


    @Operation(hidden = true)
    @Override
    public ReceivedFormDTO add(@RequestBody ReceivedFormDTO dto, Authentication authentication, HttpServletRequest request) {
        throw new UnsupportedOperationException("Method not valid!");
    }


    @Operation(hidden = true)
    @Override
    public List<ReceivedFormDTO> add(@RequestBody Collection<ReceivedFormDTO> dtos, Authentication authentication, HttpServletRequest request) {
        throw new UnsupportedOperationException("Method not valid!");
    }


    @Operation(hidden = true)
    @Override
    public ReceivedFormDTO update(@RequestBody ReceivedFormDTO dto, Authentication authentication, HttpServletRequest request) {
        throw new UnsupportedOperationException("Method not valid!");
    }


    @Operation(hidden = true)
    @Override
    public void delete(@RequestBody ReceivedFormDTO dto, Authentication authentication, HttpServletRequest request) {
        throw new UnsupportedOperationException("Method not valid!");
    }


    @Operation(hidden = true)
    @Override
    public void delete(Long id, Authentication authentication, HttpServletRequest request) {
        throw new UnsupportedOperationException("Method not valid!");
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a pdf from a result form.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "{receivedFormId}/pdf", produces = MediaType.APPLICATION_JSON_VALUE)
    public byte[] getReceivedFormAsPdf(@PathVariable(name = "receivedFormId") Long receivedFormId,
                                       HttpServletResponse response, HttpServletRequest request) {

        final ReceivedFormDTO receivedForm = getController().get(receivedFormId);

        try {
            final ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                    .filename(receivedForm.getFormName() + ".pdf").build();
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
            return getController().convertToPdf(receivedForm);
        } catch (Exception e) {
            FormResultsLogger.errorMessage(this.getClass(), e);
            throw new BadRequestException(this.getClass(), e.getMessage());
        }
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Sends a form by mail.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = "{receivedFormId}/pdf/mail", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendReceivedFormAsPdfByMail(@PathVariable(name = "receivedFormId") Long receivedFormId,
                                            HttpServletResponse response, HttpServletRequest request) {

        final ReceivedFormDTO receivedForm = getController().get(receivedFormId);

        try {
            final byte[] pdfContent = getController().convertToPdf(receivedForm);
            formServerEmailService.sendPdfForm(receivedForm.getCreatedBy(), receivedForm.getForm(), pdfContent);
        } catch (Exception e) {
            FormResultsLogger.errorMessage(this.getClass(), e);
            throw new BadRequestException(this.getClass(), e.getMessage());
        }
    }
}
