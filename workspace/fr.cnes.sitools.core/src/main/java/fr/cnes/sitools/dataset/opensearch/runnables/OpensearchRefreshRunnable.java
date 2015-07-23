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
package fr.cnes.sitools.dataset.opensearch.runnables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.opensearch.OpenSearchApplication;
import fr.cnes.sitools.dataset.opensearch.OpenSearchStoreInterface;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * OpensearchRefreshRunnable, runnable class for refreshing indexes
 * 
 * 
 * @author m.gond (AKKA Technologies)
 */
public class OpensearchRefreshRunnable extends OpensearchRunnable {

  /**
   * Default constructor
   * 
   * @param os
   *          the opensearch
   * @param store
   *          the store
   * @param solrUrl
   *          the url of solr
   * @param application
   *          the OpensearchApplication
   * @param context
   *          the context
   */
  public OpensearchRefreshRunnable(Opensearch os, OpenSearchStoreInterface store, String solrUrl, Context context,
      OpenSearchApplication application) {
    this.os = os;
    this.store = store;
    this.solrUrl = solrUrl;
    this.context = context;
    this.application = application;
  }

  public void run() {

    Request reqPOST = new Request(Method.POST, RIAPUtils.getRiapBase() + solrUrl + "/" + os.getId() + "/refresh");

    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_ALL_XML));
    reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    Restlet client = this.context.getClientDispatcher();
    org.restlet.Response r = null;
    try {
      r = this.context.getClientDispatcher().handle(reqPOST);

      if (r == null || Status.isError(r.getStatus().getCode())) {
        os.setStatus("INACTIVE");
        store.update(os);
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
      }

      @SuppressWarnings("unchecked")
      XstreamRepresentation<Response> repr = (XstreamRepresentation<Response>) r.getEntity();
      Response resp = (Response) repr.getObject();

      if (resp.getSuccess()) {
        // check if it was canceled or not
        if (this.application.isCancelled()) {
          this.os.setStatus("INACTIVE");
        }
        else {
          this.os.setStatus("ACTIVE");
          os.setLastImportDate(new Date(new GregorianCalendar().getTime().getTime()));
        }
        store.update(os);
      }
      else {
        this.os.setStatus("INACTIVE");
        this.os.setErrorMsg(resp.getMessage());
        store.update(os);
      }
    }
    catch (IOException e) {
      context.getLogger().severe(e.getMessage());
    }
    finally {
      RIAPUtils.exhaust(r);
    }
  }
}
