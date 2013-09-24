/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.resources.order.cart.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.dto.DataSetExpositionDTO;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.resources.order.AbstractOrderResource;
import fr.cnes.sitools.resources.order.cart.common.model.CartSelection;
import fr.cnes.sitools.resources.order.cart.common.model.CartSelections;
import fr.cnes.sitools.resources.order.utils.ListReferencesAPI;
import fr.cnes.sitools.resources.order.utils.OrderAPI;
import fr.cnes.sitools.resources.order.utils.OrderResourceUtils;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.TaskUtils;
import fr.cnes.sitools.util.DateUtils;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Abstract class to override for your own CartOrder process
 * 
 * @author m.gond/tx.chevallier
 * 
 * @version
 * 
 */
public abstract class AbstractCartOrderResource extends AbstractOrderResource {

  /** Application Settings */
  private SitoolsSettings settings;

  private CartSelections cartSelections;
  private Reference userStorageRef;
  private String userStorageUrl;
  private Reference tempDateStorageRef;
  private String tempDateStorageUrl;
  private File tempdir;
  private ClientInfo clientInfo;

  private Reference cartFileReference;

  private static final int START_INDEX = 0;
  private static final int LIMIT = 500;

  @Override
  public void doInit() {
    super.doInit();
    settings = ((SitoolsApplication) getApplication()).getSettings();
  }

  @Override
  public void doInitialiseOrder() throws SitoolsException {

    String cartFileUrl = getRequest().getResourceRef().getQueryAsForm().getFirstValue("cartFile");
    if (cartFileUrl == null || cartFileUrl.isEmpty()) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing cartFile parameter");
    }

    clientInfo = this.getRequest().getClientInfo();

    cartFileReference = new Reference(RIAPUtils.getRiapBase() + cartFileUrl);
    Representation repr = OrderResourceUtils.getFile(cartFileReference, clientInfo, getContext());

    cartSelections = getObject(repr, new Variant(MediaType.APPLICATION_JSON));

    if (cartSelections == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Cannot find cartSelections file");
    }
    getContext().getAttributes().put(TaskUtils.PARENT_APPLICATION, getApplication());

    // control if the dataset is activated
    for (CartSelection sel : cartSelections.getSelections()) {
      //String dsReq = sel.getDataUrl();
      String dsReq = "/datasets";
      
      
      Response resp = RIAPUtils.handleParseResponse(dsReq, Method.GET, MediaType.APPLICATION_JAVA_OBJECT, getContext());
      for (Object obj : resp.getData()){
        DataSet dataset = (DataSet)obj;
        if (dataset.getId().equals(sel.getDatasetId())){
          if (!dataset.getStatus().equals("ACTIVE"))
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Dataset [" + sel.getDatasetName() + "] is inactive");
        }
      }
    }
  
    super.doInitialiseOrder();

    String folderName = "/resources_orders/" + getOrderName();

    // use no user to get a directory in the temporary folder
    userStorageRef = OrderResourceUtils.getUserAvailableFolderPath(null, folderName, getContext());
    userStorageUrl = OrderResourceUtils.getUserAvailableFolderUrl(null, folderName, getContext());

    // use a temporary folder to manage concurrent access
    String date = DateUtils.format(new Date(), TaskUtils.getTimestampPattern());
    tempDateStorageUrl = userStorageUrl + "/" + date;
    tempDateStorageRef = new Reference(userStorageRef + "/" + date);

    tempdir = new File(settings.getString("Starter.ROOT_DIRECTORY") + settings.getStoreDIR() + tempDateStorageUrl);
    tempdir.mkdir();

  }

  @Override
  public ListReferencesAPI listFilesToOrder() throws SitoolsException {

    ListReferencesAPI listRef = new ListReferencesAPI(settings.getPublicHostDomain()
        + settings.getString(Consts.APP_URL));

    List<Record> globalrecs = new ArrayList<Record>();

    Map<Reference, String> refMap = new HashMap<Reference, String>();
    listRef.setRefSourceTarget(refMap);

    for (CartSelection sel : cartSelections.getSelections()) {

      OrderAPI.createEvent(order, getContext(), "Processing record for selection : " + sel.getName() + " on dataset "
          + sel.getDatasetName());

      String colurl = "";
      for (Column column : sel.getColModel()) {
        if (!colurl.equals("")) {
          colurl += ", " + column.getColumnAlias();
        }
        else {
          colurl = column.getColumnAlias();
        }
      }
      String urlTemplate = sel.getDataUrl() + "/records" + "?" + sel.getSelections() + "&colModel=\"" + colurl
          + "\"&start={start}&limit={limit}";

      Integer start = START_INDEX;
      Integer limit = LIMIT;

      int count;
      int offset;
      int nbTotalResult;
      do {

        String url = urlTemplate.replace("{start}", start.toString()).replace("{limit}", limit.toString());

        Response response = RIAPUtils.handleParseResponse(url, Method.GET, MediaType.APPLICATION_JAVA_OBJECT,
            getContext());
        nbTotalResult = response.getTotal();
        count = response.getCount();
        offset = response.getOffset();

        List<Record> recs = getRecordListFromResponse(response);

        // ** ADD DATA TO SOURCE REFERENCES
        Set<String> noDuplicateUrlSet = new HashSet<String>();

        // ** PREPARE LIST FOR METADATA.XML
        List<Record> extractrecs = new ArrayList<Record>();

        String[] dataColToExport = sel.getDataToExport();

        List<Column> exportableColumns = new ArrayList<Column>();
        List<Column> simpleColumns = new ArrayList<Column>();

        // ** PREPARE COLUMNS LIST
        // - TO REMOVE "NO CLIENT ACCESS" COLUMNS
        // - TO IDENTIFY EXPORTABLE COLUMNS

        for (Column column : sel.getColModel()) {
          boolean exportable = false;
          for (int i = 0; i <= (dataColToExport.length - 1); i++) {
            if (column.getColumnAlias().equals(dataColToExport[i])) {
              exportableColumns.add(column);
              exportable = true;
            }
          }
          if (!exportable) {
            simpleColumns.add(column);
          }
        }

        for (Record rec : recs) {

          List<AttributeValue> attributeValues = new ArrayList<AttributeValue>();
          Record extractrec = new Record();

          for (AttributeValue attributeValue : rec.getAttributeValues()) {

            /* (1) : SIMPLE COLUMNS ==> METADATA */
            for (Column simpleColumn : simpleColumns) {
              if (attributeValue.getName().equals(simpleColumn.getColumnAlias())) {
                attributeValues.add(attributeValue);
              }
            }

            /* (2) : EXPORTABLE COLUMNS => DATA and METADATA */
            for (Column exportableColumn : exportableColumns) {

              if (attributeValue.getName().equals(exportableColumn.getColumnAlias())) {

                if (attributeValue.getValue().toString().startsWith(settings.getString(Consts.APP_URL))) {
                  noDuplicateUrlSet.add(settings.getPublicHostDomain() + attributeValue.getValue().toString());
                }
                else {
                  noDuplicateUrlSet.add(attributeValue.getValue().toString());
                }

                String[] segments = attributeValue.getValue().toString().split("/");
                String lastsegment = segments[segments.length - 1];
                attributeValue.setValue("data/" + sel.getDatasetName() + "/" + lastsegment);
                attributeValues.add(attributeValue);

              }
            }

            extractrec.setAttributeValues(attributeValues);

          }

          extractrecs.add(extractrec);

        }

        // REMOVE DUPLICATE REFERENCES AND ADD TARGET NAME TO REFERENCE MAP
        listRef.addNoDuplicateSourceRef(noDuplicateUrlSet, sel.getDatasetName());

        globalrecs.addAll(extractrecs);

        start = offset + limit;

      } while (count + offset < nbTotalResult);

    }

    // ** SERIALIZE RECORD LIST TO METADATA REPRESENTATION

    XStream xstream = XStreamFactory.getInstance().getXStream(MediaType.APPLICATION_XML, getContext());
    xstream.setMode(XStream.NO_REFERENCES);
    xstream.alias("record", Record.class);
    xstream.alias("value", Object.class, String.class);
    xstream.alias("response", Response.class);
    xstream.alias("attributeValue", AttributeValue.class);

    XstreamRepresentation<List<Record>> metadataXmlRepresentation = new XstreamRepresentation<List<Record>>(
        MediaType.TEXT_XML, globalrecs);
    metadataXmlRepresentation.setXstream(xstream);

    // ** ADD METADATA TO SOURCE REFERENCES

    Reference metadataSourceRef = new Reference(tempDateStorageRef);
    metadataSourceRef.addSegment("metadata");
    metadataSourceRef.setExtensions("xml");

    OrderResourceUtils.addFile(metadataXmlRepresentation, metadataSourceRef, clientInfo, getContext());

    listRef.addReferenceSource(metadataSourceRef);

    // ** GENERATE INDEX.HTML (namely metadata.html)

    String xmlDir = settings.getRootDirectory() + settings.getStoreDIR() + tempDateStorageUrl + "/";
    String xsltDir = settings.getRootDirectory() + settings.getStoreDIR() + "/xslt/";

    File xmlFile = new File(xmlDir + "metadata.xml");
    File xsltFile = new File(xsltDir + "index.xsl");
    File resultFile = new File(xmlDir + "metadata.html");

    Reference htmlIndexRef = new Reference(tempDateStorageRef);
    htmlIndexRef.addSegment("metadata");
    htmlIndexRef.setExtensions("html");

    createHtmlIndex(xmlFile, xsltFile, resultFile);

    listRef.addReferenceSource(htmlIndexRef);

    return listRef;

  }

  /**
   * Get the records for the response object
   * 
   * @param response
   *          the {@link Response} object
   * @return the list of records
   */
  private List<Record> getRecordListFromResponse(Response response) {
    ArrayList<Record> listT = new ArrayList<Record>();
    ArrayList<Object> list = response.getData();
    for (Iterator<Object> iterator = list.iterator(); iterator.hasNext();) {
      Record obj = (Record) iterator.next();
      listT.add(obj);
    }
    return listT;
  }

  @Override
  public String getOrderName() {
    return "cart";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.resources.order.AbstractOrderResource#terminateOrder()
   */
  @Override
  public void terminateOrder() throws SitoolsException {
    super.terminateOrder();
    OrderResourceUtils.deleteFile(cartFileReference, clientInfo, getContext());
  }

  /**
   * Parse a {@link CartSelections} object from the given representation
   * 
   * @param representation
   *          the representation
   * @param variant
   *          the variant
   * @return a {@link CartSelections} object
   */
  public final CartSelections getObject(Representation representation, Variant variant) {

    CartSelections selections = null;

    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      XstreamRepresentation<CartSelections> repXML = new XstreamRepresentation<CartSelections>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      repXML.setXstream(xstream);
      selections = repXML.getObject();

    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      selections = new JacksonRepresentation<CartSelections>(representation, CartSelections.class).getObject();
    }

    return selections;
  }

  /**
   * createHtmlIndex
   * 
   * @param xmlFile
   * @param xsltFile
   * @param resultFile
   */
  private void createHtmlIndex(File xmlFile, File xsltFile, File resultFile) {

    Source xmlSource = new StreamSource(xmlFile);
    Source xsltSource = new StreamSource(xsltFile);
    Result result = new StreamResult(resultFile);

    TransformerFactory transFact = TransformerFactory.newInstance();
    javax.xml.transform.Transformer trans;

    try {

      // SIZE RESTRICTION REGARDING XSLT PROCESSING OF THE METADATA XML FILE

      // XSLT uses XPath and this requires that the whole XML document is
      // maintained in memory. This may lead to insufficient memory problems,
      // so a specific control is required
      // This control is based on a simple rule to approximate XML document
      // max size related to the memory allocated to Java :
      // size of XML document < max memory / 5

      if (Runtime.getRuntime().maxMemory() / xmlFile.length() > 5) {
        trans = transFact.newTransformer(xsltSource);
        trans.transform(xmlSource, result);

      }
      else {

        FileOutputStream stream = new FileOutputStream(resultFile);
        stream
            .write(new String("<html><body>WARNING: metadata.xml file is too large for being processed</body></html>")
                .getBytes());
        stream.close();

      }

    }
    catch (TransformerConfigurationException e) {
      e.printStackTrace();
    }
    catch (TransformerException e) {
      e.printStackTrace();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }

  }

}
