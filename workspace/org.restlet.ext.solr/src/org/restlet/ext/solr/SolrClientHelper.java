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
package org.restlet.ext.solr;

import java.io.File;
import java.net.URI;
import java.util.logging.Level;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.core.ConfigSolr;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.SolrQueryResponse;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.engine.ClientHelper;
import org.restlet.ext.solr.internal.SolrRepresentation;
import org.restlet.ext.solr.internal.SolrRestletQueryRequest;

/**
 * Solr client connector.
 * 
 * There are two ways of initializing the helped core container. <br>
 * First one : <br>
 * 
 * <pre>
 * Client solrClient = component.getClients().add(SolrClientHelper.SOLR_PROTOCOL);
 * solrClient.getContext().getAttributes().put("CoreContainer", new CoreContainer(...));
 * </pre>
 * 
 * <br>
 * Second one : <br>
 * 
 * <pre>
 * Client solrClient = component.getClients().add(SolrClientHelper.SOLR_PROTOCOL);
 * solrClient.getContext().getParameters().add(&quot;directory&quot;, &quot;...&quot;);
 * solrClient.getContext().getParameters().add(&quot;configFile&quot;, &quot;...&quot;);
 * </pre>
 * 
 * <br>
 * The helper handles "solr://" requests. There is one additional parameter : "DefaultCore" which gives default core for
 * "solr:///..." requests.
 * 
 * @author Remi Dewitte <remi@gide.net>
 */
public class SolrClientHelper extends ClientHelper {

  public static Protocol SOLR_PROTOCOL = new Protocol("solr", "Solr", "Solr indexer helper", Protocol.UNKNOWN_PORT);

  /** The core Solr container. */
  protected CoreContainer coreContainer;

  /**
   * Constructor.
   * 
   * @param client
   *          The client connector.
   */
  public SolrClientHelper(Client client) {
    super(client);
    getProtocols().add(SOLR_PROTOCOL);
  }

  @Override
  public void handle(Request request, Response response) {
    super.handle(request, response);

    Reference resRef = request.getResourceRef();
    String path = resRef.getPath();

    if (path != null) {
      path = resRef.getPath(true);
    }

    String coreName = request.getResourceRef().getHostDomain();

    if (coreName == null || "".equals(coreName)) {
      coreName = getContext().getParameters().getFirstValue("DefaultCore");
    }

    SolrCore core = coreContainer.getCore(coreName);

    if (core == null) {
      response.setStatus(Status.SERVER_ERROR_INTERNAL, "No such core: " + coreName);
      return;
    }

    // Extract the handler from the path or params
    SolrRequestHandler handler = core.getRequestHandler(path);

    if (handler == null) {
      if ("/select".equals(path) || "/select/".equalsIgnoreCase(path)) {
        String qt = request.getResourceRef().getQueryAsForm().getFirstValue(CommonParams.QT);
        handler = core.getRequestHandler(qt);
        if (handler == null) {
          response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "unknown handler: " + qt);
          return;
        }
      }
      // Perhaps the path is to manage the cores
      if (handler == null && coreContainer != null && path.equals(coreContainer.getAdminPath())) {
        handler = coreContainer.getMultiCoreHandler();
      }
    }

    if (handler == null) {
      core.close();
      response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "unknown handler: " + path);
      return;
    }

    try {
      SolrQueryRequest solrReq = new SolrRestletQueryRequest(request, core);

      SolrQueryResponse solrResp = new SolrQueryResponse();
      core.execute(handler, solrReq, solrResp);

      if (solrResp.getException() != null) {
        response.setStatus(Status.SERVER_ERROR_INTERNAL, solrResp.getException());
      }
      else {
        response.setEntity(new SolrRepresentation(solrReq, solrResp, core));
        response.setStatus(Status.SUCCESS_OK);
      }
    }
    catch (Exception e) {
      getLogger().log(Level.WARNING, "Unable to evaluate " + resRef.toString(), e);
      response.setStatus(Status.SERVER_ERROR_INTERNAL, e);
    }
    finally {
      core.close();
    }
  }

  @Override
  public void start() {
    try {
      coreContainer = (CoreContainer) getHelped().getContext().getAttributes().get("CoreContainer");

      if (coreContainer == null) {
        String directory = getHelped().getContext().getParameters().getFirstValue("directory");
        String configFile = getHelped().getContext().getParameters().getFirstValue("configFile");

        if (directory != null && configFile != null) {

          SolrResourceLoader resourceLoader = new SolrResourceLoader(directory);
          File config = new File(configFile);
          if (!config.exists()) {
            config = new File(new URI(configFile));
          }
          ConfigSolr configSolr = ConfigSolr.fromFile(resourceLoader, config);
          coreContainer = new CoreContainer(resourceLoader, configSolr);
          coreContainer.load();

        }
      }

      if (coreContainer == null) {
        throw new RuntimeException("Could not initialize core container");
      }
    }
    catch (Exception e) {
      throw new RuntimeException("Could not initialize core container", e);
    }
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }

}