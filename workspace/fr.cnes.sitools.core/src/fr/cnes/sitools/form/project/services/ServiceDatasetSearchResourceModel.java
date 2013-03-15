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
package fr.cnes.sitools.form.project.services;

import org.restlet.Context;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;
import fr.cnes.sitools.project.ProjectApplication;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.model.TaskResourceModel;
import fr.cnes.sitools.tasks.model.TaskRunTypeAdministration;

/**
 * Resource model for Multidataset search service
 * 
 * 
 * @author m.gond
 */
public class ServiceDatasetSearchResourceModel extends TaskResourceModel {

  /** serialVersionUID */
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor, only overrides name, description and resourceClassName
   */
  public ServiceDatasetSearchResourceModel() {
    super();
    setClassAuthor("AKKA Technologies");
    setClassOwner("CNES");
    setClassVersion("0.1");
    setName("ServiceDatasetSearchResourceModel");
    setDescription("Search service on a collection of dataset");

    setResourceClassName("fr.cnes.sitools.form.project.services.ServiceDatasetSearchResourceFacade");
    setResourceImplClassName("fr.cnes.sitools.form.project.services.ServiceDatasetSearchResource");

    ResourceParameter dictionary = new ResourceParameter("dictionary", "The id of the dictionary",
        ResourceParameterType.PARAMETER_INTERN);
    /** Type de paramètre pour lister les colonnes du dataset */
    dictionary.setValueType("xs:string");
    ResourceParameter collection = new ResourceParameter("collection", "The id of the collection to use",
        ResourceParameterType.PARAMETER_USER_INPUT);
    /** Type de colonne booléen */
    collection.setValueType("xs:string");

    ResourceParameter nbThreads = new ResourceParameter("nbThreads",
        "The number of Thread to use for the multidataset search", ResourceParameterType.PARAMETER_INTERN);
    /** Type de colonne entier */
    nbThreads.setValueType("xs:integer");

    ResourceParameter nbDatasetsMax = new ResourceParameter("nbDatasetsMax",
        "The maximal number of datasets authorized for the request", ResourceParameterType.PARAMETER_INTERN);
    /** Type de colonne entier */
    nbThreads.setValueType("xs:integer");

    this.addParam(dictionary);
    this.addParam(collection);
    this.addParam(nbThreads);
    this.addParam(nbDatasetsMax);

    this.setApplicationClassName(ProjectApplication.class.getName());

    this.setRunTypeAdministration(TaskRunTypeAdministration.TASK_FORCE_RUN_ASYNC);
    this.getParameterByName("methods").setValue("POST");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.plugins.resources.model.ResourceModel#initParametersForAdmin(org.restlet.Context)
   */
  @Override
  public void initParametersForAdmin(Context context) {
    super.initParametersForAdmin(context);

    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    ResourceParameter nbThreads = getParameterByName("nbThreads");
    nbThreads.setValue(settings.getString(Consts.DEFAULT_THREAD_POOL_SIZE));

  }

}
