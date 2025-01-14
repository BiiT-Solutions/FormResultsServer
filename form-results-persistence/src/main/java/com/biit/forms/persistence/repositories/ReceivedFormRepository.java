package com.biit.forms.persistence.repositories;

import com.biit.forms.persistence.entities.ReceivedForm;
import com.biit.server.persistence.repositories.ElementRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ReceivedFormRepository extends ElementRepository<ReceivedForm, Long> {

    Optional<ReceivedForm> findByFormName(String name);

    List<ReceivedForm> findByFormNameAndFormVersionAndCreatedByAndOrganization(String name, int version, String createdBy, String organization);

}
