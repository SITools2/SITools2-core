/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Custom DateUtils class for SITools2. Created from the DateUtils from Restlet 2.0.5
 *
 *
 * @author m.gond
 */
public final class DateUtils {
    /**
     * Default date format for date exchange between the server and the client in all the Sitools2 application
     */
    public static final String SITOOLS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    /**
     * Default date format for date exchange between the server and the client in all the Sitools2 application
     */
    public static final String SITOOLS_TIME_FORMAT = "HH:mm:ss.SSS";

    /** ISO_8601 format without time zone */
    public static final String FORMAT_ISO_8601_WITHOUT_TIME_ZONE = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String FORMAT_RFC_822_FOUR_DIGIT_YEAR = "EEE, dd MMM yyyy HH:mm:ss Z";

    public static final String FORMAT_RFC_3339 = "yyyy-MM-dd\'T\'HH:mm:ss\'Z\'";

    /**
     * Private constructor
     */
    private DateUtils() {

    }

    /**
     * Formats a Date according to the default date format.
     *
     * @param date
     *          The date to format.
     * @return The formatted date.
     */
    public static String format(final Date date) {
        return format(date, SITOOLS_DATE_FORMAT);
    }

    /**
     * Formats a Date according to the default date format.
     *
     * @param date
     *          The date to format.
     * @return The formatted date.
     */
    public static String formatTime(final Date date) {
        return format(date, SITOOLS_TIME_FORMAT);
    }

    /**
     * Formats a Date according to the first format in the array.
     *
     * @param date
     *          The date to format.
     * @param format
     *          The date format to use.
     * @return The formatted date.
     */
    public static String format(final Date date, final String format) {
        if (date == null) {
            throw new IllegalArgumentException("Date is null");
        }
        java.text.DateFormat formatter = null;
        formatter = new java.text.SimpleDateFormat(format, java.util.Locale.ROOT);

        return formatter.format(date);
    }

    /**
     * Parses a formatted date into a Date object with the default date format.
     *
     * @param date
     *          The date to parse.
     *
     * @return The parsed date.
     * @throws ParseException
     *           if there is an error while parsing the date
     */
    public static Date parse(String date) throws ParseException {
        return parse(date, SITOOLS_DATE_FORMAT);
    }

    /**
     * Parses a formatted date into a Date object.
     *
     * <p>
     * An IllegalArgumentException is raised when date is empty or <code>null</code>
     * </p>
     *
     * @param date
     *          The date to parse.
     * @param format
     *          The format of the date string
     * @return The parsed date.
     * @throws ParseException
     *           if there is an error while parsing the date
     */
    public static Date parse(String date, String format) throws ParseException {
        Date result = null;

        if (date == null || date.isEmpty()) {
            throw new IllegalArgumentException("Date is null");
        }

        java.text.DateFormat parser = null;

        parser = new java.text.SimpleDateFormat(format, java.util.Locale.ROOT);

        result = parser.parse(date);
        return result;
    }

    /**
     * Adds or subtracts the specified amount of time to the given calendar field, based on the calendar's rules. For
     * example, to subtract 5 days from the current time of the calendar, you can achieve it by calling:
     *
     *
     * @param date
     *          the Date
     * @param field
     *          the calendar field. A constant of the class {@link Calendar}
     * @param amount
     *          the amount of date or time to be added to the field.
     * @return a {@link Date} corresponding to date + minutes
     */
    public static Date add(Date date, int field, int amount) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(field, amount);
        return cal.getTime();

    }

}
