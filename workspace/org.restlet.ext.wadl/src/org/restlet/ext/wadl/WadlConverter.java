 /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package org.restlet.ext.wadl;

import java.io.IOException;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.engine.converter.ConverterHelper;
import org.restlet.engine.resource.VariantInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.UniformResource;

/**
 * A converter helper to convert between {@link ApplicationInfo} objects and
 * {@link WadlRepresentation} ones.
 * 
 * @author Thierry Boileau
 */
public class WadlConverter extends ConverterHelper {

    private static final VariantInfo VARIANT_APPLICATION_WADL = new VariantInfo(
            MediaType.APPLICATION_WADL);

    @Override
    public List<Class<?>> getObjectClasses(Variant source) {
        List<Class<?>> result = null;

        if (VARIANT_APPLICATION_WADL.includes(source)) {
            result = addObjectClass(result, ApplicationInfo.class);
        }

        return result;
    }

    @Override
    public List<VariantInfo> getVariants(Class<?> source) {
        List<VariantInfo> result = null;

        if (ApplicationInfo.class.isAssignableFrom(source)) {
            result = addVariant(result, VARIANT_APPLICATION_WADL);
        }

        return result;
    }

    @Override
    public <T> float score(Representation source, Class<T> target,
            UniformResource resource) {
        float result = -1.0F;

        if ((source != null)
                && (ApplicationInfo.class.isAssignableFrom(target))) {
            result = 1.0F;
        }

        return result;
    }

    @Override
    public float score(Object source, Variant target, UniformResource resource) {
        if (source instanceof ApplicationInfo) {
            return 1.0f;
        }

        return -1.0f;
    }

    @Override
    public <T> T toObject(Representation source, Class<T> target,
            UniformResource resource) throws IOException {
        Object result = null;

        if (ApplicationInfo.class.isAssignableFrom(target)) {
            if (source instanceof WadlRepresentation) {
                result = ((WadlRepresentation) source).getApplication();
            } else {
                result = new WadlRepresentation(source).getApplication();
            }
        }

        return target.cast(result);
    }

    @Override
    public Representation toRepresentation(Object source, Variant target,
            UniformResource resource) throws IOException {
        if (source instanceof ApplicationInfo) {
            return new WadlRepresentation((ApplicationInfo) source);
        }

        return null;
    }

    @Override
    public <T> void updatePreferences(List<Preference<MediaType>> preferences,
            Class<T> entity) {
        if (ApplicationInfo.class.isAssignableFrom(entity)) {
            updatePreferences(preferences, MediaType.APPLICATION_WADL, 1.0F);
        }
    }
}
