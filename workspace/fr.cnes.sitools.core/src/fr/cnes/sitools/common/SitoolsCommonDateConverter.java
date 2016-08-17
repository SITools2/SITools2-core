    /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 * This file is part of SITools2.
 *
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.cnes.sitools.common;

import java.text.ParseException;
import java.util.Date;

import com.thoughtworks.xstream.converters.basic.DateConverter;

import fr.cnes.sitools.util.DateUtils;

/**
 * Date converte for tests
 * See also SitoolsCommonDateConverter
 * @author m.marseille
 */
public final class SitoolsCommonDateConverter extends DateConverter {
  
  
  @Override
  public Object fromString(String arg0) {
    Date dateFormat = null;
    try {
      dateFormat = DateUtils.parse(arg0, DateUtils.SITOOLS_DATE_FORMAT);
    }
    catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return dateFormat;
  }
  
  @Override
  public String toString(Object obj) {
    if ((obj != null) && (obj instanceof Date)) {
      return DateUtils.format((Date) obj, DateUtils.SITOOLS_DATE_FORMAT);
    }
    return super.toString(obj);
  }

}
