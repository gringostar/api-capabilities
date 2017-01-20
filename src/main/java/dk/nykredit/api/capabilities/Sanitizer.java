package dk.nykredit.api.capabilities;

/**
 * API input sanitizer in a rudimental version.
 */
public class Sanitizer {
    private static final String[] SUSPICIOUS_CONTENT = {"\'", "\"", "\\", "%", "\\%", "\\_", "\0", "\b", "\n", "\t", "\r", "\\Z", "?", "#"};

    private Sanitizer(){
        // reduce scope to avoid default construction
    }

    /**
     * A simple sanitizer that needs to be extended and elaborated to cope with injections and
     * other things that pose as threats to the services and the data they contain and maintain.
     *
     * @param input an input string received from a non-trustworthy source (in reality every source)
     * @param allowSpaces should the string be stripped for spaces or allow these to stay
     * @return a sanitized string or an empty string if the sanitation failed for some reason.
     */
    public static String sanitize(String input, boolean allowSpaces) {
        if (null == input) {
            return "";
        }
        String result = input;
        if (!allowSpaces) {
            result = result.replaceAll(" ", "");
        }
        for (String s : SUSPICIOUS_CONTENT) {
            if (result.contains(s)) {
                return "";
            }
        }
        return result;
    }
}
