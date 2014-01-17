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
package fr.cnes.sitools.registry;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Router;
import org.restlet.routing.VirtualHost;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.registry.model.AppRegistry;

/**
 * ApplicationManager
 * 
 * Utilité 1 : Pouvoir appuyer des informations sur les applications en tant que resource => couplage faible - les
 * droits - la disponibilité - la configuration / analyse des logs
 * 
 * Utilité 2 : Pouvoir référencer les applications depuis les applications clientes (client-admin/client-user) =>
 * couplage faible client/serveur. Si les urls serveurs changent, le client utilise les urls obtenues via le
 * référentiel.
 * 
 * Contenu du référentiel : - des applications "singleton" pouvant être identifiées par leur nom de classe. Une seule
 * application de ce type existe dans l'application.
 * 
 * - des applications "multi-instances" plusieurs instances pour la même classe d'application.
 * 
 * Besoin organiser les ressources du référentiel par catégorie :
 * 
 * A voir ... - le type/catégorie de resource : urn:type:Application:name:<application.getName()>
 * urn:type:Dictionary:name:<dictionary.getName()>
 * 
 * - l'identifiant d'une resource : (=> indépendant de l'url d'accès) urn:uuid:<identifiant de la resource>
 * 
 * Utilité 3 : Quelles sont les applications "core" à lancer au démarrage et avec quels paramètres... Définition d'un
 * serveur + association serveur <-> application Paramètres du serveur, Paramètres de chaque application (les stores)
 * 
 * >>> voir avec Spring ou OSGi.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class AppRegistryApplication extends SitoolsApplication {

  /**
   * Parent Host
   */
  private VirtualHost host = null;

  /**
   * Store
   */
  private SitoolsStore<AppRegistry> store = null;

  /**
   * Application instances (not stored)
   */
  private Map<String, SitoolsApplication> applications = new ConcurrentHashMap<String, SitoolsApplication>();

  /**
   * Application instances (not stored)
   */
  private Map<String, Restlet> secureApplications = new ConcurrentHashMap<String, Restlet>();

  /**
   * Single object in the store
   */
  private AppRegistry resourceManager = null;

  /**
   * Constructor
   * 
   * @param context
   *          Restlet Host context
   */
  @SuppressWarnings("unchecked")
  public AppRegistryApplication(Context context) {
    super(context);
    this.store = (SitoolsStore<AppRegistry>) context.getAttributes().get(ContextAttributes.APP_STORE);

    if ((store.getList() != null) && (store.getList().size() >= 1)) {
      resourceManager = store.getList().get(0);
    }
    else {
      resourceManager = new AppRegistry();
      store.create(resourceManager);
    }
  }

  @Override
  public void sitoolsDescribe() {
    setName("AppRegistryApplication");
    setCategory(Category.SYSTEM);
    setDescription("Application for registering / exposing all applications \n"
        + "Be carrefull with this application, the administrator must have all authorizations\n"
        + "Do not stop the application, Do not let public user have PUT/POST/DELETE authorizations");
    // on attend que l'objet resourceManager soit créé.
    this.setAutoRegistration(false);

    this.setId(this.wrapToResource().getId());
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    router.attachDefault(AppRegistryResource.class);
    router.attach("/{resourceId}", AppRegistryResource.class);
    router.attach("/{resourceId}/start", AppRegistryResource.class);
    router.attach("/{resourceId}/stop", AppRegistryResource.class);
    router.attach("/{resourceId}/restart", AppRegistryResource.class);

    return router;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public SitoolsStore<AppRegistry> getStore() {
    return store;
  }

  /**
   * Gets the resourceManager value
   * 
   * @return the resourceManager
   */
  public AppRegistry getResourceManager() {
    return resourceManager;
  }

  /**
   * Sets the value of resourceManager
   * 
   * @param resourceManager
   *          the resourceManager to set
   */
  public void setResourceManager(AppRegistry resourceManager) {
    this.resourceManager = resourceManager;
  }

  /**
   * Gets the applications value
   * 
   * @return the applications
   */
  public Map<String, SitoolsApplication> getApplications() {
    return applications;
  }

  /**
   * Adds an application into the registry
   * 
   * @param application
   *          a SitoolsApplication
   */
  private void addApplication(SitoolsApplication application) {
    getLogger().info(this.getClass().getName() + ".addApplication(" + application.getId() + ")");
    applications.put(application.getId(), application);
  }

  /**
   * Gets an application by id
   * 
   * @param appId
   *          application unique identifier
   * @return SitoolsApplication
   */
  public SitoolsApplication getApplication(String appId) {
    return applications.get(appId);
    // applications.put(application.getId(), application);
  }

  /**
   * Attach application
   * 
   * @param app
   *          the application to attach
   */
  public void attachApplication(SitoolsApplication app) {
    Restlet secureApp = app.getSecure();
    secureApplications.put(app.getId(), secureApp);
    host.attach(app.getAttachementRef(), secureApp, Router.MODE_BEST_MATCH);
    try {
      secureApp.start();
      if (secureApp != app) {
        app.start();
      }
    }
    catch (Exception e) {
      getLogger().warning("Application could not be started");
      e.printStackTrace();
    }
    addApplication(app);

    // REGISTER MYSELF BUT REMOVE ALL PREVIOUSlY REGISTERED ONES
    if (app == this) {
      Resource appRegisteryAppWrapedToResource = this.wrapToResource();
      List<Resource> resources = resourceManager.getResources();
      for (Iterator<Resource> iterator = resources.iterator(); iterator.hasNext();) {
        Resource resource = (Resource) iterator.next();
        if ((resource.getId() != null) && (resource.getId().equals(appRegisteryAppWrapedToResource.getId()))) {
          // Mise a jour d'un element deja existant
          iterator.remove();
        }
        // Ou ajout d'un nouvel element avec son identifiant
      }
      resourceManager.getResources().add(appRegisteryAppWrapedToResource);
      resourceManager.setLastUpdate(new Date().toString());
      getStore().update(resourceManager);
    }
  }

  /**
   * Attach application
   * 
   * @param app
   *          the application to attach
   * @param start
   *          if the application needs to be started or not
   * @throws Exception
   *           if there is an Exception while starting the application
   */
  public void attachApplication(SitoolsApplication app, boolean start) throws Exception {
    Restlet secureApp = app.getSecure();
    secureApplications.put(app.getId(), secureApp);
    host.attach(app.getAttachementRef(), secureApp, Router.MODE_BEST_MATCH);
    if (start) {
      secureApp.start();
      if (secureApp != app) {
        app.start();
      }
    }
    addApplication(app);
  }

  /**
   * Detach application
   * 
   * @param app
   *          the application to detach
   */
  public void detachApplication(SitoolsApplication app) {
    if (app == null) {
      return;
    }

    Restlet secureApp = secureApplications.get(app.getId());
    if (secureApp == null) {
      getLogger().fine("Try to detach an application not ever attached " + app.getName());
      return;
    }

    host.detach(secureApp);

    secureApplications.remove(app.getId());

    try {
      secureApp.stop();
      if (secureApp != app) {
        app.stop();
      }
    }
    catch (Exception e1) {
      getLogger().warning("Secure Application could not be stopped");
      e1.printStackTrace();
    }

    // TODO EVOL Release 3 ? Attacher une resource de redirection vers la page
    // "Application indisponible" à la place ?
    // host.attach(appReference, app.getAppIndisponible());
  }

  /**
   * reattach all started applications (case of default authorization modification for example)
   */
  public void reattachAllApplications() {
    for (SitoolsApplication application : applications.values()) {
      if (application == this) {
        reattachApplication(application, false);
      }
      if (application.isStarted()) {
        reattachApplication(application);
      }
    }
  }

  /**
   * reattach all started applications (case of default authorization modification for example)
   * 
   * @param basedOnDefault
   *          set to true to use default settings
   */
  public void reattachAllApplications(boolean basedOnDefault) {
    for (SitoolsApplication application : applications.values()) {
      if (application == this) {
        reattachApplication(application, false);
      }
      if (application.isStarted()) {
        reattachApplication(application);
      }
    }
  }

  /**
   * To detach and attach if application was active using new defined autorizations.
   * 
   * @param app
   *          the sitools application to re-attach
   */
  public void reattachApplication(SitoolsApplication app) {
    if (app == null) {
      getLogger().warning("Can not reattachApplication(null)");
      return;
    }

    Restlet currentSecureApp = secureApplications.get(app.getId());
    boolean restart = false;

    if (currentSecureApp != null) {

      host.detach(currentSecureApp);
      secureApplications.remove(app.getId());

      if (currentSecureApp.isStarted()) {
        restart = true;
      }

      try {
        currentSecureApp.stop();
        if (currentSecureApp != app) {
          app.stop();
        }
      }
      catch (Exception e1) {
        getLogger().warning("old secure Application could not be stopped");
        e1.printStackTrace();
      }

    }

    if (restart) {
      Restlet newSecureApp = app.getSecure();
      secureApplications.put(app.getId(), newSecureApp);
      host.attach(app.getAttachementRef(), newSecureApp);
      try {
        if (restart) {
          newSecureApp.start();
          if (newSecureApp != app) {
            app.start();
          }
        }
      }
      catch (Exception e) {
        getLogger().warning("new application could not be started");
        e.printStackTrace();
      }
    }

    // Dans tous les cas
    addApplication(app);
  }

  /**
   * To detach and attach if application was active using new defined autorizations.
   * 
   * @param app
   *          the sitools application to re-attach
   * @param stop
   *          false to do not stop the application if it is active
   */
  public void reattachApplication(SitoolsApplication app, boolean stop) {
    if (app == null) {
      getLogger().warning("Can not reattachApplication(null)");
      return;
    }

    Restlet currentSecureApp = secureApplications.get(app.getId());
    boolean restart = false;

    if (currentSecureApp != null) {

      host.detach(currentSecureApp);
      secureApplications.remove(app.getId());

      if (currentSecureApp.isStarted()) {
        restart = true;
      }

      if (stop) {
        try {
          currentSecureApp.stop();
          if (currentSecureApp != app) {
            app.stop();
          }
        }
        catch (Exception e1) {
          getLogger().warning("old secure Application could not be stopped");
          e1.printStackTrace();
        }
      }

    }

    if (restart && !stop) {
      Restlet newSecureApp = app.getSecure();
      secureApplications.put(app.getId(), newSecureApp);
      host.attach(app.getAttachementRef(), newSecureApp);
      try {
        if (restart) {
          newSecureApp.start();
          if (newSecureApp != app) {
            app.start();
          }
        }
      }
      catch (Exception e) {
        getLogger().warning("new application could not be started");
        e.printStackTrace();
      }
    }

    // Dans tous les cas
    addApplication(app);
  }

  /**
   * Gets the host value
   * 
   * @return the host
   */
  public VirtualHost getHost() {
    return host;
  }

  /**
   * Sets the value of host
   * 
   * @param host
   *          the host to set
   */
  public void setHost(VirtualHost host) {
    this.host = host;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo(
        "Registry application to handle all other applications in Sitools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}
