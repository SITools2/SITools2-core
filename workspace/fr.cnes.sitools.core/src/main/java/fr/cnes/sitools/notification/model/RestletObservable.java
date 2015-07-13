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
package fr.cnes.sitools.notification.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.mail.model.Mail;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.store.NotificationStore;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.util.TemplateUtils;
import fr.cnes.sitools.util.Util;

/**
 * Observable resource Un serveur héberge des resources pour lesquelles il peut offrir un service d'enregistrement et de
 * notification de changement d'état à des observers
 * 
 * TODO pour des raisons de scalabilité : - Ne pas monter en mémoire tous les observers mais les notifier un à un en
 * streaming
 * 
 * > Requête paginée de récupération des observers de la BD > Notification si > Si la notification échoue URI invalide
 * => suppression de l'observer. > Sur DELETE de l'observable =>
 * 
 * Ce mécanisme peut fonctionner de manière optimale pour des observers et observables sur le même serveur Lorsque les
 * observers sont sur un autre serveur il peut se produire des failles
 * 
 * => On ne peut faire totalement confiance à ce mécanisme pour résoudre les problèmes d'intégrité.
 * 
 * => Autre solution : Sur suppression d'un observable, attacher cette ancienne uri à un composant de resources
 * supprimées.
 * 
 * Pour gérer l'indisponibilité d'une resource ? >> en cas d'indisponibilité
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class RestletObservable implements Serializable {

  /** serialVersionUID */
  @XStreamOmitField
  private static final long serialVersionUID = 3374364584167082902L;

  /**
   * URI of the observable
   */
  private String uri;

  /**
   * Notification manager
   */
  @XStreamOmitField
  @JsonIgnore
  private volatile NotificationStore store = null;

  /** WARNING - FIXME NOT SCALABLE */
  private List<RestletObserver> observers = new ArrayList<RestletObserver>();

  /**
   * Default constructor
   */
  public RestletObservable() {
    super();
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  @JsonIgnore
  public NotificationStore getStore() {
    return store;
  }

  /**
   * Sets the value of store
   * 
   * @param store
   *          the store to set
   */
  @JsonIgnore
  public void setStore(NotificationStore store) {
    this.store = store;
  }

  /**
   * Gets the uri value
   * 
   * @return the uri
   */
  public String getUri() {
    return uri;
  }

  /**
   * Sets the value of uri
   * 
   * @param uri
   *          the uri to set
   */
  public void setUri(String uri) {
    this.uri = uri;
  }

  /**
   * Gets the observers value
   * 
   * @return the observers
   */
  public List<RestletObserver> getObservers() {
    return observers;
  }

  /**
   * Sets the value of observers
   * 
   * @param observers
   *          the observers to set
   */
  public void setObservers(List<RestletObserver> observers) {
    this.observers = observers;
  }

  /**
   * Observer notification
   * 
   * @param context
   *          restlet context
   * @param notification
   *          notification sent
   */
  public void notifyObservers(Context context, Notification notification) {
    // TODO Recuperation des observers en streaming sur la BD ...
    List<RestletObserver> obsers = this.observers; // engine.getStore().getObservers(this.getUri());
    if (obsers == null) {
      return;
    }

    if (notification != null) {
      for (RestletObserver observer : obsers) {
        if (observer.getUriToNotify() == null) {
          continue;
        }

        // TODO OPTIMISATION conserver les differentes representation de la
        // notification pour eviter de les reconstruire pour chaque observer
        if (observer.getUriToNotify().startsWith("http") || observer.getUriToNotify().startsWith("riap")) {
          Representation notificationRepresentation = NotificationManager.getRepresentation(notification,
              MediaType.valueOf(observer.getMediaTypeToNotify()), context);
          context.getAttributes().put("entity", notificationRepresentation);
          observer.update(this, context);
        }
        else if (observer.getUriToNotify().startsWith("mailto:")) {
          // ObjectRepresentation<Mail>
          // TODO faire de l'objet notification un mail

          String[] toList = new String[] {observer.getUriToNotify().substring(7)};
          if (toList == null || toList.length == 0) {
            // mail user null
            continue;
          }
          Mail mailToUser = new Mail();
          mailToUser.setToList(Arrays.asList(toList));

          // default mail - sitools applicative email.

          // TODO EVOL : email subject should be a parameter
          mailToUser.setSubject("Sitools notification");

          // default
          mailToUser.setBody(notification.getMessage());

          SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
          if (settings != null) {
            // use a freemarker template for email body with Mail object
            String templatePath = settings.getRootDirectory() + settings.getString(Consts.TEMPLATE_DIR)
                + "mail.notification.ftl";
            Map<String, Object> root = new HashMap<String, Object>();
            root.put("mail", mailToUser);
            root.put("notification", notification);

            TemplateUtils.describeObjectClassesForTemplate(templatePath, root);

            root.put("context", context);

            String body = TemplateUtils.toString(templatePath, root);
            if (Util.isNotEmpty(body)) {
              mailToUser.setBody(body);
            }
          }

          // serveur mail systeme
          // TODO mise au point de la notification par mail ...
          observer.setUriToNotify(RIAPUtils.getRiapBase() + settings.getString(Consts.APP_MAIL_ADMIN_URL));
          observer.setMethodToNotify("POST");
          Representation mailRepresentation = new ObjectRepresentation<Mail>(mailToUser);
          context.getAttributes().put("entity", mailRepresentation);
          observer.update(this, context);
        }

      }
    }
  }

  /**
   * Remove an observer
   * 
   * @param observerUUID
   *          the observer ID
   */
  public synchronized void removeObserver(String observerUUID) {
    if ((store != null) && (observers != null) && (observerUUID != null)) {
      boolean update = false;
      for (Iterator<RestletObserver> iterator = observers.iterator(); iterator.hasNext();) {
        RestletObserver obs = (RestletObserver) iterator.next();
        if (observerUUID.equals(obs.getUuid())) {
          iterator.remove();
          update = true;
        }
      }
      if (update) {
        store.addObservable(this.getUri(), this);
      }
    }
  }

  /**
   * add an observer
   * 
   * @param observer
   *          the restlet observer to add
   */
  public synchronized void addObserver(RestletObserver observer) {
    if ((observer != null) && (observers != null)) {
      for (Iterator<RestletObserver> iterator = observers.iterator(); iterator.hasNext();) {
        RestletObserver obs = (RestletObserver) iterator.next();
        if (observer.getUuid().equals(obs.getUuid())) {
          iterator.remove();
        }
      }
      this.observers.add(observer);
      if (store != null) {
        store.addObservable(this.getUri(), this);
      }
    }
  }

}
