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
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;

import fr.cnes.sitools.portal.multidatasets.opensearch.dto.OpensearchDescriptionDTO;

/**
 * Representation used for mutlidataset opensearch RSS feeds
 * 
 * @author AKKA technologies
 * 
 * @version
 * 
 */
public final class MultiDsOsSuggestRepresentation extends OutputRepresentation {
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
  public MultiDsOsSuggestRepresentation(MediaType mediaType) {
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
  public MultiDsOsSuggestRepresentation(MediaType mediaType, MutliDsOsResource res) {
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

    JsonFactory jfactory = new JsonFactory();
    JsonGenerator jGenerator = jfactory.createJsonGenerator(out);

    // creates new object
    jGenerator.writeStartObject(); // {
    // creates new key "data"
    jGenerator.writeArrayFieldStart("data");

    for (Iterator<OpensearchDescriptionDTO> iterator = osList.iterator(); iterator.hasNext();) {
      OpensearchDescriptionDTO opensearchDescriptionDTO = iterator.next();
      // get the suggest JSON string
      String suggest = resource.getOpensearchSuggest(searchQuery, opensearchDescriptionDTO.getIdOs(),
          super.getMediaType());

      if (suggest != null) {
        // read the suggest JSON 
        ObjectMapper mapper = new ObjectMapper();
        // (note: can also use more specific type, like ArrayNode or ObjectNode!)
        JsonNode rootNode = mapper.readValue(suggest, JsonNode.class); // src can be a File, URL,
                                                                       // InputStream etc
        JsonNode data = rootNode.get("data");
        for (JsonNode jsonNode : data) {
          jGenerator.writeStartObject();
          Iterator<Entry<String, JsonNode>> values = jsonNode.fields();
          while (values.hasNext()) {
            Entry<String, JsonNode> value = values.next();
            jGenerator.writeStringField(value.getKey(), value.getValue().textValue());
          }
          jGenerator.writeEndObject();
        }
        // flush the stream to stream data
        out.flush();
      }
    }
    // close the array
    jGenerator.writeEndArray();
    // add the success node
    jGenerator.writeStringField("success", "true");
    // close the object
    jGenerator.writeEndObject();
    // flush the stream
    out.flush();
    jGenerator.close();
    out.close();
  }

}
