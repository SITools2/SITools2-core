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
import java.util.List;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Parameter;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.data.Status;
import org.restlet.engine.local.Entity;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.util.FileUtils;

/**
 * FIXME Suivre les évolutions de la classe Restlet DirectoryServerResource Resource supported by a set of context
 * representations (from file system, class loaders and webapp context). A content negotiation mechanism (similar to
 * Apache HTTP server) is available. It is based on path extensions to detect variants (languages, media types or
 * character sets).
 * 
 * @see <a href="http://httpd.apache.org/docs/2.0/content-negotiation.html">Apache mod_negotiation module</a>
 * @author Jerome Louvel
 * @author Thierry Boileau
 */
public final class DirectoryUserResource extends AbstractDirectoryServerResource {

  /** Indicates if the target resource is a directory with an index. */
  private volatile boolean indexTarget;

  /** The original target URI, in case of extensions tunneling. */
  private volatile Reference originalRef;

  /** Indicates if the directory access goes through intranet */
  private boolean isIntranet = false;

  /** Public host domain name */
  private String publicHostDomain = null;

  /** User name */
  private String username = null;

  /**
   * Can recursively delete directory with request parameter recursive=true
   * 
   * @return a representation of the delete answer
   */
  @Override
  public Representation delete() {
    if (this.getDirectory().isModifiable()) {
      Request contextRequest = new Request(Method.DELETE, this.getTargetUri());
      Response contextResponse = new Response(contextRequest);

      if (this.isDirectoryTarget() && !this.indexTarget) {

        // BEGIN PATCH SITOOLS JPB TO recursively delete a directory.
        if ((getRequest().getResourceRef() != null) && (getRequest().getResourceRef().getQueryAsForm() != null)
            && (getRequest().getResourceRef().getQueryAsForm().getFirst("recursive", false) != null)
            && getRequest().getResourceRef().getQueryAsForm().getFirst("recursive", false).getValue().equals("true")) {

          contextRequest.getResourceRef().normalize();

          // As the path may be percent-encoded, it has to be percent-decoded.
          // Then, all generated URIs must be encoded.
          String path = contextRequest.getResourceRef().getPath();
          String decodedPath = Reference.decode(path);
          try {
            FileUtils.cleanDirectory(new File(decodedPath), true);
          }
          catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        // END PATCH SITOOLS JPB

        contextRequest.setResourceRef(this.getTargetUri());
        getClientDispatcher().handle(contextRequest, contextResponse);
      }
      else {
        // Check if there is only one representation
        // Try to get the unique representation of the resource
        ReferenceList references = getVariantsReferences();
        if (!references.isEmpty()) {
          if (this.getUniqueReference() != null) {
            contextRequest.setResourceRef(this.getUniqueReference());
            getClientDispatcher().handle(contextRequest, contextResponse);
          }
          else {
            // We found variants, but not the right one
            contextResponse.setStatus(new Status(Status.CLIENT_ERROR_NOT_ACCEPTABLE,
                "Unable to process properly the request. Several variants exist but none of them suits precisely. "));
          }
        }
        else {
          contextResponse.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        }
      }

      setStatus(contextResponse.getStatus());
    }
    else {
      setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, "The directory is not modifiable.");
    }

    return null;
  }

  /**
   * This initialization method aims at answering the following questions:<br>
   * <ul>
   * <li>does this request target a directory?</li>
   * <li>does this request target a directory, with an index file?</li>
   * <li>should this request be redirected (target is a directory with no trailing "/")?</li>
   * <li>does this request target a file?</li>
   * </ul>
   * <br>
   * The following constraints must be taken into account:<br>
   * <ul>
   * <li>the underlying helper may not support content negotiation and be able to return the list of possible variants
   * of the target file (e.g. the CLAP helper).</li>
   * <li>the underlying helper may not support directory listing</li>
   * <li>the extensions tunneling cannot apply on a directory</li>
   * <li>underlying helpers that do not support content negotiation cannot support extensions tunneling</li>
   * </ul>
   */
  @Override
  public void doInit() {
    try {
      // SITOOLS
      Object intranet = getRequestAttributes().get("Sitools.intranet");
      if (intranet != null) {
        isIntranet = (Boolean) intranet;
      }
      Object phd = getRequestAttributes().get(ContextAttributes.PUBLIC_HOST_NAME);
      if (phd != null) {
        publicHostDomain = (String) phd;
      }
      // END SITOOLS
      // Update the member variables
      // PATCH SITOOLS JPB (DirectoryUser) au lieu de (Directory)
      this.setDirectory((DirectoryUser) getRequestAttributes().get("org.restlet.directory"));
      this.setRelativePart(getReference().getRemainingPart(false, false));
      setNegotiated(this.getDirectory().isNegotiatingContent());

      // PATCH SITOOLS recuperation de l'utilisateur
      username = (String) getRequestAttributes().get(((DirectoryUser) this.getDirectory()).getUserAttribute());
      // END PATCH SITOOLS

      // Restore the original URI in case the call has been tunneled.
      if ((getApplication() != null) && getApplication().getTunnelService().isExtensionsTunnel()) {
        this.originalRef = getOriginalRef();

        if (this.originalRef != null) {
          this.originalRef.setBaseRef(getReference().getBaseRef());
          this.setRelativePart(this.originalRef.getRemainingPart());
        }
      }

      if (this.getRelativePart().startsWith("/")) {
        // We enforce the leading slash on the root URI
        this.setRelativePart(this.getRelativePart().substring(1));
      }

      // PATCH SITOOLS JPB Rajout de l'identifiant de l'utilisateur

      // The target URI does not take into account the query and fragment
      // parts of the resource.
      this.setTargetUri(new Reference(getDirectory().getRootRef().toString()
          + getRequestAttributes().get(((DirectoryUser) this.getDirectory()).getUserAttribute()) + "/"
          + this.getRelativePart()).normalize().toString(false, false));

      // FIN PATCH SITOOLS JPB Rajout de l'identifiant de l'utilisateur

      if (!this.getTargetUri().startsWith(getDirectory().getRootRef().toString())) {
        // Prevent the client from accessing resources in upper
        // directories
        this.setTargetUri(getDirectory().getRootRef().toString());
      }

      // Parametre de requete pour surcharger l'indexname et empecher le retour d'index.html
      Parameter indexParameter = getRequest().getResourceRef().getQueryAsForm().getFirst("index", false);
      if (indexParameter != null) {
        setIndexName(indexParameter.getValue());
      }
      else {
        setIndexName(getDirectory().getIndexName());
      }
      
      if (getClientDispatcher() == null) {
        getLogger().warning(
            "No client dispatcher is available on the context. Can't get the target URI: " + this.getTargetUri());
      }
      else {
        // Try to detect the presence of a directory
        Response contextResponse = getRepresentation(this.getTargetUri());

        if (contextResponse.getEntity() != null) {
          // As a convention, underlying client connectors return the
          // directory listing with the media-type
          // "MediaType.TEXT_URI_LIST" when handling directories
          if (MediaType.TEXT_URI_LIST.equals(contextResponse.getEntity().getMediaType())) {
            this.setIfDirectoryTarget(true);
            this.setIfFileTarget(false);
            this.setDirectoryContent(new ReferenceList(contextResponse.getEntity()));

            if (!getReference().getPath().endsWith("/")) {
              // All requests will be automatically redirected
              this.setDirectoryRedirection(true);
            }
            // PATCH SITOOLS TO KEEP targetUri unchanged when Method is DELETE
            if (getMethod() != Method.DELETE) {
              if (!this.getTargetUri().endsWith("/")) {
                this.setTargetUri(this.getTargetUri() + "/");
                this.setRelativePart(getRelativePart() + "/");
              }

              // Append the index name
              // PATCH SITOOLS TO PREVENT automatic index.html response
              if ((getIndexName() != null) && (getIndexName().length() > 0)) {
              // if ((getDirectory().getIndexName() != null) && (getDirectory().getIndexName().length() > 0)) {
                this.setDirectoryUri(this.getTargetUri());
                // this.setBaseName(getDirectory().getIndexName());
                this.setBaseName(getIndexName());
                this.setTargetUri(this.getDirectoryUri() + this.getBaseName());
                this.indexTarget = true;
              }
              else {
                this.setDirectoryUri(this.getTargetUri());
                this.setBaseName(null);
              }
            }
            else {
              // PATCH SITOOLS : PATH FOR METHOD DELETE
              this.setDirectoryUri(this.getTargetUri());
              this.setBaseName(null);
              this.indexTarget = false;
            }
          }
          else {
            // Allows underlying helpers that do not support
            // "content negotiation" to return the targeted file.
            // Sometimes we immediately reach the target entity, so
            // we return it directly.
            this.setIfDirectoryTarget(false);
            this.setIfFileTarget(true);
            this.setFileContent(contextResponse.getEntity());
          }
        }
        else {
          this.setIfDirectoryTarget(false);
          this.setIfFileTarget(false);

          // Let's try with the optional index, in case the underlying
          // client connector does not handle directory listing.
          if (this.getTargetUri().endsWith("/")) {
            // In this case, the trailing "/" shows that the URI
            // must point to a directory
            
            // PATCH SITOOLS TO PREVENT automatic index.html response
            if ((getIndexName() != null) && (getIndexName().length() > 0)) {
            // if ((getDirectory().getIndexName() != null) && (getDirectory().getIndexName().length() > 0)) {
              this.setDirectoryUri(this.getTargetUri());
              this.setIfDirectoryTarget(true);

              // contextResponse = getRepresentation(this.getDirectoryUri() + getDirectory().getIndexName());
              contextResponse = getRepresentation(this.getDirectoryUri() + getIndexName());
              if (contextResponse.getEntity() != null) {
                // this.setBaseName(getDirectory().getIndexName());
                this.setBaseName(getIndexName());
                this.setTargetUri(getDirectoryUri() + this.getBaseName());
                this.setDirectoryContent(new ReferenceList());
                this.getDirectoryContent().add(new Reference(this.getTargetUri()));
                this.indexTarget = true;
              }
            }
          }
          else {
            // Try to determine if this target URI with no trailing
            // "/" is a directory, in order to force the
            // redirection.
            
            // PATCH SITOOLS TO PREVENT automatic index.html response
            if ((getIndexName() != null) && (getIndexName().length() > 0)) {
            // if ((getDirectory().getIndexName() != null) && (getDirectory().getIndexName().length() > 0)) {
              // Append the index name
              contextResponse = getRepresentation(this.getTargetUri() + "/" + getIndexName());
              // contextResponse = getRepresentation(this.getTargetUri() + "/" + getDirectory().getIndexName());
              if (contextResponse.getEntity() != null) {
                this.setDirectoryUri(this.getTargetUri() + "/");
                this.setBaseName(getIndexName());
                // this.setBaseName(getDirectory().getIndexName());
                this.setTargetUri(this.getDirectoryUri() + this.getBaseName());
                this.setIfDirectoryTarget(true);
                this.setDirectoryRedirection(true);
                this.setDirectoryContent(new ReferenceList());
                this.getDirectoryContent().add(new Reference(this.getTargetUri()));
                this.indexTarget = true;
              }
            }
          }
        }

        // In case the request does not target a directory and the file
        // has not been found, try with the tunneled URI.
        if (isNegotiated() && !this.isDirectoryTarget() && !this.isFileTarget() && (this.originalRef != null)) {
          this.setRelativePart(getReference().getRemainingPart());

          // The target URI does not take into account the query and
          // fragment parts of the resource.
          this.setTargetUri(new Reference(getDirectory().getRootRef().toString() + this.getRelativePart()).normalize()
              .toString(false, false));
          if (!this.getTargetUri().startsWith(getDirectory().getRootRef().toString())) {
            // Prevent the client from accessing resources in upper
            // directories
            this.setTargetUri(getDirectory().getRootRef().toString());
          }
        }

        if (!isFileTarget() || (getFileContent() == null) || !getRequest().getMethod().isSafe()) {
          // Try to get the directory content, in case the request
          // does not target a directory
          if (!this.isDirectoryTarget()) {
            int lastSlashIndex = this.getTargetUri().lastIndexOf('/');
            if (lastSlashIndex == -1) {
              this.setDirectoryUri("");
              this.setBaseName(this.getTargetUri());
            }
            else {
              this.setDirectoryUri(this.getTargetUri().substring(0, lastSlashIndex + 1));
              this.setBaseName(this.getTargetUri().substring(lastSlashIndex + 1));
            }

            contextResponse = getRepresentation(this.getDirectoryUri());
            if ((contextResponse.getEntity() != null)
                && MediaType.TEXT_URI_LIST.equals(contextResponse.getEntity().getMediaType())) {
              this.setDirectoryContent(new ReferenceList(contextResponse.getEntity()));
            }
          }

          if (this.getBaseName() != null) {
            // Analyze extensions
            this.setBaseVariant(new Variant());
            Entity.updateMetadata(this.getBaseName(), this.getBaseVariant(), true, getMetadataService());
            this.setProtoVariant(new Variant());
            Entity.updateMetadata(this.getBaseName(), this.getProtoVariant(), false, getMetadataService());

            // Remove stored extensions from the base name
            this.setBaseName(Entity.getBaseName(this.getBaseName(), getMetadataService()));
          }

          // Check if the resource exists or not.
          List<Variant> variants = getVariants(Method.GET);
          if ((variants == null) || (variants.isEmpty())) {
            setExisting(false);
          }
        }

        // Check if the resource is located in a sub directory.
        if (isExisting() && !this.getDirectory().isDeeplyAccessible()) {
          // Count the number of "/" character.
          int index = this.getRelativePart().indexOf("/");
          if (index != -1) {
            index = this.getRelativePart().indexOf("/", index);
            setExisting((index == -1));
          }
        }
      }

      // Log results
      getLogger().fine("Converted target URI: " + this.getTargetUri());
      getLogger().finest("Converted base name : " + this.getBaseName());
    }
    catch (IOException ioe) {
      throw new ResourceException(ioe);
    }
  }

  /**
   * Returns the list of variants for the given method.
   * 
   * @param method
   *          The related method.
   * @return The list of variants for the given method.
   */
  public List<Variant> getVariants(Method method) {
    // SITOOLS
    if (!isIntranet) {
      DirectoryUser proxyDirectory = (DirectoryUser) getDirectory();
      if (proxyDirectory.getProxyBaseRef() != null && publicHostDomain != null && !"".equals(publicHostDomain)) {
        // PUBLIC HOST DOMAIN + proxyDirectory.getProxyAttachRef
        getReference().setBaseRef(
            publicHostDomain
                + proxyDirectory.getProxyBaseRef().replace("{" + proxyDirectory.getUserAttribute() + "}", username) + "/files");
      }
    }
    // FIN SITOOLS

    List<Variant> result = super.getVariants(method);
    return result;
  }

  @Override
  public Representation handle() {
    Representation result = null;
    // JPB
    if (getMethod() == Method.DELETE) {
      try {
        // TODO WARNING - le handle de AbstractDirectoryServerResourceProxy n'est pas le bon (pourquoi ?).
        // d'où le handleFromUser ...
        result = (Representation) super.handleDelete();
      }
      catch (IllegalArgumentException e) {
        getLogger().severe(e.getMessage());
      }
      catch (SecurityException e) {
        getLogger().severe(e.getMessage());
      }
    }
    else {
      result = super.handle();
    }
    return result;
  }

}


  