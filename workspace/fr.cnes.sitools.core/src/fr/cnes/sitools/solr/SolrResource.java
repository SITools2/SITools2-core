/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Request;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.xml.Transformer;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

/**
 * SolrResource to execute request
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public final class SolrResource extends AbstractSolrResource {

  @Override
  public void sitoolsDescribe() {
    setName("SolrResource");
    setDescription("Solr resource for redirect client requests to solr core");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.ServerResource#get()
   */
  @Override
  @Get
  public Representation get(Variant variant) {
    Representation repr = null;
    org.restlet.Response response = null;

    do {
      if (this.getReference().getBaseRef().toString().endsWith("execute")) {

        Form query = this.getRequest().getResourceRef().getQueryAsForm();
        String request = query.getFirstValue("q");

        String start = query.getFirstValue("start");
        String rows = query.getFirstValue("rows");

        // encode special characters
        try {
          request = URLEncoder.encode(request, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
          getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
          break;
        }

        String requestStr = "solr://" + osId + "?q=" + request + "&hl=true&hl.fl=*";

        if (start != null) {
          requestStr += "&start=" + start;
        }
        if (rows != null) {
          requestStr += "&rows=" + rows;
        }

        getLogger().info("SOLR QUERY : " + requestStr);

        Request reqGET = new Request(Method.GET, requestStr);

        response = getContext().getClientDispatcher().handle(reqGET);

        if (response == null || Status.isError(response.getStatus().getCode())) {
          getResponse().setStatus(response.getStatus());
          break;
        }

        SolrApplication app = (SolrApplication) this.getApplication();
        String urlXSLTDir = app.getSolrDirectory() + "/" + osId + "/conf/xslt";

        Representation represent = response.getEntity();

        FileRepresentation xsltRSS = new FileRepresentation(urlXSLTDir + "/rss.xsl", MediaType.TEXT_XML);

        // create a transformer
        Transformer transformer = new Transformer(Transformer.MODE_RESPONSE, xsltRSS);
        transformer.setResultCharacterSet(CharacterSet.UTF_8);
        transformer.setResultMediaType(MediaType.APPLICATION_RSS);
        repr = transformer.transform(represent);
        repr.setCharacterSet(CharacterSet.UTF_8);

      }

      else if (this.getReference().getBaseRef().toString().endsWith("suggest")) {

        String fieldList = (String) this.getRequest().getAttributes().get("fieldList");
        // fieldList format is field1,field2
        String[] fields = fieldList.split(",");

        String query = this.getRequest().getResourceRef().getQueryAsForm().getFirstValue("q");
        query = query.toLowerCase();
        String fieldStr = "";

        for (int i = 0; i < fields.length; i++) {
          fieldStr += "terms.fl=" + fields[i];
          if (i < fields.length - 1) {
            fieldStr += "&";
          }
        }

        String url = "solr://" + osId + "/terms?" + fieldStr + "&terms.prefix=" + query + "&terms.sort=count";

        Request reqGET = new Request(Method.GET, url);

        getLogger().info("SOLR SUGGEST : " + url);

        response = getContext().getClientDispatcher().handle(reqGET);

        if (response == null || Status.isError(response.getStatus().getCode())) {
          getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
          break;
        }

        if (variant.getMediaType().isCompatible(MediaType.APPLICATION_JSON)) {
          repr = this.getSuggestJSON(response);
        }
        if (variant.getMediaType().isCompatible(MediaType.APPLICATION_ALL_XML)) {
          repr = this.getSuggestForBrowser(response, query);

        }

      }
    } while (false);

    return repr;
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Main method in Sitools2 to handle requests to Solr core.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramOsId = new ParameterInfo("osId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Indentifier of the opensearch to deal with.");
    ParameterInfo paramFieldList = new ParameterInfo("fieldList", false, "xs:string", ParameterStyle.TEMPLATE,
        "List of columns used for auto-completion.");
    ParameterInfo paramQuery = new ParameterInfo("q", false, "xs:string", ParameterStyle.QUERY,
        "Query sent to the Solr core.");
    ParameterInfo paramStart = new ParameterInfo("start", false, "xs:string", ParameterStyle.QUERY,
        "Starting index of results.");
    ParameterInfo paramRows = new ParameterInfo("rows", false, "xs:string", ParameterStyle.QUERY,
        "Number of rows to send back.");
    info.getRequest().getParameters().add(paramRows);
    info.getRequest().getParameters().add(paramStart);
    info.getRequest().getParameters().add(paramQuery);
    info.getRequest().getParameters().add(paramFieldList);
    info.getRequest().getParameters().add(paramOsId);
    this.addStandardResponseInfo(info);
  }

  /**
   * Returns the representation to be send to the browser
   * 
   * @param response
   *          the solr xml response
   * @param query
   *          the client query string
   * @return Representation
   */
  private Representation getSuggestForBrowser(org.restlet.Response response, String query) {

    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader;

    JSONArray json = new JSONArray();
    JSONArray arrayKeyword = new JSONArray();
    json.put(query);

    try {
      reader = factory.createXMLStreamReader(new StringReader(response.getEntity().getText()));

      boolean inTermsField = false;

      while (reader.hasNext()) {
        int type = reader.next();
        switch (type) {

          case XMLStreamReader.START_ELEMENT:
            if (reader.getLocalName().equals("lst") && "terms".equals(reader.getAttributeValue(0))) {
              inTermsField = true;
            }
            else if (reader.getLocalName().equals("int") && inTermsField) {
              arrayKeyword.put(reader.getAttributeValue(0));
            }
            break;

          default:
            break;
        }
      }
    }
    catch (XMLStreamException e) {
      getLogger().warning(e.getMessage());
    }
    catch (IOException e) {
      getLogger().warning(e.getMessage());
    }

    json.put(arrayKeyword);

    return new JsonRepresentation(json);
  }

  /**
   * Returns the representation as JSON
   * 
   * @param response
   *          the server response
   * @return the representation as JSON
   */
  private Representation getSuggestJSON(org.restlet.Response response) {
    JsonRepresentation repr = null;
    if (response.getEntity() != null) {
      XMLInputFactory factory = XMLInputFactory.newInstance();
      JSONObject json = new JSONObject();

      try {
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(response.getEntity().getText()));

        JSONArray array = getJsonFromXML(reader);

        json.put("success", "true");
        json.put("data", array);

        repr = new JsonRepresentation(json);

      }
      catch (XMLStreamException e) {
        getLogger().warning(e.getMessage());
      }
      catch (IOException e) {
        getLogger().warning(e.getMessage());
      }
      catch (JSONException e) {
        getLogger().warning(e.getMessage());
      }
    }

    return repr;
  }

  /**
   * // * XML parser // * // * @param reader // * the XMLStreamReader // * @return JSONArray containing the list of
   * keywords, the number of occurance // * and the field associated to it // * @throws XMLStreamException // * reader
   * stream error // * @throws JSONException // * json writing error //
   * 
   * @return JSON array
   */
  private JSONArray getJsonFromXML(XMLStreamReader reader) throws XMLStreamException, JSONException {

    JSONArray jsonArray = new JSONArray();

    boolean inTermsField = false;
    boolean inInt = false;
    JSONObject object = null;
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
            object = new JSONObject();
            object.put("field", fieldName);
            object.put("name", reader.getAttributeValue(0));
            inInt = true;
          }
          break;

        case XMLStreamReader.CHARACTERS:
          if (inInt) {
            object.put("nb", reader.getText());
            jsonArray.put(object);
            inInt = false;
          }
          break;

        default:
          break;
      }
    }

    return jsonArray;

  }

}
