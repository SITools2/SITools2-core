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
import java.text.NumberFormat;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.converter.business.AbstractConverter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameterType;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;

/**
 * Example de convertisseur realisant une fonction mathematique
 * 
 * @author AKKA
 */
public class ExponentialConverter extends AbstractConverter {

  /** TODO Utiliser le logger du context ? ... */
  private static Logger logger = Logger.getLogger(LinearConverter.class.getName());

  /**
   * Constructor.
   */
  public ExponentialConverter() {
    super();
    this.setName("ExponentialConverter");
    this.setDescription("A converter applying an exponentional transformation");

    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.3");
    // Math.pow(
    ConverterParameter a = new ConverterParameter("a", "a in x = a.exp(x+b)",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    ConverterParameter b = new ConverterParameter("b", "b in x = a.exp(x+b)",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    ConverterParameter precision = new ConverterParameter("precision", "result precision (#0.00)",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    ConverterParameter x = new ConverterParameter("x", "x in x = a.exp(x+b)",
        ConverterParameterType.CONVERTER_PARAMETER_INOUT);

    //
    a.setValue("1.0");
    a.setValueType("double");
    b.setValue("0.0");
    b.setValueType("double");
    precision.setValue("#0.00");
    precision.setValueType("string");
    //

    this.addParam(a);
    this.addParam(b);
    this.addParam(precision);    
    this.addParam(x);

    logger.log(Level.INFO, "Converter :" + this.getName() + " version " + this.getClassVersion());
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
    if (!isNull(attrIntOut)) {
      try {
        Double x = new Double(String.valueOf(attrIntOut.getValue()));

        x = a * Math.exp(x + b);

        attrIntOut.setValue(roundNumber(x));
      }
      catch (Exception e) {
        e.printStackTrace();
        attrIntOut.setValue(Double.NaN);
      }
    }

    return out;
  }

  /**
   * round the given number to the precision get from internal converter parameter
   * 
   * @param d
   *          the number to round
   * @return d rounded
   */
  private String roundNumber(double d) {
    NumberFormat formatter = new DecimalFormat(this.getInternParam("precision").getValue());
    return formatter.format(d);
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
