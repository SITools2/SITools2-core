package fr.cnes.sitools.proxy;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.representation.OutputRepresentation;

/**
 * The Class ReferenceListJsonRepresentation.
 * 
 * @author m.gond
 */
public class ReferenceListJsonRepresentation extends OutputRepresentation {

  /** The reference list. */
  private ReferenceList referenceList;

  /**
   * Instantiates a new reference list json representation.
   * 
   * @param mediaType
   *          the media type
   */
  public ReferenceListJsonRepresentation(MediaType mediaType) {
    super(mediaType);
  }

  /**
   * Instantiates a new reference list json representation.
   * 
   * @param mediaType
   *          the media type
   * @param referenceList
   *          the reference list
   */
  public ReferenceListJsonRepresentation(MediaType mediaType, ReferenceList referenceList) {
    super(mediaType);
    this.referenceList = referenceList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.representation.Representation#write(java.io.OutputStream)
   */
  @Override
  public void write(OutputStream outputStream) throws IOException {

    OutputStreamWriter out = new OutputStreamWriter(outputStream);

    JsonFactory jfactory = new JsonFactory();
    JsonGenerator jGenerator = jfactory.createJsonGenerator(out);

    jGenerator.writeStartObject();
    jGenerator.writeFieldName("items");
    jGenerator.writeStartArray();

    for (Reference ref : referenceList) {
      File file = null;
      if (referenceList instanceof ReferenceFileList) {
        file = ((ReferenceFileList) referenceList).get(ref.toString());
      }

      if (!ref.toString().endsWith("/")) {
        jGenerator.writeStartObject();
        String[] arrayPath = ref.toString().split("/");
        String path = arrayPath[arrayPath.length - 1];

        jGenerator.writeStringField("name", path);
        jGenerator.writeStringField("url", ref.toString());

        if ((file != null) && file.exists()) {
          jGenerator.writeNumberField("size", file.length());
          jGenerator.writeNumberField("lastmod", Math.round(file.lastModified() / 1000));

        }
        jGenerator.writeEndObject();
      }
    }
    jGenerator.writeEndArray();
    jGenerator.writeEndObject();

    // flush the stream
    out.flush();
    jGenerator.close();
    out.close();
  }

}
