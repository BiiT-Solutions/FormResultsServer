package com.biit.forms.core.providers;

import com.biit.server.providers.ElementProvider;
import com.biit.forms.persistence.entities.ReceivedForm;
import com.biit.forms.persistence.repositories.ReceivedFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReceivedFormProvider extends ElementProvider<ReceivedForm, Long, ReceivedFormRepository> {

    @Autowired
    public ReceivedFormProvider(ReceivedFormRepository repository) {
        super(repository);
    }

    public Optional<ReceivedForm> findByName(String name) {
        return getRepository().findByName(name);
    }
}
