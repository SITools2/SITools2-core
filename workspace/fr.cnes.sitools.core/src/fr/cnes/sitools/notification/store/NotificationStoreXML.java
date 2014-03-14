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
package fr.cnes.sitools.notification.store;

import java.io.File;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.persistence.FilePersistenceStrategy;
import com.thoughtworks.xstream.persistence.XmlMap;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.notification.model.RestletObservable;
import fr.cnes.sitools.notification.model.RestletObserver;

/**
 * Implementation of NotificationStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public final class NotificationStoreXML implements NotificationStore {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "notifications";

  /** static logger for this store implementation */
  private static Logger log = Engine.getLogger(NotificationStoreXML.class.getName());

  /** Persistent list of projects */
  private XmlMap list = null;
  /** The Context */
  private Context context;

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Restlet Context
   */
  public NotificationStoreXML(File location, Context context) {
    super();
    this.context = context;
    init(location);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public NotificationStoreXML(Context context) {
    this.context = context;
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  private void init(File location) {
    log.info("Store location " + location.getAbsolutePath());

    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    boolean strict = !settings.isStartWithMigration();

    XStream xstream = XStreamFactory.getInstance().getXStream(MediaType.APPLICATION_XML, context, strict);
    xstream.autodetectAnnotations(true);
    xstream.alias("observable", RestletObservable.class);
    xstream.alias("observer", RestletObserver.class);

    FilePersistenceStrategy strategy = new FilePersistenceStrategy(location, xstream);
    list = new XmlMap(strategy);
  }

  @Override
  public void close() {
    // TODO
  }

  // ==============================================================
  // OBSERVABLES

  @Override
  public RestletObservable getObservable(String observableUUID) {
    RestletObservable result = (RestletObservable) list.get(observableUUID);
    if (result != null) {
      result.setStore(this);
    }
    return result;
  }

  @Override
  public void addObservable(String observableUUID, RestletObservable observable) {
    // Remplacer l'objet
    // TODO Vérifier que la XmlMap ne contienne pas de doublon
    list.put(observableUUID, observable);
  }

  @Override
  public void removeObservable(String observableUUID) {
    list.remove(observableUUID);
  }

}
