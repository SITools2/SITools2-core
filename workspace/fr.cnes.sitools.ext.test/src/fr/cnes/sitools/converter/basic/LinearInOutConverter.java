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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
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
 * Example of converter
 * 
 * @author AKKA
 */
public class LinearInOutConverter extends AbstractConverter {

  /** Class logger */
  private static Logger logger = Engine.getLogger(LinearInOutConverter.class.getName());

  /**
   * Constructor.
   */
  public LinearInOutConverter() {
    //
    this.setName("LinearInOutConverter");
    this.setDescription("A converter applying a linear transformation on a specific column");

    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.3");
    //
    ConverterParameter a = new ConverterParameter("a", "a in x = a.x+b",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    ConverterParameter b = new ConverterParameter("b", "b in x = a.x+b",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    ConverterParameter precision = new ConverterParameter("precision", "result precision (#0.00)",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    ConverterParameter x = new ConverterParameter("x", "x in x = a.x+b",
        ConverterParameterType.CONVERTER_PARAMETER_INOUT);

    //
    a.setValue("1.0");
    a.setValueType("double");
    b.setValue("0.0");
    b.setValueType("double");
    precision.setValue("#0.00");
    precision.setValueType("string");
    //
    this.addParam(precision);
    this.addParam(a);
    this.addParam(b);
    this.addParam(x);

    logger.log(Level.INFO, "Converter :{0} version {1}", new Object[] { this.getName(), this.getClassVersion() });
  }

  @Override
  public final Record getConversionOf(final Record rec) throws Exception {
    Record out = rec;

    /*
     * Extracting internal parameters a and b
     */
    Double a = new Double(this.getInternParam("a").getValue());
    Double b = new Double(this.getInternParam("b").getValue());

    AttributeValue attrIntOut = this.getInOutParam("x", rec);
    logger.log(Level.FINEST, "x={0} a={1} b={2}", new Object[] { attrIntOut.getValue(), a, b });

    if (!isNull(attrIntOut)) {
      try {
        Double x = new Double(String.valueOf(attrIntOut.getValue()));

        x = a * x + b;

        attrIntOut.setValue(roundNumber(x));
      }
      catch (Exception e) {
        attrIntOut.setValue(Double.NaN);
      }
    }
    else {
      attrIntOut.setValue(null);
    }

    return out;
  }

  /**
   * Round a number
   * 
   * @param d
   *          the number
   * @return the rounded number
   */
  String roundNumber(double d) {
    NumberFormat formatter = new DecimalFormat(this.getInternParam("precision").getValue(),
        DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    return formatter.format(d);
  }

  @Override
  public Validator<AbstractConverter> getValidator() {
    // TODO Auto-generated method stub
    return new Validator<AbstractConverter>() {

      @Override
      public Set<ConstraintViolation> validate(AbstractConverter item) {
        Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        Map<String, ConverterParameter> params = item.getParametersMap();
        ConverterParameter param = params.get("a");
        String value = param.getValue();
        try {
          Double.valueOf(value);
        }
        catch (NumberFormatException ex) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage(ex.getMessage());
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setInvalidValue(value);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }

        param = params.get("b");
        value = param.getValue();
        try {
          Double.valueOf(value);
        }
        catch (NumberFormatException ex) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage(ex.getMessage());
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setInvalidValue(value);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }

        param = params.get("x");
        value = param.getAttachedColumn();
        if (value.equals("")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("You must choose an attribute of the dataset");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }

        param = params.get("precision");
        value = param.getValue();
        if (!value.startsWith("#0")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("Invalide precision");
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
