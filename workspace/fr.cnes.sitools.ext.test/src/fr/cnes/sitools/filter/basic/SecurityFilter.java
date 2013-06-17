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
package fr.cnes.sitools.filter.basic;

import java.util.List;
import java.util.Set;

import org.restlet.Request;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.filter.model.FilterParameter;
import fr.cnes.sitools.dataset.filter.model.FilterParameterType;
import fr.cnes.sitools.dataset.model.Predicat;
import java.util.HashSet;
import java.util.Map;

/**
 * Class to watch SQL request sent and check parameters conformity
 * 
 * @author m.marseille
 * 
 */
public final class SecurityFilter extends AbstractFilter {

  /**
   * Constructor
   */
  public SecurityFilter() {
    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    setClassVersion("0.1");

    setName("SecurityFilter");
    setDescription("Security Filter to watch request and throw exceptions");

    FilterParameter regexp = new FilterParameter("regexp", "regular expression to check",
        FilterParameterType.PARAMETER_INTERN);
    this.addParam(regexp);

  }

  @Override
  public List<Predicat> createPredicats(Request request, List<Predicat> predicats) throws Exception {

    String regexp = this.getParametersMap().get("regexp").getValue();
    if (request.toString().matches(regexp)) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Failed to match regular expression " + regexp
          + " for the request : " + request.toString());
    }

    return predicats;
  }

  @Override
  public Validator<AbstractFilter> getValidator() {
    return new Validator<AbstractFilter>() {

      @Override
      public Set<ConstraintViolation> validate(AbstractFilter item) {
        Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        Map<String, FilterParameter> params = item.getParametersMap();
        FilterParameter param = params.get("regexp");
        String value = param.getValue();

        if (value.equals("")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("The parameter must be filled");
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
