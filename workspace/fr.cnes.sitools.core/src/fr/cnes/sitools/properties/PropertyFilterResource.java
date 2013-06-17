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
package fr.cnes.sitools.properties;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.AbstractDataSetResource;
import fr.cnes.sitools.server.Consts;

/**
 * Resource to check that the given properties are compatible with the current dataset properties
 * 
 * 
 * @author m.gond
 */
public class PropertyFilterResource extends AbstractDataSetResource {

  @Override
  public void sitoolsDescribe() {
    setNegotiated(false);
    setName("PropertyFilterResource");
    setDescription("Resource to check that the given properties are compatible with the current dataset properties");

  }

  /**
   * * Check that the given properties are compatible with the current dataset properties A dataset is compatible if it
   * has the property and all the given values for a property is included in the values of the property
   * 
   * @param variant
   *          the variant asked
   * @return a Representation of a response with success = true if the dataset is compatible with the properties, false
   *         otherwise
   */
  @Get
  public Representation checkProperties(Variant variant) {
    SitoolsSettings settings = (SitoolsSettings) getContext().getAttributes().get(ContextAttributes.SETTINGS);
    try {
      Class<?> classPropertyFilters = Class.forName(settings.getString(Consts.PROPERTY_FILTERS_CLASS));
      SitoolsPropertyFilterHandler filterHandler = (SitoolsPropertyFilterHandler) classPropertyFilters.newInstance();
      boolean result = filterHandler.match(getRequest(), getContext());
      Response response = new Response();
      response.setSuccess(result);
      return getRepresentation(response, variant);

    }
    catch (ClassNotFoundException e) {
      throw new ResourceException(e);
    }
    catch (InstantiationException e) {
      throw new ResourceException(e);
    }
    catch (IllegalAccessException e) {
      throw new ResourceException(e);
    }

  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to Check that the given properties are compatible with the current dataset properties ");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  // /**
  // * Check that the given properties are compatible with the current dataset properties
  // *
  // * A dataset is compatible if it has the property and all the given values for a property is included in the values
  // of
  // * the property
  // *
  // *
  // *
  // *
  // * @param variant
  // * the variant asked
  // * @return a Representation of a response with success = true if the dataset is compatible with the properties,
  // false
  // * otherwise
  // */
  // @Get
  // public Representation checkProperties(Variant variant) {
  // boolean isCompatible = false;
  //
  // DataSetApplication dsApplication = null;
  // DataSet ds = null;
  //
  // Form params = getRequest().getResourceRef().getQueryAsForm();
  // int i = 0;
  // // Build predicat for filters param
  // String index = TEMPLATE_PARAM.replace("#", Integer.toString(i++));
  // String formParam = params.getFirstValue(index);
  // if (formParam != null) {
  //
  // String[] parameters = formParam.split("\\|");
  // if (dsApplication == null) {
  // dsApplication = (DataSetApplication) getApplication();
  // ds = dsApplication.getDataSet();
  // }
  //
  // String propertyName = parameters[PROPERTY];
  // SitoolsProperty property = ds.getProperty(propertyName);
  // if (property == null) {
  // isCompatible = false;
  // }
  // else {
  // String[] values = Arrays.copyOfRange(parameters, VALUES, parameters.length);
  // String[] valuesProperty = property.getValue().split("\\|");
  //
  // for (int k = 0; k < values.length && !isCompatible; k++) {
  // for (int j = 0; j < valuesProperty.length && !isCompatible; j++) {
  // if (valuesProperty[j].equals(values[k])) {
  // isCompatible = true;
  // }
  // }
  // }
  // }
  //
  // }
  // else {
  // isCompatible = true;
  // }
  //
  // Response response = new Response();
  // response.setSuccess(isCompatible);
  //
  // return getRepresentation(response, variant);
  //
  // }
}
