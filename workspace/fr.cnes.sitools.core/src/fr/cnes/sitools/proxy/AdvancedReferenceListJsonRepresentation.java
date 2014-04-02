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

public class AdvancedReferenceListJsonRepresentation extends OutputRepresentation {

  private ReferenceList referenceList;

  public AdvancedReferenceListJsonRepresentation(MediaType mediaType) {
    super(mediaType);
  }

  public AdvancedReferenceListJsonRepresentation(MediaType mediaType, ReferenceList referenceList) {
    super(mediaType);
    this.referenceList = referenceList;
  }

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
      jGenerator.writeStartObject();
      if (ref.toString().endsWith("/")) {
        // jo.put("leaf", "false");
        jGenerator.writeStringField("cls", "folder");
        jGenerator.writeBooleanField("checked", false);

      }
      else {
        jGenerator.writeStringField("leaf", "true");
        jGenerator.writeBooleanField("checked", false);
      }

      String[] arrayPath = ref.toString().split("/");
      String path = arrayPath[arrayPath.length - 1];
      jGenerator.writeStringField("url", ref.toString());
      jGenerator.writeStringField("text", path);

      if ((file != null) && file.exists()) {
        jGenerator.writeNumberField("size", file.length());
        jGenerator.writeNumberField("lastmod", Math.round(file.lastModified() / 1000));
      }
      jGenerator.writeEndObject();
    }
    jGenerator.writeEndArray();
    jGenerator.writeEndObject();

    // flush the stream
    out.flush();
    jGenerator.close();
    out.close();
  }

}
