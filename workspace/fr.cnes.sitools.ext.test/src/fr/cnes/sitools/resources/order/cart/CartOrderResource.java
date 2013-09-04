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
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import fr.cnes.sitools.cart.model.CartSelection;
import fr.cnes.sitools.cart.model.CartSelections;
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
import fr.cnes.sitools.tasks.TaskUtils;
import fr.cnes.sitools.util.DateUtils;
import fr.cnes.sitools.util.RIAPUtils;

public class CartOrderResource extends CartOrderResourceFacade {
  
  /** Application Settings */
  private SitoolsSettings settings;


  @Override
  @Get
  public Representation orderGet(Variant variant) {
    // TODO Auto-generated method stub
    return super.orderGet(variant);
  }

  
  
  
  @Override
  public void doInit() {
    super.doInit();
    settings = ((SitoolsApplication) getApplication()).getSettings();
  }

  /**
   * action - iterate on cart selections provided in json representation - download data found into dedicated directory
   * - serialize metadata to XML file
   * 
   * @param representation
   * @param variant
   * @return
   */
  @Override
  @Post
  public Representation orderPost(Representation representation, Variant variant) {
    
    CartSelections cartSelections = (CartSelections)getContext().getAttributes().get(TaskUtils.BODY_CONTENT);
    getContext().getAttributes().put(TaskUtils.PARENT_APPLICATION, getApplication());

    // make directories
    
    String date = DateUtils.format(new Date(), TaskUtils.getTimestampPattern());

    ClientInfo clientInfo = this.getRequest().getClientInfo();
    User user = clientInfo.getUser();

    String userName = user.getIdentifier();

    String rootdir = settings.getStoreDIR("Starter.USERSTORAGE_ROOT") +  "/" + userName;
    String folderName = "/resources_orders/dataset_" + date;

    Reference userStorageRef = OrderResourceUtils.getUserAvailableFolderPath(user, folderName, getContext());

    File ordersdir = new File(rootdir + "/resources_orders");
    File outputdir = new File(rootdir + "/resources_orders/dataset_" + date);
    File datadir = new File(outputdir + "/data");

    ordersdir.mkdir();
    outputdir.mkdir();
    datadir.mkdir();
    Order order = null;
    try {
      order = OrderAPI.createOrder(userName, getContext(), "ORDER_CART_" + date);
      OrderAPI.activateOrder(order, getContext());

      List<Record> globalrecs = new ArrayList<Record>();
      
      for (CartSelection sel : cartSelections.getSelections()) {
        
        OrderAPI.createEvent(order, getContext(), "Processing record for selection : " + sel.getName() + " on dataset "
            + sel.getDatasetName());
        
        String selections = sel.getSelections();
        String datasetUrl = sel.getDataUrl();
        
        List<Column> colModel = sel.getColModel();
        
        
        List<Record> recs = RIAPUtils.getListOfObjects(datasetUrl + "/records" + "?" + selections, getContext());
        
        globalrecs.addAll(recs);

        // download data from URLs

//        Set<String> filesUrlSet = new HashSet();
//
//        for (Map<String, String> records : sel.getRecords()) {
//          for (Object obj : records.entrySet()) {
//            Entry entry = (Entry) obj;
//            if (entry.getValue().toString().startsWith("http://") || entry.getValue().toString().startsWith("https://")) {
//              filesUrlSet.add(entry.getValue().toString());
//            }
//            if (settings.getString(Consts.APP_URL) != null) {
//              if (entry.getValue().toString().startsWith(settings.getString(Consts.APP_URL))) {
//                String urlReturn = settings.getPublicHostDomain();
//                filesUrlSet.add(urlReturn + entry.getValue().toString());
//              }
//            }
//          }
//        }
//
//        for (String strUrl : filesUrlSet) {
//          Reference ref = new Reference(strUrl);
//          Reference destRef = new Reference(userStorageRef);
//          destRef.addSegment("data");
//          destRef.addSegment(ref.getLastSegment());
//          try {
//            OrderResourceUtils.copyFile(ref, destRef, clientInfo, getContext());
//          }
//          catch (SitoolsException e) {
//            getLogger().log(Level.WARNING, "File not copied : " + ref, e);
//          }
//        }

      }

      
      XStream xstream = XStreamFactory.getInstance().getXStream(MediaType.APPLICATION_XML, getContext());
      xstream.alias("record", Record.class);
      xstream.alias("value", Object.class, String.class);
      xstream.alias("response", Response.class);
      xstream.alias("attributeValue", AttributeValue.class);
      
      XstreamRepresentation<List<Record>> rep = new XstreamRepresentation<List<Record>>(MediaType.TEXT_XML, globalrecs);
      
      rep.setXstream(xstream);

      Reference destRef = new Reference(userStorageRef);
      destRef.addSegment("metadata");
      destRef.setExtensions("xml");
      OrderResourceUtils.addFile(rep, destRef, clientInfo, getContext());

      try {
        OrderAPI.terminateOrder(order, getContext());
      }
      catch (SitoolsException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
      }

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
      Response response = new Response(false, "label.download_ko");
      return getRepresentation(response, variant);
    }

    Response response = new Response(true, "label.download_ok");
    return getRepresentation(response, variant);

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


  
  

}
