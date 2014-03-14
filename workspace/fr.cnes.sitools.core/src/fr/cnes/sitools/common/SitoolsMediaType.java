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
package fr.cnes.sitools.common;

import org.restlet.data.MediaType;

/**
 * Specific MediaTypes for Sitools2
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class SitoolsMediaType {

  /** used by riap sitools2 requests */
  public static final MediaType APPLICATION_JAVA_OBJECT_SITOOLS_MODEL = MediaType.register(
      "application/x-java-serialized-object+sitools-model", "MediaType for Internal Sitools Object Model");

  /** used by Resource datasets in projects */
  public static final MediaType APPLICATION_JAVA_OBJECT_SITOOLS_MODEL_RESOURCE = MediaType.register(
      "application/x-java-serialized-object+sitools-model-resource", "MediaType for Internal Sitools Object Model");

  /** used by client */
  public static final MediaType APPLICATION_SITOOLS_JSON_DATASET = MediaType.register(
      "application/json+sitools-dataset", "MediaType for Sitools DataSet Json Representation");

  /** used by client */
  public static final MediaType APPLICATION_SITOOLS_JSON_ORDER = MediaType.register(
      "application/json+sitools-order", "MediaType for Sitools Order Json Representation");
  
  /** used by client */
  public static final MediaType APPLICATION_SITOOLS_JSON_DIRECTORY = MediaType.register(
      "application/json+sitools-directory", "MediaType for Sitools Directory Json Representation");
 
  
  /**
   * private constructor
   */
  private SitoolsMediaType() {
    super();
  }

}
