package com.biit.forms.rest.api;


import com.biit.form.result.dto.FormResultDTO;
import com.biit.form.result.dto.converter.FormResultConverter;
import com.biit.form.result.pdf.exceptions.EmptyPdfBodyException;
import com.biit.form.result.pdf.exceptions.InvalidElementException;
import com.biit.forms.core.controllers.ReceivedFormController;
import com.biit.server.exceptions.InvalidRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pdf")
public class PdfFormServices {

    private final ReceivedFormController receivedFormController;
    private final FormResultConverter formResultConverter;

    public PdfFormServices(ReceivedFormController receivedFormController, FormResultConverter formResultConverter) {
        this.receivedFormController = receivedFormController;
        this.formResultConverter = formResultConverter;
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Generates a PDF from a FormResult.", description = """
                - Locale from pdf is obtained from the 'Accept-Language' header or the system field 'localization' in the form.
            """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "", produces = {org.springframework.http.MediaType.APPLICATION_PDF_VALUE, org.springframework.http.MediaType.APPLICATION_JSON_VALUE},
            consumes = MediaType.TEXT_PLAIN)
    @ResponseStatus(HttpStatus.OK)
    public byte[] executeDroolsEngine(@Parameter(description = "Form Result", required = true,
                                              content = @Content(schema = @Schema(implementation = FormResultDTO.class)))
                                      @RequestBody final FormResultDTO formResult,
                                      HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws InvalidElementException, EmptyPdfBodyException {
        if (formResult == null) {
            throw new InvalidRequestException(this.getClass(), "Invalid payload!");
        }

        final byte[] pdfForm = receivedFormController.convertToPdf(formResultConverter.reverse(formResult), authentication.getName(), request.getLocale());
        final ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(formResult.getName() + ".pdf").build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        return pdfForm;
    }
}
