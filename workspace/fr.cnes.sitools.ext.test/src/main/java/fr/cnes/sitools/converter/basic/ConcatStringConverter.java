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

import java.util.Set;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.converter.business.AbstractConverter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameterType;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;

/**
 * Converter that concatenates 2 string get from 2 column into another column
 * 
 * @author m.gond
 */
public class ConcatStringConverter extends AbstractConverter {
  /**
   * Default Constructor
   */
  public ConcatStringConverter() {
    this.setName("Concat String");
    this.setDescription("Concat 2 columns and a Parameter");

    ConverterParameter cp1 = new ConverterParameter("col1", "column 1", ConverterParameterType.CONVERTER_PARAMETER_IN);
    ConverterParameter cp2 = new ConverterParameter("col2", "column 2", ConverterParameterType.CONVERTER_PARAMETER_IN);
    ConverterParameter cp3 = new ConverterParameter("colOut", "column out",
        ConverterParameterType.CONVERTER_PARAMETER_OUT);
    ConverterParameter cp4 = new ConverterParameter("Text", "Free text added in the middle",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);

    this.addParam(cp1);
    this.addParam(cp2);
    this.addParam(cp3);
    this.addParam(cp4);
    
    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");

  }

  @Override
  public Record getConversionOf(Record rec) throws Exception {

    String text = this.getInternParam("Text").getValue();

    AttributeValue attr1 = this.getInParam("col1", rec);
    AttributeValue attr2 = this.getInParam("col2", rec);
    AttributeValue attrOut = this.getOutParam("colOut", rec);

    attrOut.setValue((String) attr1.getValue() + text + (String) attr2.getValue());

    return rec;

  }

  @Override
  public Validator<AbstractConverter> getValidator() {
    // TODO Auto-generated method stub
    return new Validator<AbstractConverter>() {
      @Override
      public Set<ConstraintViolation> validate(AbstractConverter item) {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }

}
