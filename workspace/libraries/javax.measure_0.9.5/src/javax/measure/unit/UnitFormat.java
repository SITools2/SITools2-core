/**
 *   Copyright (c) 2005-2010, Jean-Marie Dautelle, Werner Keil
 *   All rights reserved.
 *
 *   See LICENSE.txt for the Specification License
 */
package javax.measure.unit;

import java.io.IOException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Locale;

import javax.measure.unit.format.LocalFormat;


/**
 * <p> This class provides the interface for formatting and parsing {@link
 *     Unit units}.</p>
 *
 * <p> For all metric units, the 20 SI prefixes used to form decimal
 *     multiples and sub-multiples of SI units are recognized.
 *     {@link USCustomarySystem US Customary} units are directly recognized. For example:[code]
 *        Unit.valueOf("m°C").equals(SI.MILLI(SI.CELSIUS))
 *        Unit.valueOf("kW").equals(SI.KILO(SI.WATT))
 *        Unit.valueOf("ft").equals(SI.METRE.multiply(3048).divide(10000))[/code]</p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author Eric Russell
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @version 1.1 ($Revision: 188 $), $Date: 2010-02-24 13:07:13 +0100 (Mi, 24 Feb 2010) $
 */
public abstract class UnitFormat extends Format {

    /**
     * Returns the unit format for the default locale.
     *
     * @return {@link LocalFormat#getInstance()}
     */
    public static UnitFormat getInstance() {
        return LocalFormat.getInstance();
    }

    /**
     * Returns the unit format for the specified locale.
     *
     * @param locale the locale for which the format is returned.
     * @return {@link LocalFormat#getInstance(java.util.Locale)}
     */
    public static UnitFormat getInstance(Locale locale) {
        return LocalFormat.getInstance(locale);
    }

    /**
     * Base constructor.
     */
    protected UnitFormat() {
    }

   /**
     * Returns the {@link UnitFormat.SymbolMap} for this unit format.
     *
     * @return the symbol map used by this format.
     */
    public abstract SymbolMap getSymbolMap();

    /**
     * Formats the specified unit.
     *
     * @param unit the unit to format.
     * @param appendable the appendable destination.
     * @return The appendable destination passed in as {@code appendable},
     *         with formatted text appended.
     * @throws IOException if an error occurs.
     */
    public abstract Appendable format(Unit<?> unit, Appendable appendable)
            throws IOException;

    /**
     * Parses a portion of the specified <code>CharSequence</code> from the
     * specified position to produce a unit. If there is no unit to parse
     * {@link Unit#ONE} is returned.
     *
     * @param csq the <code>CharSequence</code> to parse.
     * @param cursor the cursor holding the current parsing index.
     * @return the unit parsed from the specified character sub-sequence.
     * @throws IllegalArgumentException if any problem occurs while parsing the
     *         specified character sequence (e.g. illegal syntax).
     */
    public abstract Unit<?> parse(CharSequence csq, ParsePosition cursor)
            throws IllegalArgumentException;

    @Override
    public final StringBuffer format(Object obj, final StringBuffer toAppendTo,
            FieldPosition pos) {
        if (!(obj instanceof Unit<?>))
            throw new IllegalArgumentException("obj: Not an instance of Unit"); //$NON-NLS-1$
        if ((toAppendTo == null) || (pos == null))
            throw new NullPointerException(); // Format contract.
        try {
            return (StringBuffer) format((Unit<?>) obj, (Appendable) toAppendTo);
        } catch (IOException ex) {
            throw new Error(ex); // Cannot happen.
        }
    }

    @Override
    public final Unit<?> parseObject(String source, ParsePosition pos) {
        try {
            return parse(source, pos);
        } catch (IllegalArgumentException e) {
            return null; // Unfortunately the message why the parsing failed
        }                // is lost; but we have to follow the Format spec.

    }

    /**
     * Convenience method equivalent to {@link #format(Unit, Appendable)}
     * except it does not raise an IOException.
     *
     * @param unit the unit to format.
     * @param dest the appendable destination.
     * @return the specified <code>StringBuilder</code>.
     */
    final StringBuilder format(Unit<?> unit, StringBuilder dest) {
        try {
            return (StringBuilder) this.format(unit, (Appendable) dest);
        } catch (IOException ex) {
            throw new Error(ex); // Can never happen.
        }
    }

    /**
     * <p> This interface provides a set of mappings between
     *     {@link javax.measure.unit.Unit Units} and symbols (both ways),
     *     and from {@link javax.measure.unit.UnitConverter
     *     UnitConverter}s to prefixes symbols (also both ways).</p>
     */
    public interface SymbolMap {

        /**
         * Attaches a label to the specified unit. For example:[code]
         *    symbolMap.label(DAY.multiply(365), "year");
         *    symbolMap.label(NonSI.FOOT, "ft");
         * [/code]
         *
         * @param unit the unit to label.
         * @param symbol the new symbol for the unit.
         * @throws UnsupportedOperationException if setting a unit label
         *         is not allowed.
         */
        void label(Unit<?> unit, String symbol);

        /**
         * Attaches an alias to the specified unit. Multiple aliases may be
         * attached to the same unit. Aliases are used during parsing to
         * recognize different variants of the same unit.[code]
         *     symbolMap.alias(NonSI.FOOT, "foot");
         *     symbolMap.alias(NonSI.FOOT, "feet");
         *     symbolMap.alias(SI.METER, "meter");
         *     symbolMap.alias(SI.METER, "metre");
         * [/code]
         *
         * @param unit the unit to label.
         * @param symbol the new symbol for the unit.
         * @throws UnsupportedOperationException if setting a unit alias
         *         is not allowed.
         */
        void alias(Unit<?> unit, String symbol);

        /**
         * Attaches a label to the specified prefix. For example:[code]
         *    symbolMap.prefix(new RationalConverter(1000000000, 1), "G"); // GIGA
         *    symbolMap.prefix(new RationalConverter(1, 1000000), "µ"); // MICRO
         * [/code]
         *
         * @param cvtr the unit converter.
         * @param prefix the prefix for the converter.
         * @throws UnsupportedOperationException if setting a prefix
         *         is not allowed.
         */
        void prefix(UnitConverter cvtr, String prefix);

        /**
         * Returns the unit for the specified symbol.
         *
         * @param symbol the symbol.
         * @return the corresponding unit or <code>null</code> if none.
         */
        Unit<?> getUnit(String symbol);

        /**
         * Returns the symbol (label) for the specified unit.
         *
         * @param unit the corresponding symbol.
         * @return the corresponding symbol or <code>null</code> if none.
         */
        String getSymbol(Unit<?> unit);

        /**
         * Returns the unit converter for the specified prefix.
         *
         * @param prefix the prefix symbol.
         * @return the corresponding converter or <code>null</code> if none.
         */
        UnitConverter getConverter(String prefix);

        /**
         * Returns the prefix for the specified converter.
         *
         * @param converter the unit converter.
         * @return the corresponding prefix or <code>null</code> if none.
         */
        String getPrefix(UnitConverter converter);
    }

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2046025267890654321L;
}
