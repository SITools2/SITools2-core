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
package fr.cnes.sitools.form.dataset;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.form.dataset.model.Form;
import fr.cnes.sitools.notification.model.Notification;

/**
 * Resource for updating/deleting Forms definition when notified by DataSets change events.
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class FormNotificationResource extends AbstractFormResource {

  @Override
  public void sitoolsDescribe() {
    setName("ProjectNotificationResource");
    setDescription("Manage notification of project resources updating");
  }

  /**
   * Update / Validate existing project
   * 
   * @param representation
   *          Project representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation notification(Representation representation, Variant variant) {
    try {
      Notification notification = null;
      if (representation != null) {
        notification = getNotificationObject(representation);
      }

      if ((notification != null) && "DELETED".equals(notification.getStatus())) {
        // Business service
        boolean ok = getStore().delete(getFormId());
        if (ok) {
          return new StringRepresentation("OK");
        }
        else {
          return new StringRepresentation("DEPRECATED");
        }
      }
      if (notification != null && "DATASET_UPDATED".equals(notification.getEvent())) {
        Form form = getStore().retrieve(getFormId());
        DataSet dataset = this.getDataset(form.getParent());
        form.setParentUrl(dataset.getSitoolsAttachementForUsers());
        getStore().update(form);
        return new StringRepresentation("OK");
      }
      else {
        // Others status
        return new StringRepresentation("OK");
      }
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

  /**
   * Describe the Put command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describePut(MethodInfo info) {
    this.addStandardNotificationInfo(info);
    ParameterInfo pic = new ParameterInfo("formId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the form");
    info.getRequest().getParameters().add(pic);
  }

}
