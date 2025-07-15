package com.biit.forms.rest.api;

import com.biit.form.result.pdf.exceptions.EmptyPdfBodyException;
import com.biit.form.result.pdf.exceptions.InvalidElementException;
import com.biit.forms.core.controllers.ReceivedFormController;
import com.biit.forms.core.converters.ReceivedFormConverter;
import com.biit.forms.core.converters.models.ReceivedFormConverterRequest;
import com.biit.forms.core.email.FormServerEmailService;
import com.biit.forms.core.exceptions.ReceivedFormNotFoundException;
import com.biit.forms.core.models.ReceivedFormDTO;
import com.biit.forms.core.providers.ReceivedFormProvider;
import com.biit.forms.logger.FormResultsLogger;
import com.biit.forms.persistence.entities.ReceivedForm;
import com.biit.forms.persistence.repositories.ReceivedFormRepository;
import com.biit.server.exceptions.BadRequestException;
import com.biit.server.rest.ElementServices;
import com.biit.usermanager.client.providers.UserManagerClient;
import com.biit.usermanager.dto.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/received")
public class ReceivedFormServices extends ElementServices<ReceivedForm, Long, ReceivedFormDTO, ReceivedFormRepository,
        ReceivedFormProvider, ReceivedFormConverterRequest, ReceivedFormConverter, ReceivedFormController> {

    private final FormServerEmailService formServerEmailService;

    private final UserManagerClient userManagerClient;

    public ReceivedFormServices(ReceivedFormController controller, FormServerEmailService formServerEmailService,
                                UserManagerClient userManagerClient) {
        super(controller);
        this.formServerEmailService = formServerEmailService;
        this.userManagerClient = userManagerClient;
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
                                       Authentication authentication, HttpServletResponse response, HttpServletRequest request) {

        final ReceivedFormDTO receivedForm = getController().get(receivedFormId);

        try {
            final ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                    .filename(receivedForm.getFormName() + ".pdf").build();
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
            return getController().convertToPdf(receivedForm, authentication.getName());
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
                                            Authentication authentication) {

        final ReceivedFormDTO receivedForm = getController().get(receivedFormId);

        try {
            final byte[] pdfContent = getController().convertToPdf(receivedForm, authentication.getName());
            formServerEmailService.sendPdfForm(receivedForm.getCreatedBy(), receivedForm.getForm(), pdfContent);
        } catch (Exception e) {
            FormResultsLogger.errorMessage(this.getClass(), e);
            throw new BadRequestException(this.getClass(), e.getMessage());
        }
    }


    @Operation(summary = "Search forms as PDF.", description = """
            Parameters:
            - form: the form name.
            - version: the form version.
            - createdBy: who has filled up the form. If no user is selected by default is the authenticated user.
            - createdByExternalReference: who has filled up the form. Using an external reference for a 3rd party application.
            - organization: which organization the form belongs to.
            """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/find/latest", produces = MediaType.APPLICATION_PDF_VALUE)
    public ReceivedFormDTO getLatest(
            @Parameter(name = "form", required = false) @RequestParam(value = "form") String form,
            @Parameter(name = "version", required = false) @RequestParam(value = "version", required = false) Integer version,
            @Parameter(name = "createdBy", required = false) @RequestParam(value = "createdBy", required = false) String createdBy,
            @Parameter(name = "createdByExternalReference", required = false) @RequestParam(value = "createdByExternalReference", required = false)
            String externalReference,
            @Parameter(name = "organization", required = false) @RequestParam(value = "organization", required = false) String organization,
            Authentication authentication, HttpServletRequest request, HttpServletResponse response) {

        if (createdBy == null && organization == null) {
            if (externalReference == null) {
                createdBy = authentication.getName();
            } else {
                final Optional<UserDTO> user = userManagerClient.findByExternalReference(externalReference);
                if (user.isPresent()) {
                    createdBy = user.get().getUsername();
                }
            }
        }
        canBeDoneByDifferentUsers(createdBy, authentication);

        return getController().findBy(form, version != null ? version : 1, createdBy, organization);
    }


    @Operation(summary = "Search forms as PDF.", description = """
            Parameters:
            - form: the form name.
            - version: the form version.
            - createdBy: who has filled up the form. If no user is selected by default is the authenticated user.
            - createdByExternalReference: who has filled up the form. Using an external reference for a 3rd party application.
            - organization: which organization the form belongs to.
            """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/find/latest/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] getLatestAsPdf(
            @Parameter(name = "form", required = false) @RequestParam(value = "form") String form,
            @Parameter(name = "version", required = false) @RequestParam(value = "version", required = false) Integer version,
            @Parameter(name = "createdBy", required = false) @RequestParam(value = "createdBy", required = false) String createdBy,
            @Parameter(name = "createdByExternalReference", required = false) @RequestParam(value = "createdByExternalReference", required = false)
            String externalReference,
            @Parameter(name = "organization", required = false) @RequestParam(value = "organization", required = false) String organization,
            Authentication authentication, HttpServletRequest request, HttpServletResponse response)
            throws InvalidElementException, JsonProcessingException, EmptyPdfBodyException {

        final ReceivedFormDTO receivedForm = getLatest(form, version, createdBy, externalReference, organization, authentication, request, response);

        if (receivedForm == null) {
            throw new ReceivedFormNotFoundException(this.getClass(), "No document found!");
        }

        final ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename((form != null ? form : "form") + ".pdf").build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

        return getController().convertToPdf(receivedForm, authentication.getName());
    }
}
