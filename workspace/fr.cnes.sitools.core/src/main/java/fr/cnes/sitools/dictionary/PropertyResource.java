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
package fr.cnes.sitools.dictionary;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dictionary.model.ConceptTemplate;
import fr.cnes.sitools.util.Property;

/**
 * Notion. parent object is fully returned with its table of items.
 * No pagination service, to be managed on client side.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class PropertyResource extends AbstractConceptTemplateResource {

  @Override
  public void sitoolsDescribe() {
    setName("PropertyResource");
    setDescription("Resource for managing properties defining a template of concept");
  }

  /**
   * Gets the required Property representation
   * 
   * @param variant
   *          client preference for Response media type
   * @return Representation
   */
  @Get
  public Representation getRepresentation(Variant variant) {
    try {
      ConceptTemplate template = getStore().retrieve(getConceptTemplateId());
      Property propertyFound = null;
      
      Response response = null;
      if (template == null) {
        response = new Response(false, "TEMPLATE_NOT_FOUND");
      }
      else {
        List<Property> properties = template.getProperties();
        for (Iterator<Property> iterator = properties.iterator(); iterator.hasNext();) {
          Property property = iterator.next();
          if (property.getName().equals(getPropertyId())) {
            propertyFound = property;
            break;
          }
        }
        
        if (propertyFound == null) {
          response = new Response(false, "PROPERTY_NOT_FOUND");
        }
        else {
          response = new Response(true, propertyFound, Property.class, "property");
  
        }
      }
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }
  
  @Override
  protected void describeGet(MethodInfo info) {

    // -> Global method info
    info.setIdentifier("get_property");
    info.setDocumentation("Get a single property from its ID");

    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("templateId", true, "xs:string", ParameterStyle.TEMPLATE, "Identifier of the template to work with.");
    info.getRequest().getParameters().add(param);
    ParameterInfo paramProperty = new ParameterInfo("propertyId", true, "xs:string", ParameterStyle.TEMPLATE, "Identifier of the property to get.");
    info.getRequest().getParameters().add(param);
    info.getRequest().getParameters().add(paramProperty);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);

  }

}
