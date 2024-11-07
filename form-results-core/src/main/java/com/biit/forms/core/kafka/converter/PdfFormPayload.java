package com.biit.forms.core.kafka.converter;

import com.biit.kafka.events.EventPayload;
import com.biit.server.persistence.entities.Element;

import java.io.Serial;

public class PdfFormPayload extends Element<Long> implements EventPayload {

    @Serial
    private static final long serialVersionUID = 1556415818325513987L;

    private Long id;

    private String formName;

    private int formVersion;

    private String organization;

    private byte[] pdfContent;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public int getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(int formVersion) {
        this.formVersion = formVersion;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public byte[] getPdfContent() {
        return pdfContent;
    }

    public void setPdfContent(byte[] pdfContent) {
        this.pdfContent = pdfContent;
    }
}
