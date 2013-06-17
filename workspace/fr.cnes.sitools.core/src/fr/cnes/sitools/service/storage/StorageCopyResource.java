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
package fr.cnes.sitools.service.storage;

import org.restlet.data.Method;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.service.storage.model.StorageDirectory;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * 
 * A resource to copy the content of a storage into another one
 * 
 * @author m.gond
 */
public class StorageCopyResource extends SitoolsResource {
  /** The name of the directory destination */
  private String directoryNameDest;
  /** The name of the source directory */
  private String directoryNameSrc;

  @Override
  public void sitoolsDescribe() {
    setName("StorageCopyResource");
    setDescription("A resource to copy the content of a storage into another one");
  }

  /**
   * Initialize the resource
   */
  @Override
  protected void doInit() {
    super.doInit();
    directoryNameSrc = (String) this.getRequest().getAttributes().get("directoryNameSrc");
    directoryNameDest = (String) this.getRequest().getAttributes().get("directoryNameDest");
  }

  /**
   * Method to copy the content of a storage into another one
   * 
   * @param entity
   *          the Entity
   * @param variant
   *          the variant needed in return
   * @return a representation telling whether or not the copy has been successful
   */
  @Put
  public Representation copyDirectory(Representation entity, Variant variant) {
    Response response;
    if (directoryNameDest != null && directoryNameSrc != null) {

      StorageDirectory dirSrc = RIAPUtils.getObjectFromName(getSettings().getString(Consts.APP_DATASTORAGE_ADMIN_URL)
          + "/directories", directoryNameSrc, getApplication().getContext());
      StorageDirectory dirDest = RIAPUtils.getObjectFromName(getSettings().getString(Consts.APP_DATASTORAGE_ADMIN_URL)
          + "/directories", directoryNameDest, getApplication().getContext());

      if (dirSrc != null && dirDest != null) {
        String directoryIdSrc = dirSrc.getId();
        String directoryIdDest = dirDest.getId();

        return RIAPUtils.handle(getSettings().getString(Consts.APP_DATASTORAGE_ADMIN_URL) + "/directories/"
            + directoryIdSrc + "?action=copy&idDest=" + directoryIdDest, Method.PUT, getMediaType(variant),
            getApplication().getContext());

      }
      else {
        response = new Response(false, "Cannot find directories");
      }

    }
    else {
      response = new Response(false, "Please set directories name");
    }
    return getRepresentation(response, variant);
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to create a storage sending its representation.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardObjectResponseInfo(info);

    ParameterInfo paramSrc = new ParameterInfo("directoryNameSrc", false, "xs:string", ParameterStyle.QUERY,
        "The name of the source datastorage");
    info.getRequest().getParameters().add(paramSrc);

    ParameterInfo paramDest = new ParameterInfo("directoryNameDest", false, "xs:string", ParameterStyle.QUERY,
        "The name of the destination datastorage");
    info.getRequest().getParameters().add(paramDest);

  }

}
