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
package fr.cnes.sitools.project.modules;

import java.util.List;

import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import fr.cnes.sitools.project.modules.model.ListComponents;
import fr.cnes.sitools.project.modules.model.ProjectModuleModel;

/**
 * Resource to return list of components in the same form that the file /client-user/tmp/listComponents.json
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class ListComponentsResource extends AbstractProjectModuleResource {

  @Override
  public void sitoolsDescribe() {
    setName("ListComponentsResource");
    setDescription("Resource for managing an identified project modules");
    setNegotiated(false);
  }

  /**
   * Gets the list of Components (ProjectModule) as JSON
   * 
   * @return the list of Components (ProjectModule) as JSON
   */
  @Get
  public Representation getListComponentsJson() {
    List<ProjectModuleModel> modules = getStore().getList();

    ListComponents lc = new ListComponents();
    lc.setComponents(modules);
    JacksonRepresentation<ListComponents> jr = new JacksonRepresentation<ListComponents>(lc);

    // jr.getObjectMapper().
    // {
    //
    // jr.get
    // /* (non-Javadoc)
    // * @see org.restlet.ext.jackson.JacksonRepresentation#write(java.io.Writer)
    // */
    // @Override
    // public void write(Writer writer) throws IOException {
    //
    // JsonFactory f = new JsonFactory();
    // JsonGenerator g = f.createJsonGenerator(writer);
    //
    // g.writeStartObject();
    // g.writeArrayFieldStart("components");
    //
    // Object object = getObject();
    // List<ProjectModule> modules = (List<ProjectModule>) object;
    // for (Iterator<ProjectModule> iterator = modules.iterator(); iterator.hasNext();) {
    // ProjectModule projectModule = (ProjectModule) iterator.next();
    // getObjectMapper().writeValue(writer, projectModule);
    // }
    //
    //
    // g.writeEndArray(); // for field 'name'
    // g.close(); // important: will force flushing of output, close underlying output stream
    //
    //
    // // super.write(writer);
    // }
    //
    // };

    return jr;

  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of all project modules as it was used in the older version of SITools2");
    info.setIdentifier("retrieve_project_module");
    addStandardGetRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

}
