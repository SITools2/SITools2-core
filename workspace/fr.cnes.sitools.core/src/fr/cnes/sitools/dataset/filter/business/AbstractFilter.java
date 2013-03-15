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
package fr.cnes.sitools.dataset.filter.business;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.measure.unit.ConversionException;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitConverter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.ext.wadl.ParameterInfo;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.business.AbstractExtension;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.validator.Validable;
import fr.cnes.sitools.dataset.filter.model.FilterParameter;
import fr.cnes.sitools.dataset.filter.model.FilterParameterType;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.units.dimension.model.SitoolsDimension;
import fr.cnes.sitools.units.dimension.model.SitoolsUnit;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Abstract class from which all filters must inherits.
 * 
 * @author m.marseille (AKKA Technologies)
 */
public abstract class AbstractFilter extends AbstractExtension<FilterParameter> implements Validable {

  /** Default filter */
  private Boolean defaultFilter;

  /** Request parameter description for WADL description */
  private HashMap<String, ParameterInfo> requestParamsDescription;

  /**
   * Default constructor
   */
  public AbstractFilter() {
    super();
  }

  /**
   * Constructor with DataSet parameter
   * 
   * @param ctx
   *          Context Object, contains the dataset as a property "DATASET"
   */
  public AbstractFilter(Context ctx) {

    super();
  }

  /**
   * Gets whether or not a Filter is a added by default to a Dataset when a new Dataset is created
   * 
   * @return the defaultFilter value
   */
  public final Boolean getDefaultFilter() {
    return defaultFilter;
  }

  /**
   * Gets the requestParamsDescription value
   * 
   * @return the requestParamsDescription
   */
  public HashMap<String, ParameterInfo> getRequestParamsDescription() {
    return requestParamsDescription;
  }

  /**
   * Sets the value of requestParamsDescription
   * 
   * @param requestParamsDescription
   *          the requestParamsDescription to set
   */
  public final void setRequestParamsDescription(HashMap<String, ParameterInfo> requestParamsDescription) {
    this.requestParamsDescription = requestParamsDescription;
  }

  /**
   * The default filter value is used to tell whether or not a Filter is a added by default to a Dataset when a new
   * Dataset is created
   * 
   * @param defaultFilter
   *          true to set the filter to default, false otherwise
   */
  public final void setDefaultFilter(Boolean defaultFilter) {
    this.defaultFilter = defaultFilter;
  }

  /**
   * Method to process conversion, Must be overridden
   * 
   * @param request
   *          the request to be processed
   * @param predicats
   *          the ArrayList<Predicats> to be processed
   * @return the ArrayList<Predicats> processed
   * @throws Exception
   * 
   *           TODO EVOLUTION - Possibilité de passer des paramètres d'un filtre à l'autre autrement que par les
   *           predicats Voir si besoin.
   * 
   */
  public abstract List<Predicat> createPredicats(final Request request, List<Predicat> predicats) throws Exception;

  /**
   * Add a parameter to the list of parameter The key to this parameter is its name
   * 
   * @param param
   *          the Record
   */
  public final void addParam(final FilterParameter param) {
    Map<String, FilterParameter> parameters = this.getParametersMap();
    parameters.put(param.getName(), param);
  }

  /**
   * Gets the <code>FilterParameter</code> of type PARAMETER_INTERN corresponding to the following <code>name</code>
   * 
   * @param name
   *          The name of the <code>FilterParameter</code>
   * @return a <code>FilterParameter</code> of type PARAMETER_INTERN corresponding to the following <code>name</code> or
   *         null if the <code>FilterParameter</code> is not found
   */
  public final FilterParameter getInternParam(final String name) {
    FilterParameter param = this.getParametersMap().get(name);
    FilterParameter paramOut = null;
    if (param.getParameterType() == FilterParameterType.PARAMETER_INTERN) {
      paramOut = param;
    }
    return paramOut;
  }

  /**
   * Convert valueFrom from unitFromName to unitToName
   * 
   * @param unitFromName
   *          the name of the unit from
   * @param unitToName
   *          the name of the unit to
   * @param valueFrom
   *          the value to convert
   * @param dimension
   *          the dimension
   * @throws SitoolsException
   *           if something wrong occurs
   * @return the value converted
   */
  public final Number convert(String unitFromName, String unitToName, String valueFrom, String dimension)
    throws SitoolsException {
    Number valueTo = null;
    BigDecimal valueFromBig = new BigDecimal(valueFrom);
    // perform conversion
    if (unitFromName != null && !unitFromName.equals("") && unitToName != null && !unitToName.equals("")) {
      SitoolsUnit unitFrom = new SitoolsUnit(unitFromName);
      SitoolsUnit unitTo = new SitoolsUnit(unitToName);
      if (unitFrom != null && unitTo != null) {
        UnitConverter converter;
        try {
          Unit<?> unit = unitFrom.getUnit();
          if (unit == null) {
            throw new SitoolsException("Unit \"" + unitFrom.getUnitName() + "\" unknown");
          }
          converter = unit.getConverterToAny(unitTo.getUnit());
          valueTo = converter.convert(valueFromBig);
        }
        catch (ConversionException e) {
          if (dimension != null && !dimension.equals("")) {
            SitoolsDimension dimensionSent = getDimension(dimension);
            if (dimensionSent == null) {
              throw new SitoolsException("Dimension \"" + dimension + "\" unknown");
            }
            converter = dimensionSent.getUnitConverter(unitFromName, unitToName);
            if (converter == null) {
              throw new SitoolsException("No converter found between \"" + unitFromName + "\" and \"" + unitToName
                  + "\"");
            }
            valueTo = converter.convert(valueFromBig);
          }
        }
      }
    }
    return valueTo;
  }

  /**
   * Get an instance of dimension according to the given dimension name
   * 
   * @param dimension
   *          the name of the dimension
   * @return the instance of dimension
   */
  private SitoolsDimension getDimension(String dimension) {
    SitoolsSettings settings = (SitoolsSettings) getContext().getAttributes().get(ContextAttributes.SETTINGS);

    return RIAPUtils.getObject(settings.getString(Consts.APP_DIMENSIONS_ADMIN_URL) + "/dimension/" + dimension,
        getContext());
  }

  /**
   * Get a dictionary from its name
   * 
   * @param dicoName
   *          the name of the dictionary
   * @return the dictionary with the corresponding dicoName name
   */
  public Dictionary getDictionaryFromName(String dicoName) {
    SitoolsSettings settings = (SitoolsSettings) getContext().getAttributes().get(ContextAttributes.SETTINGS);
    return RIAPUtils.getObjectFromName(settings.getString(Consts.APP_DICTIONARIES_URL), dicoName, getContext());
  }

}
