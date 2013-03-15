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
package fr.cnes.sitools.project.modules;

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.project.modules.model.ProjectModuleModel;

/**
 * Class Resource for managing single ProjectModule (GET UPDATE DELETE)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class ProjectModuleResource extends AbstractProjectModuleResource {
  
  @Override
  public void sitoolsDescribe() {
    setName("ProjectModuleResource");
    setDescription("Resource for managing an identified project modules");
    setNegotiated(false);
  }

  /**
   * get all projectModules
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveProjectModule(Variant variant) {
    // XStream xstream = XStreamFactory.getInstance().getXStreamWriter(variant.getMediaType(), false);
    if (getProjectModuleId() != null) {
      ProjectModuleModel projectModule = getStore().retrieve(getProjectModuleId());
      if (projectModule != null) {
        Response response = new Response(true, projectModule, ProjectModuleModel.class, "projectModule");
        return getRepresentation(response, variant);
      }
      else {
        Response response = new Response(false, "NO_MODULE_FOUND");
        return getRepresentation(response, variant);
      }
    }
    else {
      ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
      List<ProjectModuleModel> projectModules = getStore().getList(filter);
      int total = projectModules.size();
      projectModules = getStore().getPage(filter, projectModules);
      Response response = new Response(true, projectModules, ProjectModuleModel.class, "projectModules");
      response.setTotal(total);
      return getRepresentation(response, variant);
    }
  }
  
  @Override
  public final void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve a single ProjectModule by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("projectModuleId", true, "class", ParameterStyle.TEMPLATE, "Module identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    addStandardResourceCollectionFilterInfo(info);
  }

  /**
   * Update / Validate existing projectModule
   * 
   * @param representation
   *          ProjectModule representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateProjectModule(Representation representation, Variant variant) {
    ProjectModuleModel projectModuleOutput = null;
    try {

      ProjectModuleModel projectModuleInput = null;
      if (representation != null) {
        // Parse object representation
        projectModuleInput = getObject(representation, variant);

        // Business service
        projectModuleOutput = getStore().update(projectModuleInput);
      }

      Response response = new Response(true, projectModuleOutput, ProjectModuleModel.class, "projectModule");
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
  public final void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify a single module sending its new representation");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo param = new ParameterInfo("projectModuleId", true, "class", ParameterStyle.TEMPLATE, "Module identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Delete projectModule
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteProjectModule(Variant variant) {
    try {
      // Business service
      getStore().delete(getProjectModuleId());

      // Response
      Response response = new Response(true, "projectModule.delete.success");
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
  public final void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a single module by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("projectModuleId", true, "class", ParameterStyle.TEMPLATE, "Module identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
