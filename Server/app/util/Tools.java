package util;

import com.fasterxml.jackson.databind.JsonNode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static play.mvc.Controller.request;
import static play.mvc.Controller.session;

/**
 * Miscellaneous functions and constants being used throughout the application.
 */
public final class Tools {

    /**
     * Set to private to avoid instantiation.
     */
    private Tools() {}

    /**
     * The date format being used in the application.
     */
    public static final String DATE_FORMAT =  "yyyy-MM-dd HH:mm:ss";

    /**
     * Check if any of the given strings are null or empty, if that's the case it returns true, otherwise false.
     *
     * @param strings An array of String objects.
     * @return true if any string is null or an empty string, false otherwise.
     */
    public static boolean isAnyStringEmpty(String... strings) {
        for (String string : strings) {
            if (string == null || string.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if all attributes are present in a JSON object.
     *
     * @param node JsonNode object.
     * @param attributes Array of attributes that should be present in the node.
     * @return True if all attributes are present, False if not.
     */
    public static boolean jsonHasAttributes(JsonNode node, String... attributes) {
        for (String attribute : attributes)
            if (!node.has(attribute))
                return false;
        return true;
    }

    /**
     * Interpret it as a unix timestamp or as a date string, and create a Date object out of it.
     *
     * @param dateString Date string with the format "yyyy-MM-dd HH:mm:ss" or a unix timestamp as a String.
     * @return A Date object.
     * @throws ParseException If the dateString isn't a unix timestamp and has the wrong date format.
     */
    public static Date parseDate(String dateString) throws ParseException {
        try {
            Long unixTimestamp = Long.parseLong(dateString);
            // Multiply with 1000 because Date expect the timestamp as milliseconds.
            return new Date(1000 * unixTimestamp);
        } catch (NumberFormatException e) {
            return new SimpleDateFormat(DATE_FORMAT).parse(dateString);
        }
    }

    /**
     * Check if the user is authenticated.
     *
     * @return True if authenticated, otherwise false.
     */
    public static boolean isAuthenticated() {
        return session().get("email") != null;
    }

}
