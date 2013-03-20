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
package fr.cnes.sitools.client;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

import fr.cnes.sitools.client.model.Url;
import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.server.Starter;
import fr.cnes.sitools.util.TemplateUtils;

/**
 * Create a SiteMap with key/url from sitools.properties
 * 
 * @author b.fiorito (AKKA Technologies)
 */
public class ClientSiteMapResource extends SitoolsResource {

  /** Resource bundle name */
  public static final String BUNDLE = "sitools";

  /** URL list from sitools.properties */
  private ArrayList<Url> listUrl = new ArrayList<Url>();

  /** sitools settings */
  private SitoolsSettings settings = SitoolsSettings.getInstance(BUNDLE, Starter.class.getClassLoader(), Locale.FRANCE,
      true);

  /**
   * ClientAdminSiteMapResource Describe
   * 
   */
  @Override
  public void sitoolsDescribe() {
    setName("ClientSiteMapResource");
    setDescription("Create a SiteMap with key/url from sitools.properties");
    setNegotiated(false);
  }

  /**
   * Create a xml site map with sitools.properties url/name
   * 
   * @return siteMap
   *          xml file contening url/name
   * 
   */
  @Get
  public Representation createSiteMap() {

    if (settings.getBundle() != null) {
      populateMapUrl(settings.getBundle());
    }

    String templatePath = settings.getRootDirectory() + settings.getString(Consts.TEMPLATE_DIR) + "site.map.header.ftl";

    String siteMap = createXml(templatePath);

    return new StringRepresentation(siteMap, MediaType.APPLICATION_XML);
  }

  /**
   * filled in a Map with key/value URL
   * 
   * @param bundle
   *          the bundle contening key/value url
   * 
   */
  public void populateMapUrl(ResourceBundle bundle) {
    String key;
    String value;
    Enumeration<String> bundleKeys = bundle.getKeys();

    while (bundleKeys.hasMoreElements()) {
      key = (String) bundleKeys.nextElement();
      if (key.startsWith("Starter.APP_") && key.endsWith("_URL")) {
        value = bundle.getString(key);
        String[] name = key.split("Starter.");
        listUrl.add(new Url(name[1], value));
      }
    }
  }

  /**
   * generate xml file contening sitools properties name/url
   * 
   * @param tempPath
   *          the path of the template
   * @return siteMap
   */
  public String createXml(String tempPath) {
    String siteMap = "";

    Map<String, Object> root = new HashMap<String, Object>();

    Object[] list = listUrl.toArray();
    root.put("listUrl", list);
    root.put("context", getContext());

    siteMap = TemplateUtils.toString(tempPath, root);

    return siteMap;
  }

}
