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
package fr.cnes.sitools.datasource.mongodb.business;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import fr.cnes.sitools.datasource.mongodb.dbexplorer.MongoDBExplorerResource;

/**
 * Representation for simple MongoDB datasource exploring
 * 
 * @author b.fiorito (AKKA Technologies)
 */
public class MongoDBExplorerRepresentation extends OutputRepresentation {

  /** The mongoDB cursor **/
  private DBCursor cursor;

  /** DBExplorerResource for connection **/
  private MongoDBExplorerResource mongoDbResource;

  /**
   * Constructor
   * 
   * @param mediaType
   *          the mediaType
   * @param cursor
   *          the mongo cursor
   * @param res
   *          the resource for pooled conenction
   */
  public MongoDBExplorerRepresentation(MediaType mediaType, DBCursor cursor, MongoDBExplorerResource res) {
    super(MediaType.APPLICATION_JSON);
    this.cursor = cursor;
    this.mongoDbResource = res;

  }

  @Override
  public void write(OutputStream outputStream) throws IOException {
    PrintStream out = new PrintStream(outputStream);
    out.println("{\"success\": true,");
    out.println("\"total\":" + cursor.count() + ",");

    out.println("\"data\":[");
    boolean first = true;
    while (cursor.hasNext()) {
      if (!first) {
        out.println(",");
      }
      else {
        first = false;
      }
      BasicDBObject record = (BasicDBObject) cursor.next();
      out.print(record.toString());
      out.flush();
    }

    out.println("],");

    out.println("\"count\":" + cursor.size() + ",");
    out.println("\"offset\":" + mongoDbResource.getStart());
    out.println("}");
    out.close();
  }
}
