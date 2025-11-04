package com.biit.forms.core.providers;

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
