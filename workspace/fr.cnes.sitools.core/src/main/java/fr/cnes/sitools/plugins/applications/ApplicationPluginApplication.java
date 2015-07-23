/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * <p/>
 * This file is part of SITools2.
 * <p/>
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.cnes.sitools.plugins.applications;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.plugins.applications.business.AbstractApplicationPlugin;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginModel;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Router;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Application to handle ApplicationPluginModel. It offers a CRUD on ApplicationPluginModel and instantiate and attach
 * new Applications
 *
 *
 * @author m.gond (AKKA Technologies)
 */
public final class ApplicationPluginApplication extends SitoolsApplication {

    /** Store */
    private ApplicationPluginStoreInterface store = null;

    /** host parent router */
    private Router parentRouter = null;

    /**
     * Constructor
     *
     * @param parentRouter
     *          the parent Router
     *
     * @param context
     *          Restlet Host Context
     *
     */
    public ApplicationPluginApplication(Router parentRouter, Context context) {
        super(context);
        this.store = (ApplicationPluginStoreInterface) context.getAttributes().get(ContextAttributes.APP_STORE);
        this.parentRouter = parentRouter;

        // start all ApplicationPlugins
        Collection<ApplicationPluginModel> apps = store.getList();

        // int nb = store.getList().size();
        for (Iterator<ApplicationPluginModel> iterator = apps.iterator(); iterator.hasNext(); ) {
            ApplicationPluginModel app = iterator.next();
            if ("ACTIVE".equals(app.getStatus())) {
                try {
                    this.attachApplication(app, true);
                } catch (NoSuchMethodException e) {
                    this.getLogger().warning(
                            "APPLICATION PLUGIN :" + app.getName() + " cannot be started, constructor cannot be found in class : "
                                    + app.getClassName() + e);
                } catch (InstantiationException e) {
                    this.getLogger().warning(
                            "APPLICATION PLUGIN :" + app.getName() + " cannot be started, class : " + app.getClassName()
                                    + " cannot be instanciated " + e);
                } catch (IllegalAccessException e) {
                    this.getLogger().warning(
                            "APPLICATION PLUGIN :" + app.getName() + " cannot be started, IllegalAccessException with class : "
                                    + app.getClassName() + e);
                } catch (InvocationTargetException e) {
                    this.getLogger().warning(
                            "APPLICATION PLUGIN :" + app.getName() + " cannot be started, InvocationTargetException with class : "
                                    + app.getClassName() + e);
                } catch (ClassNotFoundException e) {
                    this.getLogger().warning(
                            "APPLICATION PLUGIN :" + app.getName() + " cannot be started, class : " + app.getClassName()
                                    + " cannot be found " + e);
                } catch (Exception e) {
                    this.getLogger().warning(
                            "APPLICATION PLUGIN :" + app.getName() + " cannot be started, error while starting : "
                                    + app.getClassName() + e);
                }
            }
        }

    }

    @Override
    public void sitoolsDescribe() {
        setCategory(Category.ADMIN);
        setName("ApplicationPluginApplication");
        setDescription("Application for administration of application plugins. Also expose the application plugin classes");
    }

    @Override
    public Restlet createInboundRoot() {

        Router router = new Router(getContext());

        router.attachDefault(ApplicationPluginCollectionResource.class);

        // GET : gets the list of registered applications
        router.attach("/classes", ApplicationPluginListingCollectionResource.class);
        // GET : gets the representation of a registered class of application
        router.attach("/classes/{applicationPluginClass}", ApplicationPluginListingResource.class);

        router.attach("/instances", ApplicationPluginCollectionResource.class);
        router.attach("/instances/{applicationPluginId}", ApplicationPluginResource.class);
        router.attach("/instances/{applicationPluginId}/start", ApplicationPluginActionResource.class);
        router.attach("/instances/{applicationPluginId}/stop", ApplicationPluginActionResource.class);

        router.setDefaultMatchingMode(Router.MODE_BEST_MATCH);

        return router;
    }

    /**
     * Gets the store value
     *
     * @return the store
     */
    public ApplicationPluginStoreInterface getStore() {
        return store;
    }

    /**
     * Create and attach a DataSetApplication according to the given DataSet object
     *
     * @param model
     *          the model
     * @param start
     *          indicates if the application has to be started
     * @throws Exception
     *           when attach fails
     */
    public void attachApplication(ApplicationPluginModel model, boolean start) throws Exception {

        @SuppressWarnings("unchecked")
        Class<AbstractApplicationPlugin> classImpl = (Class<AbstractApplicationPlugin>) Class.forName(model.getClassName());

        Context appContext = parentRouter.getContext().createChildContext();
        // Le register est fait explicitement dans le constructeur de la plugin application
        // une fois l'instance complètement initialisée
        appContext.getAttributes().put(ContextAttributes.SETTINGS, getSettings());
        appContext.getAttributes().put(ContextAttributes.APP_REGISTER, false);
        appContext.getAttributes().put(ContextAttributes.APP_ATTACH_REF, model.getUrlAttach());
        appContext.getAttributes().put(ContextAttributes.APP_ID, model.getId());
        appContext.getAttributes().put(ContextAttributes.APP_STORE, store);

        Class<?>[] objParam = new Class<?>[2];
        objParam[0] = Context.class;
        objParam[1] = ApplicationPluginModel.class;

        Constructor<AbstractApplicationPlugin> constructor;

        constructor = classImpl.getConstructor(objParam);

        AbstractApplicationPlugin appImpl = constructor.newInstance(appContext, model);

        getSettings().getAppRegistry().attachApplication(appImpl, start);

    }

    /**
     * Detach the application according to the given ApplicationPluginModel object
     *
     * @param model
     *          ApplicationPluginModel object
     */
    public void detachApplication(ApplicationPluginModel model) {

        Request reqPOST = new Request(Method.PUT, RIAPUtils.getRiapBase()
                + getSettings().getString(Consts.APP_APPLICATIONS_URL) + "/" + model.getId() + "?action=stop");

        ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
        objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_ALL_XML));
        reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);

        org.restlet.Response r = getContext().getClientDispatcher().handle(reqPOST);
        try {
            if (r == null || Status.isError(r.getStatus().getCode())) {
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
            }
        } finally {
            r.release();
        }
    }

    /**
     * Detach definitively the application according to the given ApplicationPluginModel object
     *
     * @param appOutput
     *          the ApplicationPluginModel
     */
    public void detachApplicationDefinively(ApplicationPluginModel appOutput) {
        this.detachApplication(appOutput);
        Request reqDELETE = new Request(Method.DELETE, RIAPUtils.getRiapBase()
                + getSettings().getString(Consts.APP_APPLICATIONS_URL) + "/" + appOutput.getId());

        ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
        objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_ALL_XML));
        reqDELETE.getClientInfo().setAcceptedMediaTypes(objectMediaType);

        org.restlet.Response r = getContext().getClientDispatcher().handle(reqDELETE);
        try {
            if (r == null || Status.isError(r.getStatus().getCode())) {
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
            }
        } finally {
            r.release();
        }
    }

    @Override
    public ApplicationInfo getApplicationInfo(Request request, Response response) {
        ApplicationInfo result = super.getApplicationInfo(request, response);
        DocumentationInfo docInfo = new DocumentationInfo("Application plug-in management.");
        docInfo.setTitle("API documentation.");
        result.getDocumentations().add(docInfo);
        return result;
    }

}
