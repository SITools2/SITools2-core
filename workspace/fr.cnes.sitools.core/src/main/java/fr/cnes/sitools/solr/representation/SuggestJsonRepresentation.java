package fr.cnes.sitools.solr.representation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.representation.OutputRepresentation;

/**
 * The Class SuggestForBrowserRepresentation.
 * 
 * @author m.gond
 */
public class SuggestJsonRepresentation extends OutputRepresentation {

  /** The xml input stream. */
  private InputStream xmlInputStream;

  /**
   * Instantiates a new suggest for browser representation.
   * 
   * @param mediaType
   *          the media type
   * @param xmlInputStream
   *          the xml input stream
   */
  public SuggestJsonRepresentation(MediaType mediaType, InputStream xmlInputStream) {
    super(mediaType);
    this.xmlInputStream = xmlInputStream;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.representation.Representation#write(java.io.OutputStream)
   */
  @Override
  public void write(OutputStream outputStream) throws IOException {

    // creates a JSONWriter to properly format JSON
    OutputStreamWriter out = new OutputStreamWriter(outputStream);

    JsonFactory jfactory = new JsonFactory();
    JsonGenerator jGenerator = jfactory.createJsonGenerator(out);

    jGenerator.writeStartObject();
    if (xmlInputStream != null) {
      XMLInputFactory factory = XMLInputFactory.newInstance();
      XMLStreamReader reader = null;
      try {
        reader = factory.createXMLStreamReader(xmlInputStream);

        jGenerator.writeStringField("success", "true");
        jGenerator.writeFieldName("data");
        writeSuggest(reader, jGenerator);

        reader.close();

      }
      catch (XMLStreamException e) {
        Engine.getLogger(this.getClass().getName()).warning(e.getMessage());
      }
      catch (IOException e) {
        Engine.getLogger(this.getClass().getName()).warning(e.getMessage());
      }
      finally {
        if (reader != null) {
          try {
            reader.close();
          }
          catch (XMLStreamException e) {
            Engine.getLogger(this.getClass().getName()).warning(e.getMessage());
          }
        }
      }
    }
    else {
      jGenerator.writeStringField("success", "false");

    }
    jGenerator.writeEndObject();

    // flush the stream
    out.flush();
    jGenerator.close();
    out.close();
  }

  /**
   * Write suggest.
   * 
   * @param reader
   *          the reader
   * @param jGenerator
   *          the j generator
   * @throws XMLStreamException
   *           the xML stream exception
   * @throws JsonGenerationException
   *           the json generation exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void writeSuggest(XMLStreamReader reader, JsonGenerator jGenerator) throws XMLStreamException,
          JsonGenerationException, IOException {

    jGenerator.writeStartArray();

    boolean inTermsField = false;
    boolean inInt = false;
    String fieldName = "";
    while (reader.hasNext()) {
      int type = reader.next();
      switch (type) {
        case XMLStreamReader.START_ELEMENT:
          inInt = false;
          if (reader.getLocalName().equals("lst") && "terms".equals(reader.getAttributeValue(0))) {
            inTermsField = true;
          }
          else if (reader.getLocalName().equals("lst") && inTermsField) {

            fieldName = reader.getAttributeValue(0);
          }
          else if (reader.getLocalName().equals("int") && inTermsField) {
            jGenerator.writeStartObject();
            jGenerator.writeStringField("field", fieldName);
            jGenerator.writeStringField("name", reader.getAttributeValue(0));
            inInt = true;
          }
          break;

        case XMLStreamReader.CHARACTERS:
          if (inInt) {
            jGenerator.writeStringField("nb", reader.getText());
            jGenerator.writeEndObject();
            inInt = false;
          }
          break;

        default:
          break;
      }
    }

    jGenerator.writeEndArray();
  }

}
