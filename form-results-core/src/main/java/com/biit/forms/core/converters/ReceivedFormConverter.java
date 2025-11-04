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

import com.biit.server.controller.converters.ElementConverter;
import com.biit.forms.core.converters.models.ReceivedFormConverterRequest;
import com.biit.forms.core.models.ReceivedFormDTO;
import com.biit.forms.persistence.entities.ReceivedForm;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ReceivedFormConverter extends ElementConverter<ReceivedForm, ReceivedFormDTO, ReceivedFormConverterRequest> {


    @Override
    protected ReceivedFormDTO convertElement(ReceivedFormConverterRequest from) {
        final ReceivedFormDTO receivedFormDTO = new ReceivedFormDTO();
        BeanUtils.copyProperties(from.getEntity(), receivedFormDTO);
        return receivedFormDTO;
    }

    @Override
    public ReceivedForm reverse(ReceivedFormDTO to) {
        if (to == null) {
            return null;
        }
        final ReceivedForm receivedForm = new ReceivedForm();
        BeanUtils.copyProperties(to, receivedForm);
        return receivedForm;
    }
}
