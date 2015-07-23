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
package fr.cnes.sitools.applications;

import java.io.File;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

import fr.cnes.sitools.client.AdminIndex;
import fr.cnes.sitools.client.ClientSiteMapResource;
import fr.cnes.sitools.common.application.StaticWebApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.proxy.DirectoryProxy;
import fr.cnes.sitools.common.Consts;

/**
 * Application Web Client Administration.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class ClientAdminApplication extends StaticWebApplication {

  /**
   * adminIndex URL
   */
  private String adminIndexUrl;

  /**
   * template Dir
   */
  private String templateDir;

  /**
   * css Dir
   */
  private String cssDir;

  /**
   * licence Dir
   */
  private String licenceDir;

  /**
   * Constructor with folder of exposed files
   * 
   * @param context
   *          Restlet {@code Context}
   * @param appPath
   *          Directory path
   * @param baseUrl
   *          public domain name when list files.
   */
  public ClientAdminApplication(final Context context, final String appPath, final String baseUrl) {
    super(context, appPath, baseUrl);

    // File Editable Directories
    templateDir = getSettings().getRootDirectory() + getSettings().getString(Consts.FILEEDITOR_FTL_DIR);
    cssDir = getSettings().getRootDirectory() + getSettings().getString(Consts.FILEEDITOR_CSS_DIR);
    licenceDir = getSettings().getRootDirectory() + getSettings().getString(Consts.FILEEDITOR_LICENCE_DIR);

    // Application settings

    adminIndexUrl = getSettings().getRootDirectory() + getSettings().getString(Consts.TEMPLATE_DIR)
        + getSettings().getString("Starter.client-admin.adminIndex");

    File portalIndexFile = new File(adminIndexUrl);
    if (portalIndexFile == null || !portalIndexFile.exists()) {
      getLogger().severe("Template file for Admin/index.html not found :" + adminIndexUrl);
    }
  }

  // Unavailable
  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("client-admin");
    setDescription("The application is used by the administrator to access the Web client administrator application\n"
        + "-> public user must have GET authorization otherwise the login page will be unavailable\n"
        + "-> administrator user must have GET authorization has well");
  }

  @Override
  public Restlet createInboundRoot() {
    Router router = (Router) super.createInboundRoot();

    DirectoryProxy dpTemplate = new DirectoryProxy(getContext(), "file:///" + templateDir, getAttachementRef());
    dpTemplate.setName("FTL directoryProxy");
    dpTemplate.setDescription("Exposes the freemarker template");

    DirectoryProxy dpCss = new DirectoryProxy(getContext(), "file:///" + cssDir, getAttachementRef());
    dpCss.setName("CSS directoryProxy");
    dpCss.setDescription("Exposes the css files");

    DirectoryProxy dpLicence = new DirectoryProxy(getContext(), "file:///" + licenceDir, getAttachementRef());
    dpLicence.setName("Licence directoryProxy");
    dpLicence.setDescription("Exposes the licences files");

    this.getMetadataService().addCommonExtensions();

    /** Template access config */
    dpTemplate.setDeeplyAccessible(true);
    dpTemplate.setListingAllowed(true);
    dpTemplate.setModifiable(true);

    /** Css access config */
    dpCss.setDeeplyAccessible(true);
    dpCss.setListingAllowed(true);
    dpCss.setModifiable(true);

    /** Licence access config */
    dpLicence.setDeeplyAccessible(true);
    dpLicence.setListingAllowed(true);
    dpLicence.setModifiable(true);

    this.getTunnelService().setEnabled(true);
    this.getTunnelService().setMethodTunnel(true);
    this.getTunnelService().setMethodParameter("method");

    router.attach("/ftl", dpTemplate);
    router.attach("/css", dpCss);
    router.attach("/licence", dpLicence);

    router.attach("/", AdminIndex.class);

    router.attach("/index.html", AdminIndex.class).getTemplate().setMatchingMode(Template.MODE_EQUALS);

    router.attach("/siteMap", ClientSiteMapResource.class);

    return router;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo appInfo = super.getApplicationInfo(request, response);
    appInfo.setDocumentation("Web client application for SITools2 administrators.");
    return appInfo;
  }

  /**
   * Gets the adminIndexUrl value
   * 
   * @return the adminIndexUrl
   */
  public String getAdminIndexUrl() {
    return adminIndexUrl;
  }

  /**
   * Sets the value of adminIndexUrl
   * 
   * @param adminIndexUrl
   *          the adminIndexUrl to set
   */
  public void setAdminIndexUrl(String adminIndexUrl) {
    this.adminIndexUrl = adminIndexUrl;
  }

  /**
   * Gets the templateDir value
   * 
   * @return the templateDir
   */
  public String getTemplateDir() {
    return templateDir;
  }

  /**
   * Sets the value of templateDir
   * 
   * @param templateDir
   *          the templateDir to set
   */
  public void setTemplateDir(String templateDir) {
    this.templateDir = templateDir;
  }

  /**
   * Gets the cssDir value
   * 
   * @return the cssDir
   */
  public String getCssDir() {
    return cssDir;
  }

  /**
   * Sets the value of cssDir
   * 
   * @param cssDir
   *          the cssDir to set
   */
  public void setCssDir(String cssDir) {
    this.cssDir = cssDir;
  }

  /**
   * Gets the licenceDir value
   * 
   * @return the licenceDir
   */
  public String getLicenceDir() {
    return licenceDir;
  }

  /**
   * Sets the value of licenceDir
   * 
   * @param licenceDir
   *          the licenceDir to set
   */
  public void setLicenceDir(String licenceDir) {
    this.licenceDir = licenceDir;
  }

}
