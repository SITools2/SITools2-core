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
package fr.cnes.sitools.converter.tests;

import java.util.Set;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.converter.business.AbstractConverter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameterType;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
/**
 * Converter used for tests
 * 
 *
 * @author Akka Technologies
 */
public class ConverterTest extends AbstractConverter {

  /**
   * Constructor.
   */
  public ConverterTest() {
    //
    this.setName("ConverterTest");
    this.setDescription("A converter test");

    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.2");
    //
    ConverterParameter a = new ConverterParameter("a", "a in y = a.x/b",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    ConverterParameter b = new ConverterParameter("b", "b in y = a.x/b",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    ConverterParameter x = new ConverterParameter("x", "x in y = a.x/b", ConverterParameterType.CONVERTER_PARAMETER_IN);
    ConverterParameter y = new ConverterParameter("y", "y in y = a.x/b", ConverterParameterType.CONVERTER_PARAMETER_OUT);
    //
    a.setValue("1.0");
    a.setValueType("double");
    b.setValue("1.0");
    b.setValueType("double");
    //

    this.addParam(a);
    this.addParam(b);
    this.addParam(y);
    this.addParam(x);

  }

  @Override
  public Record getConversionOf(Record rec) throws Exception {

    Record out = rec;

    /*
     * Extracting internal parameters a and b
     */
    Double a = new Double(this.getInternParam("a").getValue());
    Double b = new Double(this.getInternParam("b").getValue());

    AttributeValue attrOut = this.getOutParam("y", rec);

    if (this.getInParam("x", rec) != null) {
      Double x = new Double((String) this.getInParam("x", rec).getValue());
      Double y = a * x / b;
      attrOut.setValue(y.toString());
    }
    else {
      attrOut.setValue("");
    }

    return out;

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
