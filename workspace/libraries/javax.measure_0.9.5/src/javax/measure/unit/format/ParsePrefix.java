/**
 *   Copyright (c) 2005-2010, Jean-Marie Dautelle, Werner Keil
 *   All rights reserved.
 *
 *   See LICENSE.txt for the Specification License
 */
package javax.measure.unit.format;

import javax.measure.unit.converter.RationalConverter;
import java.math.BigInteger;
import javax.measure.unit.UnitConverter;


/**
 * This class represents the prefixes recognized when parsing/formatting.
 *
 * @version 1.0
 */
enum ParsePrefix  {

    YOTTA(new RationalConverter(BigInteger.TEN.pow(24), BigInteger.ONE)),
    ZETTA(new RationalConverter(BigInteger.TEN.pow(21), BigInteger.ONE)),
    EXA(new RationalConverter(BigInteger.TEN.pow(18), BigInteger.ONE)),
    PETA(new RationalConverter(BigInteger.TEN.pow(15), BigInteger.ONE)),
    TERA(new RationalConverter(BigInteger.TEN.pow(12), BigInteger.ONE)),
    GIGA(new RationalConverter(BigInteger.TEN.pow(9), BigInteger.ONE)),
    MEGA(new RationalConverter(BigInteger.TEN.pow(6), BigInteger.ONE)),
    KILO(new RationalConverter(BigInteger.TEN.pow(3), BigInteger.ONE)),
    HECTO(new RationalConverter(BigInteger.TEN.pow(2), BigInteger.ONE)),
    DEKA(new RationalConverter(BigInteger.TEN.pow(1), BigInteger.ONE)),
    DECI(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(1))),
    CENTI(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(2))),
    MILLI(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(3))),
    MICRO(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(6))),
    NANO(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(9))),
    PICO(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(12))),
    FEMTO(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(15))),
    ATTO(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(18))),
    ZEPTO(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(21))),
    YOCTO(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(24)));

    private final UnitConverter converter;

    /**
     * Creates a new prefix.
     *
     * @param converter the associated unit converter.
     */
    ParsePrefix (UnitConverter converter) {
        this.converter = converter;
    }

    /**
     * Returns the corresponding unit converter.
     *
     * @return the unit converter.
     */
    public UnitConverter getConverter() {
        return converter;
    }
}
