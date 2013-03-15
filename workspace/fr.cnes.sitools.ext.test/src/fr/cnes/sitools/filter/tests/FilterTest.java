/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.filter.tests;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.restlet.Context;
import org.restlet.Request;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.filter.model.FilterParameter;
import fr.cnes.sitools.dataset.filter.model.FilterParameterType;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Predicat;

/**
 * FilterTest
 * 
 * @author m.gond
 * 
 */
public class FilterTest extends AbstractFilter {
  /**
   * Constructor
   */
  public FilterTest() {
    super();
    commonConstructor();
  }

  /**
   * Constructor
   * 
   * @param ctx
   *          The context
   */
  public FilterTest(Context ctx) {
    super();
    commonConstructor();
    DataSet ds = (DataSet) ctx.getAttributes().get("DATASET");
    if (ds != null) {
      List<Column> cm = ds.getColumnModel();
      FilterParameter param;
      for (Iterator<Column> iterator = cm.iterator(); iterator.hasNext();) {
        Column column = iterator.next();
        param = new FilterParameter(column.getColumnAlias(), column.getColumnAlias(),
            FilterParameterType.PARAMETER_INTERN);
        this.addParam(param);
      }
    }

  }

  /**
   * Common part of the constructor for both constructors
   */
  private void commonConstructor() {
    FilterParameter param1 = new FilterParameter("1", "1", FilterParameterType.PARAMETER_INTERN);
    FilterParameter param2 = new FilterParameter("2", "2", FilterParameterType.PARAMETER_INTERN);
    this.addParam(param1);
    this.addParam(param2);

    this.setName("TestFilter");
    this.setDescription("FOR TEST PURPOSE ONLY, DON'T USE IT, IT DOESN'T DO ANYTHING");

    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("1.0");
  }

  @Override
  public List<Predicat> createPredicats(Request request, List<Predicat> predicats) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.dataset.filter.business.AbstractFilter#getValidator()
   */
  @Override
  public Validator<AbstractFilter> getValidator() {
    // Create a new Instance of Validator on an FilterModel object
    return new Validator<AbstractFilter>() {
      /**
       * //only for tests validation, test that parameter 1 value is 1 and
       * parameter 2 value is 2
       * 
       * @param item
       *          the AbstractFilter to validate
       * @return a set of constraintViolation
       */
      @Override
      public Set<ConstraintViolation> validate(AbstractFilter item) {
        Map<String, FilterParameter> params = item.getParametersMap();
        HashSet<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        FilterParameter param = params.get("1");
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
