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
package fr.cnes.sitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.userstorage.UserStorageStoreInterface;
import fr.cnes.sitools.userstorage.model.DiskStorage;
import fr.cnes.sitools.userstorage.model.UserStorage;
import fr.cnes.sitools.userstorage.model.UserStorageStatus;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test UserStorage Rest API
 *
 * @since UserStory : ADM UserStorage, Sprint : 5
 *
 * @author AKKA Technologies
 */
public class UserStorageTestCase extends AbstractSitoolsServerTestCase {

    /** Root Directory for UserStorage */
    private static String userStoragePATH = null;

    /** URL for TESTS */
    private static final String ROOT_ADMIN = SitoolsSettings.getInstance().getString(Consts.APP_USERSTORAGE_URL)
            + "/users";

    /** user identifier for test purpose */
    private static final String USERSTORAGE = "identifier";

    /** action to notify user by mail of disk quota exceeding */
    private static final String ACTION = "notify";

    static {
        setMediaTest(MediaType.APPLICATION_JSON);
    }

    @Before
    @Override
    /**
     * Create component, store and application and start server
     * @throws java.lang.Exception
     */
    public void setUp() throws Exception {
        super.setUp();
        userStoragePATH = "${ROOT_DIRECTORY}" + TEST_FILES_REPOSITORY
                + SitoolsSettings.getInstance().getString("Starter.USERSTORAGE_ROOT") + "/"; // au lieu de file separator
    }

    @After
    @Override
    /**
     * Stop server
     * @throws java.lang.Exception
     */
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Tests quota exceeding notification action.
     */
    @Test
    public void quotaExceededNotifySuccess() {
        try {
            ClientResource cr = new ClientResource(getBaseUrl() + ROOT_ADMIN + "/" + USERSTORAGE);
            UserStorage userStorage = new UserStorage();
            userStorage.setStatus(UserStorageStatus.DISACTIVE);
            userStorage.setUserId(USERSTORAGE);
            DiskStorage disk = new DiskStorage();
            disk.setQuota(new Long(50));
            disk.setBusyUserSpace(new Long(200000));
            userStorage.setStorage(disk);

            //programmatically create a store
            UserStorageStoreInterface store = (UserStorageStoreInterface) settings.getStores().get(Consts.APP_STORE_USERSTORAGE);
            store.create(userStorage);

            // userstorage.notify.success
            cr = new ClientResource(getBaseUrl() + ROOT_ADMIN + "/" + USERSTORAGE + "/" + ACTION);
            Representation rep = cr.put(null);
            assertNotNull(rep);
            assertTrue(cr.getStatus().isSuccess());
            String expected = "{  \"success\": true,  " + "\"message\": \"userstorage.notify.success\"";
            String result = "";
            try {
                result = rep.getText();
            } catch (IOException e) {
                Assert.fail(e.getMessage());
            }
            result = result.replaceAll("[\r\n]+", "");
            if (!result.startsWith(expected)) {
                Assert.fail(result + "<>" + expected);
            }
        } finally {
            testUserStorageDelete();
        }
    }

    /**
     * Tests quota execeeding notification action when not necessary. //
     */
    @Test
    public void quotaExceededNotifyUnnecessary() {
        try {
            ClientResource cr = new ClientResource(getBaseUrl() + ROOT_ADMIN + "/" + USERSTORAGE);
            UserStorage userStorage = new UserStorage();
            userStorage.setStatus(UserStorageStatus.DISACTIVE);
            userStorage.setUserId(USERSTORAGE);
            DiskStorage disk = new DiskStorage();
            disk.setQuota(new Long(100000));
            disk.setBusyUserSpace(new Long(50000));

            userStorage.setStorage(disk);
            Representation rep = createUserStorage(userStorage);
            assertNotNull(rep);

            Response response = getResponse(getMediaTest(), rep, UserStorage.class, false);
            assertTrue(response.getSuccess());
            assertNotNull(response.getItem());
            assertTrue((response.getItem() instanceof UserStorage));
            // userstorage.notify.success
            String params = "?start=0&limit=10&media=json";
            cr = new ClientResource(getBaseUrl() + ROOT_ADMIN + params);
            Representation resul = cr.get();
            try {
                resul.getText();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            cr = new ClientResource(getBaseUrl() + ROOT_ADMIN + "/" + USERSTORAGE + "/" + ACTION);
            rep = cr.put(null);
            assertNotNull(rep);
            assertTrue(cr.getStatus().isSuccess());
            String expected = "{  \"success\": true,  " + "\"message\": \"userstorage.notify.unnecessary\"";
            String result = "";
            try {
                result = rep.getText();
            } catch (IOException e) {
                Assert.fail(e.getMessage());
            }
            result = result.replaceAll("[\r\n]+", "");
            if (!result.startsWith(expected)) {
                Assert.fail(result + "<>" + expected);
            }
        } finally {
            testUserStorageDelete();
        }
    }

    /**
     * Test Create UserStorage
     */
    @Test
    public void testUserStorageCreate() {
        try {
            UserStorage userStorage = new UserStorage();
            userStorage.setStatus(UserStorageStatus.DISACTIVE);
            userStorage.setUserId(USERSTORAGE);
            DiskStorage disk = new DiskStorage();
            disk.setQuota(new Long(100000));
            disk.setBusyUserSpace(new Long(50000));

            userStorage.setStorage(disk);
            Representation result = createUserStorage(userStorage);
            assertNotNull(result);

            Response response = getResponse(getMediaTest(), result, UserStorage.class, false);
            assertTrue(response.getSuccess());
            assertNotNull(response.getItem());
            assertTrue((response.getItem() instanceof UserStorage));

            UserStorage userStorageResp = (UserStorage) response.getItem();
            checkUserStorage(userStorageResp, userStorage);
            // TODO Create pref for check directory on HD
        } finally {
            testUserStorageDelete();
        }
    }

    /**
     * Bad Storage test case : no identifier avec constructeur de copie throws an exception
     */
    @Test
    public void testUserStorageBadCreate() {
        UserStorageStoreInterface store = (UserStorageStoreInterface) settings.getStores()
                .get(Consts.APP_STORE_USERSTORAGE);
        UserStorage userStorage = new UserStorage();
        boolean exceptionThrown = false;
        try {
            store.create(userStorage);
        } catch (Exception se) {
            exceptionThrown = true;
            assertEquals(se.getMessage(), "USERSTORAGE_USERIDENTIFIER_MANDATORY");
        }
        assertTrue(exceptionThrown);
    }

    /**
     * Test DataSet Metadatas. TODO SPRINT 6
     *
     */
    @Test
    public void testUserStorageList() {

        UserStorage userStorage = new UserStorage();
        userStorage.setStatus("Disactive");
        userStorage.setUserId(USERSTORAGE);
        DiskStorage disk = new DiskStorage();
        disk.setQuota(new Long(100000));

        userStorage.setStorage(disk);

        // createUserStorage(userStorage);
        Response response = getList();

        assertTrue(response.getSuccess());
        assertNotNull(response.getData());
        ArrayList<Object> userStorages = (ArrayList<Object>) response.getData();
        Assert.assertEquals(1, userStorages.size());
        // for (UserStorage userStorageResp : userStorages) {
        // assertNotNull(userStorageResp);
        // checkUserStorage(userStorageResp, userStorage);
        // }
    }

    /**
     * Test DataSet Metadatas. TODO SPRINT 6
     *
     */
    @Test
    public void testUserStorageDelete() {

        ClientResource cr = new ClientResource(getBaseUrl() + ROOT_ADMIN + "/" + USERSTORAGE);
        Representation result = cr.delete();
        assertTrue(cr.getStatus().isSuccess());
        assertNotNull(result);

        RIAPUtils.exhaust(result);
    }

    /**
     * Compares two UserStorage (expected and to check)
     *
     * @param userStorageResp
     *          UserStorage
     * @param expected
     *          UserStorage
     */
    public void checkUserStorage(UserStorage userStorageResp, UserStorage expected) {
        Assert.assertEquals(userStorageResp.getUserId() + " <> " + expected.getUserId(),
                expected.getUserId(), userStorageResp.getUserId());

        DiskStorage diskResp = userStorageResp.getStorage();
        assertNotNull(diskResp);

        Assert.assertEquals(diskResp.getQuota() + " <> " + expected.getStorage().getQuota(), expected.getStorage().getQuota(), diskResp.getQuota());
        Assert.assertEquals(diskResp.getUserStoragePath() + " <> " + userStoragePATH + userStorageResp.getUserId(), userStoragePATH + userStorageResp.getUserId(), diskResp.getUserStoragePath());
    }

    /**
     * Invokes GET method for getting user storage list with different parameters and in json format.
     *
     * @return Response
     */
    public Response getList() {
        String params = "?start=0&limit=10&media=json";
        ClientResource cr = new ClientResource(getBaseUrl() + ROOT_ADMIN + params);
        Representation result = cr.get();
        assertTrue(cr.getStatus().isSuccess());
        assertNotNull(result);
        Response response = getResponse(getMediaTest(), result, UserStorage.class, true);

        RIAPUtils.exhaust(result);

        return response;
    }

    /**
     * REST API Response wrapper for single item expected.
     *
     * @param media
     *          MediaType expected
     * @param representation
     *          service response representation
     * @param dataClass
     *          class expected in the item property of the Response object
     * @return Response the response.
     */
    public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass) {
        return getResponse(media, representation, dataClass, false);
    }

    /**
     * REST API Response Representation wrapper for single or multiple items expected
     *
     * @param media
     *          MediaType expected
     * @param representation
     *          service response representation
     * @param dataClass
     *          class expected for items of the Response object
     * @param isArray
     *          if true wrap the data property else wrap the item property
     * @return Response
     */
    public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass, boolean isArray) {
        try {
            if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
                Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
                return null;
            }

            XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
            configure(xstream);

            if (isArray) {
                xstream.addImplicitCollection(Response.class, "data", dataClass);
            } else {
                xstream.alias("item", dataClass);
                xstream.alias("item", Object.class, dataClass);

                if (dataClass == UserStorage.class) {
                    xstream.aliasField("userstorage", Response.class, "item");
                }
            }
            xstream.aliasField("data", Response.class, "data");

            SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
            rep.setXstream(xstream);

            if (media.isCompatible(getMediaTest())) {
                Response response = rep.getObject("response");
                return response;
            } else {
                Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
                return null; // TODO complete test for XML, Object
            }
        } finally {
            RIAPUtils.exhaust(representation);
        }
    }

    /**
     * Basic XStream configuration for parser/writer
     *
     * @param xstream
     *          XStream
     */
    private static void configure(XStream xstream) {
        xstream.autodetectAnnotations(false);
        xstream.alias("response", Response.class);
        xstream.alias("userstorage", UserStorage.class);
        xstream.alias("diskStorage", DiskStorage.class);
    }

    /**
     * Calls POST method for creating a new UserStorage
     *
     * @param userStorage
     *          UserStorage
     * @return Representation UserStorage representation
     */
    private Representation createUserStorage(UserStorage userStorage) {
        Representation rep = new JacksonRepresentation<UserStorage>(userStorage);
        ClientResource cr = new ClientResource(getBaseUrl() + ROOT_ADMIN);
        Representation result = cr.post(rep, getMediaTest());
        assertTrue(cr.getStatus().isSuccess());

        // RIAPUtils.exhaust(result);
        return result;
    }
}
