 /*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.converter.basic;

import java.util.Date;

import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.converter.business.AbstractConverter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameterType;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.util.DateUtils;

/**
 * Format date converter
 * 
 * 
 * @author m.gond
 */
public class DateConverter extends AbstractConverter {
  /**
   * Create new DateConverter
   */
  public DateConverter() {
    //
    this.setName("DateConverter");
    this.setDescription("A date format converter. TO BE USED ONLY WHEN THE RESULT IS NOT TO BE DISPLAYED IN THE SITOOLS CLIENT");

    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.5");

    ConverterParameter templateOut = new ConverterParameter("template_out", "The output date template format",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    //
    templateOut.setValue("yyyy-MM-dd'T'HH:mm:ss");
    templateOut.setValueType("string");

    //
    ConverterParameter col = new ConverterParameter("column", "The column to convert",
        ConverterParameterType.CONVERTER_PARAMETER_INOUT);

    this.addParam(templateOut);
    this.addParam(col);

  }

  @Override
  public Validator<?> getValidator() {
    return null;
  }

  @Override
  public Record getConversionOf(Record rec) throws Exception {

    AttributeValue value = this.getInOutParam("column", rec);
    if (!isNull(value)) {

      ConverterParameter templateOut = this.getInternParam("template_out");
      try {
        // parse the date
        Date date = DateUtils.parse((String) value.getValue(), DateUtils.SITOOLS_DATE_FORMAT);
        value.setValue(DateUtils.format(date, templateOut.getValue()));
      }
      catch (Exception e) {
        value.setValue(e.getMessage());
      }
    }

    return rec;
  }
}
