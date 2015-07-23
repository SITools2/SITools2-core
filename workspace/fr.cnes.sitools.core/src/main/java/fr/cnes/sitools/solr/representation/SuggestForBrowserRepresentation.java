package fr.cnes.sitools.solr.representation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.representation.OutputRepresentation;

/**
 * The Class SuggestForBrowserRepresentation.
 * 
 * @author m.gond
 */
public class SuggestForBrowserRepresentation extends OutputRepresentation {

  /** The xml input stream. */
  private InputStream xmlInputStream;

  /** The query. */
  private String query;

  /**
   * Instantiates a new suggest for browser representation.
   * 
   * @param mediaType
   *          the media type
   * @param xmlInputStream
   *          the xml input stream
   * @param query
   *          the original query
   */
  public SuggestForBrowserRepresentation(MediaType mediaType, InputStream xmlInputStream, String query) {
    super(mediaType);
    this.xmlInputStream = xmlInputStream;
    this.query = query;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.representation.Representation#write(java.io.OutputStream)
   */
  @Override
  public void write(OutputStream outputStream) throws IOException {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader;

    // creates a JSONWriter to properly format JSON
    OutputStreamWriter out = new OutputStreamWriter(outputStream);

    JsonFactory jfactory = new JsonFactory();
    JsonGenerator jGenerator = jfactory.createJsonGenerator(out);

    jGenerator.writeStartArray();
    jGenerator.writeString(query);
    jGenerator.writeStartArray();

    try {
      reader = factory.createXMLStreamReader(xmlInputStream);

      boolean inTermsField = false;

      while (reader.hasNext()) {
        int type = reader.next();
        switch (type) {

          case XMLStreamReader.START_ELEMENT:
            if (reader.getLocalName().equals("lst") && "terms".equals(reader.getAttributeValue(0))) {
              inTermsField = true;
            }
            else if (reader.getLocalName().equals("int") && inTermsField) {
              jGenerator.writeString(reader.getAttributeValue(0));
            }
            break;

          default:
            break;
        }
      }
    }
    catch (XMLStreamException e) {
      Engine.getLogger(this.getClass().getName()).warning(e.getMessage());
    }
    catch (IOException e) {
      Engine.getLogger(this.getClass().getName()).warning(e.getMessage());
    }

    jGenerator.writeEndArray();
    jGenerator.writeEndArray();

    // flush the stream
    out.flush();
    jGenerator.close();
    out.close();
  }

}
