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
package fr.cnes.sitools.notification;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.store.NotificationStore;

/**
 * Notification application
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class NotificationApplication extends SitoolsApplication {

  /**
   * Store
   */
  private NotificationStore store = null;

  /**
   * Single object in the store
   */
  private NotificationManager engine = null;
  
  /** TRIGGER RESOURCES */
  private Map<String, Class<?>> triggerTable = new ConcurrentHashMap<String, Class<?>>();

  /**
   * Constructor
   * 
   * @param context
   *          Restlet Host context
   */
  public NotificationApplication(Context context) {
    super(context);
    this.store = (NotificationStore) context.getAttributes().get(ContextAttributes.APP_STORE);
    this.engine = getSettings().getNotificationManager();
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.SYSTEM);
    setName("NotificationApplication");
    setDescription("Application for registering observables and observers and for notifying observers when observable changes.");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    // POST observable
    router.attach("/observables", ObservableResource.class);
    // DELETE observable
    router.attach("/observables/{observableUUID}", ObservableResource.class);

    // notify all observers
    router.attach("/observables/{observableUUID}/notify", NotificationResource.class);

    // POST observer
    router.attach("/observables/{observableUUID}/observers", ObserverResource.class);
    // DELETE observer
    router.attach("/observables/{observableUUID}/observers/{observerUUID}", ObserverResource.class);

    // notify one observer
    router.attach("/observables/{observableUUID}/observers/{observerUUID}/notify", NotificationResource.class);

    
    return router;
  }

  /**
   * Gets the engine value
   * 
   * @return the engine
   */
  public NotificationManager getEngine() {
    return engine;
  }

  /**
   * Sets the value of engine
   * 
   * @param engine
   *          the engine to set
   */
  public void setEngine(NotificationManager engine) {
    this.engine = engine;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public NotificationStore getStore() {
    return store;
  }


  /**
   * Attach trigger to a class
   * @param eventName the name of the event to trigger
   * @param targetClass the class targeted
   */
  public void attachTrigger(String eventName, Class<?> targetClass) {
    Class<?> existingTargetClass = triggerTable.get(eventName);
    if (existingTargetClass == null) {
      ((Router) getInboundRoot()).attach("/triggers/" + eventName, targetClass);
      triggerTable.put(eventName, targetClass);
    }
    else {
      getLogger().warning(
          "Target class " + existingTargetClass.getName() + " is already registered to " + eventName + " event.");
    }
  }

  /**
   * Detach trigger 
   * @param eventName event name to detach
   */
  public void detachTrigger(String eventName) {
    Class<?> targetClass = triggerTable.get(eventName);
    if (targetClass != null) {
      ((Router) getInboundRoot()).detach(targetClass);
      triggerTable.put(eventName, null);
    }
  }

  /** register a new event if it does not ever registered 
   * @param eventName the event name to register 
   */
  public void registerEvent(String eventName) {
    if (triggerTable.get(eventName) == null) {
      triggerTable.put(eventName, null);
    }
  }

  /**
   * Unregister an event
   * @param eventName the name of the event to unregister
   */
  public void unregisterEvent(String eventName) {
    triggerTable.remove(eventName);
  }

  /**
   * Get the table of triggers
   * @return the table of triggers
   */
  public Map<String, Class<?>> getTriggerTable() {
    return triggerTable;
  }
  
  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Notification application for SITools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}
