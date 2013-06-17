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
package fr.cnes.sitools.proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.ExtendedResourceInfo;
import org.restlet.ext.wadl.ResourceInfo;
import org.restlet.ext.wadl.WadlDescribable;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Directory;

import fr.cnes.sitools.common.SitoolsMediaType;

/**
 * Directory proxy
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class DirectoryProxy extends Directory implements WadlDescribable {

  /** Proxy base reference */
  private String proxyBaseRef = null;

  /** Root node for serial */
  private String rootNode = "items";

  /** Regexp to file list restriction */
  private String regexp = null;

  /** true to set the no-cache directive on every response header */
  private boolean nocache = false;

  /**
   * Constructor
   * 
   * @param context
   *          restlet context
   * @param rootLocalReference
   *          root local reference
   * @param proxyBaseRef
   *          proxy bas reference
   */
  public DirectoryProxy(Context context, Reference rootLocalReference, String proxyBaseRef) {
    super(context, rootLocalReference);

    // if ((proxyBaseRef != null) && !proxyBaseRef.equals("")) {
    setTargetClass(DirectoryProxyResource.class);
    this.proxyBaseRef = proxyBaseRef;
    // }
    this.setNegotiatingContent(false);
  }

  /**
   * Constructor with proxyBaseRef
   * 
   * @param context
   *          restlet context
   * @param rootUri
   *          root URI
   * @param proxyBaseRef
   *          proxy base reference
   */
  public DirectoryProxy(Context context, String rootUri, String proxyBaseRef) {
    super(context, rootUri);
    // if ((proxyBaseRef != null) && !proxyBaseRef.equals("")) {
    setTargetClass(DirectoryProxyResource.class);
    this.proxyBaseRef = proxyBaseRef;
    // }
  }

  /**
   * Constructor
   * 
   * @param context
   *          parent context
   * @param rootUri
   *          Directory path
   */
  public DirectoryProxy(Context context, String rootUri) {
    super(context, rootUri);
    setTargetClass(DirectoryProxyResource.class);
  }

  /**
   * Gets the proxyBaseRef value
   * 
   * @return the proxyBaseRef
   */
  public String getProxyBaseRef() {
    return proxyBaseRef;
  }

  @Override
  public Representation getIndexRepresentation(Variant variant, ReferenceList indexContent) {
    Representation result = null;
    if (variant.getMediaType().isCompatible(MediaType.TEXT_HTML)) {
      result = indexContent.getWebRepresentation();
    }
    else if (variant.getMediaType().isCompatible(MediaType.TEXT_URI_LIST)) {
      result = indexContent.getTextRepresentation();
    }
    else if (variant.getMediaType().isCompatible(MediaType.APPLICATION_JSON)) {
      result = getJsonRepresentation(indexContent);
    }
    else if (variant.getMediaType().isCompatible(SitoolsMediaType.APPLICATION_SITOOLS_JSON_DIRECTORY)) {
      result = getAdvancedJsonRepresentation(indexContent);
    }
    return result;
  }

  /**
   * Get JSon from reference
   * 
   * @param reference
   *          the reference used
   * @return a JSON representation
   */
  protected JsonRepresentation getJsonRepresentation(ReferenceList reference) {

    Collection<JSONObject> entries = new ArrayList<JSONObject>();

    for (Reference ref : reference) {
      File file = null;
      if (reference instanceof ReferenceFileList) {
        file = ((ReferenceFileList) reference).get(ref.toString());
      }

      try {
        if (!ref.toString().endsWith("/")) {
          JSONObject jo = new JSONObject();
          String[] arrayPath = ref.toString().split("/");
          String path = arrayPath[arrayPath.length - 1];
          jo.put("name", path);
          jo.put("url", ref.toString());

          if ((file != null) && file.exists()) {
            jo.put("size", file.length());
            jo.put("lastmod", Math.round(file.lastModified() / 1000));

          }
          entries.add(jo);
        }
      }
      catch (org.json.JSONException e) {
        getLogger().warning("JSON exception: " + e.getMessage());
      }
    }

    JSONObject result = new JSONObject();
    try {
      result.put(this.getRootNode(), entries);
    }
    catch (JSONException e) {
      getLogger().warning("DirectoryProxy.getJsonRepresentation >> JSONException: " + e.getMessage());
    }

    JsonRepresentation jsonRep = new JsonRepresentation(result);
    jsonRep.setMediaType(MediaType.APPLICATION_JSON);
    return jsonRep;
  }

  /**
   * Get JSon from reference overrides DirectoryProxy getJsonRepresentation for producing a specific representation of a
   * user directory
   * 
   * @param reference
   *          the reference used
   * @return a JSON representation
   */
  protected JsonRepresentation getAdvancedJsonRepresentation(ReferenceList reference) {
    JSONArray array = new JSONArray();
    for (Reference ref : reference) {
      JSONObject jo = new JSONObject();
      File file = null;
      if (reference instanceof ReferenceFileList) {
        file = ((ReferenceFileList) reference).get(ref.toString());
      }
      try {
        if (ref.toString().endsWith("/")) {
          // jo.put("leaf", "false");
          jo.put("cls", "folder");
          jo.put("checked", false);

        }
        else {
          jo.put("leaf", "true");
          jo.put("checked", false);
        }

        String[] arrayPath = ref.toString().split("/");
        String path = arrayPath[arrayPath.length - 1];
        jo.put("url", ref.toString());
        jo.put("text", path);

        if ((file != null) && file.exists()) {
          jo.put("size", file.length());
          jo.put("lastmod", Math.round(file.lastModified() / 1000));
        }
        array.put(jo);
      }
      catch (org.json.JSONException e) {
        getLogger().warning("DirectoryProxy.getAdvancedJsonRepresentation >> JSON exception: " + e.getMessage());
      }
    }
    JsonRepresentation jsonRep = new JsonRepresentation(array);
    jsonRep.setMediaType(SitoolsMediaType.APPLICATION_SITOOLS_JSON_DIRECTORY);
    return jsonRep;
  }

  /**
   * Get the root node
   * 
   * @return the root node
   */
  private String getRootNode() {
    return rootNode;
  }

  /**
   * Returns the variant representations of a directory index. This method can be subclassed in order to provide
   * alternative representations.
   * 
   * By default it returns a simple HTML document and a textual URI list as variants. Note that a new instance of the
   * list is created for each call.
   * 
   * @param indexContent
   *          The list of references contained in the directory index.
   * @return The variant representations of a directory.
   */
  @Override
  public List<Variant> getIndexVariants(ReferenceList indexContent) {
    final List<Variant> result = new ArrayList<Variant>();
    result.add(new Variant(MediaType.TEXT_HTML));
    result.add(new Variant(MediaType.TEXT_URI_LIST));
    result.add(new Variant(MediaType.APPLICATION_JSON));
    result.add(new Variant(SitoolsMediaType.APPLICATION_SITOOLS_JSON_DIRECTORY));
    return result;
  }

  @Override
  public ResourceInfo getResourceInfo(ApplicationInfo applicationInfo) {
    ResourceInfo resourceInfo = new ResourceInfo();
    ExtendedResourceInfo.describe(applicationInfo, resourceInfo, this, this.getRootRef().getRelativePart());
    describe(resourceInfo);

    if (getName() != null && !"".equals(getName())) {
      DocumentationInfo doc = null;
      if (resourceInfo.getDocumentations().isEmpty()) {
        doc = new DocumentationInfo();
        resourceInfo.getDocumentations().add(doc);
      }
      else {
        doc = resourceInfo.getDocumentations().get(0);
      }

      doc.setTitle(getName());
      if (getDescription() != null && !getDescription().isEmpty()) {
        doc.setTextContent(getDescription());
      }

    }
    return resourceInfo;
  }

  /**
   * WADL describe method
   * 
   * @param resource
   *          the ResourceInfo
   */
  private void describe(ResourceInfo resource) {
    // TODO Auto-generated method stub
  }

  /**
   * Gets the regexp value
   * 
   * @return the regexp
   */
  public String getRegexp() {
    return regexp;
  }

  /**
   * Sets the value of regexp
   * 
   * @param regexp
   *          the regexp to set
   */
  public void setRegexp(String regexp) {
    this.regexp = regexp;
  }

  /**
   * Sets the value of nocache
   * 
   * @param nocache
   *          the nocache to set
   */
  public void setNocache(boolean nocache) {
    this.nocache = nocache;
  }

  /**
   * Gets the nocache value
   * 
   * @return the nocache
   */
  public boolean isNocache() {
    return nocache;
  }

}
