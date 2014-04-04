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
package fr.cnes.sitools.datasource.jdbc;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.datasource.jdbc.model.JDBCDataSource;

/**
 * Abstract resource for DataSource Objects management
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractDataSourceResource extends SitoolsResource {
  
  /** Parent application */
  private JDBCDataSourceAdministration application = null;
  
  /** Store */
  private SitoolsStore<JDBCDataSource> store = null;
  
  /** DataSource identifier parameter */
  private String datasourceId = null;

  @Override
  protected void doInit() {
    super.doInit();
    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    getVariants().add(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (JDBCDataSourceAdministration) getApplication();
    store = application.getStore();

    datasourceId = (String) this.getRequest().getAttributes().get("datasourceId");
  }

  /**
   * Get the representation
   * @param response the response to use
   * @param media the media to use
   * @return Representation
   */
  public final Representation getRepresentation(Response response, MediaType media) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("jdbcdatasource", JDBCDataSource.class);
    
    // MASQUER LES PASSWORDS EN SORTIE
    xstream.omitField(JDBCDataSource.class, "userPassword");
    
    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Get object from representation
   * @param representation the representation to use
   * @return JDBCDataSource
   */
  public final JDBCDataSource getObject(Representation representation) {
    JDBCDataSource object = null;

    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the bean
      // Default parsing :
      // object = new XstreamRepresentation<JDBCDataSource>(representation).getObject();
      XstreamRepresentation<JDBCDataSource> repXML = new XstreamRepresentation<JDBCDataSource>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      xstream.alias("jdbcdatasource", JDBCDataSource.class);
      
      repXML.setXstream(xstream);
      object = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      object = new JacksonRepresentation<JDBCDataSource>(representation, JDBCDataSource.class).getObject();
    }

    return object;
  }

  /**
   * Gets the application value
   * @return the application
   */
  public final JDBCDataSourceAdministration getJDBCDataSourceAdministration() {
    return application;
  }

  /**
   * Gets the store value
   * @return the store
   */
  public final SitoolsStore<JDBCDataSource> getStore() {
    return store;
  }

  /**
   * Gets the datasourceId value
   * @return the datasourceId
   */
  public final String getDatasourceId() {
    return datasourceId;
  }

}
