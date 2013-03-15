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
package fr.cnes.sitools.datasource.mongodb.dbexplorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bson.types.ObjectId;
import org.restlet.Context;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.datasource.mongodb.business.MongoDBExplorerRepresentation;
import fr.cnes.sitools.datasource.mongodb.business.SitoolsMongoDBDataSource;
import fr.cnes.sitools.datasource.mongodb.model.Collection;
import fr.cnes.sitools.datasource.mongodb.model.Database;
import fr.cnes.sitools.datasource.mongodb.model.MongoDBAttributeValue;
import fr.cnes.sitools.datasource.mongodb.model.MongoDBRecord;

/**
 * DBExplorerResource using DataSource for pooled connections
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class MongoDBExplorerResource extends SitoolsResource {

  /** If the resource is a database, this contains its content. */
  private volatile boolean databaseTarget;

  /** If the resource is a list of collection */
  private volatile boolean collectionsTarget;

  /** If the resource is a collection, this contains its details. */
  private volatile boolean collectionTarget;

  /** If the resource is a recordSet with pagination, this contains its content. */
  private volatile boolean recordSetTarget;

  /** If the resource is a collection, this contains its fields. */
  private volatile boolean metadataTarget;

  /** If the resource is a record, this contains its content. */
  private volatile boolean recordTarget;

  /** if resource is relative to a schema or if schema is given */
  private String collectionName;

  /** if resource is relative to a record */
  private String idDocument;

  /** Logger current */
  private Logger logger = Context.getCurrentLogger();

  /** Max number of rows */
  private int maxrows = 300;

  /** Fetch size */
  private int fetchSize = 0;

  /** The parent DBexplorer application handler */
  private volatile MongoDBExplorerApplication dbexplorer = null;

  /** Form */
  private Form pagination = null;

  /** Base reference */
  private String baseRef;
  /** The start index */
  private int start = 0;
  /** The limit index */
  private int limit = 0;

  @Override
  public void sitoolsDescribe() {
    setName("MongoDBExplorerResource");
    setDescription("Explore mongoDB datasource");
  }

  /**
   * Returns if target is a database
   * 
   * @return true if database
   */
  public final boolean isDatabaseTarget() {
    return databaseTarget;
  }

  /**
   * Returns if target is a record set
   * 
   * @return true if record set
   */
  public final boolean isRecordSetTarget() {
    return recordSetTarget;
  }

  /**
   * Returns if target is a record
   * 
   * @return true if record
   */
  public final boolean isRecordTarget() {
    return recordTarget;
  }

  @Override
  public final void doInit() {

    super.doInit();

    // parent : dbexplorer
    this.dbexplorer = (MongoDBExplorerApplication) getApplication();

    // target : database, table, record
    Map<String, Object> attributes = this.getRequest().getAttributes();

    this.collectionName = (attributes.get("collectionName") != null) ? Reference.decode(
        (String) attributes.get("collectionName"), CharacterSet.UTF_8) : null;

    this.idDocument = (attributes.get("_id") != null) ? Reference.decode((String) attributes.get("_id"),
        CharacterSet.UTF_8) : null;

    this.databaseTarget = (this.collectionName == null) && (this.idDocument == null)
        && !this.getReference().getLastSegment().equals("collections");
    this.collectionsTarget = (this.collectionName == null) && (this.idDocument == null) && !this.databaseTarget;
    this.recordSetTarget = (this.collectionName != null) && (this.getReference().getLastSegment().equals("records"));
    this.metadataTarget = (this.collectionName != null) && (this.getReference().getLastSegment().equals("metadata"));
    this.collectionTarget = (this.collectionName != null) && (this.idDocument == null) && !recordSetTarget
        && !this.metadataTarget;
    this.recordTarget = (this.collectionName != null) && (this.idDocument != null);

    // parameters : pagination, ...
    this.pagination = this.getQuery();

    // TODO baseRef / publicBaseRef
    // pas de / à la fin...
    if (this.getReference().getBaseRef().toString().endsWith("/")) {
      this.baseRef = this.getReference().getBaseRef().toString()
          .substring(1, this.getReference().getBaseRef().toString().length());
    }
    else {
      this.baseRef = this.getReference().getBaseRef().toString();
    }
  }

  /**
   * Get the DataSource
   * 
   * @return the DataSource associated
   */
  public final SitoolsMongoDBDataSource getDataSource() {
    return this.dbexplorer.getDataSource();
  }

  /**
   * Process constraint
   * 
   * @param variant
   *          Client preference
   * @return Representation to be used
   */
  public Representation processConstraint(Variant variant) {
    Representation represent = null;
    SitoolsMongoDBDataSource datasource = getDataSource();
    DB database = datasource.getDatabase();

    // DATABASE TARGET
    if (this.databaseTarget) {
      CommandResult cmd = database.getStats();

      List<String> messages = new ArrayList<String>();

      traceObjects(cmd, messages);

      Response response = new Response(true, messages, String.class, "statusInfo");
      represent = getRepresentation(response, variant);
    }
    // COLLECTIONS TARGET => GET THE LIST OF COLLECTIONS
    if (this.collectionsTarget) {
      try {
        Set<String> collections = database.getCollectionNames();
        Database mongoDatabase = new Database();
        mongoDatabase.setUrl(getBaseRef());
        for (String colName : collections) {
          Collection collection = new Collection();
          collection.setName(colName);
          collection.setUrl(getBaseRef() + "/" + colName);
          mongoDatabase.getCollections().add(collection);
        }
        Response response = new Response(true, mongoDatabase, Database.class, "mongodbdatabase");
        return getRepresentation(response, variant);
      }
      catch (MongoException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
      }

    }
    // COLLECTION TARGET => GET THE DESCRIPTION OF THE COLLECTION
    if (this.collectionTarget) {
      List<String> statusDetails = new ArrayList<String>();
      DBCollection collectionMongo = database.getCollection(this.collectionName);
      Collection collection = new Collection();
      collection.setName(collectionName);
      collection.setUrl(getBaseRef());
      traceObjects(collectionMongo.getStats(), statusDetails);
      collection.setStatusDetails(statusDetails);
      Response response = new Response(true, collection, Collection.class, "collection");
      return getRepresentation(response, variant);
    }

    if (this.recordSetTarget) {
      DBCollection collectionMongo = database.getCollection(this.collectionName);
      this.start = getPaginationStartRecord();
      this.limit = getPaginationExtend();
      limit = (limit > maxrows) ? maxrows : limit;
      if (limit == 0) {
        limit = maxrows;
      }

      DBCursor cursor = collectionMongo.find().skip(start).limit(limit);
      // cursor.setOptions(Bytes.QUERYOPTION_EXHAUST);
      return new MongoDBExplorerRepresentation(getMediaType(variant), cursor, this);

    }
    // TODO ça marche pas trop
    if (this.recordTarget) {
      DBCollection collectionMongo = database.getCollection(this.collectionName);

      DBObject query = new BasicDBObject();
      if (ObjectId.isValid(this.idDocument)) {
        query.put("_id", new ObjectId(this.idDocument));
      }
      else {
        query.put("_id", this.idDocument);
      }
      DBCursor cursor = collectionMongo.find(query).limit(1);
      this.setStart(0);
      this.setLimit(1);
      return new MongoDBExplorerRepresentation(getMediaType(variant), cursor, this);
    }

    if (this.metadataTarget) {
      DBCollection collectionMongo = database.getCollection(this.collectionName);
      BasicDBObject object = (BasicDBObject) collectionMongo.findOne();
      List<MongoDBAttributeValue> values = null;
      if (object != null && !object.isEmpty()) {
        values = getAttributeValue(object, false);
      }
      Response response = new Response(true, values, MongoDBAttributeValue.class, "fields");
      return getRepresentation(response, variant);
    }

    return represent;
  }

  /**
   * Gets if the given Object is a MongoDB object or not
   * 
   * @param value
   *          the object
   * @return true if object is a MongoDB object, false otherwise
   */
  private boolean isObject(Object value) {
    return value.getClass().isAssignableFrom(BasicDBObject.class);
  }

  /**
   * Create a MongoDBRecord from a BasicDBObject
   * 
   * @param object
   *          a {@link BasicDBObject}
   * @return a {@link MongoDBRecord}
   */
  private MongoDBRecord getMongoDBRecord(BasicDBObject object) {
    MongoDBRecord rec = new MongoDBRecord();
    rec.setAttributeValues(getAttributeValue(object, true));
    if (!getBaseRef().contains(object.getString("_id"))) {
      rec.setId(getBaseRef());
    }
    else {
      rec.setId(getBaseRef() + "/" + object.getString("_id"));
    }
    return rec;
  }

  /**
   * Create a List of {@link MongoDBAttributeValue} from a {@link BasicDBObject} Each {@link MongoDBAttributeValue}
   * contains the key and its children. If withValue is true, it also contains the value
   * 
   * @param dbObject
   *          the {@link BasicDBObject}
   * @param withValue
   *          true to set the value as well
   * @return the List of {@link MongoDBAttributeValue}
   */
  private List<MongoDBAttributeValue> getAttributeValue(BasicDBObject dbObject, boolean withValue) {
    List<MongoDBAttributeValue> children = new ArrayList<MongoDBAttributeValue>();
    for (String key : dbObject.keySet()) {
      Object value = dbObject.get(key);
      MongoDBAttributeValue attr = new MongoDBAttributeValue();
      attr.setName(key.toString());
      // TODO check null values
      attr.setType(dbObject.get(key).getClass().getSimpleName());
      if (isObject(value)) {
        BasicDBObject dbObjectValue = (BasicDBObject) value;
        attr.setChildren(getAttributeValue(dbObjectValue, withValue));
      }
      else if (withValue) {
        attr.setValue(dbObject.get(key));
      }

      children.add(attr);
    }
    return children;
  }

  /**
   * Get an representation
   * 
   * @param variant
   *          the variant needed by the client
   * @return representation with the following variant
   */
  @Get
  public Representation get(Variant variant) {
    return processConstraint(variant);
  }

  /**
   * Get base reference
   * 
   * @return base reference
   */
  public final String getBaseRef() {
    return this.baseRef;
  }

  /**
   * Read startRecord request parameter -> integer - 0 by default
   * 
   * @return startRecord
   */
  public final int getPaginationStartRecord() {
    String str = this.pagination.getFirstValue("start", true);
    try {
      int startrecord = ((str != null) && !str.equals("")) ? Integer.parseInt(str) : 0;
      return (startrecord > 0) ? startrecord : 0;
    }
    catch (NumberFormatException e) {
      logger.severe(e.getMessage());
      return 0;
    }
  }

  /**
   * Read extend request parameter -> integer - maxrows by default
   * 
   * @return extend
   */
  public final int getPaginationExtend() {
    String nbHits = this.pagination.getFirstValue("limit", true);
    try {
      int extend;
      if (nbHits != null && !nbHits.equals("")) {
        extend = Integer.parseInt(nbHits);
      }
      else {
        extend = maxrows;
      }
      return (extend > 0) ? extend : 0;
    }
    catch (NumberFormatException e) {
      logger.severe(e.getMessage());
      return maxrows;
    }
  }

  /**
   * Gets the fetchSize value
   * 
   * @return the fetchSize
   */
  public final int getFetchSize() {
    return fetchSize;
  }

  /**
   * Gets the maxrows value
   * 
   * @return the maxrows
   */
  public final int getMaxrows() {
    return maxrows;
  }

  /**
   * Configure the XStream
   * 
   * @param xstream
   *          the XStream to treat
   * @param response
   *          the response used
   */
  public void configure(XStream xstream, Response response) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);

    // Because annotations are apparently missed
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");
    xstream.alias("collection", Collection.class);
    xstream.alias("attribute", MongoDBAttributeValue.class);
    xstream.alias("record", MongoDBRecord.class);

    // If a class is present inside the response, link the item alias with this class
    if (response.getItemClass() != null) {
      xstream.alias("item", Object.class, response.getItemClass());
    }

    // If the object has a name, associate its name instead of item in the response
    if (response.getItemName() != null) {
      xstream.aliasField(response.getItemName(), Response.class, "item");
    }

  }

  /**
   * Trace informations for a sitoolsDataSource
   * 
   * @param object
   *          The DBObject to trace
   * @param messages
   *          ArrayList<String> messages
   */
  private void traceObjects(DBObject object, List<String> messages) {
    for (Object key : object.keySet()) {
      messages.add(key + ": " + object.get(key.toString()));
    }
  }

  /**
   * Gets the start value
   * 
   * @return the start
   */
  public int getStart() {
    return start;
  }

  /**
   * Sets the value of start
   * 
   * @param start
   *          the start to set
   */
  public void setStart(int start) {
    this.start = start;
  }

  /**
   * Gets the limit value
   * 
   * @return the limit
   */
  public int getLimit() {
    return limit;
  }

  /**
   * Sets the value of limit
   * 
   * @param limit
   *          the limit to set
   */
  public void setLimit(int limit) {
    this.limit = limit;
  }

}
