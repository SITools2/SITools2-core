/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.solr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.solr.model.DBConfigDTO;
import fr.cnes.sitools.solr.model.EntityDTO;
import fr.cnes.sitools.solr.model.RssXSLTDTO;
import fr.cnes.sitools.solr.model.SchemaConfigDTO;
import fr.cnes.sitools.solr.model.SolRConfigDTO;
import fr.cnes.sitools.util.FileCopyUtils;
import fr.cnes.sitools.util.RIAPUtils;

/*
 * Commands
 * 
 * The handler exposes all its API as http requests. The following are the
 * possible operations
 * 
 * full-import : Full Import operation can be started by hitting the URL
 * http://<host>:<port>/solr/dataimport?command=full-import
 * 
 * This operation will be started in a new thread and the status attribute in
 * the response should be shown busy now. o The operation may take some time
 * depending on size of dataset. When full-import command is executed, it stores
 * the start time of the operation in a file located at
 * conf/dataimport.properties o This stored timestamp is used when a
 * delta-import operation is executed. o Queries to Solr are not blocked during
 * full-imports. o It takes in extra parameters + entity : Name of an entity
 * directly under the <document> tag. Use this to execute one or more entities
 * selectively. Multiple 'entity' parameters can be passed on to run multiple
 * entities at once. If nothing is passed , all entities are executed + clean :
 * (default 'true'). Tells whether to clean up the index before the indexing is
 * started + commit: (default 'true'). Tells whether to commit after the
 * operation + optimize: (default 'true'). Tells whether to optimize after the
 * operation + debug : (default false). Runs in debug mode.It is used by the
 * interactive development mode (see here) # Please note that in debug mode,
 * documents are never committed automatically. If you want to run debug mode
 * and commit the results too, add 'commit=true' as a request parameter.
 * 
 * delta-import : For incremental imports and change detection run the command
 * `http://<host>:<port>/solr/dataimport?command=delta-import . It supports the
 * same clean, commit, optimize and debug parameters as full-import command.
 * 
 * status : To know the status of the current command , hit the URL
 * http://<host>:<port>/solr/dataimport .It gives an elaborate statistics on
 * no:of docs created, deleted, queries run, rows fetched , status etc
 * 
 * reload-config : If the data-config is changed and you wish to reload the file
 * without restarting Solr. run the command
 * http://<host>:<port>/solr/dataimport?command=reload-config
 * 
 * abort : Abort an ongoing operation by hitting the url
 * http://<host>:<port>/solr/dataimport?command=abort
 * 
 */

/**
 * Resource to manage SolR
 * 
 * @author AKKA Technologies
 * 
 */
public final class SolrActionResource extends AbstractSolrResource {

  @Override
  public void sitoolsDescribe() {
    setName("SolrActionResource");
    setDescription("Resource to handle all Solr requests");
    setNegotiated(false);
  }

  @Override
  @Post
  public Representation post(Representation entity, Variant variant) {
    Response response = null;
    do {
      // refresh index, do a full import
      if (this.getReference().toString().endsWith("refresh")) {
        org.restlet.Response resp = null;
        try {
          resp = doFullImport(osId);
          if (resp != null && !Status.isError(resp.getStatus().getCode())) {
            response = new Response(true, "GOOD_SOLR_REFRESH");
          }
          else {
            response = new Response(false, "BAD_SOLR_REFRESH : " + getCauseMsg(resp));
          }
        }
        finally {
          RIAPUtils.exhaust(resp);
        }

      }
      // create index, do a full import
      if (this.getReference().toString().endsWith("create")) {
        response = createIndex(entity);
      }
      // update index, do a full import
      if (this.getReference().toString().endsWith("update")) {
        // update function not already implemented we need to recreate the index
        response = createIndex(entity);
      }
      // unload the index
      if (this.getReference().toString().endsWith("delete")) {
        org.restlet.Response responseSolr = null;
        Request reqGET = new Request(Method.GET, "solr://default/admin/cores?action=UNLOAD&core=" + osId
            + "&deleteInstanceDir=true");
        try {
          responseSolr = getContext().getClientDispatcher().handle(reqGET);
          if (responseSolr == null || Status.isError(responseSolr.getStatus().getCode())) {
            response = new Response(false, "BAD_SOLR_DELETE");
            log.warning("BAD_SOLR_UNLOAD_CORE " + responseSolr);
          }
          else {
            response = new Response(true, "GOOD_SOLR_DELETE");
          }
        }
        finally {
          RIAPUtils.exhaust(responseSolr);
        }
      }
      // cancel the current operation
      if (this.getReference().toString().endsWith("cancel")) {
        Request reqGET = new Request(Method.GET, "solr://" + osId + "/dataimport?command=abort");
        org.restlet.Response responseSolr = null;
        try {
          responseSolr = getContext().getClientDispatcher().handle(reqGET);

          if (responseSolr == null || Status.isError(responseSolr.getStatus().getCode())) {
            response = new Response(false, "BAD_SOLR_CANCEL");
            log.warning("BAD_SOLR_CANCEL " + responseSolr);
          }
          else {
            response = new Response(true, "GOOD_SOLR_CANCEL");
          }
        }
        finally {
          RIAPUtils.exhaust(responseSolr);
        }
      }
    } while (false);

    return getRepresentation(response, variant);
  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Main resource of the Solr core activities on Sitools. Allows to create, update, delete solr indexes.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo parameterOsId = new ParameterInfo("osId", false, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the opensearch to deal with.");
    ParameterInfo parameterCreate = new ParameterInfo("create", false, "xs:string", ParameterStyle.TEMPLATE,
        "indicates that the Solr object must be created according to the representation sent.");
    ParameterInfo parameterUpdate = new ParameterInfo("update", false, "xs:string", ParameterStyle.TEMPLATE,
        "indicates that the Solr object must be modified according to the new representation sent.");
    ParameterInfo parameterRefresh = new ParameterInfo("refresh", false, "xs:string", ParameterStyle.TEMPLATE,
        "indicates that the Solr core must be refreshed according to the opensearch ID.");
    ParameterInfo parameterDelete = new ParameterInfo("delete", false, "xs:string", ParameterStyle.TEMPLATE,
        "indicates that the Solr object must be deleted according to the opensearch ID.");
    ParameterInfo parameterCancel = new ParameterInfo("cancel", false, "xs:string", ParameterStyle.TEMPLATE,
        "indicates that the Solr index creation must be cancelled.");
    ParameterInfo parameterRefreshXSLT = new ParameterInfo("refreshXSLT", false, "xs:string", ParameterStyle.TEMPLATE,
        "NOT USED YET.");
    info.getRequest().getParameters().add(parameterOsId);
    info.getRequest().getParameters().add(parameterCreate);
    info.getRequest().getParameters().add(parameterUpdate);
    info.getRequest().getParameters().add(parameterRefresh);
    info.getRequest().getParameters().add(parameterDelete);
    info.getRequest().getParameters().add(parameterCancel);
    info.getRequest().getParameters().add(parameterRefreshXSLT);
    this.addStandardResponseInfo(info);
  }

  /**
   * Create the index
   * 
   * @param entity
   *          the Representation containing the index description
   * @return a reponse to send
   */
  @SuppressWarnings("unchecked")
  private Response createIndex(Representation entity) {
    Response response = null;
    if (entity.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      try {
        ObjectRepresentation<SolRConfigDTO> obj = (ObjectRepresentation<SolRConfigDTO>) entity;

        SolRConfigDTO solR = obj.getObject();

        // first we need to make a copy of the template folder if it does not
        // exist
        String solrDirectory = application.getSolrDirectory();
        String solRIndexDir = solrDirectory + "/" + solR.getIndexName();
        copyTemplateFolder(solRIndexDir);

        // then we get the DTOs
        String solRIndexConfDir = solRIndexDir + "/conf";
        SchemaConfigDTO scConfig = solR.getSchemaConfigDTO();
        DBConfigDTO dataConfig = (DBConfigDTO) solR.getDataConfigDTO();

        RssXSLTDTO rssXSLT = solR.getRssXSLTDTO();

        // then we create the config files
        boolean success = this.createSchema(scConfig, solRIndexConfDir);
        if (success) {
          success = this.createDataConfigFile(dataConfig, solRIndexConfDir);
          if (success) {
            // then we create the xslt file to convert SolR xml returned file to
            // valid RSS
            success = this.createXSLTFile(rssXSLT, solRIndexConfDir);
            if (success) {
              // add a core
              org.restlet.Response resp = null;
              try {
                resp = createCore(solR.getIndexName());
                if (resp != null && !Status.isError(resp.getStatus().getCode())) {
                  response = new Response(true, "SOLR_INDEX_CREATED");
                }
                else {
                  // try to reload the index
                  resp = reloadCore(solR.getIndexName());
                  if (resp != null && !Status.isError(resp.getStatus().getCode())) {
                    response = new Response(true, "SOLR_INDEX_CREATED");
                  }
                  else {
                    response = new Response(false, "ERROR_CREATING_CORE : " + getCauseMsg(resp));
                  }
                }
              }
              finally {
                RIAPUtils.exhaust(resp);
              }
            }
            else {
              response = new Response(false, "ERROR_CREATING_TEMPLATE");
            }
          }
          else {
            response = new Response(false, "ERROR_CREATING_DB_DATA_CONFIG_XML");
          }
        }
        else {
          response = new Response(false, "ERROR_CREATING_SCHEMA_XML");
        }
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        response = new Response(false, "SERVER_ERROR");
      }

    }
    return response;
  }

  /**
   * Create a core
   * 
   * @param indexName
   *          the name of the core to create
   * @return a representation representing the result of the creation or null if there is a failure
   */
  private org.restlet.Response createCore(String indexName) {
    Request reqGET = new Request(Method.GET, "solr://default/admin/cores?action=CREATE&name=" + indexName
        + "&instanceDir=" + indexName + "&persist=true");
    org.restlet.Response response = null;

    response = getContext().getClientDispatcher().handle(reqGET);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      log.log(Level.WARNING, indexName + " : creating core failure" + response, response.getStatus().getThrowable());
      return response;
    }

    log.info(indexName + " : creating core");
    return response;

  }

  /**
   * reload a core
   * 
   * @param indexName
   *          the name of the core to create
   * @return a representation representing the result of the creation or null if there is a failure
   */
  private org.restlet.Response reloadCore(String indexName) {
    Request reqGET = new Request(Method.GET, "solr://default/admin/cores?action=RELOAD&core=" + indexName
        + "&instanceDir=" + indexName);
    org.restlet.Response response = null;

    response = getContext().getClientDispatcher().handle(reqGET);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      log.log(Level.WARNING, indexName + " : reload core failure" + response, response.getStatus().getThrowable());
      return response;
    }

    log.info(indexName + " : creating core");
    return response;

  }

  /**
   * Create the custom xslt file
   * 
   * @param rssXSLT
   *          the rssXSLTDTO representing the XSLT parameters
   * @param solRIndexConfDir
   *          the path to the
   * @return true if the creating is OK or false
   */
  private boolean createXSLTFile(final RssXSLTDTO rssXSLT, final String solRIndexConfDir) {
    try {

      Representation xsltFtl = new ClientResource(LocalReference.createClapReference(getClass().getPackage())
          + "/rss.ftl").get();

      // Wraps the bean with a FreeMarker representation
      TemplateRepresentation result = new TemplateRepresentation(xsltFtl, rssXSLT, MediaType.APPLICATION_ALL_XML);

      File xslt = new File(solRIndexConfDir + "/xslt", "rss.xsl");

      FileOutputStream fout = new FileOutputStream(xslt);

      result.write(fout);
      fout.close();
      return true;

    }
    catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Execute a full-import on the current solR core.
   * 
   * @param indexName
   *          solR core name
   * @return Representation
   */
  private org.restlet.Response doFullImport(String indexName) {
    org.restlet.Response response = null;
    try {
      Request reqGET = new Request(Method.GET, "solr://" + indexName
          + "/dataimport?command=full-import&config=data-config.xml");
      log.info("START IMPORTING");
      response = getContext().getClientDispatcher().handle(reqGET);
      log.info("IMPORTING SUCCESSFULL");

      if (response == null || Status.isError(response.getStatus().getCode())) {
        log.log(Level.WARNING, indexName + " : doFullInput core failure" + response, response.getStatus()
            .getThrowable());
        return response;
      }
      log.info(indexName + " : full import");

      return response;
    }
    catch (Exception e) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }

  }

  /**
   * Copy the content of the template folder.
   * 
   * @param solRIndexDir
   *          the directory path of the solr index
   */
  private void copyTemplateFolder(String solRIndexDir) {
    String solrTemplateDirectory = application.getSolrDirectoryTemplate();

    File file = new File(solRIndexDir);
    // on commence par creer le dossier
    file.mkdir();
    // si il n'existe pas, on copie le contenu du dossier template
    File fileTemplate = new File(solrTemplateDirectory);

    File[] fileList = fileTemplate.listFiles();
    for (int i = 0; i < fileList.length; i++) {
      FileCopyUtils.copyAFolderExclude(fileList[i], solRIndexDir, ".svn");
    }
  }

  /**
   * Create the schema.xml file from a freemarker template
   * 
   * @param schemaConfig
   *          : SchemaConfigDTO
   * @param solRIndexConfDir
   *          : String, the url of the conf directory
   * @return true if the creation was successful, false otherwise
   */
  private boolean createSchema(SchemaConfigDTO schemaConfig, String solRIndexConfDir) {
    try {

      Representation schemaConfigFtl = new ClientResource(LocalReference.createClapReference(getClass().getPackage())
          + "/schema.ftl").get();

      // Wraps the bean with a FreeMarker representation
      TemplateRepresentation result = new TemplateRepresentation(schemaConfigFtl, schemaConfig,
          MediaType.APPLICATION_ALL_XML);

      File fileSchema = new File(solRIndexConfDir, "schema.xml");

      FileOutputStream fout = new FileOutputStream(fileSchema);

      result.write(fout);
      fout.close();
      return true;

    }
    catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Create the db-data-config.xml file using the db-data-config.ftl freemaker template
   * 
   * 
   * @param dataConfig
   *          the dataConfigDTO model object
   * @param solRIndexConfDir
   *          the solrIndex configuration directory path
   * @return true if the creation if successful, false otherwise
   */
  private boolean createDataConfigFile(DBConfigDTO dataConfig, String solRIndexConfDir) {
    try {

      List<EntityDTO> entities = dataConfig.getEntities();
      for (Iterator<EntityDTO> iterator = entities.iterator(); iterator.hasNext();) {
        EntityDTO entityDTO = iterator.next();
        // entityDTO.setQuery(URLEncoder.encode(entityDTO.getQuery(), "UTF-8"));
        entityDTO.setQuery(entityDTO.getQuery().replace("'", "&apos;"));
        entityDTO.setQuery(entityDTO.getQuery().replace("\"", "&quot;"));
        entityDTO.setQuery(entityDTO.getQuery().replace("<", "&lt;"));
        entityDTO.setQuery(entityDTO.getQuery().replace(">", "&gt;"));

      }

      Representation dataConfigFtl = new ClientResource(LocalReference.createClapReference(getClass().getPackage())
          + "/db-data-config.ftl").get();

      // Wraps the bean with a FreeMarker representation
      TemplateRepresentation result = new TemplateRepresentation(dataConfigFtl, dataConfig,
          MediaType.APPLICATION_ALL_XML);

      File file = new File(solRIndexConfDir, "data-config.xml");

      FileOutputStream fout = new FileOutputStream(file);

      result.write(fout);
      fout.close();
      return true;
    }
    catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
    catch (IOException e) {
      e.printStackTrace();
      // TODO Auto-generated catch block
      return false;
    }
  }

  /**
   * Get the clause message of the throwable object in the given response
   * 
   * @param response
   *          the response
   * @return the cla
   */
  private String getCauseMsg(org.restlet.Response response) {
    String msg = null;
    if (response != null && response.getStatus() != null && response.getStatus().getThrowable() != null
        && response.getStatus().getThrowable().getCause() != null) {
      msg = response.getStatus().getThrowable().getCause().getLocalizedMessage();
    }
    return msg;
  }

}
