package com.biit.forms.persistence.logger;

/*-
 * #%L
 * Form Results Server (Persistence)
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

import com.biit.forms.logger.BasicLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLogging extends BasicLogger {

    private static Logger logger = LoggerFactory.getLogger(TestLogging.class);

    public static void info(String className, String message) {
        info(logger, className, message);
    }

    public static void info(String message) {
        info(logger, message);
    }

    public static void debug(String className, String message) {
        debug(logger, className, message);
    }

    public static void debug(String message) {
        debug(logger, message);
    }

    public static void severe(String className, String message) {
        severe(logger, className, message);
    }

    public static void errorMessage(String className, Throwable throwable) {
        errorMessageNotification(logger, className, getStackTrace(throwable));
    }

    public static void errorMessage(Class<?> clazz, Throwable throwable) {
        errorMessageNotification(logger, clazz.getName(), getStackTrace(throwable));
    }

    public static void errorMessage(String className, String error) {
        errorMessageNotification(logger, className, error);
    }

    public static void errorMessage(String error) {
        errorMessageNotification(logger, error);
    }

    public static void errorMessage(Object object, Throwable throwable) {
        errorMessageNotification(logger, object.getClass().getName(), getStackTrace(throwable));
    }
}
