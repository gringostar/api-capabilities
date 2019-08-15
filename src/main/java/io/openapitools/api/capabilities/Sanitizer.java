package io.openapitools.api.capabilities;

import java.util.regex.Pattern;

/**
 * API input sanitizer in a rudimental version.
 */
public final class Sanitizer {
    private static final String[] SUSPICIOUS_CONTENT = {"\'", "\"", "\\", "%", "\\%", "\\_", "\0", "\b", "\n", "\t", "\r", "\\Z", "?", "#"};

    private Sanitizer() {
        // reduce scope to avoid default construction
    }

    /**
     * Joined and escaped string of suspicious content.
     *
     * @return String.
     */
    static String regexQuotedSuspiciousContent() {
        return Pattern.quote(String.join("", SUSPICIOUS_CONTENT));
    }

    /**
     * A simple sanitizer that needs to be extended and elaborated to cope with injections and
     * other things that pose as threats to the services and the data they contain and maintain.
     *
     * @param input        an input string received from a non-trustworthy source (in reality every source)
     * @param allowSpaces  should the string be stripped for spaces or allow these to stay
     * @param allowNumbers can the input contain numbers or not
     * @return a sanitized string or an empty string if the sanitation failed for some reason.
     */
    public static String sanitize(String input, boolean allowSpaces, boolean allowNumbers) {
        if (null == input) {
            return "";
        }
        String result = input;
        if (!allowSpaces) {
            result = result.replaceAll(" ", "");
        }
        if (!allowNumbers) {
            result = result.matches(".*\\d.*") ? "" : result;
        }
        for (String s : SUSPICIOUS_CONTENT) {
            if (result.contains(s)) {
                return "";
            }
        }
        return result;
    }

    /**
     * A default version of the santizer used from the local capabilities and thus
     *
     * @param input       an input string received from a non-trustworthy source (in reality every source)
     * @param allowSpaces should the string be stripped for spaces or allow these to stay
     * @return a sanitized string or an empty string if the sanitation failed for some reason
     */

    static String sanitize(String input, boolean allowSpaces) {
        return sanitize(input, allowSpaces, true);
    }
}
