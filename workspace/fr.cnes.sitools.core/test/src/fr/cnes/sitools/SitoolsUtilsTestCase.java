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
package fr.cnes.sitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dictionary.DictionaryAdministration;
import fr.cnes.sitools.dictionary.DictionaryStoreXML;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.util.Util;

public class SitoolsUtilsTestCase extends AbstractSitoolsTestCase {
  /** DictionaryStoreXML */
  private static DictionaryStoreXML store = null;

  /**
   * Restlet Component for server
   */
  private Component component = null;
  /** The sitoolsSettings */
  private SitoolsSettings settings = SitoolsSettings.getInstance();

  /**
   * static xml store instance for the test
   */
  private Context context;
  /** The id of the dictionary to get */
  private String dicoId = "429c7ff8-960a-4a49-9565-59d5c189ad22";

  /** The number of dictionary expected */
  private int nbDico = 2;
  /** The name of the dictionary to get */
  private String dicoName = "Test dictionary";

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getDicoUrl() {
    return settings.getString(Consts.APP_DICTIONARIES_URL);
  }

  /**
   * Absolute path location for data set store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_DICTIONARIES_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Create component, store and application and start server  
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {

    

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);

      // Context
      context = this.component.getContext().createChildContext();
      context.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());
      if (store == null) {
        File storeDirectory = new File(getTestRepository());
        cleanDirectory(storeDirectory);
        store = new DictionaryStoreXML(storeDirectory, context);
      }
      context.getAttributes().put(ContextAttributes.APP_STORE, store);
      context.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());
      // this.component.getDefaultHost().attach(getAttachUrl(), new DictionaryAdministration(ctx));

      component.getInternalRouter().attach(getDicoUrl(), new DictionaryAdministration(context));

    }

    if (!this.component.isStarted()) {
      this.component.start();
    }

  }

  @After
  @Override
  /**
   * Stop server
   * @throws java.lang.Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();

    store.delete(dicoId);
    store.delete(dicoId + "_1");
    this.component.stop();
    this.component = null;

  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   * 
   * @throws SitoolsException
   * @throws IOException
   */
  @Test
  public void testUtil() {

    String test = new String("test");
    assertTrue(Util.isNotEmpty(test));
    assertFalse(Util.isEmpty(test));

    test = "true";
    assertTrue(Util.isTrue(test));
    test = "false";
    assertFalse(Util.isTrue(test));

    test = "test@akka.eu";
    assertTrue(Util.isValidEmail(test));
    test = "test";
    assertFalse(Util.isValidEmail(test));
  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   * 
   * @throws SitoolsException
   * @throws IOException
   */
  @Test
  public void testRiapUtils() {
    // create a dictionary
    Dictionary dico = new Dictionary();
    dico.setId(dicoId);
    dico.setName(dicoName);

    Dictionary model = RIAPUtils.persistObject(dico, getDicoUrl(), context);
    assertNotNull(model);
    assertEquals(dicoId, model.getId());

    // create another dictionary
    dico = new Dictionary();
    dico.setId(dicoId + "_1");
    dico.setName(dicoName + "_1");

    model = RIAPUtils.persistObject(dico, getDicoUrl(), context);
    assertNotNull(model);
    assertEquals(dicoId + "_1", model.getId());

    model = RIAPUtils.getObject(getDicoUrl() + "/" + dicoId, context);
    assertNotNull(model);
    assertEquals(dicoId, model.getId());

    model = RIAPUtils.getObject(dicoId, getDicoUrl(), context);
    assertNotNull(model);
    assertEquals(dicoId, model.getId());

    model = RIAPUtils.getObject(dicoId, getDicoUrl(), context, MediaType.APPLICATION_JAVA_OBJECT);
    assertNotNull(model);
    assertEquals(dicoId, model.getId());

    model = RIAPUtils.getObjectFromName(getDicoUrl(), dicoName, context);
    assertNotNull(model);
    assertEquals(dicoId, model.getId());
    assertEquals(dicoName, model.getName());

    List<Dictionary> models = RIAPUtils.getListOfObjects(getDicoUrl(), context);
    assertNotNull(models);
    assertEquals(nbDico, models.size());

    model.setName(model.getName() + "_modified");
    Dictionary modelModified = RIAPUtils.updateObject(model, getDicoUrl() + "/" + dicoId, context);
    assertNotNull(modelModified);
    assertEquals(dicoId, modelModified.getId());
    assertEquals(model.getName(), modelModified.getName());

    assertTrue(RIAPUtils.deleteObject(dico, getDicoUrl(), context));
    models = RIAPUtils.getListOfObjects(getDicoUrl(), context);
    assertNotNull(models);
    assertEquals(1, models.size());

    assertTrue(RIAPUtils.deleteObject(getDicoUrl() + "/" + dicoId, context));

    models = RIAPUtils.getListOfObjects(getDicoUrl(), context);
    assertNotNull(models);
    assertEquals(0, models.size());

  }

}
