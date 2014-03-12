 /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.engine.Engine;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.converter.business.AbstractConverter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameterType;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;

/**
 * Example of a converter to investigate on possible Filter usage of a converter.
 * 
 * To be continued...
 * 
 * @author jp.boignard
 */
public class SimpleFilterConverter extends AbstractConverter {
  /** The logger */
  private static final Logger LOGGER = Engine.getLogger(SimpleFilterConverter.class.getName());

  /**
   * Constructor.
   */
  public SimpleFilterConverter() {
    this.setName("FilterThreshold");
    this.setDescription("Filter records according to a threshold");

    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.2");

    // Parameters for configuration

    ConverterParameter field = new ConverterParameter("field", "Attribute of the dataset where data is",
        ConverterParameterType.CONVERTER_PARAMETER_INOUT);
    ConverterParameter seuil = new ConverterParameter("threshold",
        "threshold: when field is superior to the threshold, then the field is not displayed",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    //
    seuil.setValue("2.0");
    seuil.setValueType("double");
    //

    this.addParam(field);
    this.addParam(seuil);
  }

  @Override
  public Record getConversionOf(Record rec) throws Exception {

    Double threshold = new Double(this.getInternParam("threshold").getValue());

    AttributeValue field = this.getInOutParam("field", rec);

    LOGGER.log(Level.FINEST, "value={0} threshold={1}", new Object[] {field.getValue(), threshold});
    Double valueField = new Double(String.valueOf(field.getValue()));

    if (valueField <= threshold) {
      return rec;
    }
    else {
      field.setValue("");
      return rec;
    }

  }

  @Override
  public Validator<AbstractConverter> getValidator() {
    // TODO Auto-generated method stub
    return new Validator<AbstractConverter>() {

      @Override
      public Set<ConstraintViolation> validate(AbstractConverter item) {
        Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        Map<String, ConverterParameter> params = item.getParametersMap();
        ConverterParameter param = params.get("field");
        String value = param.getAttachedColumn();
        if (value.equals("")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("An attribute of the dataset must be choosen");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        param = params.get("threshold");
        value = param.getValue();
        try {
          Double.valueOf(value);
        }
        catch (NumberFormatException ex) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("This parameter must be set");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setInvalidValue(value);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        return constraints;
      }
    };
  }
}
