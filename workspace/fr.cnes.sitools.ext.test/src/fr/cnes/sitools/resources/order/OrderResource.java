 /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.resources.order.utils.ListReferencesAPI;
import fr.cnes.sitools.resources.order.utils.OrderAPI;
import fr.cnes.sitools.resources.order.utils.OrderResourceUtils;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Default OrderResource implementation.
 * <p>
 * The parameter colUrl is used to specify a column containing the list of URLs of the files to order. Each file is then
 * either copied or Zipped to the userstorage of the user.
 * </p>
 * 
 * 
 * @author m.gond
 */
public class OrderResource extends AbstractDatasetOrderResource {
  /** Maximum number of file to download authorized, default to -1 => no limit */
  private int nbMaxDownload = -1;

  @Override
  public ListReferencesAPI listFilesToOrder(DatabaseRequest dbRequest) throws SitoolsException {
    task.setCustomStatus("Creating list of files to order");
    ResourceModel resourceModel = getModel();
    ResourceParameter nbMaxDownloadParam = resourceModel.getParameterByName("too_many_selected_threshold");
    if (nbMaxDownloadParam != null && !"".equals(nbMaxDownloadParam)) {
      try {
        nbMaxDownload = Integer.parseInt(nbMaxDownloadParam.getValue());
      }
      catch (NumberFormatException e) {
        nbMaxDownload = -1;
      }
    }
    
    if (nbMaxDownload != -1 && nbMaxDownload < dbRequest.getCount()) {
      ResourceParameter errorTextParam = resourceModel.getParameterByName("too_many_selected_threshold_text");
      String errorText = (errorTextParam != null && !"".equals(errorTextParam.getValue())) ? errorTextParam.getValue()
          : "Too many file selected";
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, errorText);
    }

    ResourceParameter colUrl = resourceModel.getParameterByName("colUrl");
    if (colUrl.getValue() == null || colUrl.getValue().equals("")) {
      throw new SitoolsException("NO COLUMN DEFINED FOR ORDER");
    }

    ListReferencesAPI list = new ListReferencesAPI(settings.getPublicHostDomain() + settings.getString(Consts.APP_URL));
    while (dbRequest.nextResult()) {
      Record rec = dbRequest.getRecord();
      AttributeValue attributeValue = OrderResourceUtils.getInParam(colUrl, rec);

      if (attributeValue != null && attributeValue.getValue() != null) {
        // get the file path
        String filePath = (String) attributeValue.getValue();
        String urlAttach = settings.getString(Consts.APP_DATASTORAGE_URL);
        // if it contains "/datastorage" get rid of everything before
        if (filePath.contains(urlAttach)) {
          filePath = filePath.substring(filePath.lastIndexOf(urlAttach));
        }
        if (filePath.startsWith("http://")) {
          list.addReferenceSource(new Reference(filePath));
        }
        else {
          list.addReferenceSource(new Reference(RIAPUtils.getRiapBase() + filePath));
        }

      }
    }
    return list;
  }

  @Override
  public Representation processOrder(ListReferencesAPI listReferences) throws SitoolsException {
    task.setCustomStatus("Order processing");
    OrderAPI.createEvent(order, getContext(), "PROCESSING ORDER");

    List<Reference> listOfFilesToOrder = listReferences.getReferencesSource();

    
    Reference destRef = OrderResourceUtils.getUserAvailableFolderPath(task.getUser(),
        settings.getString(Consts.USERSTORAGE_RESOURCE_ORDER_DIR) + folderName, getContext());

    ResourceModel resourceModel = getModel();
    ResourceParameter zipParam = resourceModel.getParameterByName("zip");

    // zip is a USER_INPUT parameter, let's get it from the request
    // parameters
    String zipValue = this.getRequest().getResourceRef().getQueryAsForm().getFirstValue("zip");
    if (zipValue == null || zipValue.equals("") || (!"false".equals(zipValue) && !"true".equals(zipValue))) {
      zipValue = zipParam.getValue();
    }

    Boolean zip = Boolean.parseBoolean(zipValue);
    if (zip) {
      task.getLogger().log(Level.INFO, zipParam.getName().toUpperCase() + " in progress for user : " 
          + task.getUser().getIdentifier() + " -> ip :" + getClientInfo().getUpstreamAddress());
      
      task.getLogger().info("List of files ordered :");
      for (Reference r : listReferences.getReferencesSource()) {
        task.getLogger().info(" - " + r.getIdentifier().substring(16));
        r.getPath();
      }
      zip(listOfFilesToOrder, listReferences, destRef);
    }
    else {
      task.getLogger().log(Level.INFO, "FILE in progress for user : "
          + task.getUser().getIdentifier() + " -> ip :" + getClientInfo().getUpstreamAddress());
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
          ref.addSegment(sourceRef.getLastSegment());
          OrderResourceUtils.copyFile(sourceRef, ref, getRequest().getClientInfo(), getContext());
          listReferences.addReferenceDest(ref);
        }
        catch (SitoolsException e) {
          task.getLogger().log(Level.WARNING, "File not copied : " + sourceRef, e);
        }
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
        orderFileListName = OrderResourceUtils.FILE_LIST_PATTERN.replace("{orderName}", ds.getName());
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

  /**
   * Create the Zip from the listOfFilesToOrder
   * 
   * @param listOfFilesToOrder
   *          the list of files to order
   * @param listReferences
   *          the ListReferenceAPI to add some reference
   * @param destRef
   *          the destination reference
   * @throws SitoolsException
   *           if there is an error
   */
  private void zip(List<Reference> listOfFilesToOrder, ListReferencesAPI listReferences, Reference destRef)
    throws SitoolsException {

    String zipFileName = fileName;
    if (zipFileName == null || "".equals(zipFileName)) {
      zipFileName = OrderResourceUtils.ZIP_FILE_PATTERN.replace("{orderName}", ds.getName());
      zipFileName = zipFileName.replace("{timestamp}", formatedDate);
    }

    Reference zipRef = new Reference(RIAPUtils.getRiapBase() + settings.getString(Consts.APP_TMP_FOLDER_URL));
    zipRef.addSegment(zipFileName);
    zipRef.setExtensions("zip");

    // create an index and add it to the zip files
    Reference ref;
    Reference sourceRef;
    for (Iterator<Reference> iterator = listOfFilesToOrder.iterator(); iterator.hasNext();) {
      sourceRef = iterator.next();
      ref = new Reference(destRef);
      ref.addSegment(sourceRef.getLastSegment());
      listReferences.addReferenceDest(ref);
    }

    // copy the indexFile to the destination reference
    Reference destRefListFileInZip = new Reference(destRef);

    String orderFileListName = fileName;
    if (orderFileListName == null || "".equals(orderFileListName)) {
      orderFileListName = OrderResourceUtils.FILE_LIST_PATTERN.replace("{orderName}", ds.getName());
      orderFileListName = orderFileListName.replace("{timestamp}", formatedDate);
    }
    else {
      orderFileListName += "_fileList";
    }
    destRefListFileInZip.addSegment(orderFileListName);
    destRefListFileInZip.setExtensions("txt");
    try {
      listReferences.copyToUserStorage(destRefListFileInZip, getContext(), getClientInfo());
      listReferences.clearReferencesDest();
      listReferences.addReferenceSource(destRefListFileInZip);
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    OrderResourceUtils.zipFiles(listOfFilesToOrder, settings.getTmpFolderUrl() + "/" + zipFileName + ".zip",
        getRequest().getClientInfo(), getContext());
    destRef.addSegment(zipRef.getLastSegment());
    OrderResourceUtils.copyFile(zipRef, destRef, getRequest().getClientInfo(), getContext());
    OrderResourceUtils.deleteFile(zipRef, getRequest().getClientInfo(), getContext());

    Reference destZipRef = new Reference(destRef);
    listReferences.addReferenceDest(destZipRef);

    destRef.setLastSegment("");
  }
}
