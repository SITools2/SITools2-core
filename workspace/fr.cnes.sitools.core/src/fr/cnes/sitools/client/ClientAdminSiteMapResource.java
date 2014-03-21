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
package fr.cnes.sitools.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;

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
public class ClientAdminSiteMapResource extends SitoolsResource {

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
    setName("ClientAdminSiteMapResource");
    setDescription("Create a SiteMap with key/url from sitools.properties");
    setNegotiated(false);
  }

  /**
   * Create a xml site map with sitools.properties url/name
   * 
   */
  @Get
  public void createSiteMap() {
    // ?? getSitoolsSetting(BUNDLE);
    // ?? SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();

    populateMapUrl(settings.getBundle());

    String templatePath = settings.getRootDirectory() + settings.getString(Consts.TEMPLATE_DIR) + "site.map.header.ftl";
    String siteMapPath = settings.getRootDirectory() + settings.getString(Consts.APP_CLIENT_PUBLIC_PATH) + "/res/siteMap";

    createXml(templatePath, siteMapPath);
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
   * @param sitePath
   *          the destination path of xml file
   * 
   */
  public void createXml(String tempPath, String sitePath) {
    String siteMap = "";

    Map<String, Object> root = new HashMap<String, Object>();

    Object[] list = listUrl.toArray();
    root.put("listUrl", list);
    root.put("context", getContext());

    siteMap = TemplateUtils.toString(tempPath, root);

    File file = new File(sitePath, "client-admin-site-map.xml");

    BufferedWriter fout = null;
    try {
      fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
    }
    catch (UnsupportedEncodingException e) {
      getLogger().log(Level.INFO, null, e);
    }
    catch (FileNotFoundException e) {
      getLogger().log(Level.INFO, null, e);
    }
   
    try {
      fout.write(siteMap);
    }
    catch (IOException e) {
      getLogger().log(Level.INFO, null, e);
    }
    try {
      fout.close();
    }
    catch (IOException e) {
      getLogger().log(Level.INFO, null, e);
    }
  }

}
