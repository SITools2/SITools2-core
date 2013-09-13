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
/**
 * 
 */
package fr.cnes.sitools.resources.order.cart.wget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.restlet.data.Reference;
import org.restlet.representation.Representation;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.mail.model.Mail;
import fr.cnes.sitools.resources.order.cart.common.AbstractCartOrderResource;
import fr.cnes.sitools.resources.order.utils.ListReferencesAPI;
import fr.cnes.sitools.resources.order.utils.OrderAPI;
import fr.cnes.sitools.resources.order.utils.OrderResourceUtils;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.TemplateUtils;
import fr.cnes.sitools.util.Util;

/**
 * @author tx.chevallier
 * 
 * @version
 * 
 */
public class WgetListFileOrderResource extends AbstractCartOrderResource {
  /** Sitools settings */
  private SitoolsSettings settings;

  @Override
  public void doInit() {
    super.doInit();
    settings = ((SitoolsApplication) getApplication()).getSettings();
  }

  @Override
  public Representation processOrder(ListReferencesAPI listReferences) throws SitoolsException {

    task.setCustomStatus("Order processing");
    OrderAPI.createEvent(order, getContext(), "PROCESSING ORDER");

    List<Reference> listOfFilesToOrder = listReferences.getReferencesSource();

    Reference destRef = OrderResourceUtils.getUserAvailableFolderPath(task.getUser(),
        settings.getString(Consts.USERSTORAGE_RESOURCE_ORDER_DIR) + folderName, getContext());

    task.getLogger().log(
        Level.INFO,
        "FILE in progress for user : " + task.getUser().getIdentifier() + " -> ip :"
            + getClientInfo().getUpstreamAddress());
    task.getLogger().info("List of files ordered :");
    for (Reference r : listReferences.getReferencesSource()) {
      task.getLogger().info(" - " + r.getIdentifier().substring(16));
      r.getPath();
    }

    Reference ref;
    for (Iterator<Reference> iterator = listOfFilesToOrder.iterator(); iterator.hasNext();) {
      Reference sourceRef = iterator.next();
      task.getLogger().log(Level.WARNING, "{0}", sourceRef);
      try {
        ref = new Reference(destRef);

        String folder = listReferences.getRefSourceTarget().get(sourceRef);
        if (folder != null) {
          ref.addSegment("data/" + folder);
        }
        ref.addSegment(sourceRef.getLastSegment());
        OrderResourceUtils.copyFile(sourceRef, ref, getRequest().getClientInfo(), getContext());
        listReferences.addReferenceDest(ref);
      }
      catch (SitoolsException e) {
        task.getLogger().log(Level.WARNING, "File not copied : " + sourceRef, e);
      }
    }

    task.getLogger().log(Level.INFO, "Number of downloaded files : " + listOfFilesToOrder.size());

    // set the result in the task
    task.setUrlResult(settings.getString(Consts.APP_URL) + settings.getString(Consts.APP_ORDERS_USER_URL) + "/"
        + order.getId());

    try {
      // copy the indexFile to the destination reference
      String orderFileListName = fileName;
      if (orderFileListName == null || "".equals(orderFileListName)) {
        orderFileListName = OrderResourceUtils.FILE_LIST_PATTERN.replace("{orderName}", getOrderName());
        orderFileListName = orderFileListName.replace("{timestamp}", formatedDate);
      }
      else {
        orderFileListName += "_fileList";
      }
      destRef.addSegment(orderFileListName);
      destRef.setExtensions("txt");
      Reference urlUserIndex = listReferences.copyToUserStorage(destRef, getContext(), getClientInfo());

      // add it the order
      ArrayList<String> orderedResource = new ArrayList<String>();
      orderedResource.add(urlUserIndex.toString());
      order.setResourceCollection(orderedResource);
      order = OrderAPI.updateOrder(order, getContext());

    }
    catch (IOException e) {
      throw new SitoolsException("Error while creating the file index in the userstorage", e);
    }
    return null;

  }

  protected String getMailBody(Mail mailToUser) {
    // default body
    String mailBody = "Your command is complete <br/>" + "Name : " + order.getName() + "<br/>" + "Description : "
        + order.getDescription() + "<br/>" + "Check the status at :" + task.getStatusUrl() + "<br/>" + "Get the result at :"
        + task.getUrlResult();

    // use a freemarker template for email body with Mail object
    String templatePath = settings.getRootDirectory() + settings.getString(Consts.TEMPLATE_DIR)
        + "mail.order.complete.wget.ftl";

    Map<String, Object> root = new HashMap<String, Object>();
    root.put("mail", mailToUser);
    root.put("order", order);    
    root.put("task", task);

    TemplateUtils.describeObjectClassesForTemplate(templatePath, root);

    root.put("context", getContext());

    String body = TemplateUtils.toString(templatePath, root);
    if (Util.isNotEmpty(body)) {
      return body;
    }
    else {
      return mailBody;
    }
  }

}
