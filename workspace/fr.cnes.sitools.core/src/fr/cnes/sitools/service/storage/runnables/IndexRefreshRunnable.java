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
package fr.cnes.sitools.service.storage.runnables;

import java.util.ArrayList;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.service.storage.DataStorageStore;
import fr.cnes.sitools.service.storage.StorageAdministration;
import fr.cnes.sitools.service.storage.model.StorageDirectory;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * IndexRefreshRunnable, runnable class for refreshing indexes
 * 
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class IndexRefreshRunnable extends StorageRunnable {

  /**
   * Default constructor
   * 
   * @param sd
   *          the storage
   * @param store
   *          the store
   * @param solrUrl
   *          the url of solr
   * @param application
   *          the Storage Application
   * @param context
   *          the context
   */
  public IndexRefreshRunnable(StorageDirectory sd, DataStorageStore store, String solrUrl, Context context,
      StorageAdministration application) {
    this.sd = sd;
    this.store = store;
    this.solrUrl = solrUrl;
    this.context = context;
    this.application = application;
  }

  @Override
  public void run() {
    Request reqPOST = new Request(Method.POST, RIAPUtils.getRiapBase() + solrUrl + "/" + sd.getId() + "/refresh");

    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_ALL_XML));
    reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    Client client = this.context.getClientDispatcher();
    client.getConnectTimeout();
    org.restlet.Response r = null;
    try {
      r = this.context.getClientDispatcher().handle(reqPOST);

      if (r == null || Status.isError(r.getStatus().getCode())) {
        sd.setStatus("INACTIVE");
        store.update(sd);
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
      }

      @SuppressWarnings("unchecked")
      XstreamRepresentation<Response> repr = (XstreamRepresentation<Response>) r.getEntity();
      Response resp = (Response) repr.getObject();

      if (resp.getSuccess()) {
//        // check if it was canceled or not
//        if (this.application.isCancelled()) {
//          this.sd.setStatus("INACTIVE");
//        }
//        else {
//          this.sd.setStatus("ACTIVE");
//          sd.setLastImportDate(new Date(new GregorianCalendar().getTime().getTime()));
//        }
//        store.update(sd);
        this.sd.setIndexed(true);
        store.update(sd);
        
      }
      else {
//        this.sd.setStatus("INACTIVE");
//        this.sd.setErrorMsg(resp.getMessage());
//        store.update(sd);
        this.sd.setIndexed(false);
        store.update(sd);
        
      }
    }
    finally {
      RIAPUtils.exhaust(r);
    }
  }
}
