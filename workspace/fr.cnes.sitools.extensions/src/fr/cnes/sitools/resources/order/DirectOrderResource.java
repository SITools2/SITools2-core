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
package fr.cnes.sitools.resources.order;

import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.order.model.Order;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.resources.order.representations.TarOutputRepresentation;
import fr.cnes.sitools.resources.order.representations.ZipOutputRepresentation;
import fr.cnes.sitools.resources.order.utils.ListReferencesAPI;
import fr.cnes.sitools.tasks.business.Task;

/**
 * {@link OrderResource} to order files and create an archive file 'on the fly' Multiple archive format are available
 * including ZIP, TAR or TAR.GZ
 * 
 * 
 * @author m.gond
 */
public class DirectOrderResource extends OrderResource {
  /** The type of archive to create */
  private String archiveType;

  @Override
  public void doInit() {
    super.doInit();
    // initialise the archiveType, first let's get it from the request parameters
    archiveType = getRequest().getResourceRef().getQueryAsForm().getFirstValue("archiveType");
    if (archiveType == null || "".equals(archiveType)) {
      // if it is not in the request parameters, let's get from the model
      ResourceParameter param = getModel().getParameterByName("archiveType");
      archiveType = param.getValue();
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.resources.order.AbstractOrderResource#checkUser()
   */
  @Override
  public void checkUser() {
    // do nothing because the archives can be downloaded even if there is no user logged (depending on the rights on the
    // application)
  }

  @Override
  protected void sendMail(Order order, Context context, fr.cnes.sitools.security.model.User user, Task task)
    throws SitoolsException {
    // do nothing because the user directly download an archive we don't send an email as well
  }

  /**
   * Process the list of files to order. Create a Representation of a Archive containing all the ordered files
   * 
   * @param listReferences
   *          the {@link ListReferencesAPI} containing the list of Reference to order
   * @return a {@link Representation} of a Archive containing all the ordered files
   * @throws SitoolsException
   *           if there is any error
   */
  @Override
  public Representation processOrder(ListReferencesAPI listReferences) throws SitoolsException {
    String fileName = getFileName();
    Representation repr = null;
    
    task.getLogger().log(Level.INFO, archiveType.toUpperCase() + " in progress for user : " 
        + task.getUser().getIdentifier() + " -> ip : " + getClientInfo().getUpstreamAddress());
    
    task.getLogger().info("List of files ordered :");
    for (Reference r : listReferences.getReferencesSource()) {
      task.getLogger().info(" - " + r.getIdentifier().substring(16));
      r.getPath();
    }
    
    
    if ("zip".equals(archiveType)) {
      repr = new ZipOutputRepresentation(listReferences.getReferencesSource(), getClientInfo(), getContext(), fileName
          + ".zip");
    }
    else if ("tar.gz".equals(archiveType)) {
      repr = new TarOutputRepresentation(listReferences.getReferencesSource(), getClientInfo(), getContext(), fileName
          + ".tar.gz", true);
    }
    else if ("tar".equals(archiveType)) {
      repr = new TarOutputRepresentation(listReferences.getReferencesSource(), getClientInfo(), getContext(), fileName
          + ".tar", false);
    }
    else {
      getResponse().setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
      return repr;
    }

    task.getLogger().log(Level.INFO, "Number of downloaded files : " + listReferences.getReferencesSource().size());
    
    return repr;
  }

}
