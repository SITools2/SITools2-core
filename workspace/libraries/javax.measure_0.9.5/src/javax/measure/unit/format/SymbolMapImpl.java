/**
 *   Copyright (c) 2005-2010, Jean-Marie Dautelle, Werner Keil
 *   All rights reserved.
 *
 *   See LICENSE.txt for the Specification License
 */
package javax.measure.unit.format;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitConverter;
import javax.measure.unit.UnitFormat;


/**
 * <p> This class holds the default implementation of the SymbolMap
 *     interface.</p>
 *
 * <p> No attempt is made to verify the uniqueness of the mappings.</p>
 *
 * <p> Mappings are read from a <code>ResourceBundle</code>, the keys
 *     of which should consist of a fully-qualified class name, followed
 *     by a dot ('.'), and then the name of a static field belonging
 *     to that class, followed optionally by another dot and a number.
 *     If the trailing dot and number are not present, the value
 *     associated with the key is treated as a
 *     {@linkplain SymbolMap#label(javax.measure.Unit, String) label},
 *     otherwise if the trailing dot and number are present, the value
 *     is treated as an {@linkplain SymbolMap#alias(javax.measure.Unit,String) alias}.
 *     Aliases map from String to Unit only, whereas labels map in both
 *     directions. A given unit may have any number of aliases, but may
 *     have only one label.</p>
 *
 * @author <a href="mailto:eric-r@northwestern.edu">Eric Russell</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @version 1.4 ($Revision: 180 $), $Date: 2010-02-23 16:11:17 +0100 (Di, 23 Feb 2010) $
 */
public class SymbolMapImpl implements UnitFormat.SymbolMap {

    private final Map<String, Unit<?>> symbolToUnit;
    private final Map<Unit<?>, String> unitToSymbol;
    private final Map<String, Object> symbolToPrefix;
    private final Map<Object, String> prefixToSymbol;
    private final Map<UnitConverter, ParsePrefix> converterToPrefix;

    /**
     * Creates an empty mapping.
     */
    public SymbolMapImpl () {
        symbolToUnit = new HashMap<String, Unit<?>>();
        unitToSymbol = new HashMap<Unit<?>, String>();
        symbolToPrefix = new HashMap<String, Object>();
        prefixToSymbol = new HashMap<Object, String>();
        converterToPrefix = new HashMap<UnitConverter, ParsePrefix>();
    }

    /**
     * Creates a symbol map from the specified resource bundle,
     *
     * @param rb the resource bundle.
     */
    public SymbolMapImpl (ResourceBundle rb) {
        this();
        for (Enumeration<String> i = rb.getKeys(); i.hasMoreElements();) {
            String fqn = i.nextElement();
            String symbol = rb.getString(fqn);
            boolean isAlias = false;
            int lastDot = fqn.lastIndexOf('.');
            String className = fqn.substring(0, lastDot);
            String fieldName = fqn.substring(lastDot+1, fqn.length());
            if (Character.isDigit(fieldName.charAt(0))) {
                isAlias = true;
                fqn = className;
                lastDot = fqn.lastIndexOf('.');
                className = fqn.substring(0, lastDot);
                fieldName = fqn.substring(lastDot+1, fqn.length());
            }
            try {
                Class<?> c = Class.forName(className);
                Field field = c.getField(fieldName);
                Object value = field.get(null);
                if (value instanceof Unit<?>) {
                    if (isAlias) {
                        alias((Unit<?>)value, symbol);
                    } else {
                        label((Unit<?>)value, symbol);
                    }
                } else if (value instanceof ParsePrefix) {
                    label((ParsePrefix)value, symbol);
                } else {
                    throw new ClassCastException("unable to cast "+value+" to Unit or Prefix");
                }
            } catch (Exception e) {
                System.err.println("ERROR reading Unit names: " + e.toString());
            }
        }
    }

    public void label (Unit<?> unit, String symbol) {
        symbolToUnit.put(symbol, unit);
        unitToSymbol.put(unit, symbol);
    }

    public void alias (Unit<?> unit, String symbol) {
        symbolToUnit.put(symbol, unit);
    }

    public void prefix (UnitConverter cvtr, String symbol) {
        throw new UnsupportedOperationException("Prefixes are not modifiable");
    }

    public Unit<?> getUnit (String symbol) {
        return symbolToUnit.get(symbol);
    }

    public String getSymbol (Unit<?> unit) {
        return unitToSymbol.get(unit);
    }

    public String getPrefix (UnitConverter cvtr) {
        ParsePrefix prefix = getPrefixObject(cvtr);
        if (prefix == null) return null;
        return prefixToSymbol.get(prefix);
    }

   public UnitConverter getConverter(String prefix) {
        ParsePrefix prefixObject = (ParsePrefix) symbolToPrefix.get(prefix);
        if (prefixObject == null) return null;
        return prefixObject.getConverter();
    }

    /**
     * Attaches a label to the specified prefix. For example:[code]
     *    symbolMap.label(Prefix.GIGA, "G");
     *    symbolMap.label(Prefix.MICRO, "µ");
     * [/code]
     */
    void label(ParsePrefix prefix, String symbol) {
        symbolToPrefix.put(symbol, prefix);
        prefixToSymbol.put(prefix, symbol);
        converterToPrefix.put(prefix.getConverter(), prefix);
    }

    /**
     * Returns the prefix (if any) for the specified symbol.
     *
     * @param symbol the unit symbol.
     * @return the corresponding prefix or <code>null</code> if none.
     */
    ParsePrefix getPrefix (String symbol) {
        for (Iterator<String> i = symbolToPrefix.keySet().iterator(); i.hasNext(); ) {
            String pfSymbol = i.next();
            if (symbol.startsWith(pfSymbol)) {
                return (ParsePrefix)symbolToPrefix.get(pfSymbol);
            }
        }
        return null;
    }

    /**
     * Returns the prefix for the specified converter.
     *
     * @param converter the unit converter.
     * @return the corresponding prefix or <code>null</code> if none.
     */
    ParsePrefix getPrefixObject (UnitConverter converter) {
        return converterToPrefix.get(converter);
    }

    /**
     * Returns the symbol for the specified prefix.
     *
     * @param prefix the prefix.
     * @return the corresponding symbol or <code>null</code> if none.
     */
    String getSymbol (ParsePrefix prefix) {
        return prefixToSymbol.get(prefix);
    }
}
