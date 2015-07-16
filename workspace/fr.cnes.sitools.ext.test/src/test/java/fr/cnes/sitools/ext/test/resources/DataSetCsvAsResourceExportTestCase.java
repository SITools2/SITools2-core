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
package fr.cnes.sitools.ext.test.resources;

 import fr.cnes.sitools.common.SitoolsSettings;
 import fr.cnes.sitools.ext.test.tasks.AbstractTaskResourceTestCase;
 import fr.cnes.sitools.plugins.resources.model.ResourceModel;
 import fr.cnes.sitools.server.Consts;
 import fr.cnes.sitools.util.RIAPUtils;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 import org.restlet.data.MediaType;
 import org.restlet.data.Preference;
 import org.restlet.representation.Representation;
 import org.restlet.resource.ClientResource;

 import java.io.IOException;

 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;

 /**
  * Test case for CSV export
  *
  * @author m.gond (AKKA Technologies)
  */
 public class DataSetCsvAsResourceExportTestCase extends AbstractTaskResourceTestCase {

   /**
    * The if of the dataset
    */
   private static final String DATASET_ID = "1d9e040c-5fb4-4e7e-af39-978dc4500183";
   /**
    * The url of the dataset
    */
   private static final String DATASET_URL = "/fuse";

   /**
    * The class name of the resourceModel
    */
   private String csvResourceModelClassName = "fr.cnes.sitools.resources.csv.CsvResourceModel";

   /**
    * The url attachment for the resource model
    */
   private String urlAttach = "/csv";
   private ResourceModel csvResource;

   /**
    * absolute url for dataset management REST API
    *
    * @return url
    */
   public final String getBaseDatasetUrl() {
     return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL) + "/" + DATASET_ID;
   }

   /*
    * (non-Javadoc)
    *
    * @see fr.cnes.sitools.SitoolsServerTestCase#setUp()
    */
   @Override
   @Before
   public void setUp() throws Exception {
     super.setUp();
     csvResource = createResourceModel(csvResourceModelClassName, "1000qsdqssdq", urlAttach);
     csvResource.getParameterByName("max_rows").setValue("2");
     create(csvResource, getBaseDatasetUrl());

   }

   /*
    * (non-Javadoc)
    *
    * @see fr.cnes.sitools.AbstractSitoolsServerTestCase#tearDown()
    */
   @Override
   @After
   public void tearDown() throws Exception {
     super.tearDown();
     delete(csvResource, getBaseDatasetUrl());
   }

   /**
    * Query the Resoure
    *
    * @throws java.io.IOException
    *           if the response cannot be read
    */
   @Test
   public void queryResourceCsv() throws IOException {
     String url = getHostUrl() + DATASET_URL + urlAttach;
     ClientResource cr = new ClientResource(url);
     cr.getRequest().getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.TEXT_CSV));
     Representation rep = cr.get(MediaType.TEXT_CSV);
     try {
       String res = rep.getText();
       assertNotNull(res);
       assertTrue(res.startsWith("#"));
     }
     finally {
       RIAPUtils.exhaust(rep);
       cr.release();
     }
   }

   /**
    * Get the total number of records with limit=0
    *
    * @throws java.io.IOException
    */
   @Test
   public void getCountCsv() throws IOException {
     String url = getHostUrl() + DATASET_URL + urlAttach;
     ClientResource cr = new ClientResource(url);
     cr.getRequest().getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.TEXT_CSV));
     cr.getRequest().getResourceRef().addQueryParameter("limit", "0");
     Representation rep = cr.get(MediaType.TEXT_CSV);
     try {
       String res = rep.getText();
       assertNotNull(res);
       assertTrue(res.matches("(.*\\n)*#NRECORDS : [0-9]*\\n(.*\\n)*"));
     }
     finally {
       cr.release();
       RIAPUtils.exhaust(rep);
     }
   }

 }
