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
/**
 * 
 */
package fr.cnes.sitools.resources.order.cart.wget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.data.Reference;
import org.restlet.representation.Representation;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.mail.model.Mail;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
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
public class WgetArchiveOrderResource extends AbstractCartOrderResource {
  /** Sitools settings */
  private SitoolsSettings settings;

  /** The type of archive to create */
  private String archiveType;

  @Override
  public void doInit() {
    super.doInit();
    settings = ((SitoolsApplication) getApplication()).getSettings();

    super.doInit();
    // initialise the archiveType, first let's get it from the request
    // parameters
    archiveType = getRequest().getResourceRef().getQueryAsForm().getFirstValue("archiveType");
    if (archiveType == null || "".equals(archiveType)) {
      // if it is not in the request parameters, let's get from the model
      ResourceParameter param = getModel().getParameterByName("archiveType");
      archiveType = param.getValue();
    }
  }

  @Override
  public Representation processOrder(ListReferencesAPI listReferences) throws SitoolsException {

    task.setCustomStatus("Order processing");
    OrderAPI.createEvent(order, getContext(), "PROCESSING ORDER");

    String fileName = getFileName();

    List<Reference> listOfFilesToOrder = listReferences.getReferencesSource();

    // Reference zipRef = new Reference(settings.getStoreDIR());
    // zipRef.addSegment(fileName);
    // zipRef.setExtensions(archiveType);

    String username = getClientInfo().getUser().getIdentifier();

    String archiveDir = settings.getUserStorageDir(username)
        + settings.getString(Consts.USERSTORAGE_RESOURCE_ORDER_DIR) + "/" + folderName;

    String archiveName = fileName + "." + archiveType;

    if ("zip".equals(archiveType)) {
      OrderResourceUtils.zipFiles(listOfFilesToOrder, listReferences.getRefSourceTarget(), archiveDir + "/",
          archiveName, getRequest().getClientInfo(), getContext());
    }
    else if ("tar.gz".equals(archiveType)) {
      OrderResourceUtils.tarFiles(listOfFilesToOrder, listReferences.getRefSourceTarget(), archiveDir, archiveName,
          getRequest().getClientInfo(), getContext(), true);
    }
    else if ("tar".equals(archiveType)) {
      OrderResourceUtils.tarFiles(listOfFilesToOrder, listReferences.getRefSourceTarget(), archiveDir, archiveName,
          getRequest().getClientInfo(), getContext(), false);
    }

    Reference destRef = OrderResourceUtils.getUserAvailableFolderPath(task.getUser(),
        settings.getString(Consts.USERSTORAGE_RESOURCE_ORDER_DIR) + folderName, getContext());
    //
    destRef.addSegment(fileName);
    destRef.setExtensions(archiveType);
    //
    // OrderResourceUtils.copyFile(zipRef, destRef,
    // getRequest().getClientInfo(), getContext());
    // OrderResourceUtils.deleteFile(zipRef, getRequest().getClientInfo(),
    // getContext());

    // set the result in the task
    task.setUrlResult(settings.getString(Consts.APP_URL) + settings.getString(Consts.APP_ORDERS_USER_URL) + "/"
        + order.getId());

    destRef = OrderResourceUtils.riapToExternal(destRef, settings);

    // add it the order
    ArrayList<String> orderedResource = new ArrayList<String>();
    orderedResource.add(destRef.toString());
    order.setResourceCollection(orderedResource);
    OrderAPI.updateOrder(order, getContext());

    return null;

  }

  protected String getMailBody(Mail mailToUser) {
    // default body
    String mailBody = "Your command is complete <br/>" + "Name : " + order.getName() + "<br/>" + "Description : "
        + order.getDescription() + "<br/>" + "Check the status at :" + task.getStatusUrl() + "<br/>"
        + "Get the result at :" + task.getUrlResult();

    // use a freemarker template for email body with Mail object
    String templatePath = settings.getRootDirectory() + settings.getString(Consts.TEMPLATE_DIR)
        + "mail.order.complete.wget.archive.ftl";

    Map<String, Object> root = new HashMap<String, Object>();
    root.put("mail", mailToUser);
    root.put("order", order);
    root.put("task", task);
    root.put("user", getClientInfo().getUser());

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
