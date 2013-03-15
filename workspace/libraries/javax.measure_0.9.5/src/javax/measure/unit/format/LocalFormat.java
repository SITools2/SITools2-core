/**
 *   Copyright (c) 2005-2010, Jean-Marie Dautelle, Werner Keil
 *   All rights reserved.
 *
 *   See LICENSE.txt for the Specification License
 */
package javax.measure.unit.format;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.measure.unit.BaseUnit;
import javax.measure.unit.MetricSystem;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitConverter;
import javax.measure.unit.UnitFormat;

/**
 * <p> This class represents the local sensitive format.</p>
 *
 * <h3>Here is the grammar for Units in Extended Backus-Naur Form (EBNF)</h3>
 * <p>
 *   Note that the grammar has been left-factored to be suitable for use by a top-down
 *   parser generator such as <a href="https://javacc.dev.java.net/">JavaCC</a>
 * </p>
 * <table width="90%" align="center">
 *   <tr>
 *     <th colspan="3" align="left">Lexical Entities:</th>
 *   </tr>
 *   <tr valign="top">
 *     <td>&lt;sign&gt;</td>
 *     <td>:=</td>
 *     <td>"+" | "-"</td>
 *   </tr>
 *   <tr valign="top">
 *     <td>&lt;digit&gt;</td>
 *     <td>:=</td>
 *     <td>"0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"</td>
 *   </tr>
 *   <tr valign="top">
 *     <td>&lt;superscript_digit&gt;</td>
 *     <td>:=</td>
 *     <td>"⁰" | "¹" | "²" | "³" | "⁴" | "⁵" | "⁶" | "⁷" | "⁸" | "⁹"</td>
 *   </tr>
 *   <tr valign="top">
 *     <td>&lt;integer&gt;</td>
 *     <td>:=</td>
 *     <td>(&lt;digit&gt;)+</td>
 *   </tr>
 *   <tr valign="top">
 *     <td>&lt;number&gt;</td>
 *     <td>:=</td>
 *     <td>(&lt;sign&gt;)? (&lt;digit&gt;)* (".")? (&lt;digit&gt;)+ (("e" | "E") (&lt;sign&gt;)? (&lt;digit&gt;)+)? </td>
 *   </tr>
 *   <tr valign="top">
 *     <td>&lt;exponent&gt;</td>
 *     <td>:=</td>
 *     <td>( "^" ( &lt;sign&gt; )? &lt;integer&gt; ) <br>| ( "^(" (&lt;sign&gt;)? &lt;integer&gt; ( "/" (&lt;sign&gt;)? &lt;integer&gt; )? ")" ) <br>| ( &lt;superscript_digit&gt; )+</td>
 *   </tr>
 *   <tr valign="top">
 *     <td>&lt;initial_char&gt;</td>
 *     <td>:=</td>
 *     <td>? Any Unicode character excluding the following: ASCII control & whitespace (&#92;u0000 - &#92;u0020), decimal digits '0'-'9', '(' (&#92;u0028), ')' (&#92;u0029), '*' (&#92;u002A), '+' (&#92;u002B), '-' (&#92;u002D), '.' (&#92;u002E), '/' (&#92;u005C), ':' (&#92;u003A), '^' (&#92;u005E), '²' (&#92;u00B2), '³' (&#92;u00B3), '·' (&#92;u00B7), '¹' (&#92;u00B9), '⁰' (&#92;u2070), '⁴' (&#92;u2074), '⁵' (&#92;u2075), '⁶' (&#92;u2076), '⁷' (&#92;u2077), '⁸' (&#92;u2078), '⁹' (&#92;u2079) ?</td>
 *   </tr>
 *   <tr valign="top">
 *     <td>&lt;unit_identifier&gt;</td>
 *     <td>:=</td>
 *     <td>&lt;initial_char&gt; ( &lt;initial_char&gt; | &lt;digit&gt; )*</td>
 *   </tr>
 *   <tr>
 *     <th colspan="3" align="left">Non-Terminals:</th>
 *   </tr>
 *   <tr valign="top">
 *     <td>&lt;unit_expr&gt;</td>
 *     <td>:=</td>
 *     <td>&lt;compound_expr&gt;</td>
 *   </tr>
 *   <tr valign="top">
 *     <td>&lt;compound_expr&gt;</td>
 *     <td>:=</td>
 *     <td>&lt;add_expr&gt; ( ":" &lt;add_expr&gt; )*</td>
 *   </tr>
 *   <tr valign="top">
 *     <td>&lt;add_expr&gt;</td>
 *     <td>:=</td>
 *     <td>( &lt;number&gt; &lt;sign&gt; )? &lt;mul_expr&gt; ( &lt;sign&gt; &lt;number&gt; )?</td>
 *   </tr>
 *   <tr valign="top">
 *     <td>&lt;mul_expr&gt;</td>
 *     <td>:=</td>
 *     <td>&lt;exponent_expr&gt; ( ( ( "*" | "·" ) &lt;exponent_expr&gt; ) | ( "/" &lt;exponent_expr&gt; ) )*</td>
 *   </tr>
 *   <tr valign="top">
 *     <td>&lt;exponent_expr&gt;</td>
 *     <td>:=</td>
 *     <td>( &lt;atomic_expr&gt; ( &lt;exponent&gt; )? ) <br>| (&lt;integer&gt; "^" &lt;atomic_expr&gt;) <br>| ( ( "log" ( &lt;integer&gt; )? ) | "ln" ) "(" &lt;add_expr&gt; ")" )</td>
 *   </tr>
 *   <tr valign="top">
 *     <td>&lt;atomic_expr&gt;</td>
 *     <td>:=</td>
 *     <td>&lt;number&gt; <br>| &lt;unit_identifier&gt; <br>| ( "(" &lt;add_expr&gt; ")" )</td>
 *   </tr>
 * </table>
 *
 * @author <a href="mailto:eric-r@northwestern.edu">Eric Russell</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @version 1.4 ($Revision: 187 $), $Date: 2010-02-24 12:34:43 +0100 (Mi, 24 Feb 2010) $
 */
public class LocalFormat extends UnitFormat {

    //////////////////////////////////////////////////////
    // Class variables                                  //
    //////////////////////////////////////////////////////
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2046025264383639924L;

    /**
     * Name of the resource bundle
     */
    private static final String BUNDLE_NAME = LocalFormat.class.getPackage().getName() + ".messages"; //$NON-NLS-1$

    /** Default locale instance. If the default locale is changed after the class is
    initialized, this instance will no longer be used. */
    private static final LocalFormat DEFAULT_INSTANCE = new LocalFormat(new SymbolMapImpl(ResourceBundle.getBundle(BUNDLE_NAME)));

    /** Multiplicand character */
    private static final char MIDDLE_DOT = '\u00b7'; //$NON-NLS-1$

    /** Operator precedence for the addition and subtraction operations */
    private static final int ADDITION_PRECEDENCE = 0;

    /** Operator precedence for the multiplication and division operations */
    private static final int PRODUCT_PRECEDENCE = ADDITION_PRECEDENCE + 2;

    /** Operator precedence for the exponentiation and logarithm operations */
    private static final int EXPONENT_PRECEDENCE = PRODUCT_PRECEDENCE + 2;
    
    /**
     * Operator precedence for a unit identifier containing no mathematical
     * operations (i.e., consisting exclusively of an identifier and possibly
     * a prefix). Defined to be <code>Integer.MAX_VALUE</code> so that no
     * operator can have a higher precedence.
     */
    private static final int NOOP_PRECEDENCE = Integer.MAX_VALUE;

    ///////////////////
    // Class methods //
    ///////////////////
    /** Returns the instance for the current default locale (non-ascii characters are allowed) */
    public static LocalFormat getInstance() {
        return DEFAULT_INSTANCE;
    }

    /**
     * Returns an instance for the given locale.
     * @param locale
     */
    public static LocalFormat getInstance(Locale locale) {
        return new LocalFormat(new SymbolMapImpl(ResourceBundle.getBundle(BUNDLE_NAME, locale)));
    }

    /** Returns an instance for the given symbol map. */
    protected static LocalFormat getInstance(SymbolMapImpl symbols) {
        return new LocalFormat(symbols);
    }
    ////////////////////////
    // Instance variables //
    ////////////////////////
    /**
     * The symbol map used by this instance to map between
     * {@link javax.measure.Unit Unit}s and <code>String</code>s, etc...
     */
    private transient SymbolMapImpl symbolMap;

    //////////////////
    // Constructors //
    //////////////////
    /**
     * Base constructor.
     *
     * @param symbols the symbol mapping.
     */
    private LocalFormat(SymbolMapImpl symbols) {
        symbolMap = symbols;
    }

    ////////////////////////
    // Instance methods //
    ////////////////////////
    /**
     * Get the symbol map used by this instance to map between
     * {@link javax.measure.Unit Unit}s and <code>String</code>s, etc...
     * @return SymbolMap the current symbol map
     */
    public SymbolMapImpl getSymbolMap() {
        return symbolMap;
    }

    ////////////////
    // Formatting //
    ////////////////
    @Override
    public Appendable format(Unit<?> unit, Appendable appendable) throws IOException {
        formatInternal(unit, appendable);
        if (unit instanceof AnnotatedUnit<?>) {
        	AnnotatedUnit<?> annotatedUnit = (AnnotatedUnit<?>)unit;
	        if (annotatedUnit.getAnnotation() != null) {
	            appendable.append('{');
	            appendable.append(annotatedUnit.getAnnotation());
	            appendable.append('}');
	        }
        }
        return appendable;
    }

    @Override
    public Unit<?> parse(CharSequence csq, ParsePosition cursor) throws IllegalArgumentException {
        // Parsing reads the whole character sequence from the parse position.
        int start = cursor.getIndex();
        int end = csq.length();
        if (end <= start) {
            return Unit.ONE;
        }
        String source = csq.subSequence(start, end).toString().trim();
        if (source.length() == 0) {
            return Unit.ONE;
        }
        try {
            UnitParser parser = new UnitParser(symbolMap, new StringReader(source));
            Unit<?> result = parser.parseUnit();
            cursor.setIndex(end);
            return result;
        } catch (ParseException e) {
            if (e.currentToken != null) {
                cursor.setErrorIndex(start + e.currentToken.endColumn);
            } else {
                cursor.setErrorIndex(start);
            }
            throw new IllegalArgumentException(e.getMessage());
        } catch (TokenMgrError e) {
            cursor.setErrorIndex(start);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Format the given unit to the given StringBuffer, then return the operator
     * precedence of the outermost operator in the unit expression that was
     * formatted. See {@link ConverterFormat} for the constants that define the
     * various precedence values.
     * @param unit the unit to be formatted
     * @param buffer the <code>StringBuffer</code> to be written to
     * @return the operator precedence of the outermost operator in the unit
     *   expression that was output
     */
    private int formatInternal0(Unit<?> unit, Appendable buffer) throws IOException {
        String symbol = symbolMap.getSymbol(unit);
        if (symbol != null) {
            buffer.append(symbol);
            return NOOP_PRECEDENCE;
        } else {
            symbol = unit.getSymbol();
            if (symbol != null) {
                buffer.append(symbol);
                return NOOP_PRECEDENCE;
            }
        }
        Map<Unit<?>, Integer> units = unit.getProductUnits();
        if (units == null) { // Simple unit.
            buffer.append(unit.getConverterToMetric().toString());
            buffer.append(' ');
            buffer.append(unit.toMetric().toString());
            return NOOP_PRECEDENCE;
        }
        int negativeExponentCount = 0;
        boolean start = true;
        // Write positive exponents first...
        for (Map.Entry<Unit<?>, Integer> entry : units.entrySet()) {
            int pow = entry.getValue();
            if (pow >= 0) {
                formatExponent(entry.getKey(), pow, 1, !start, buffer);
                start = false;
            } else {
                negativeExponentCount += 1;
            }
        }
        // ..then write negative exponents.
        if (negativeExponentCount > 0) {
            if (start) {
                buffer.append('1');
            }
            buffer.append('/');
            if (negativeExponentCount > 1) {
                buffer.append('(');
            }
            start = true;
            for (Map.Entry<Unit<?>, Integer> entry : units.entrySet()) {
                int pow = entry.getValue();
                if (pow < 0) {
                    formatExponent(entry.getKey(), -pow, 1, !start, buffer);
                    start = false;
                }
            }
            if (negativeExponentCount > 1) {
                buffer.append(')');
            }
        }
        return PRODUCT_PRECEDENCE;
    }
    
    /**
     * Format the given unit to the given StringBuffer, then return the operator
     * precedence of the outermost operator in the unit expression that was 
     * formatted. See {@link ConverterFormat} for the constants that define the
     * various precedence values.
     * @param unit the unit to be formatted
     * @param buffer the <code>StringBuffer</code> to be written to
     * @return the operator precedence of the outermost operator in the unit 
     *   expression that was output
     */
    private int formatInternal(Unit<?> unit, Appendable buffer) throws IOException {
        if (unit instanceof AnnotatedUnit<?>) {
            unit = ((AnnotatedUnit<?>) unit).getActualUnit();
        }
        String symbol = symbolMap.getSymbol(unit);
        if (symbol != null) {
            buffer.append(symbol);
            return NOOP_PRECEDENCE;
        } else if (unit.getProductUnits() != null) {
            Map<Unit<?>, Integer> productUnit = unit.getProductUnits();
            int negativeExponentCount = 0;
            // Write positive exponents first...
            boolean start = true;
            for (Map.Entry<Unit<?>, Integer> e : productUnit.entrySet()) {
                int pow = e.getValue();
                if (pow >= 0) {
                    formatExponent(e.getKey(), pow, 1, !start, buffer);
                    start = false;
                } else {
                    negativeExponentCount += 1;
                }
            }
            // ..then write negative exponents.
            if (negativeExponentCount > 0) {
                if (start) {
                    buffer.append('1');
                }
                buffer.append('/');
                if (negativeExponentCount > 1) {
                    buffer.append('(');
                }
                start = true;
                for (Map.Entry<Unit<?>, Integer> e : productUnit.entrySet()) {
                    int pow = e.getValue();
                    if (pow < 0) {
                        formatExponent(e.getKey(), -pow, 1, !start, buffer);
                        start = false;
                    }
                }
                if (negativeExponentCount > 1) {
                    buffer.append(')');
                }
            }
            return PRODUCT_PRECEDENCE;
        } else if (unit instanceof BaseUnit<?>) {
            buffer.append(((BaseUnit<?>) unit).getSymbol());
            return NOOP_PRECEDENCE;
        } else if (unit.getSymbol() != null) { // Alternate unit.
            buffer.append(unit.getSymbol());
            return NOOP_PRECEDENCE;
        } else { // A transformed unit or new unit type!
            UnitConverter converter = null;
            boolean printSeparator = false;
            StringBuilder temp = new StringBuilder();
            int unitPrecedence = NOOP_PRECEDENCE;
            Unit<?> parentUnits = unit.toMetric();
            converter = unit.getConverterToMetric();
            if (parentUnits.equals(MetricSystem.KILOGRAM)) {
                // More special-case hackery to work around gram/kilogram 
                // incosistency
                if (unit.equals(MetricSystem.GRAM)) {
                    buffer.append("g");
                    return NOOP_PRECEDENCE;
                }
                parentUnits = MetricSystem.GRAM;
                converter = unit.getConverterTo((Unit)MetricSystem.GRAM);
            }
            unitPrecedence = formatInternal(parentUnits, temp);
            printSeparator = !parentUnits.equals(Unit.ONE);
            int result = formatConverter(converter, printSeparator, unitPrecedence, temp);
            buffer.append(temp);
            return result;
        } 
    }


    /**
     * Format the given unit raised to the given fractional power to the
     * given <code>StringBuffer</code>.
     * @param unit Unit the unit to be formatted
     * @param pow int the numerator of the fractional power
     * @param root int the denominator of the fractional power
     * @param continued boolean <code>true</code> if the converter expression
     *    should begin with an operator, otherwise <code>false</code>. This will
     *    always be true unless the unit being modified is equal to Unit.ONE.
     * @param buffer StringBuffer the buffer to append to. No assumptions should
     *    be made about its content.
     */
    private void formatExponent(Unit<?> unit, int pow, int root, boolean continued, Appendable buffer) throws IOException {
        if (continued) {
            buffer.append(MIDDLE_DOT);
        }
        StringBuffer temp = new StringBuffer();
        int unitPrecedence = formatInternal(unit, temp);

        if (unitPrecedence < PRODUCT_PRECEDENCE) {
            temp.insert(0, '('); //$NON-NLS-1$
            temp.append(')'); //$NON-NLS-1$
        }
        buffer.append(temp);
        if ((root == 1) && (pow == 1)) {
            // do nothing
        } else if ((root == 1) && (pow > 1)) {
            String powStr = Integer.toString(pow);
            for (int i = 0; i
                    < powStr.length(); i += 1) {
                char c = powStr.charAt(i);
                switch (c) {
                    case '0':
                        buffer.append('\u2070'); //$NON-NLS-1$
                        break;
                    case '1':
                        buffer.append('\u00b9'); //$NON-NLS-1$
                        break;
                    case '2':
                        buffer.append('\u00b2'); //$NON-NLS-1$
                        break;
                    case '3':
                        buffer.append('\u00b3'); //$NON-NLS-1$
                       break;
                    case '4':
                        buffer.append('\u2074'); //$NON-NLS-1$
                       break;
                    case '5':
                        buffer.append('\u2075'); //$NON-NLS-1$
                        break;
                    case '6':
                        buffer.append('\u2076'); //$NON-NLS-1$
                        break;
                    case '7':
                        buffer.append('\u2077'); //$NON-NLS-1$
                        break;
                   case '8':
                        buffer.append('\u2078'); //$NON-NLS-1$
                        break;
                    case '9':
                        buffer.append('\u2079'); //$NON-NLS-1$
                        break;
                }
            }
        } else if (root == 1) {
            buffer.append('^'); //$NON-NLS-1$
            buffer.append(String.valueOf(pow));
        } else {
            buffer.append("^("); //$NON-NLS-1$
            buffer.append(String.valueOf(pow));
            buffer.append('/'); //$NON-NLS-1$
            buffer.append(String.valueOf(root));
            buffer.append(')'); //$NON-NLS-1$
        }
    }
    
    /**
     * Formats the given converter to the given StringBuffer and returns the
     * operator precedence of the converter's mathematical operation. This is
     * the default implementation, which supports all built-in UnitConverter
     * implementations. Note that it recursively calls itself in the case of 
     * a {@link javax.measure.converter.UnitConverter.Compound Compound} 
     * converter.
     * @param converter the converter to be formatted
     * @param continued <code>true</code> if the converter expression should 
     *    begin with an operator, otherwise <code>false</code>.
     * @param unitPrecedence the operator precedence of the operation expressed
     *    by the unit being modified by the given converter.
     * @param buffer the <code>StringBuffer</code> to append to.
     * @return the operator precedence of the given UnitConverter
     */
    private int formatConverter(UnitConverter converter,
            boolean continued,
            int unitPrecedence,
            StringBuilder buffer) {
        ParsePrefix prefix = symbolMap.getPrefixObject(converter);
        if ((prefix != null) && (unitPrecedence == NOOP_PRECEDENCE)) {
            buffer.insert(0, symbolMap.getSymbol(prefix));
            return NOOP_PRECEDENCE;
        } else if (converter instanceof javax.measure.unit.converter.AddConverter) {
            if (unitPrecedence < ADDITION_PRECEDENCE) {
                buffer.insert(0, '(');
                buffer.append(')');
            }
            double offset = ((javax.measure.unit.converter.AddConverter) converter).getOffset();
            if (offset < 0) {
                buffer.append("-");
                offset = -offset;
            } else if (continued) {
                buffer.append("+");
            }
            long lOffset = (long) offset;
            if (lOffset == offset) {
                buffer.append(lOffset);
            } else {
                buffer.append(offset);
            }
            return ADDITION_PRECEDENCE;
        } else if (converter instanceof javax.measure.unit.converter.LogConverter) {
            double base = ((javax.measure.unit.converter.LogConverter) converter).getBase();
            StringBuffer expr = new StringBuffer();
            if (base == StrictMath.E) {
                expr.append("ln");
            } else {
                expr.append("log");
                if (base != 10) {
                    expr.append((int) base);
                }
            }
            expr.append("(");
            buffer.insert(0, expr);
            buffer.append(")");
            return EXPONENT_PRECEDENCE;
        } else if (converter instanceof javax.measure.unit.converter.ExpConverter) {
            if (unitPrecedence < EXPONENT_PRECEDENCE) {
                buffer.insert(0, '(');
                buffer.append(')');
            }
            StringBuffer expr = new StringBuffer();
            double base = ((javax.measure.unit.converter.ExpConverter) converter).getBase();
            if (base == StrictMath.E) {
                expr.append('e');
            } else {
                expr.append((int) base);
            }
            expr.append('^');
            buffer.insert(0, expr);
            return EXPONENT_PRECEDENCE;
        } else if (converter instanceof javax.measure.unit.converter.MultiplyConverter) {
            if (unitPrecedence < PRODUCT_PRECEDENCE) {
                buffer.insert(0, '(');
                buffer.append(')');
            }
            if (continued) {
                buffer.append(MIDDLE_DOT);
            }
            double factor = ((javax.measure.unit.converter.MultiplyConverter) converter).getFactor();
            long lFactor = (long) factor;
            if (lFactor == factor) {
                buffer.append(lFactor);
            } else {
                buffer.append(factor);
            }
            return PRODUCT_PRECEDENCE;
        } else if (converter instanceof javax.measure.unit.converter.RationalConverter) {
            if (unitPrecedence < PRODUCT_PRECEDENCE) {
                buffer.insert(0, '(');
                buffer.append(')');
            }
            javax.measure.unit.converter.RationalConverter rationalConverter = (javax.measure.unit.converter.RationalConverter) converter;
            if (!rationalConverter.getDividend().equals(BigInteger.ONE)) {
                if (continued) {
                    buffer.append(MIDDLE_DOT);
                }
                buffer.append(rationalConverter.getDividend());
            }
            if (!rationalConverter.getDivisor().equals(BigInteger.ONE)) {
                buffer.append('/');
                buffer.append(rationalConverter.getDivisor());
            }
            return PRODUCT_PRECEDENCE;
        } else {
            throw new IllegalArgumentException("Unable to format the given UnitConverter: " + converter.getClass());
        }
    }
}
