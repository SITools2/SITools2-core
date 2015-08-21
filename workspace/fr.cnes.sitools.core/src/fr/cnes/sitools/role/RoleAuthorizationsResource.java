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
package fr.cnes.sitools.role;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;
import fr.cnes.sitools.security.authorization.client.RoleAndMethodsAuthorization;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Class Resource for managing authorizations from Role (GET UPDATE DELETE)
 *
 * @author jp.boignard (AKKA Technologies)
 */
public final class RoleAuthorizationsResource extends AbstractRoleResource {

    @Override
    public void sitoolsDescribe() {
        setName("RoleAuthorizationResource");
        setDescription("Resource for managing authorizations from a role - RUD");
        setNegotiated(false);
    }

    /**
     * get all authorizations from a role
     *
     * @param variant client preferred media type
     * @return Representation
     */
    @Get
    public Representation retrieveAuthorizations(Variant variant) {
        try {
            Role role = getStore().retrieve(getRoleId());
            Response response;
            if (role != null) {

                List<ResourceAuthorization> roleUsedInAuthorizationList = getResourceAuthorizations(role);
                response = new Response(true, roleUsedInAuthorizationList, ResourceAuthorization.class, "authorizations");
            } else {
                trace(Level.INFO, "Cannot view profile information for the profile - id: " + getRoleId());
                response = new Response(false, role, Role.class, "role");
            }
            return getRepresentation(response, variant);
        } catch (ResourceException e) {
            trace(Level.INFO, "Cannot view profile information for the profile - id: " + getRoleId());
            getLogger().log(Level.INFO, null, e);
            throw e;
        } catch (Exception e) {
            trace(Level.INFO, "Cannot view profile information for the profile - id: " + getRoleId());
            getLogger().log(Level.WARNING, null, e);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
        }
    }

    @Override
    public void describeGet(MethodInfo info) {
        info.setDocumentation("Method to get all authorizations attached to a role by ID.");
        this.addStandardGetRequestInfo(info);
        ParameterInfo paramRoleId = new ParameterInfo("roleId", true, "xs:string", ParameterStyle.TEMPLATE,
                "Identifier of the role to get.");
        info.getRequest().getParameters().add(paramRoleId);
        this.addStandardObjectResponseInfo(info);
        this.addStandardInternalServerErrorInfo(info);
    }

    /**
     * Delete all authorizations attached to a role
     *
     * @param variant client preferred media type
     * @return Representation
     */
    @Delete
    public Representation deleteAuthorizations(Variant variant) {
        try {
            // Business service
            Role roleOutput = getStore().retrieve(getRoleId());
            Response response;

            if (roleOutput != null) {

                List<ResourceAuthorization> roleUsedInAuthorizationList = getResourceAuthorizations(roleOutput);

                if (!roleUsedInAuthorizationList.isEmpty()) {
                    for (ResourceAuthorization authorization : roleUsedInAuthorizationList) {
                        List<RoleAndMethodsAuthorization> roleAuthorizationList = authorization.getAuthorizations();

                        for (Iterator<RoleAndMethodsAuthorization> iterator = roleAuthorizationList.iterator(); iterator.hasNext(); ) {
                            RoleAndMethodsAuthorization roleAuthorization = iterator.next();
                            if (roleAuthorization.getRole().equals(roleOutput.getName())) {
                                iterator.remove();

                                String url = getRoleApplication().getSettings().getString(Consts.APP_AUTHORIZATIONS_URL) + "/" + authorization.getId();
                                ResourceAuthorization authorizationOutput = RIAPUtils.updateObject(authorization, url, getContext());
                                trace(Level.INFO, "Removing Role '" + getRoleId() + "' from Authorization : " + authorizationOutput.getId());
                            }
                        }
                    }
                    response = new Response(true, "role.authorizations.deleted");
                } else {
                    response = new Response(true, "role.authorizations.empty");
                }
            } else {
                trace(Level.INFO, "Cannot delete profile - id: " + getRoleId());
                response = new Response(false, "role.delete.failure");
            }

            return getRepresentation(response, variant);

        } catch (ResourceException e) {
            getLogger().log(Level.INFO, null, e);
            throw e;
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, null, e);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
        }
    }

    @Override
    public void describeDelete(MethodInfo info) {
        info.setDocumentation("Method to delete a role by ID.");
        this.addStandardGetRequestInfo(info);
        ParameterInfo paramRoleId = new ParameterInfo("roleId", true, "xs:string", ParameterStyle.TEMPLATE,
                "Identifier of the role to get.");
        info.getRequest().getParameters().add(paramRoleId);
        this.addStandardSimpleResponseInfo(info);
        this.addStandardInternalServerErrorInfo(info);
    }
}
