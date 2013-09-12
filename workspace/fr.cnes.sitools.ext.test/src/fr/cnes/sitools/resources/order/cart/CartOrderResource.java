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
package fr.cnes.sitools.resources.order.cart;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import fr.cnes.sitools.resources.order.AbstractOrderResource;
import fr.cnes.sitools.cart.model.CartSelection;
import fr.cnes.sitools.cart.model.CartSelections;
import fr.cnes.sitools.cart.utils.ListReferencesAPI;
import fr.cnes.sitools.cart.utils.OrderAPI;
import fr.cnes.sitools.cart.utils.OrderResourceUtils;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.order.model.Order;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.TaskUtils;
import fr.cnes.sitools.util.DateUtils;
import fr.cnes.sitools.util.RIAPUtils;


public class CartOrderResource extends AbstractOrderResource {
  
  /** Application Settings */
  private SitoolsSettings settings;
  
  private CartSelections cartSelections;
  private Reference userStorageRef;
  private ClientInfo clientInfo;
  private String date;
  private String userName;


  @Override
  public void doInit() {
    super.doInit();
    settings = ((SitoolsApplication) getApplication()).getSettings();
  }

  
  @Override
  public void doInitialiseOrder() throws SitoolsException {
  
    super.doInitialiseOrder();
    
    cartSelections = (CartSelections)getContext().getAttributes().get(TaskUtils.BODY_CONTENT);
    getContext().getAttributes().put(TaskUtils.PARENT_APPLICATION, getApplication());
    
    date = DateUtils.format(new Date(), TaskUtils.getTimestampPattern());

    clientInfo = this.getRequest().getClientInfo();
    User user = clientInfo.getUser();
    userName = user.getIdentifier();

    String rootdir = settings.getStoreDIR("Starter.USERSTORAGE_ROOT") +  "/" + userName;

    // make directories
    
    File ordersdir = new File(rootdir + "/resources_orders");
    File outputdir = new File(rootdir + "/resources_orders/dataset_" + date);
    File datadir = new File(outputdir + "/data");

    ordersdir.mkdir();
    outputdir.mkdir();
    datadir.mkdir();
    
    String folderName = "/resources_orders/dataset_" + date;
    userStorageRef = OrderResourceUtils.getUserAvailableFolderPath(user, folderName, getContext());


  }
  

  /**
   * MapEntryConverter inner class
   * */
  public static class MapEntryConverter implements Converter {

    public boolean canConvert(Class cls) {
      return AbstractMap.class.isAssignableFrom(cls);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

      AbstractMap map = (AbstractMap) value;

      for (Object obj : map.entrySet()) {
        Entry entry = (Entry) obj;
        writer.startNode(entry.getKey().toString());
        writer.setValue(entry.getValue().toString());
        writer.endNode();
      }

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader arg0, UnmarshallingContext arg1) {
      // TODO Auto-generated method stub
      return null;
    }
  }

  @Override
  public ListReferencesAPI listFilesToOrder() throws SitoolsException {

    ListReferencesAPI listRef = new ListReferencesAPI(settings.getPublicHostDomain() + settings.getString(Consts.APP_URL));

    Order order = null;
    try {
      
      order = OrderAPI.createOrder(userName, getContext(), "ORDER_CART_" + date);
      OrderAPI.activateOrder(order, getContext());

      List<Record> globalrecs = new ArrayList<Record>();
      
      for (CartSelection sel : cartSelections.getSelections()) {
        
        OrderAPI.createEvent(order, getContext(), "Processing record for selection : " + sel.getName() + " on dataset "
            + sel.getDatasetName());
        
        String colurl = "";
        for ( Column column : sel.getColModel() ) {
          if (!colurl.equals(""))
            colurl += ", " + column.getColumnAlias();
          else
            colurl = column.getColumnAlias();
        }
        String url = sel.getDataUrl() + "/records" + "?" + sel.getSelections() + "&colModel=\"" + colurl + "\"" ;
        List<Record> recs = RIAPUtils.getListOfObjects(url  , getContext());
        
        // ** ADD DATA TO SOURCE REFERENCES 

        Set<String> noDuplicateUrlSet = new HashSet();
        
        for ( Record rec : recs ) {

          List<AttributeValue> attributeValues = new ArrayList<AttributeValue>();

          // ADD DATA FILES FROM COLUMNS SPECIFIED AS "DATA" TYPE
          if (sel.getDataToExport() != null){
            String[] dataColToExport = sel.getDataToExport();  
            for ( AttributeValue attributeValue : rec.getAttributeValues() ) {
              for (int i=0; i<= (dataColToExport.length - 1); i++){
                if (attributeValue.getName().equals(dataColToExport[i])){
                  if (attributeValue.getValue().toString().startsWith(settings.getString(Consts.APP_URL)))
                    noDuplicateUrlSet.add(settings.getPublicHostDomain() + attributeValue.getValue().toString());
                  else 
                    noDuplicateUrlSet.add(attributeValue.getValue().toString());
                }
              }
            }
          }
        
        }
        
        // USE STAGING SET TO REMOVE DUPLICATE REFERENCES
        listRef.addNoDuplicateSourceRef(noDuplicateUrlSet);
        
        // ** PREPARE LIST FOR METADATA.XML 
        
        List<Record> extractrecs = new ArrayList<Record>();
        
        for ( Record rec : recs ) {

          // REMOVE "NO CLIENT ACCESS" COLUMNS FROM LIST
          List<AttributeValue> attributeValues = new ArrayList<AttributeValue>();
          Record extractrec = new Record();
          for ( AttributeValue attributeValue : rec.getAttributeValues() ) {
            for ( Column column : sel.getColModel() ){
              if (attributeValue.getName().equals(column.getColumnAlias())){
                attributeValues.add(attributeValue);
              }
            }
            extractrec.setAttributeValues(attributeValues);
            extractrec.setId(rec.getId());
          }
          extractrecs.add(extractrec);
       }
       globalrecs.addAll(extractrecs);
 
      }

      // ** SERIALIZE RECORD LIST TO METADATA REPRESENTATION
      
      XStream xstream = XStreamFactory.getInstance().getXStream(MediaType.APPLICATION_XML, getContext());
      xstream.alias("record", Record.class);
      xstream.alias("value", Object.class, String.class);
      xstream.alias("response", Response.class);
      xstream.alias("attributeValue", AttributeValue.class);
      
      XstreamRepresentation<List<Record>> metadataXmlRepresentation = new XstreamRepresentation<List<Record>>(MediaType.TEXT_XML, globalrecs);
      metadataXmlRepresentation.setXstream(xstream);

      // ** ADD METADATA TO SOURCE REFERENCES
      
      Reference metadataSourceRef = new Reference(userStorageRef);
      metadataSourceRef.addSegment("metadata");
      metadataSourceRef.setExtensions("xml");

      OrderResourceUtils.addFile(metadataXmlRepresentation, metadataSourceRef, clientInfo, getContext());

      listRef.addReferenceSource(metadataSourceRef);

    }  
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      if (order != null) {
        try {
          OrderAPI.orderFailed(order, getContext(), e.getMessage());
        }
        catch (SitoolsException e1) {
          throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
        }
      }
    }
    
    
    return listRef;

  }

  @Override
  public Representation processOrder(ListReferencesAPI listReferences) throws SitoolsException {
    
    return null;
    
  }

  @Override
  public String getOrderName() {
    // TODO Auto-generated method stub
    return null;
  }


  
  

}
