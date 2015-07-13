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
package fr.cnes.sitools.dictionary;

import fr.cnes.sitools.dictionary.model.Concept;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.persistence.XmlMapStore;
import org.restlet.Context;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DictionaryStoreXMLMap extends XmlMapStore<Dictionary> implements DictionaryStoreInterface {

    /** default location for file persistence */
    private static final String COLLECTION_NAME = "dictionaries";

    /**
     * Constructor with the XML file location
     *
     * @param location
     *          directory for file persistence
     * @param context
     *          the Restlet Context
     */
    public DictionaryStoreXMLMap(File location, Context context) {
        super(Dictionary.class, location, context);
    }

    /**
     * Default Constructor
     *
     * @param context
     *          the Restlet Context
     */
    public DictionaryStoreXMLMap(Context context) {
        super(Dictionary.class, context);
        File defaultLocation = new File(COLLECTION_NAME);
        init(defaultLocation);
    }

    public List<Dictionary> retrieveByParent(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCollectionName() {
        return COLLECTION_NAME;
    }

    @Override
    public void init(File location) {
        Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
        aliases.put("dictionary", Dictionary.class);
        this.init(location, aliases);
    }

    @Override
    public Dictionary update(Dictionary dictionary) {
        Dictionary result = null;

        Map<String, Dictionary> map = getMap();
        Dictionary current = map.get(dictionary.getId());
        if (current == null) {
            getLog().warning("Cannot update " + COLLECTION_NAME + " that doesn't already exists");
            return null;
        }
        getLog().info("Updating dictionary");

        result = current;
        current.setName(dictionary.getName());
        current.setDescription(dictionary.getDescription());
        current.setConceptTemplate(dictionary.getConceptTemplate());
        current.setConcepts(dictionary.getConcepts());
        // loop through the concepts and assign an id for each
        int i = 0;
        if (current.getConcepts() != null) {
            for (Iterator<Concept> iterator = current.getConcepts().iterator(); iterator.hasNext(); ) {
                Concept concept = iterator.next();
                concept.setId(new Integer(i++).toString());
            }
        }

        if (result != null) {
            map.put(dictionary.getId(), current);
        } else {
            getLog().info("Dictionary not found.");
        }

        return result;
    }

}
