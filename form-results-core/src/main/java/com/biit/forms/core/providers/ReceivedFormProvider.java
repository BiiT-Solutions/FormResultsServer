package com.biit.forms.core.providers;

import com.biit.forms.persistence.entities.ReceivedForm;
import com.biit.forms.persistence.repositories.ReceivedFormRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ReceivedFormProvider extends ElementProvider<ReceivedForm, Long, ReceivedFormRepository> {

    @Autowired
    public ReceivedFormProvider(ReceivedFormRepository repository) {
        super(repository);
    }

    public Optional<ReceivedForm> findByName(String name) {
        return getRepository().findByFormName(name);
    }

    public Optional<ReceivedForm> findBy(String name, int version, String createdBy, String organization) {
        final List<ReceivedForm> forms = getRepository().findByFormNameAndFormVersionAndCreatedByAndOrganization(name, version, createdBy, organization);
        if (forms.isEmpty()) {
            return Optional.empty();
        }
        return forms.stream().max(Comparator.comparing(ReceivedForm::getCreatedAt));
    }

}
