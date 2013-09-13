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
package fr.cnes.sitools.resources.order.utils;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.ClientInfo;
import org.restlet.data.LocalReference;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.engine.util.DateUtils;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.security.User;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.proxy.ProxySettings;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.TaskUtils;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Utils methods for OrderResource
 * 
 * 
 * @author m.gond
 */
public final class OrderResourceUtils {

  /** zipFilePattern name 0 = orderName, 1 = timestamp */
  public static final String ZIP_FILE_PATTERN = "/{orderName}_{timestamp}";
  /** zipFilePattern name */
  public static final String DIRECTORY_PATTERN = "/{orderName}_{timestamp}";
  /** fileListPattern name */
  public static final String FILE_LIST_PATTERN = "/{orderName}_{timestamp}_fileList";

  /**
   * OrderResourceUtils default constructor
   */
  private OrderResourceUtils() {
    super();
  }

  /**
   * Create a file from the following Representation at the given urlDest. urlDest must be relative url using RIAP. It
   * must contains the file name as well
   * 
   * @param repr
   *          the representation
   * @param refDest
   *          the destination reference. Must be complete with file name in it
   * @param clientInfo
   *          the ClientInfo to check if the user has the rights to add the file
   * @param context
   *          The context
   * @return The location of the new File
   * @throws SitoolsException
   *           if there is an error while creating the file
   */
  public static Reference addFile(Representation repr, Reference refDest, ClientInfo clientInfo, Context context)
    throws SitoolsException {

    //context.getLogger().info("ADD FILE TO : " + refDest);
    
    Request reqPOST = new Request(Method.PUT, refDest, repr);
    reqPOST.setClientInfo(clientInfo);
    org.restlet.Response r = context.getClientDispatcher().handle(reqPOST);
    try {
      if (r == null) {
        throw new SitoolsException("ERROR GETTING FILE : " + refDest);
      }
      else if (Status.CLIENT_ERROR_FORBIDDEN.equals(r.getStatus())) {
        throw new SitoolsException("CLIENT_ERROR_FORBIDDEN : " + refDest);
      }
      else if (Status.CLIENT_ERROR_UNAUTHORIZED.equals(r.getStatus())) {
        throw new SitoolsException("CLIENT_ERROR_UNAUTHORIZED : " + refDest);
      }
      else if (Status.isError(r.getStatus().getCode())) {
        throw new SitoolsException("ERROR : " + r.getStatus() + " getting file : " + refDest);
      }
      return refDest;
    }
    finally {
      RIAPUtils.exhaust(r);
    }
  }

  /**
   * Copy a file from <code>fileUrl</code> to <code>destUrl</code> destUrl must contain the fileName
   * 
   * @param fileUrl
   *          the file to copy
   * @param destUrl
   *          the destination folder url
   * @param clientInfo
   *          the ClientInfo to check if the user has the rights to copy the file
   * @param context
   *          The context
   * @return the url of the created file
   * @throws SitoolsException
   *           if the copy is unsuccessful
   */
  public static Reference copyFile(Reference fileUrl, Reference destUrl, ClientInfo clientInfo, Context context)
    throws SitoolsException {
//    context.getLogger().info("COPY FILE FROM " + fileUrl + " to " + destUrl);
    Representation repr = getFile(fileUrl, clientInfo, context);
    return addFile(repr, destUrl, clientInfo, context);
  }

  /**
   * Create a file from the following Representation at the given urlDest. urlDest must be relative url using RIAP. It
   * must contains the file name as well
   * 
   * @param refFile
   *          the file reference
   * @param clientInfo
   *          the ClientInfo to check if the user has the rights to delete the file
   * @param context
   *          The context
   * @throws SitoolsException
   *           if there is an error while deleting the file
   */
  public static void deleteFile(Reference refFile, ClientInfo clientInfo, Context context) throws SitoolsException {

    context.getLogger().info("DELETE FILE : " + refFile);

    Request reqDELETE = new Request(Method.DELETE, refFile);
    reqDELETE.setClientInfo(clientInfo);
    org.restlet.Response r = context.getClientDispatcher().handle(reqDELETE);
    try {
      if (r == null) {
        throw new SitoolsException("ERROR GETTING FILE : " + refFile);
      }
      else if (Status.CLIENT_ERROR_FORBIDDEN.equals(r.getStatus())) {
        throw new SitoolsException("CLIENT_ERROR_FORBIDDEN : " + refFile);
      }
      else if (Status.CLIENT_ERROR_UNAUTHORIZED.equals(r.getStatus())) {
        throw new SitoolsException("CLIENT_ERROR_UNAUTHORIZED : " + refFile);
      }
      else if (Status.isError(r.getStatus().getCode())) {
        throw new SitoolsException("ERROR : " + r.getStatus().getName() + "getting file : " + refFile);
      }
    }
    finally {
      RIAPUtils.exhaust(r);
    }
  }

  /**
   * Get an available folder path for a given User If there is a user , the folder will point into the userstorage of
   * this user If not it will point into the tmp directory Folder hierarchy is <code>SVAClassName/SvaTaskId</code>
   * 
   * @param user
   *          the User
   * @param context
   *          The context
   * @return the url of the folder
   */
  public static Reference getUserAvailableFolderPath(User user, Context context) {
    return getUserAvailableFolderPath(user, null, context);
  }

  /**
   * Get an available folder path for a given User and a given folderName If there is a user, the folder will point into
   * the userstorage of this user If not it will point into the tmp directory Folder hierarchy is
   * <code>folderName</code> or <code>SVAClassName/SvaTaskId</code> if folderName is null
   * 
   * @param user
   *          the User
   * @param folderName
   *          the name of the folder
   * @param context
   *          The context
   * @return the url of the folder
   */
  public static Reference getUserAvailableFolderPath(User user, String folderName, Context context) {
    if (folderName == null) {
      SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
      folderName = settings.getString(Consts.USERSTORAGE_RESOURCE_ORDER_DIR) + "/"
          + getDataSetApplication(context).getDataSet().getName() + "_"
          + DateUtils.format(new Date(), TaskUtils.getTimestampPattern());
    }

    String url = "";
    if (user != null) {
      String identifier = user.getIdentifier();
      url += getUserStorageUrl(identifier, context);
      url += folderName;
    }
    else {
      url += getTemporaryStorageUrl(context);
      url += folderName;
    }

    Reference reference = new Reference(RIAPUtils.getRiapBase() + url);
    return reference;
  }

  /**
   * Gets the representation of a File
   * 
   * @param fileUrl
   *          the url of the file
   * @param clientInfo
   *          the ClientInfo to check if the user has the rights to get the file
   * @param context
   *          The context
   * @return the Representation of a File
   * @throws SitoolsException
   *           if there is an error while getting the file
   */
  public static Representation getFile(Reference fileUrl, ClientInfo clientInfo, Context context)
    throws SitoolsException {
    Request reqGET = null;
    if (fileUrl.getScheme().equals("http")) {
      reqGET = new Request(Method.GET, fileUrl);
      if ((ProxySettings.getProxyAuthentication() != null) && reqGET.getProxyChallengeResponse() == null) {
        reqGET.setProxyChallengeResponse(ProxySettings.getProxyAuthentication());
      }
    }
    else {
      reqGET = new Request(Method.GET, fileUrl);
      reqGET.setClientInfo(clientInfo);
    }
    
    org.restlet.Response r = context.getClientDispatcher().handle(reqGET);

    if (r == null) {
      throw new SitoolsException("ERROR GETTING FILE : " + fileUrl);
    }
    else if (Status.CLIENT_ERROR_FORBIDDEN.equals(r.getStatus())) {
      throw new SitoolsException("CLIENT_ERROR_FORBIDDEN : " + fileUrl);
    }
    else if (Status.CLIENT_ERROR_UNAUTHORIZED.equals(r.getStatus())) {
      throw new SitoolsException("CLIENT_ERROR_UNAUTHORIZED : " + fileUrl);
    }
    else if (Status.isError(r.getStatus().getCode())) {
      throw new SitoolsException("ERROR : " + r.getStatus() + " getting file : " + fileUrl);
    }

    return r.getEntity();

  }

  /**
   * Zip the files listed in listOfFiles into the given destFilePath
   * 
   * @param listOfFiles
   *          list of files to zip, must be RIAP or HTTP file url
   * @param destFilePath
   *          the complete file path of the destination zip file, it must contains the zip file names and must be a
   *          local path
   * @param clientInfo
   *          the ClientInfo to check if the user has the rights to zip the file
   * @param context
   *          The context
   * @throws SitoolsException
   *           If something is wrong
   */
  public static void zipFiles(List<Reference> listOfFiles, String destFilePath, ClientInfo clientInfo, Context context)
    throws SitoolsException {

    // create the zip file
    File zipFile = new File(destFilePath);
    // create a localReference to access it
    LocalReference fr = LocalReference.createFileReference(zipFile);
    // create a reference to the zip file with the restlet zip protocol
    Reference fileRef = new Reference("zip:" + fr.toString());

    Reference fileUrl;
    String fileName = null;
    ClientResource crFile;
    // Loop through the list of files
    for (Iterator<Reference> iterator = listOfFiles.iterator(); iterator.hasNext();) {
      fileUrl = iterator.next();

      // get the file name with is the end of the url
      // fileName = getFileName(fileUrl.getPath());
      fileName = fileUrl.getLastSegment();
      if (fileName != null) {
        context.getLogger().info("Adding to zip : " + fileUrl);
        // get the file using REST call
        Representation fileRepr = getFile(fileUrl, clientInfo, context);
        // create a clientResource into the zip file
        crFile = new ClientResource(fileRef + "!/" + fileName);
        crFile.setClientInfo(clientInfo);
        // put the file into the zip file
        crFile.put(fileRepr);
        crFile.release();
      }
    }
  }

  /**
   * Get the fileName contained in the given url
   * 
   * @param url
   *          the url of a file
   * @return the file name if it exists, null if not
   */
  public static String getFileName(String url) {
    if (url.lastIndexOf("/") != -1) {
      return url.substring(url.lastIndexOf("/"));
    }
    else {
      return null;
    }
  }

  /**
   * Gets the DatasetApplication from the context corresponding to the TaskUtils.PARENT_APPLICATION variable
   * 
   * @param context
   *          The context
   * @return the DatasetApplication or null if the application is not a DatasetApplication
   */
  public static DataSetApplication getDataSetApplication(Context context) {
    SitoolsApplication app = getApplication(context);
    if (app.getClass().isInstance(DataSetApplication.class)) {
      return (DataSetApplication) app;
    }
    else {
      return null;
    }

  }

  /**
   * Gets the SitoolsApplication from the context corresponding to the TaskUtils.PARENT_APPLICATION variable
   * 
   * @param context
   *          The context
   * @return the SitoolsApplication
   */
  public static SitoolsApplication getApplication(Context context) {
    return (SitoolsApplication) context.getAttributes().get(TaskUtils.PARENT_APPLICATION);
  }

  /**
   * Gets the userStorageUrl value for a given userIdentifier
   * 
   * @param userIdentifier
   *          the userIdentifier
   * @param context
   *          The context
   * @return the userStorageUrl
   */
  public static String getUserStorageUrl(String userIdentifier, Context context) {
    String userStorageUrlPatern = getApplication(context).getSettings().getString(Consts.APP_USERSTORAGE_USER_URL)
        + "/files";
    return userStorageUrlPatern.replace("{identifier}", userIdentifier);
  }

  /**
   * Gets the dataStorageUrl value
   * 
   * @param context
   *          The context
   * @return the dataStorageUrl
   */
  public static String getDataStorageUrl(Context context) {
    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    return settings.getString(Consts.APP_DATASTORAGE_URL);
  }

  /**
   * Gets the userStorageUrl value
   * 
   * @param context
   *          The context
   * @return the userStorageUrl
   */
  public static String getTemporaryStorageUrl(Context context) {
    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    return settings.getString(Consts.APP_TMP_FOLDER_URL);
  }

  /**
   * Gets the userStorageUrl value
   * 
   * @param context
   *          The context
   * 
   * 
   * @return the userStorageUrl
   */
  public static String getResourceOrderStorageUrl(Context context) {
    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    return settings.getString(Consts.APP_ADMINSTORAGE_ORDERS_URL)
        + settings.getString(Consts.USERSTORAGE_RESOURCE_ORDER_DIR);

  }

  /**
   * Util method to retrieve an {@link AttributeValue} from a {@link Record} from a given USER_INPUT
   * {@link ResourceParameter}
   * 
   * @param param
   *          the {@link ResourceParameter}
   * @param rec
   *          the {@link Record}
   * @return the {@link AttributeValue}
   */
  public static AttributeValue getInParam(ResourceParameter param, Record rec) {
    List<AttributeValue> listRecord = rec.getAttributeValues();
    boolean found = false;
    AttributeValue attr = null;
    for (Iterator<AttributeValue> it = listRecord.iterator(); it.hasNext() && !found;) {
      attr = it.next();
      if (attr.getName().equals(param.getValue())) {
        found = true;
      }
    }
    if (found) {
      return attr;
    }
    else {
      return null;
    }
  }

}
