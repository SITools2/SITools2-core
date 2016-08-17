 /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package org.restlet.ext.solr.internal;

import java.util.Iterator;

import org.apache.solr.common.params.SolrParams;
import org.restlet.Request;
import org.restlet.data.Form;

/**
 * Wrap Restlet query parameters as Solr params.
 * 
 * @author Remi Dewitte <remi@gide.net>
 */
public class SolrRestletParams extends SolrParams {

    private static final long serialVersionUID = 1L;

    /** The wrapped Restlet request. */
    private final Request request;

    /**
     * Constructor.
     * 
     * @param request
     *            The wrapped Restlet request.
     */
    public SolrRestletParams(Request request) {
        this.request = request;
    }

    /**
     * Returns the request query form.
     * 
     * @return The request query form.
     */
    protected Form getForm() {
        return request.getResourceRef().getQueryAsForm();
    }

    /**
     * Reads parameter from the form returned {@link #getForm()}.
     * 
     */
    @Override
    public String get(String param) {
        return getForm().getFirstValue(param);
    }

    /**
     * Reads parameter names from the form returned {@link #getForm()}.
     * 
     */
    @Override
    public Iterator<String> getParameterNamesIterator() {
        return getForm().getNames().iterator();
    }

    /**
     * Reads parameter values from the form returned {@link #getForm()}.
     * 
     */
    @Override
    public String[] getParams(String param) {
        return getForm().getValuesArray(param);
    }

}