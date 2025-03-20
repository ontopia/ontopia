/*-
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2025 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.utils;

import java.time.Duration;
import org.apache.commons.beanutils.ConvertUtilsBean2;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.AbstractConverter;

public final class DurationConverter extends AbstractConverter {
  private static final DurationConverter INSTANCE = new DurationConverter();

  @Override
  protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
    if (Duration.class.equals(type)) {
      return type.cast(Duration.parse(String.valueOf(value)));
    }
    throw conversionException(type, value);
  }

  @Override
  protected Class<Duration> getDefaultType() {
    return Duration.class;
  }

  public static class DurationAwareConvertUtilsBean extends ConvertUtilsBean2 {

    @Override
    public Converter lookup(Class<?> pClazz) {
      Converter converter = super.lookup(pClazz);

      if (converter == null && pClazz.isAssignableFrom(Duration.class)) {
        return INSTANCE;
      } else {
        return converter;
      }
    }
  }
}
