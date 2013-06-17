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
package fr.cnes.sitools.converter.tests;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.restlet.Context;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.converter.business.AbstractConverter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameterType;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;

/**
 * Converter used for Test purpose only
 * 
 * @author m.gond
 * 
 */
public class ConverterValidatorTest extends AbstractConverter {
  /**
   * Constructor
   */
  public ConverterValidatorTest() {
    super();
    commonConstructor();
  }

  /**
   * Constructor
   * 
   * @param ctx
   *          the Context used to instantiate this Converter, contains the dataset
   */
  public ConverterValidatorTest(Context ctx) {
    super();
    commonConstructor();
    DataSet ds = (DataSet) ctx.getAttributes().get("DATASET");
    if (ds != null) {
      List<Column> cm = ds.getColumnModel();
      ConverterParameter param;
      for (Iterator<Column> iterator = cm.iterator(); iterator.hasNext();) {
        Column column = iterator.next();
        param = new ConverterParameter(column.getColumnAlias(), column.getColumnAlias(),
            ConverterParameterType.CONVERTER_PARAMETER_INTERN);
        this.addParam(param);
      }
    }
  }

  /**
   * Common part of the constructor for both constructors
   */
  private void commonConstructor() {
    ConverterParameter param1 = new ConverterParameter("1", "1", ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    ConverterParameter param2 = new ConverterParameter("2", "2", ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    this.addParam(param1);
    this.addParam(param2);

    ConverterParameter paramColIn = new ConverterParameter("colIn", "colIn",
        ConverterParameterType.CONVERTER_PARAMETER_IN);
    ConverterParameter paramColOut = new ConverterParameter("colOut", "colOut",
        ConverterParameterType.CONVERTER_PARAMETER_OUT);
    ConverterParameter paramColInOut = new ConverterParameter("colInOut", "colInOut",
        ConverterParameterType.CONVERTER_PARAMETER_INOUT);

    this.addParam(paramColIn);
    this.addParam(paramColOut);
    this.addParam(paramColInOut);

    this.setName("TestConverter");
    this.setDescription("FOR TEST PURPOSE ONLY, DON'T USE IT, IT DOESN'T DO ANYTHING");

    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("1.0");
  }

  @Override
  public Record getConversionOf(Record rec) throws Exception {
    ConverterParameter param1 = getInternParam("1");
    AttributeValue paramInAttr = getInParam("colIn", rec);
    AttributeValue paramOutAttr = getOutParam("colOut", rec);
    AttributeValue paramInOutAttr = getInOutParam("colInOut", rec);
    return rec;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.dataset.converter.business.AbstractConverter#getValidator()
   */
  @Override
  public Validator<AbstractConverter> getValidator() {
    // Create a new Instance of Validator on an ConverterModel object
    return new Validator<AbstractConverter>() {
      /**
       * //only for tests validation, test that parameter 1 value is 1 and parameter 2 value is 2
       * 
       * @param item
       *          the AbstractConverter to validate
       * @return a set of constraintViolation
       */
      @Override
      public Set<ConstraintViolation> validate(AbstractConverter item) {
        Map<String, ConverterParameter> params = item.getParametersMap();
        HashSet<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        ConverterParameter param = params.get("1");
        if (!param.getValue().equals("param1_value")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("Param 1 value must be param1_value");
          constraint.setInvalidValue(param.getValue());
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        param = params.get("2");
        if (!param.getValue().equals("param2_value")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("Param 2 value must be param2_value");
          constraint.setInvalidValue(param.getValue());
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }

        return constraints;
      }
    };
  }

}
