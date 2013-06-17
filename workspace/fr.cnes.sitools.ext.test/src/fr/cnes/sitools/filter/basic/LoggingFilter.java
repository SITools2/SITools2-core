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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Request;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.filter.model.FilterParameter;
import fr.cnes.sitools.dataset.filter.model.FilterParameterType;
import fr.cnes.sitools.dataset.model.Predicat;

/**
 * Example of a Filter for logging request and parameters
 * 
 * @author jp.boignard
 */
public class LoggingFilter extends AbstractFilter {

  /** Class logger */
  private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

  /**
   * Default constructor
   */
  public LoggingFilter() {
    super();
    this.setName("LoggingFilter");
    this.setDescription("An example of filter for logging request and input predicates.");

    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.1");

    //
    FilterParameter level = new FilterParameter("level", "level", FilterParameterType.PARAMETER_INTERN);

    level.setValue("SEVERE|WARNING|CONFIG|INFO|FINE|FINER|FINEST");
    level.setValueType("string");
    this.addParam(level);

  }

  @Override
  public List<Predicat> createPredicats(Request request, List<Predicat> predicats) throws Exception {
    // request.get...
    FilterParameter level = this.getInternParam("level");
    Level loggingLevel = Level.parse((String) level.getValue());

    String logString = "REQUEST: " + request.toString();
    if (predicats != null) {
      logString += predicats.toString();
    }

    LOGGER.log(loggingLevel, "REQUEST: {0}\nPREDICATS: {1}", new Object[]{request.toString(), predicats.toString()});

    return predicats;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.dataset.filter.business.AbstractFilter#getValidator()
   */
  @Override
  public HashMap<String, ParameterInfo> getRequestParamsDescription() {
    // TODO Auto-generated method stub
    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

    ParameterInfo paramInfo = new ParameterInfo("level", false, "xs:string", ParameterStyle.QUERY, "Using this method, you can specify the value of a filter paramater : " + this.getParametersMap().get("level").getValue());
    rpd.put("loggingFilter", paramInfo);
    
    return rpd;
  }
  
  @Override
  public Validator<AbstractFilter> getValidator() {
    return new Validator<AbstractFilter>() {

      @Override
      public Set<ConstraintViolation> validate(AbstractFilter item) {
        Map<String, FilterParameter> params = item.getParametersMap();
        HashSet<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        FilterParameter param = params.get("level");
        String value = param.getValue();
        if (!(value.equals("SEVERE") || value.equals("WARNING") || value.equals("CONFIG") || value.equals("INFO")
            || value.equals("FINE") || value.equals("FINER") || value.equals("FINEST"))) {

          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("Parameter 'level' must be one of 'SEVERE|WARNING|CONFIG|INFO|FINE|FINER|FINEST'");
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
