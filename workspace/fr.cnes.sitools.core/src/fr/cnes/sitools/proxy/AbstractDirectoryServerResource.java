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
package fr.cnes.sitools.proxy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CacheDirective;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.data.Status;
import org.restlet.engine.local.Entity;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Directory;
import org.restlet.resource.ServerResource;

/**
 * Base class for Sitools Directory resources
 * 
 * Additional methods :
 * 
 * @see {@link #upload(Representation)}
 * 
 * @see Original class in Restlet : {@link #DirectoryServerResource()}
 * 
 * @author m.marseille (AKKA Technologies)
 */

public abstract class AbstractDirectoryServerResource extends ServerResource {

  /**
   * If the resource is a directory, the non-trailing slash character leads to redirection.
   */
  private volatile boolean directoryRedirection;

  /** The original target URI, in case of extensions tunneling. */
  private volatile Reference originalRef;

  /**
   * The local base name of the resource. For example, "foo.en" and "foo.en-GB.html" return "foo".
   */
  private volatile String baseName;

  /** The parent directory handler. */
  private volatile DirectoryProxy directory;

  /** If the resource is a directory, this contains its content. */
  private volatile ReferenceList directoryContent;

  /** The context's directory URI (file, clap URI). */
  private volatile String directoryUri;

  /** The context's target URI (file, clap URI). */
  private volatile String targetUri;

  /** Indicates if the target resource is a directory. */
  private volatile boolean directoryTarget;

  /** Indicates if the target resource is a file. */
  private volatile boolean fileTarget;

  /** The unique representation of the target URI, if it exists. */
  private volatile Reference uniqueReference;

  /** The prototype variant. */
  private volatile Variant protoVariant;

  /** The base variant. */
  private volatile Variant baseVariant;

  /** The list of variants for the GET method. */
  private volatile List<Variant> variantsGet;

  /** If the resource is a file, this contains its content. */
  private volatile Representation fileContent;

  /** The resource path relative to the directory URI. */
  private volatile String relativePart;

  /** To override directory indexName and prevent automatic index.html response */
  private volatile String indexName;
  
  /**
   * Gets the variantsGet value
   * 
   * @return the variantsGet
   */
  public final List<Variant> getVariantsGet() {
    return variantsGet;
  }

  /**
   * Sets the value of variantsGet
   * 
   * @param variantsGet
   *          the variantsGet to set
   */
  public final void setVariantsGet(List<Variant> variantsGet) {
    this.variantsGet = variantsGet;
  }

  /**
   * Gets the fileContent value
   * 
   * @return the fileContent
   */
  public final Representation getFileContent() {
    return fileContent;
  }

  /**
   * Sets the value of fileContent
   * 
   * @param fileContent
   *          the fileContent to set
   */
  public final void setFileContent(Representation fileContent) {
    this.fileContent = fileContent;
  }

  /**
   * Gets the relativePart value
   * 
   * @return the relativePart
   */
  public final String getRelativePart() {
    return relativePart;
  }

  /**
   * Sets the value of relativePart
   * 
   * @param relativePart
   *          the relativePart to set
   */
  public final void setRelativePart(String relativePart) {
    this.relativePart = relativePart;
  }

  @Override
  public final Representation get() {
    // Content negotiation has been disabled
    // The variant that may need to meet the request conditions
    Representation result = null;

    List<Variant> variants = getVariants(Method.GET);
    if ((variants == null) || (variants.isEmpty())) {
      // Resource not found
      getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
    }
    else {
      if (variants.size() == 1) {
        result = (Representation) variants.get(0);
      }
      else {
        ReferenceList variantRefs = new ReferenceList();

        for (Variant variant : variants) {
          if (variant.getLocationRef() != null) {
            variantRefs.add(variant.getLocationRef());
          }
          else {
            getLogger()
                .warning(
                    "A resource with multiple variants should provide a location for each variant when content negotiation is turned off");
          }
        }

        if (variantRefs.size() > 0) {
          // Return the list of variants
          setStatus(Status.REDIRECTION_MULTIPLE_CHOICES);
          result = variantRefs.getTextRepresentation();
        }
        else {
          setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        }
      }
    }

    return result;
  }

  /**
   * Returns the local base name of the file. For example, "foo.en" and "foo.en-GB.html" return "foo".
   * 
   * @return The local name of the file.
   */
  public final String getBaseName() {
    return this.baseName;
  }

  /**
   * Set the base name
   * 
   * @param base
   *          the base name to set
   */
  public final void setBaseName(String base) {
    this.baseName = base;
  }

  /**
   * Returns a client dispatcher.
   * 
   * @return A client dispatcher.
   */
  public final Client getClientDispatcher() {
    return getDirectory().getContext() == null ? null : getDirectory().getContext().getClientDispatcher();
  }

  /**
   * Returns the parent directory handler.
   * 
   * @return The parent directory handler.
   */
  public final Directory getDirectory() {
    return this.directory;
  }

  /**
   * Set the parent directory handler.
   * 
   * @param dir
   *          the directory to set
   */
  public final void setDirectory(DirectoryProxy dir) {
    this.directory = dir;
  }

  /**
   * If the resource is a directory, this returns its content.
   * 
   * @return The directory content.
   */
  public final ReferenceList getDirectoryContent() {
    return directoryContent;
  }

  /**
   * If the resource is a directory, this returns its content.
   * 
   * @param dirContent
   *          the directory content
   */
  public final void setDirectoryContent(ReferenceList dirContent) {
    directoryContent = dirContent;
  }

  /**
   * Returns the context's directory URI (file, clap URI).
   * 
   * @return The context's directory URI (file, clap URI).
   */
  public final String getDirectoryUri() {
    return this.directoryUri;
  }

  /**
   * Returns the context's directory URI (file, clap URI).
   * 
   * @param dirUri
   *          set the directory URI
   */
  public final void setDirectoryUri(String dirUri) {
    this.directoryUri = dirUri;
  }

  /**
   * Sets the context's target URI (file, clap URI).
   * 
   * @param targetUri
   *          The context's target URI.
   */
  public final void setTargetUri(String targetUri) {
    this.targetUri = targetUri;
  }

  /**
   * Returns a representation of the resource at the target URI. Leverages the client dispatcher of the parent
   * directory's context.
   * 
   * @param resourceUri
   *          The URI of the target resource.
   * @return A response with the representation if success.
   */
  public final Response getRepresentation(String resourceUri) {
    return getClientDispatcher().handle(new Request(Method.GET, resourceUri));
  }

  /**
   * Returns a representation of the resource at the target URI. Leverages the client dispatcher of the parent
   * directory's context.
   * 
   * @param resourceUri
   *          The URI of the target resource.
   * @param acceptedMediaType
   *          The accepted media type or null.
   * @return A response with the representation if success.
   */
  public final Response getRepresentation(String resourceUri, MediaType acceptedMediaType) {
    if (acceptedMediaType == null) {
      return getClientDispatcher().handle(new Request(Method.GET, resourceUri));
    }

    Request request = new Request(Method.GET, resourceUri);
    request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(acceptedMediaType));
    return getClientDispatcher().handle(request);
  }

  /**
   * Allows to sort the list of representations set by the resource.
   * 
   * @return A Comparator instance imposing a sort order of representations or null if no special order is wanted.
   */
  public final Comparator<Representation> getRepresentationsComparator() {
    // Sort the list of representations by their identifier.
    Comparator<Representation> identifiersComparator = new Comparator<Representation>() {
      public int compare(Representation rep0, Representation rep1) {
        boolean bRep0Null = (rep0.getLocationRef() == null);
        boolean bRep1Null = (rep1.getLocationRef() == null);

        if (bRep0Null && bRep1Null) {
          return 0;
        }
        if (bRep0Null) {
          return -1;
        }

        if (bRep1Null) {
          return 1;
        }

        return rep0.getLocationRef().getLastSegment().compareTo(rep1.getLocationRef().getLastSegment());
      }
    };
    return identifiersComparator;
  }

  /**
   * Returns the context's target URI (file, clap URI).
   * 
   * @return The context's target URI (file, clap URI).
   */
  public final String getTargetUri() {
    return this.targetUri;
  }

  @Override
  public final List<Variant> getVariants() {
    return getVariants(getMethod());
  }

  /**
   * Indicates if the target resource is a directory.
   * 
   * @param isDirTarget
   *          true if is a directory target
   */
  public final void setIfDirectoryTarget(boolean isDirTarget) {
    this.directoryTarget = isDirTarget;
  }

  /**
   * Indicates if the target resource is a file.
   * 
   * @param isFileTarget
   *          true if is a file target
   */
  public final void setIfFileTarget(boolean isFileTarget) {
    this.fileTarget = isFileTarget;
  }

  /**
   * Indicates if the target resource is a directory.
   * 
   * @return True if the target resource is a directory.
   */
  public final boolean isDirectoryTarget() {
    return this.directoryTarget;
  }

  /**
   * Indicates if the target resource is a file.
   * 
   * @return True if the target resource is a file.
   */
  public final boolean isFileTarget() {
    return this.fileTarget;
  }

  @Override
  public final Representation post(Representation entity) {
    return upload(entity);
  }

  @Override
  public final Representation put(Representation entity) {
    return upload(entity);
  }

  /**
   * upload
   * 
   * @param entity
   *          Representation
   * @return Representation
   */
  public final Representation upload(Representation entity) {
    String media = getRequest().getOriginalRef().getQueryAsForm().getFirstValue("media");

    if (this.getDirectory().isModifiable()) {
      if ((entity != null) && (entity.getMediaType() != null)
          && (entity.getMediaType().getName().startsWith(MediaType.MULTIPART_FORM_DATA.getName()))) {
        // 1/ Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(1000240);

        // 2/ Create a new file upload handler based on the Restlet
        // FileUpload extension that will parse Restlet requests and
        // generates FileItems.
        RestletFileUpload upload = new RestletFileUpload(factory);

        List<FileItem> items;

        try {
          // 3/ Request is parsed by the handler which generates a
          // list of FileItems
          items = upload.parseRequest(getRequest());

          // Process only the uploaded item called "fileToUpload" and
          // save it on disk
          for (final Iterator<FileItem> it = items.iterator(); it.hasNext();) {
            FileItem fi = (FileItem) it.next();
            if ((fi != null) && (fi.getName() != null)) {
              // Adding url relative part to post at the real url (not only root directory)
              String path = getDirectory().getRootRef().getPath() + getRelativePart();
              // Prepare a classloader URI, removing the leading slash
              if ((path != null) && path.startsWith("/")) {
                path = path.substring(1);
              }
              String repertoire = Reference.decode(path);
              File file = new File(repertoire, fi.getName());
              fi.write(file);
            }
          }
        }
        catch (FileUploadException e) {
          getLogger().log(Level.INFO, null, e);
          getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        }
        catch (Exception e) {
          getLogger().log(Level.INFO, null, e);
          getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        }

        if ((media != null) && !media.equals("")) {
          media = "?media=" + media;
        }
        else {
          media = "";
        }

        // String redirectUrl = getReference().toString() + media; // address of newly created resource
        // getResponse().redirectSeeOther(redirectUrl);

        setStatus(Status.SUCCESS_CREATED);
      }

      else {

        // Transfer of PUT calls is only allowed if the readOnly flag is
        // not set.
        Request contextRequest = new Request(Method.PUT, this.getTargetUri());

        // Add support of partial PUT calls.
        contextRequest.getRanges().addAll(getRanges());
        contextRequest.setEntity(entity);

        Response contextResponse = new Response(contextRequest);
        contextRequest.setResourceRef(this.getTargetUri());
        getClientDispatcher().handle(contextRequest, contextResponse);
        setStatus(contextResponse.getStatus());
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, "The directory is not modifiable.");
    }

    return null;
  }

  @Override
  public Representation handle() {
    Representation result = null;

    if (this.directoryRedirection) {
      if (this.originalRef != null) {
        if (this.originalRef.hasQuery()) {
          redirectSeeOther(this.originalRef.getPath() + "/?" + this.originalRef.getQuery());
        }
        else {
          redirectSeeOther(this.originalRef.getPath() + "/");
        }
      }
      else {
        if (getReference().hasQuery()) {
          redirectSeeOther(getReference().getPath() + "/?" + getReference().getQuery());
        }
        else {
          redirectSeeOther(getReference().getPath() + "/");
        }
      }
    }
    else {
      result = super.handle();
    }

    // fix no cache
    if (((DirectoryProxy) getDirectory()).isNocache()) {
      getResponse().getCacheDirectives().add(CacheDirective.noCache());
    }
    // end fix no cache

    return result;
  }

  /**
   * Gets the directoryRedirection value
   * 
   * @return the directoryRedirection
   */
  public final boolean isDirectoryRedirection() {
    return directoryRedirection;
  }

  /**
   * Sets the value of directoryRedirection
   * 
   * @param directoryRedirection
   *          the directoryRedirection to set
   */
  public final void setDirectoryRedirection(boolean directoryRedirection) {
    this.directoryRedirection = directoryRedirection;
  }

  /**
   * Gets the originalRef value
   * 
   * @return the originalRef
   */
  public final Reference getOriginalRef() {
    return originalRef;
  }

  /**
   * Sets the value of originalRef
   * 
   * @param originalRef
   *          the originalRef to set
   */
  public final void setOriginalRef(Reference originalRef) {
    this.originalRef = originalRef;
  }

  /**
   * Handling the call for delete method using base handle
   * 
   * @return the representation according to the call
   */
  public final Representation handleDelete() {
    return super.handle();
  }

  /**
   * Returns the references of the representations of the target resource according to the directory handler property
   * 
   * @return The list of variants references
   */
  public final ReferenceList getVariantsReferences() {
    ReferenceList result = new ReferenceList(0);

    try {
      this.setUniqueReference(null);

      // Ask for the list of all variants of this resource
      Response contextResponse = getRepresentation(this.getTargetUri(), MediaType.TEXT_URI_LIST);
      if (contextResponse.getEntity() != null) {
        // Test if the given response is the list of all variants for
        // this resource
        if (MediaType.TEXT_URI_LIST.equals(contextResponse.getEntity().getMediaType())) {
          ReferenceList listVariants = new ReferenceList(contextResponse.getEntity());
          String entryUri;
          String fullEntryName;
          String baseEntryName;
          int lastSlashIndex;
          int firstDotIndex;

          for (Reference ref : listVariants) {
            entryUri = ref.toString();
            lastSlashIndex = entryUri.lastIndexOf('/');
            fullEntryName = (lastSlashIndex == -1) ? entryUri : entryUri.substring(lastSlashIndex + 1);
            baseEntryName = fullEntryName;

            // Remove the extensions from the base name
            firstDotIndex = fullEntryName.indexOf('.');
            if (firstDotIndex != -1) {
              baseEntryName = fullEntryName.substring(0, firstDotIndex);
            }

            // Check if the current file is a valid variant
            if (baseEntryName.equals(this.getBaseName())) {
              // Test if the variant is included in the base
              // prototype variant
              Variant variant = new Variant();
              Entity.updateMetadata(fullEntryName, variant, true, getMetadataService());
              if (this.getProtoVariant().includes(variant)) {
                result.add(ref);
              }

              // Test if the variant is equal to the base variant
              if (this.getBaseVariant().equals(variant)) {
                // The unique reference has been found.
                this.setUniqueReference(ref);
              }
            }
          }
        }
        else {
          result.add(contextResponse.getEntity().getLocationRef());
        }
      }
    }
    catch (IOException ioe) {
      getLogger().log(Level.WARNING, "Unable to get resource variants", ioe);
    }

    return result;

  }

  /**
   * Sets the value of uniqueReference
   * 
   * @param uniqueReference
   *          the uniqueReference to set
   */
  public final void setUniqueReference(Reference uniqueReference) {
    this.uniqueReference = uniqueReference;
  }

  /**
   * Gets the uniqueReference value
   * 
   * @return the uniqueReference
   */
  public final Reference getUniqueReference() {
    return uniqueReference;
  }

  /**
   * Sets the value of baseVariant
   * 
   * @param baseVariant
   *          the baseVariant to set
   */
  public final void setBaseVariant(Variant baseVariant) {
    this.baseVariant = baseVariant;
  }

  /**
   * Gets the baseVariant value
   * 
   * @return the baseVariant
   */
  public final Variant getBaseVariant() {
    return baseVariant;
  }

  /**
   * Sets the value of protoVariant
   * 
   * @param protoVariant
   *          the protoVariant to set
   */
  public final void setProtoVariant(Variant protoVariant) {
    this.protoVariant = protoVariant;
  }

  /**
   * Gets the protoVariant value
   * 
   * @return the protoVariant
   */
  public final Variant getProtoVariant() {
    return protoVariant;
  }

  /**
   * Returns the list of variants for the given method.
   * 
   * @param method
   *          The related method.
   * @return The list of variants for the given method.
   * 
   * @see DirectoryServerResource.getVariants(Method method)
   */
  public List<Variant> getVariants(Method method) {

    List<Variant> result = null;

    if ((Method.GET.equals(method) || Method.HEAD.equals(method))) {
      if (variantsGet != null) {
        result = variantsGet;
      }
      else {
        getLogger().fine("Getting variants for : " + getTargetUri());

        if ((this.getDirectoryContent() != null) && (getReference() != null) && (getReference().getBaseRef() != null)) {

          // Allows to sort the list of representations
          SortedSet<Representation> resultSet = new TreeSet<Representation>(getRepresentationsComparator());

          // Compute the base reference (from a call's client point of
          // view)
          String baseRef = getReference().getBaseRef().toString(false, false);

          if (!baseRef.endsWith("/")) {
            baseRef += "/";
          }

          int lastIndex = this.relativePart.lastIndexOf("/");

          if (lastIndex != -1) {
            baseRef += this.relativePart.substring(0, lastIndex);
          }

          int rootLength = getDirectoryUri().length();

          if (this.getBaseName() != null) {
            String filePath;
            for (Reference ref : getVariantsReferences()) {
              // Add the new variant to the result list
              Response contextResponse = getRepresentation(ref.toString());
              if (contextResponse.getStatus().isSuccess() && (contextResponse.getEntity() != null)) {
                filePath = ref.toString(false, false).substring(rootLength);
                Representation rep = contextResponse.getEntity();

                if (filePath.startsWith("/")) {
                  rep.setLocationRef(baseRef + filePath);
                }
                else {
                  rep.setLocationRef(baseRef + "/" + filePath);
                }

                resultSet.add(rep);
              }
            }
          }

          if (!resultSet.isEmpty()) {
            result = new ArrayList<Variant>(resultSet);
          }

          if (resultSet.isEmpty()) {
            if (isDirectoryTarget() && getDirectory().isListingAllowed()) {
              ReferenceFileList userList = new ReferenceFileList(this.getDirectoryContent().size());
              // Set the list identifier
              userList.setIdentifier(baseRef);
              userList.setRegexp(directory.getRegexp());

              SortedSet<Reference> sortedSet = new TreeSet<Reference>(getDirectory().getComparator());
              sortedSet.addAll(this.getDirectoryContent());

              for (Reference ref : sortedSet) {
                String filePart = ref.toString(false, false).substring(rootLength);
                StringBuilder filePath = new StringBuilder();
                if ((!baseRef.endsWith("/")) && (!filePart.startsWith("/"))) {
                  filePath.append('/');
                }
                filePath.append(filePart);

                // SITOOLS2 - ReferenceFileList = reference and File
                String path = ref.getPath();
                // Prepare a classloader URI, removing the leading slash
                if ((path != null) && path.startsWith("/")) {
                  path = path.substring(1);
                }

                String repertoire = Reference.decode(path);
                File file = new File(repertoire);

                userList.addFileReference(baseRef + filePath, file);
                // SITOOLS2 instead of simple reference : userList.add(baseRef + filePath);
              }
              List<Variant> list = getDirectory().getIndexVariants(userList);
              for (Variant variant : list) {
                if (result == null) {
                  result = new ArrayList<Variant>();
                }

                result.add(getDirectory().getIndexRepresentation(variant, userList));
              }

            }
          }
        }
        else if (isFileTarget() && (this.fileContent != null)) {
          // Sets the location of the target representation.
          if (getOriginalRef() != null) {
            this.fileContent.setLocationRef(getRequest().getOriginalRef());
          }
          else {
            this.fileContent.setLocationRef(getReference());
          }

          result = new ArrayList<Variant>();
          result.add(this.fileContent);
        }

        this.variantsGet = result;
      }
    }

    return result;
  }

  /**
   * Gets the indexName value
   * @return the indexName
   */
  public String getIndexName() {
    return indexName;
  }

  /**
   * Sets the value of indexName
   * @param indexName the indexName to set
   */
  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  
  
}
