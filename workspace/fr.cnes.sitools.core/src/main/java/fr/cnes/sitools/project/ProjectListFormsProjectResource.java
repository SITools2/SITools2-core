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
package fr.cnes.sitools.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.form.project.dto.FormProjectDTO;
import fr.cnes.sitools.form.project.model.FormProject;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * 
 * Get the list of project forms (multidataset) for the project
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class ProjectListFormsProjectResource extends AbstractProjectResource {
  /** The project Application */
  private ProjectApplication application;

  @Override
  public void sitoolsDescribe() {
    setName("ProjectListFormsResource");
    setDescription("List of project forms (multidataset) for a project");
  }

  /**
   * Get the list of project forms (multidataset) for the project
   * 
   * @param variant
   *          the variant asked
   * @return a representation of the list of Forms
   */
  @Get
  public Representation getProjectFormsList(Variant variant) {

    Representation rep = null;

    application = (ProjectApplication) getApplication();

    Project proj = application.getProject();

    List<FormProject> formListOutput = getFormProjectList(proj.getId());
    List<FormProjectDTO> dtos = new ArrayList<FormProjectDTO>();

    // create a Map to store temporarly the Collections not to get it again if it has already been gotten
    Map<String, Collection> collections = new HashMap<String, Collection>();
    // create a Map to store temporarly the Collections not to get it again if it has already been gotten
    Map<String, Dictionary> dictionaries = new HashMap<String, Dictionary>();
    SitoolsSettings settings = (SitoolsSettings) getContext().getAttributes().get(ContextAttributes.SETTINGS);

    // create dto from each of the projects
    for (FormProject formProject : formListOutput) {
      // get the collection
      String collectionId = formProject.getCollection().getId();
      Collection collection = collections.get(collectionId);
      if (collection == null) {
        collection = RIAPUtils.getObject(collectionId, settings.getString(Consts.APP_COLLECTIONS_URL), getContext());
        collections.put(collectionId, collection);
      }
      // get the dictionary
      String dictionaryId = formProject.getDictionary().getId();
      Dictionary dictionary = dictionaries.get(dictionaryId);
      if (dictionary == null) {
        dictionary = RIAPUtils.getObject(dictionaryId, settings.getString(Consts.APP_DICTIONARIES_URL), getContext());
        dictionaries.put(dictionaryId, dictionary);
      }

      FormProjectDTO dto = FormProjectDTO.fromObjectToDto(formProject, dictionary, collection);
      dtos.add(dto);

    }

    Response response = new Response(true, dtos, FormProjectDTO.class);
    rep = getRepresentation(response, variant);

    return rep;
  }

  /**
   * Get the list of forms for a dataset
   * 
   * @param id
   *          the id of the dataset
   * @return a Response containing the list of Forms
   */
  private List<FormProject> getFormProjectList(String id) {
    return RIAPUtils.getListOfObjects(application.getSettings().getString(Consts.APP_PROJECTS_URL) + "/" + id
        + application.getSettings().getString(Consts.APP_FORMPROJECT_URL), getContext());
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of FormProjects (multidataset) associated to the project.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
