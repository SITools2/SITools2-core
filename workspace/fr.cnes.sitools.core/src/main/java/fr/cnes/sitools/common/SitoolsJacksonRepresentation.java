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
package fr.cnes.sitools.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.logging.Level;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

/**
 * Representation based on the JACKSON library. 
 * It can serialize and unserialize automatically to JSON.
 * 
 * @see <a href="http://jackson.codehaus.org/">Jackson project</a>
 * @author Jerome Louvel
 * @param <T>
 *          The type to wrap.
 */
public final class SitoolsJacksonRepresentation<T> extends WriterRepresentation {

  /** The (parsed) object to format. */
  private T object;

  /** The object class to instantiate. */
  private Class<T> objectClass;

  /** The JSON representation to parse. */
  private Representation jsonRepresentation;

  /** The modifiable Jackson object mapper. */
  private ObjectMapper objectMapper;

  /**
   * Constructor.
   * 
   * @param mediaType
   *          The target media type.
   * @param object
   *          The object to format.
   */
  @SuppressWarnings("unchecked")
  public SitoolsJacksonRepresentation(MediaType mediaType, T object) {
    super(mediaType);
    this.object = object;
    this.objectClass = (Class<T>) ((object == null) ? null : object.getClass());
    this.jsonRepresentation = null;
    this.objectMapper = null;
  }

  /**
   * Constructor.
   * 
   * @param representation
   *          The representation to parse.
   * @param objectClass
   *          class of the object to parse
   */
  public SitoolsJacksonRepresentation(Representation representation, Class<T> objectClass) {
    super(representation.getMediaType());
    this.object = null;
    this.objectClass = objectClass;
    this.jsonRepresentation = representation;
    this.objectMapper = null;
  }

  /**
   * Constructor.
   * 
   * @param object
   *          The object to format.
   */
  public SitoolsJacksonRepresentation(T object) {
    this(MediaType.APPLICATION_JSON, object);
  }

  /**
   * Creates a Jackson object mapper based on a media type. By default, it calls {@link ObjectMapper#ObjectMapper()}.
   * 
   * @return The Jackson object mapper.
   */
  protected ObjectMapper createObjectMapper() {
    JsonFactory jsonFactory = new JsonFactory();
    jsonFactory.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    ObjectMapper mapper = new ObjectMapper(jsonFactory);
// CONFIGURATION avec la 1.6 de Jackson  ? ...
    return mapper;
  }

  /**
   * Returns the wrapped object, deserializing the representation with Jackson if necessary.
   * 
   * @return The wrapped object.
   */
  public T getObject() {
    T result = null;

    if (this.object != null) {
      result = this.object;
    }
    else if (this.jsonRepresentation != null) {
      try {
        result = getObjectMapper().readValue(this.jsonRepresentation.getStream(), this.objectClass);
      }
      catch (IOException e) {
        Context.getCurrentLogger().log(Level.WARNING, "Unable to parse the object with Jackson.", e);
      }
    }

    return result;
  }

  /**
   * Get the object
   * 
   * @param rootNode
   *          the node where you have root
   * @return an object of type T
   */
  public T getObject(String rootNode) {
    T result = null;

    if (this.object != null) {
      result = this.object;
    }
    else if (this.jsonRepresentation != null) {
      try {

        String resultString = "{\"" + rootNode + "\": " + convertStreamToString(this.jsonRepresentation.getStream())
            + "\n}";
        /*
         * byte[] bytes = resultString.getBytes("UTF8"); result = (T) getXstream().fromXML(new
         * ByteArrayInputStream(bytes));
         */
        result = getObjectMapper().readValue(resultString, this.objectClass);
      }
      catch (IOException e) {
        Context.getCurrentLogger().log(Level.WARNING, "Unable to parse the object with Jackson.", e);
      }
    }

    return result;
  }

  /**
   * Returns the object class to instantiate.
   * 
   * @return The object class to instantiate.
   */
  public Class<T> getObjectClass() {
    return objectClass;
  }

  /**
   * Returns the modifiable Jackson object mapper. Useful to customize mappings.
   * 
   * @return The modifiable Jackson object mapper.
   */
  public ObjectMapper getObjectMapper() {
    if (this.objectMapper == null) {
      this.objectMapper = createObjectMapper();
    }

    return this.objectMapper;
  }

  /**
   * Sets the object to format.
   * 
   * @param object
   *          The object to format.
   */
  public void setObject(T object) {
    this.object = object;
  }

  /**
   * Sets the object class to instantiate.
   * 
   * @param objectClass
   *          The object class to instantiate.
   */
  public void setObjectClass(Class<T> objectClass) {
    this.objectClass = objectClass;
  }

  /**
   * Sets the Jackson object mapper.
   * 
   * @param objectMapper
   *          The Jackson object mapper.
   */
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void write(Writer writer) throws IOException {
    if (jsonRepresentation != null) {
      jsonRepresentation.write(writer);
    }
    else if (object != null) {
      getObjectMapper().writeValue(writer, object);
    }
  }

  /**
   * Converts a stream into a simple string
   * 
   * @param is
   *          the input stream
   * @return a string representing the stream
   * @throws IOException
   *           when a problem occurs in reading the stream
   */
  public String convertStreamToString(InputStream is) throws IOException {
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine() method. We iterate until the
     * BufferedReader return null which means there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
    if (is != null) {
      StringBuilder sb = new StringBuilder();
      String line;

      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        while ((line = reader.readLine()) != null) {
          sb.append(line).append("\n");
        }
      }
      finally {
        is.close();
      }
      return sb.toString();
    }
    else {
      return "";
    }
  }

}
