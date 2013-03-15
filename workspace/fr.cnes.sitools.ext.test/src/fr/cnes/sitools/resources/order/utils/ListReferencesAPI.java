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
package fr.cnes.sitools.resources.order.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.restlet.Context;
import org.restlet.data.ClientInfo;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Util class to manage Reference Lists for Order Resource.
 * <p>
 * It manages 2 lists, one for source file to order the other for the destination Reference. It can also generate index
 * files into the admin storage for source list or into any storage for destination list
 * 
 * 
 * 
 * 
 * @author m.gond
 */
public class ListReferencesAPI {

  /**
   * The list to store references of source files (used to create admin index) TODO store it in a File to avoid memory
   * problem
   */
  private List<Reference> refSource;

  /**
   * The list to store reference of destination files ( used to create user index)
   * */
  private List<Reference> refDest;

  /**
   * The rootPath of the server to add to have absolute uri instead of relative. If the url are absolute, set it to null
   */
  private String rootPath;

  /**
   * Default constructor
   * 
   * @param rootPath
   *          the root URL String added before each Reference in the index file. If the URLs are absolute set it to null
   * 
   */
  public ListReferencesAPI(String rootPath) {
    refSource = new ArrayList<Reference>();
    refDest = new ArrayList<Reference>();
    this.rootPath = rootPath;
  }

  /**
   * Clear the reference source list
   */
  public void clearReferencesSource() {
    this.refSource.clear();
  }

  /**
   * Clear the reference destination list
   */
  public void clearReferencesDest() {
    this.refDest.clear();
  }

  /**
   * Add a reference to the index
   * 
   * @param reference
   *          the reference to add
   */
  public void addReferenceSource(Reference reference) {
    this.refSource.add(reference);
  }

  /**
   * Get the list of Reference in the index
   * 
   * @return the list of Reference in the index
   */
  public List<Reference> getReferencesSource() {
    return refSource;
  }

  /**
   * Add a reference to the index
   * 
   * @param reference
   *          the reference to add
   */
  public void addReferenceDest(Reference reference) {
    this.refDest.add(reference);
  }

  /**
   * Get the list of Reference in the index
   * 
   * @return the list of Reference in the index
   */
  public List<Reference> getReferencesDest() {
    return refDest;
  }

  /**
   * Copy the list of Reference destination to the given rootRef {@link Reference}
   * 
   * @param rootRef
   *          the destination {@link Reference}
   * @param context
   *          the {@link Context}
   * @param clientInfo
   *          the {@link ClientInfo}
   * @return the absolute {@link Reference} to the newly created file
   * @throws SitoolsException
   *           if there is an error while copying the file to the file system
   * @throws IOException
   *           if there is an error while creating the file
   */
  public Reference copyToUserStorage(Reference rootRef, Context context, ClientInfo clientInfo)
    throws SitoolsException, IOException {
    Reference refReturn = createAndCopyIndexFile(getReferencesDest(), true, context, rootRef, clientInfo);

    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    String urlReturn = settings.getPublicHostDomain() + settings.getString(Consts.APP_URL)
        + refReturn.toString().replace(RIAPUtils.getRiapBase(), "");

    return new Reference(urlReturn);

  }

  /**
   * Copy the list of Reference Source to the admin storage ( root url is given by the function
   * OrderResourceUtils.getResourceOrderStorageUrl(context)) with the specified. The name of file will be fileName and
   * it will be located in the folder folderName.
   * 
   * @param context
   *          the Context
   * @param folderName
   *          the name of the folder where to create the file
   * @param fileName
   *          the name of the file
   * @param clientInfo
   *          the clientInfo
   * @return the relative {@link Reference} to the newly created file
   * @throws SitoolsException
   *           if there is an error while add the file to the file system
   * @throws IOException
   *           if there is an error while creating the file
   */
  public Reference copyToAdminStorage(Context context, String folderName, String fileName, ClientInfo clientInfo)
    throws SitoolsException, IOException {
    Reference rootRef = new Reference(RIAPUtils.getRiapBase() + OrderResourceUtils.getResourceOrderStorageUrl(context));
    rootRef.addSegment(folderName);
    rootRef.addSegment(fileName);
    rootRef.setExtensions("txt");
    Reference refReturn = createAndCopyIndexFile(getReferencesSource(), false, context, rootRef, clientInfo);
    String urlIndex = refReturn.toString().replace(RIAPUtils.getRiapBase(), "");

    return new Reference(urlIndex);
  }

  /**
   * Create the an index file from the given {@link List} of {@link Reference}. The file is created at the given
   * absolute {@link Reference}. removeRIAP is used to specify whether or not to remove the RIAP base in the list of
   * Reference
   * 
   * @param references
   *          the list of {@link Reference}
   * @param removeRIAP
   *          true to remove the RIAP base in the index, false otherwise ( if true, the parameter rootPath will be added
   *          at the beginning)
   * @param context
   *          the Context
   * @param destReference
   *          the {@link Reference} where to create the file, must be absolute
   * @param clientInfo
   *          the ClientInfo
   * @return the absolute {@link Reference} to the created file
   * @throws SitoolsException
   *           if there is an error while add the file to the file system
   * @throws IOException
   *           if there is an error while creating the file
   */
  private Reference createAndCopyIndexFile(List<Reference> references, Boolean removeRIAP, Context context,
      Reference destReference, ClientInfo clientInfo) throws SitoolsException, IOException {

    String indexFile = getListIndex(references, removeRIAP);

    Representation indexFileRepr = new StringRepresentation(indexFile);

    return OrderResourceUtils.addFile(indexFileRepr, destReference, clientInfo, context);
  }

  /**
   * Get the list of references as text for file index export
   * 
   * @param references
   *          the list of references
   * @param removeRIAP
   *          if the RIAP part must be removed or not
   * @return the list of references as text for file index export
   * @throws IOException
   *           if there is an error while creating the list of references
   */
  private String getListIndex(List<Reference> references, boolean removeRIAP) throws IOException {
    ReferenceList refList = new ReferenceList();
    for (Reference reference : references) {
      String ref = reference.toString();
      if (removeRIAP) {
        // remove the RiapBase if the file were accessed with RIAP
        ref = ref.replace(RIAPUtils.getRiapBase(), "");
        refList.add(rootPath + ref);
      }
      else {
        refList.add(ref);
      }
    }
    return refList.getTextRepresentation().getText();
  }
}
