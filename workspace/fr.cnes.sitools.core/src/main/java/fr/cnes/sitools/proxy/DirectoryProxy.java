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
package fr.cnes.sitools.proxy;

import fr.cnes.sitools.common.SitoolsMediaType;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.ext.wadl.*;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Directory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Directory proxy
 *
 * @author jp.boignard (AKKA Technologies)
 *
 */
public class DirectoryProxy extends Directory implements WadlDescribable {

    /** Proxy base reference */
    private String proxyBaseRef = null;

    /** Root node for serial */
    private String rootNode = "items";

    /** Regexp to file list restriction */
    private String regexp = null;

    /** true to set the no-cache directive on every response header */
    private boolean nocache = false;

    /**
     * Constructor
     *
     * @param context
     *          restlet context
     * @param rootLocalReference
     *          root local reference
     * @param proxyBaseRef
     *          proxy bas reference
     */
    public DirectoryProxy(Context context, Reference rootLocalReference, String proxyBaseRef) {
        super(context, rootLocalReference);

        // if ((proxyBaseRef != null) && !proxyBaseRef.equals("")) {
        setTargetClass(DirectoryProxyResource.class);
        this.proxyBaseRef = proxyBaseRef;
        // }
        this.setNegotiatingContent(false);
    }

    /**
     * Constructor with proxyBaseRef
     *
     * @param context
     *          restlet context
     * @param rootUri
     *          root URI
     * @param proxyBaseRef
     *          proxy base reference
     */
    public DirectoryProxy(Context context, String rootUri, String proxyBaseRef) {
        super(context, rootUri);
        // if ((proxyBaseRef != null) && !proxyBaseRef.equals("")) {
        setTargetClass(DirectoryProxyResource.class);
        this.proxyBaseRef = proxyBaseRef;
        // }
    }

    /**
     * Constructor
     *
     * @param context
     *          parent context
     * @param rootUri
     *          Directory path
     */
    public DirectoryProxy(Context context, String rootUri) {
        super(context, rootUri);
        setTargetClass(DirectoryProxyResource.class);
    }

    /**
     * Gets the proxyBaseRef value
     *
     * @return the proxyBaseRef
     */
    public String getProxyBaseRef() {
        return proxyBaseRef;
    }

    @Override
    public Representation getIndexRepresentation(Variant variant, ReferenceList indexContent) {
        Representation result = null;
        String publicHostDomain = ((SitoolsSettings) getContext().getAttributes().get(ContextAttributes.SETTINGS))
                .getPublicHostDomain();
        ReferenceList refListOut = rewriteReferenceList(indexContent, publicHostDomain);

        if (variant.getMediaType().isCompatible(MediaType.TEXT_HTML)) {
            result = refListOut.getWebRepresentation();
        } else if (variant.getMediaType().isCompatible(MediaType.TEXT_URI_LIST)) {
            result = refListOut.getTextRepresentation();
        } else if (variant.getMediaType().isCompatible(MediaType.APPLICATION_JSON)) {
            result = getJsonRepresentation(refListOut);
        } else if (variant.getMediaType().isCompatible(SitoolsMediaType.APPLICATION_SITOOLS_JSON_DIRECTORY)) {
            result = getAdvancedJsonRepresentation(refListOut);
        }
        return result;
    }

    /**
     * Rewrite the given {@link ReferenceList} with the following publicHostDomain instead of the ones from the
     * {@link Reference}
     *
     * @param indexContent
     *          the {@link ReferenceList}
     * @param publicHostDomain
     *          the publicHostDomain to set to all {@link Reference} in the {@link ReferenceList}
     * @return a new {@link ReferenceList}
     */
    private ReferenceList rewriteReferenceList(ReferenceList indexContent, String publicHostDomain) {
        ReferenceList out = null;
        boolean withFile = false;
        if (indexContent instanceof ReferenceFileList) {
            withFile = true;
            out = new ReferenceFileList();
        } else {
            out = new ReferenceList();
        }

        Reference identifier = rewriteReference(indexContent.getIdentifier(), publicHostDomain);
        out.setIdentifier(identifier);

        for (Reference reference : indexContent) {
            Reference rightUrl = rewriteReference(reference, publicHostDomain);
            if (withFile) {
                File file = ((ReferenceFileList) indexContent).get(reference.toString());
                ((ReferenceFileList) out).addFileReference(rightUrl.toString(), file);
            } else {
                out.add(rightUrl);
            }
        }
        return out;
    }

    /**
     * Rewrite the given reference. Basically changes the host to the given publicHostDomain given.
     *
     * @param reference
     *          the reference
     * @param publicHostDomain
     *          the public host domain
     * @return the reference
     */
    private Reference rewriteReference(Reference reference, String publicHostDomain) {
        // update url to set the publicHostDomain instead of the Origin domain, which can be the apache proxy
        Reference rightUrl = new Reference(publicHostDomain);
        rightUrl.setPath(reference.getPath());
        rightUrl.setQuery(reference.getQuery());
        rightUrl.setRelativePart(reference.getRelativePart());

        return rightUrl;
    }

    /**
     * Get JSon from reference
     *
     * @param reference
     *          the reference used
     * @return a JSON representation
     */
    protected Representation getJsonRepresentation(ReferenceList reference) {
        return new ReferenceListJsonRepresentation(MediaType.APPLICATION_JSON, reference);
    }

    /**
     * Get JSon from reference overrides DirectoryProxy getJsonRepresentation for producing a specific representation of a
     * user directory
     *
     * @param reference
     *          the reference used
     * @return a JSON representation
     */
    protected Representation getAdvancedJsonRepresentation(ReferenceList reference) {
        return new AdvancedReferenceListJsonRepresentation(SitoolsMediaType.APPLICATION_SITOOLS_JSON_DIRECTORY, reference);
    }

    /**
     * Get the root node
     *
     * @return the root node
     */
    private String getRootNode() {
        return rootNode;
    }

    /**
     * Returns the variant representations of a directory index. This method can be subclassed in order to provide
     * alternative representations.
     *
     * By default it returns a simple HTML document and a textual URI list as variants. Note that a new instance of the
     * list is created for each call.
     *
     * @param indexContent
     *          The list of references contained in the directory index.
     * @return The variant representations of a directory.
     */
    @Override
    public List<Variant> getIndexVariants(ReferenceList indexContent) {
        final List<Variant> result = new ArrayList<Variant>();
        result.add(new Variant(MediaType.TEXT_HTML));
        result.add(new Variant(MediaType.TEXT_URI_LIST));
        result.add(new Variant(MediaType.APPLICATION_JSON));
        result.add(new Variant(SitoolsMediaType.APPLICATION_SITOOLS_JSON_DIRECTORY));
        return result;
    }

    public ResourceInfo getResourceInfo(ApplicationInfo applicationInfo) {
        ResourceInfo resourceInfo = new ResourceInfo();
        //ExtendedResourceInfo.describe(applicationInfo, resourceInfo, this, this.getRootRef().getRelativePart());
        describe(resourceInfo);

        if (getName() != null && !"".equals(getName())) {
            DocumentationInfo doc = null;
            if (resourceInfo.getDocumentations().isEmpty()) {
                doc = new DocumentationInfo();
                resourceInfo.getDocumentations().add(doc);
            } else {
                doc = resourceInfo.getDocumentations().get(0);
            }

            doc.setTitle(getName());
            if (getDescription() != null && !getDescription().isEmpty()) {
                doc.setTextContent(getDescription());
            }

        }
        return resourceInfo;
    }

    /**
     * WADL describe method
     *
     * @param resource
     *          the ResourceInfo
     */
    private void describe(ResourceInfo resource) {

    }

    /**
     * Gets the regexp value
     *
     * @return the regexp
     */
    public String getRegexp() {
        return regexp;
    }

    /**
     * Sets the value of regexp
     *
     * @param regexp
     *          the regexp to set
     */
    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    /**
     * Sets the value of nocache
     *
     * @param nocache
     *          the nocache to set
     */
    public void setNocache(boolean nocache) {
        this.nocache = nocache;
    }

    /**
     * Gets the nocache value
     *
     * @return the nocache
     */
    public boolean isNocache() {
        return nocache;
    }

}
