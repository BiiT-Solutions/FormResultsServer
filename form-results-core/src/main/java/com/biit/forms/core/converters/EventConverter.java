package com.biit.forms.core.converters;

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
import com.biit.forms.persistence.entities.ReceivedForm;
import com.biit.kafka.events.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventConverter {

    @Value("${spring.application.name:#{null}}")
    private String applicationName;

    public ReceivedForm getReceivedForm(Event event, FormResult formResult) {
        final ReceivedForm receivedForm = new ReceivedForm();
        receivedForm.setForm(event.getPayload());
        receivedForm.setCreatedBy(event.getCreatedBy());
        receivedForm.setFormName(formResult.getName());
        receivedForm.setFormVersion(formResult.getVersion());
        receivedForm.setOrganization(formResult.getOrganization());
        return receivedForm;
    }
}
