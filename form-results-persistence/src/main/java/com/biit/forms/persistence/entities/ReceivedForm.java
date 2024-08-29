package com.biit.forms.persistence.entities;

import com.biit.database.encryption.StringCryptoConverter;
import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "received_forms",
        indexes = {
                @Index(name = "ind_name", columnList = "form_name"),
                @Index(name = "ind_version", columnList = "form_version"),
                @Index(name = "ind_organization", columnList = "organization")
        })
public class ReceivedForm extends Element<Long> {
    public static final int MAX_JSON_LENGTH = 5242880;  //5MB

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "form_name")
    private String formName;

    @Column(name = "form_version")
    private int formVersion;

    @Column(name = "organization")
    private String organization;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "form_content", length = MAX_JSON_LENGTH, nullable = false)
    @Convert(converter = StringCryptoConverter.class)
    private String form;

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    @Override
    public String toString() {
        return "ReceivedForm{"
                + "formName='" + formName + '\''
                + ", formVersion=" + formVersion
                + '}';
    }
}
