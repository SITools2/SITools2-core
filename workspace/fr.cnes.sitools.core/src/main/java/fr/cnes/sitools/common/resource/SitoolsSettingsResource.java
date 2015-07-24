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
package fr.cnes.sitools.common.resource;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.util.Util;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import java.util.List;

/**
 * Simple utility class to expose sitools.properties
 *
 * @author jp.boignard (AKKA Technologies)
 */
public class SitoolsSettingsResource extends AbstractSitoolsResource {

    /** parent application */
    private SitoolsApplication application = null;

    @Override
    protected void doInit() {
        super.doInit();

        // Declares the two variants supported
        addVariant(new Variant(MediaType.APPLICATION_XML));
        addVariant(new Variant(MediaType.APPLICATION_JSON));

        application = (SitoolsApplication) getApplication();
    }

    protected void addVariant(Variant variant) {
        List<Variant> variants = getVariants();
        if (!variants.contains(variant)) {
            variants.add(variant);
        }
    }

    @Override
    public void sitoolsDescribe() {
        setName("SitoolsSettingsResource");
        setDescription("Simple utility class to expose sitools.properties");
    }

    /**
     * Get a setting value for the PARAMETER request attribute
     *
     * @return StringRepresentation of setting value
     */
    @Get
    public Representation getSettings() {
        String parameter = (String) getRequest().getAttributes().get("PARAMETER");
        if (Util.isNotEmpty(parameter)) {
            return new StringRepresentation(application.getSettings().getString(parameter));
        } else {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
        }
    }


    // TODO PUT to dynamically change settings values ...

}
