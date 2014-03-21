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
package fr.cnes.sitools.portal.multidatasets.opensearch;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.portal.multidatasets.opensearch.dto.OpensearchDescriptionDTO;

/**
 * Representation used for mutlidataset opensearch RSS feeds
 * 
 * @author AKKA technologies
 * 
 * @version
 * 
 */
public final class MutliDsOsSuggestRepresentation extends OutputRepresentation {
  /**
   * A MultiDatasetsOpensearchResource
   */
  private MutliDsOsResource resource;

  /**
   * The default constructor
   * 
   * @param mediaType
   *          The mediaType
   */
  public MutliDsOsSuggestRepresentation(MediaType mediaType) {
    super(mediaType);
  }

  /**
   * A constructor with MultiDatasetsOpensearchResource parameter
   * 
   * @param mediaType
   *          The mediaType
   * @param res
   *          The MultiDatasetsOpensearchResource
   */
  public MutliDsOsSuggestRepresentation(MediaType mediaType, MutliDsOsResource res) {
    super(mediaType);
    this.resource = res;
  }

  /**
   * Writes the JSON
   * 
   * @param outputStream
   *          The outputStream
   * @throws IOException
   *           if there is any error in the method
   */
  @Override
  public void write(OutputStream outputStream) throws IOException {
    List<OpensearchDescriptionDTO> osList = resource.getOsList();

    String searchQuery = resource.getSearchQuery();
    // creates a JSONWriter to properly format JSON
    OutputStreamWriter out = new OutputStreamWriter(outputStream);
    JSONWriter writer = new JSONWriter(out);
    try {
      // creates new object
      writer.object();
      // creates new key "data"
      writer.key("data");
      // creates new array for key "data"
      writer.array();

      for (Iterator<OpensearchDescriptionDTO> iterator = osList.iterator(); iterator.hasNext();) {
        OpensearchDescriptionDTO opensearchDescriptionDTO = iterator.next();
        // get the suggest JSON string
        String suggest = resource.getOpensearchSuggest(searchQuery, opensearchDescriptionDTO.getIdOs(),
            super.getMediaType());
        
        if (suggest != null) {
          // parse the suggest JSON string
          JSONObject json = new JSONObject(suggest);
          // gets the array of the "data" node
          JSONArray array = json.getJSONArray("data");
          // loop through the array and add each element to the writer
          for (int i = 0; i < array.length(); i++) {
            JSONObject obj = (JSONObject) array.get(i);
            writer.value(obj);
          }
          // flush the stream to stream data
          out.flush();
        }
      }
      // close the array
      writer.endArray();
      // add the success node
      writer.key("success").value("true");
      // close the object
      writer.endObject();
      // flush the stream
      out.flush();
      out.close();
    }
    catch (JSONException e) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }

  }

}
