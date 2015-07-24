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
package fr.cnes.sitools.dictionary.resource;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dictionary.model.ConceptTemplate;
import fr.cnes.sitools.notification.model.Notification;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Resource for concept template services
 *
 * @author jp.boignard (AKKA technologies)
 */
public class ConceptTemplateResource extends AbstractConceptTemplateResource {

    /** Template id in the request */
    private String templateId = null;

    @Override
    public void doInit() {
        super.doInit();
        templateId = getAttribute("templateId");
    }

    @Override
    public void sitoolsDescribe() {
        setName("ConceptTemplateResource");
        setDescription("Resource for managing a template of concept - CRUD");
        setNegotiated(false);
    }

    /**
     * get template
     */
    @Get
    public ConceptTemplate retrieveConceptTemplate() throws ResourceException {
        try {
            ConceptTemplate template = getStore().retrieve(templateId);
            if (template != null) {
                trace(Level.FINE, "Edit information for the dictionary structure " + template.getName());
                return template;
            } else {
                trace(Level.INFO, "Cannot edit information for the dictionary structure " + templateId);
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "cannot find dictionary stucture");
            }
        } catch (ResourceException e) {
            trace(Level.INFO, "Cannot view available dictionary structures");
            getLogger().log(Level.INFO, null, e);
            throw e;
        } catch (Exception e) {
            trace(Level.INFO, "Cannot view available dictionary structures");
            getLogger().log(Level.WARNING, null, e);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
        }
    }

    @Override
    protected void describeGet(MethodInfo info) {

        // -> Global method info
        info.setIdentifier("get_template");
        info.setDocumentation("Get a single template from its ID");

        this.addStandardGetRequestInfo(info);
        ParameterInfo param = new ParameterInfo("templateId", true, "xs:string", ParameterStyle.TEMPLATE,
                "Identifier of the template to work with.");
        info.getRequest().getParameters().add(param);
        this.addStandardResponseInfo(info);
        this.addStandardInternalServerErrorInfo(info);

    }

    /**
     * Update existing template
     */
    @Put
    public ConceptTemplate updateConceptTemplate(ConceptTemplate templateInput) throws ResourceException {
        ConceptTemplate templateOutput = null;
        try {
            if (templateInput != null) {
                // Parse object representation
                ConceptTemplate oldConceptTemplate = getStore().retrieve(templateInput.getId());

                // Business service
                templateOutput = getStore().update(templateInput);

                Map<String, ConceptTemplate> map = new HashMap<String, ConceptTemplate>();
                map.put("oldConceptTemplate", oldConceptTemplate);
                map.put("newConceptTemplate", templateInput);

                Notification notification = new Notification();
                notification.setObservable(templateId);
                notification.setEvent("TEMPLATE_UPDATED");
                notification.setMessage("template.delete.success");
                notification.setEventSource(map);

                getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);
            }

            if (templateOutput != null) {
                trace(Level.INFO, "Update information for the dictionary structure " + templateOutput.getName());
                return templateOutput;

            } else {
                trace(Level.INFO, "Cannot update information for the dictionary structure - id: " + templateId);
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
            }

        } catch (ResourceException e) {
            trace(Level.INFO, "Cannot update information for the dictionary structure - id: " + templateId);
            getLogger().log(Level.INFO, null, e);
            throw e;
        } catch (Exception e) {
            trace(Level.INFO, "Cannot update information for the dictionary structure - id: " + templateId);
            getLogger().log(Level.WARNING, null, e);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
        }
    }

    @Override
    protected void describePut(MethodInfo info) {

        // -> Global method info
        info.setIdentifier("modify_template");
        info.setDocumentation("Method to modify an existing template.");

        // -> Response info

        this.addStandardPostOrPutRequestInfo(info);
        ParameterInfo param = new ParameterInfo("templateId", true, "xs:string", ParameterStyle.TEMPLATE,
                "Identifier of the template to work with.");
        info.getRequest().getParameters().add(param);
        this.addStandardResponseInfo(info);

        // Failures
        this.addStandardInternalServerErrorInfo(info);

    }

    /**
     * Delete template
     */
    @Delete
    public void deleteConceptTemplate() throws ResourceException {
        try {
            ConceptTemplate templateToDelete = getStore().retrieve(templateId);
            if (templateToDelete != null) {
                // Business service
                getStore().delete(templateId);

                // Notify observers
                Notification notification = new Notification();
                notification.setObservable(templateId);
                notification.setEvent("TEMPLATE_DELETED");
                notification.setMessage("template.delete.success");
                notification.setEventSource(templateToDelete);
                getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

                trace(Level.INFO, "Delete the dictionary structure " + templateToDelete.getName());
                getResponse().setStatus(Status.SUCCESS_NO_CONTENT);

            } else {
                // Response
                String msg = "Delete the dictionary structure - id:" + templateId;
                trace(Level.INFO, msg);
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, msg);
            }

        } catch (ResourceException e) {
            getLogger().log(Level.INFO, null, e);
            throw e;
        } catch (Exception e) {
            getLogger().log(Level.WARNING, null, e);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
        }
    }

    @Override
    protected void describeDelete(MethodInfo info) {

        // -> Global method info
        info.setIdentifier("delete_template");
        info.setDocumentation("Method to delete an existing template.");

        this.addStandardGetRequestInfo(info);
        ParameterInfo param = new ParameterInfo("templateId", true, "xs:string", ParameterStyle.TEMPLATE,
                "Identifier of the template to work with.");
        info.getRequest().getParameters().add(param);
        this.addStandardSimpleResponseInfo(info);

        // Failures
        this.addStandardInternalServerErrorInfo(info);

    }

}
