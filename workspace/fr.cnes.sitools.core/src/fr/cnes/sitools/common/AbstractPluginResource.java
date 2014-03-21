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
package fr.cnes.sitools.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.model.ExtensionParameter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validable;
import fr.cnes.sitools.common.validator.Validator;

/**
 * Abstract class for plugin adminstration ressources
 * 
 * 
 * @author m.gond
 */
public abstract class AbstractPluginResource extends SitoolsResource {

  /**
   * Configure the XStream
   * 
   * @param xstream
   *          the XStream to treat
   * @param response
   *          the response used
   */
  public void configure(XStream xstream, Response response) {
    super.configure(xstream, response);
    xstream.alias("constraintViolation", ConstraintViolation.class);
  }

  /**
   * Transform a {@link List} of {@link ExtensionParameter} into a {@link Map} of String, {@link ExtensionParameter}
   * 
   * @param <E>
   *          The extensionParameter used
   * @param list
   *          the {@link List} of {@link ExtensionParameter}
   * @return a {@link Map} of String, {@link ExtensionParameter}
   */
  public <E extends ExtensionParameter> Map<String, E> fromListToMap(List<E> list) {
    if (list == null) {
      return null;
    }
    Map<String, E> map = new HashMap<String, E>();
    E extParam;
    for (Iterator<E> itParam = list.iterator(); itParam.hasNext();) {
      extParam = itParam.next();
      map.put(extParam.getName(), extParam);
    }
    return map;
  }

  /**
   * Check the validaty of the given T object
   * 
   * @param input
   *          the SvaModel to validate
   * @param <T>
   *          The type of Validable Object to check
   * @return a set of ConstraintViolation if the validation fail, null otherwise
   */
  @SuppressWarnings("unchecked")
  public <T extends Validable> Set<ConstraintViolation> checkValidity(T input) {
    Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
    if (input.getValidator() == null) {
      return null;
    }
    Validator<T> validator = (Validator<T>) input.getValidator();
    if (validator == null) {
      return null;
    }
    validator.setContext(getContext());
    constraints = validator.validate(input);

    boolean suppressWarnings = true;
    String suppressWarningsStr = getSitoolsSetting("Starter.plugins.suppressWarning");
    if (suppressWarningsStr != null && !"".equals(suppressWarningsStr)) {
      suppressWarnings = new Boolean(suppressWarningsStr);
    }
    if (constraints != null && !constraints.isEmpty()) {
      boolean validationFailed;
      if (!suppressWarnings) {
        validationFailed = true;
      }
      else {
        validationFailed = false;
        // check if there are some critical errors
        for (Iterator<ConstraintViolation> iterator = constraints.iterator(); iterator.hasNext() && !validationFailed;) {
          ConstraintViolation constV = iterator.next();
          if (ConstraintViolationLevel.CRITICAL.equals(constV.getLevel())) {
            validationFailed = true;
          }
        }
      }
      if (validationFailed) {
        return constraints;
      }
    }
    return null;
  }

}
