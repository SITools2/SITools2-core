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

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.plugins.applications.dto.ApplicationPluginModelDTO;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginModel;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

/**
 * Handle Actions on an ApplicationPluginModel object Actions are Start and Stop
 *
 * @author m.gond (AKKA Technologies)
 */
public class ApplicationPluginActionResource extends AbstractApplicationPluginResource {

    /** the ApplicationPluginModel object */
    private ApplicationPluginModel model;

    @Override
    public void sitoolsDescribe() {
        setName("ApplicationPluginActionResource");
        setDescription("Actions on an ApplicationPluginModel : start or stop.");
        setNegotiated(false);
    }

    /**
     * Main method, handle actions
     *
     * @param representation
     *          the representation
     * @param variant
     *          the variant needed
     * @return a Representation
     */
    @Put
    public Representation action(Representation representation, Variant variant) {
        Response response = null;
        ApplicationPluginStoreInterface store = getStore();

        synchronized (store) {
            model = store.retrieve(getAppId());

            do {

                if (model == null) {
                    response = new Response(false, "APP_PLUGIN_NOT_FOUND");
                    break;
                }

                if (this.getReference().toString().endsWith("start")) {
                    if ("ACTIVE".equals(model.getStatus())) {
                        response = new Response(false, "appPlugin.active");
                        break;
                    }

                    try {
                        // we attach the application and start it
                        getResourceApplication().attachApplication(model, true);
                    } catch (ClassNotFoundException e) {
                        response = new Response(false, "ClassNotFoundException");
                        break;
                    } catch (NoSuchMethodException e) {
                        response = new Response(false, "NoSuchMethodException");
                        break;
                    } catch (InstantiationException e) {
                        response = new Response(false, "InstantiationException");
                        break;
                    } catch (IllegalAccessException e) {
                        response = new Response(false, "IllegalAccessException");
                        break;
                    } catch (InvocationTargetException e) {
                        response = new Response(false, "InvocationTargetException");
                        break;
                    } catch (Exception e) {
                        response = new Response(false, "app.plugin.create.error");
                        this.getLogger().warning(
                                "APPLICATION PLUGIN :" + model.getName() + " cannot be started, error while starting : "
                                        + model.getClassName() + e);
                        break;
                    }
                    if (getResourceApplication().isStarted()) {
                        model = store.retrieve(model.getId());

                        ApplicationPluginModelDTO appModelOutDTO = getApplicationModelDTO(model);
                        response = new Response(true, appModelOutDTO, ApplicationPluginModelDTO.class, "ApplicationPluginModel");
                        response.setMessage("appPlugin.start.success");
                        trace(Level.INFO, "Start the application plugin " + appModelOutDTO.getName());
                    } else {
                        response = new Response(false, "appPlugin.start.error");
                        trace(Level.INFO, "Cannot start the application plugin ");
                    }

                }

                if (this.getReference().toString().endsWith("stop")) {
                    if ("INACTIVE".equals(model.getStatus())) {
                        response = new Response(false, "appPlugin.inactive");
                        trace(Level.INFO, "Cannot stop the application plugin ");
                        break;
                    }
                    getResourceApplication().detachApplication(model);

                    model = store.retrieve(model.getId());
                    ApplicationPluginModelDTO appModelOutDTO = getApplicationModelDTO(model);
                    response = new Response(true, appModelOutDTO, ApplicationPluginModelDTO.class, "ApplicationPluginModel");
                    response.setMessage("appPlugin.stop.success");
                    trace(Level.INFO, "Stop the application plugin " + appModelOutDTO.getName());
                }
            } while (false);
        }

        return getRepresentation(response, variant);
    }

    protected void describePut(MethodInfo info, String path) {
        info.setIdentifier("PUT /" + path);
        if (path.endsWith("start")) {
            info.setDocumentation(" PUT /" + path + " : Starts the dynamic application plugin.");
        } else if (path.endsWith("stop")) {
            info.setDocumentation(" PUT /" + path + " : Stops the dynamic application plugin.");
        } else {
            info.setDocumentation(" PUT /" + path + " : TO BE DESCRIBED.");
        }

        // Request
        this.addStandardGetRequestInfo(info);
        ParameterInfo paramInfo = new ParameterInfo("applicationPluginId", true, "xs:string", ParameterStyle.TEMPLATE,
                "Identifier of the class to use");
        info.getRequest().getParameters().add(paramInfo);

        // Response 200
        this.addStandardSimpleResponseInfo(info);
    }
}
