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
package fr.cnes.sitools.units.dimension;

import java.io.IOException;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.units.dimension.model.SitoolsDimension;
import fr.cnes.sitools.units.dimension.model.SitoolsUnit;

/**
 * Base class for Dimension resources
 * @author m.marseille
 */
public abstract class AbstractDimensionResource extends SitoolsResource {
  
  /** Application linked to the resource */
  private AbstractDimensionApplication dimApp;
  
  /** Storage for resources */
  private DimensionStoreInterface store;
  
  
  @Override
  public void doInit() {
    super.doInit();
    
    // Declares the variants supported
    addVariant(new Variant(MediaType.APPLICATION_XML));
    addVariant(new Variant(MediaType.APPLICATION_JSON));
    addVariant(new Variant(MediaType.APPLICATION_JAVA));
    
    dimApp = (AbstractDimensionApplication) getApplication();
    store = dimApp.getStore();
  }

  /**
   * Gets the dimApp value
   * @return the dimApp
   */
  public final AbstractDimensionApplication getDimensionApplication() {
    return dimApp;
  }

  /**
   * Gets the store value
   * @return the store
   */
  public final DimensionStoreInterface getStore() {
    return store;
  }
  
  /**
   * Gets representation according to the specified MediaType.
   * 
   * @param response
   *          : The response to get the representation from
   * @param media
   *          : The MediaType asked
   * @return The Representation of the response with the selected mediaType
   */
  public final Representation getRepresentation(Response response, MediaType media) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    xstream.autodetectAnnotations(false);
    
    xstream.alias("dimension", SitoolsDimension.class);
    xstream.alias("response", Response.class);
    xstream.alias("item", Object.class, SitoolsDimension.class);
    xstream.alias("dimension", Object.class, SitoolsDimension.class);
    xstream.alias("unit", SitoolsUnit.class);
    
    xstream.aliasField("dimension", Response.class, "item");
    
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");
    
    ClassAliasingMapper mapper = new ClassAliasingMapper(xstream.getMapper());
    mapper.addClassAlias("unitConverter", String.class);
    xstream.registerLocalConverter(SitoolsDimension.class, "unitConverters", new CollectionConverter(mapper));
//    mapper = new ClassAliasingMapper(xstream.getMapper());
//    mapper.addClassAlias("unitName", String.class);
//    xstream.registerLocalConverter(SitoolsDimension.class, "unitNames", new CollectionConverter(mapper));

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Gets SitoolsDimension object from Representation
   * 
   * @param representation
   *          of a SitoolsDimension
   * @return SitoolsDimension
   * @throws IOException
   *           if there is an error while deserializing Java Object
   */
  protected final SitoolsDimension getObject(Representation representation) throws IOException {
    SitoolsDimension input = null;
    if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      @SuppressWarnings("unchecked")
      ObjectRepresentation<SitoolsDimension> obj = (ObjectRepresentation<SitoolsDimension>) representation;
      input = obj.getObject();
    }
    if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      JacksonRepresentation<SitoolsDimension> json = new JacksonRepresentation<SitoolsDimension>(representation,
          SitoolsDimension.class);
      input = json.getObject();
    }
    return input;
  }

}
