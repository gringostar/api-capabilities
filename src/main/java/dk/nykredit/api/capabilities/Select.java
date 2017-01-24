package dk.nykredit.api.capabilities;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Selection on the basis of attribute values.
 * <p>
 * Indicated by using an API Query Parameter called {@code select}.
 * <p>
 * The syntax is: {@code select="<attribute>::<value>|<atribute>::<value>|..."}
 * <p>
 * Example:
 * <p>
 * {@code https://banking.services.sample-bank.dk/accounts?select="no::123456789+|no::234567890"}
 * <p>
 * So the {@code select="no::123456789+|no::234567890" }
 * will return the two accounts having account numbers "123456789" and "234567890" and
 * thus it works as a way to select certain objects, in this case based on the semantic
 * key for an account.
 */
public class Select {
    private static final Pattern REGEX =
        Pattern.compile("^(([a-z][a-zA-Z_0-9]*)::([a-zA-Z_0-9]+)(-|\\+)?)?((\\|[a-z][a-zA-Z_0-9]*)?::([a-zA-Z_0-9]+)(-|\\+)?)*");
    private static final CapabilityParser<Select> PARSER = new CapabilityParser<>(REGEX, Select::parseToken);

    private String attribute = "";
    private String value = "";

    private Select(String attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getValue() {
        return value;
    }

    /**
     * Delivers a set of Select back containing a number of attributes from the part that is within
     * the {@code http:// ..../ some-resource?select="value"} that value may contain one or more attributes
     * which is part of the resource attributes for the endpoint that the request is targeting
     * <p>
     * the format of the {@code select="value"} is
     * {@code "attribute::value|anotherAttribute::thatValue|yetAnotherAttribute::thisValue"}
     * and so on, it may also take the form {@code "attribute::value|attribute::thatValue|attribute::thisValue"}
     * <p>
     * The regexp is:
     * {@code ^(([a-z][a-zA-Z_0-9]*)::([a-zA-Z_0-9]+)(-|\\+)?)?((\\|[a-z][a-zA-Z_0-9]*)?::([a-zA-Z_0-9]+)(-|\\+)?)*}
     *
     * @param select the select Query Parameter
     * @return a set of attribute(s) and value(s) used for selecting candidates for the response
     */
    public static List<Select> getSelections(String select) {
        return PARSER.parse(select);
    }

    private static Optional<Select> parseToken(String token) {
        String attribute = token.substring(0, token.indexOf(':'));
        return Optional.of(new Select(attribute, getValuefrom(token)));        
    }

    private static String getValuefrom(String selection) {
        int startsAt = selection.indexOf("::") + "::".length();
        int endsAt = !selection.contains("|") ? selection.length() : selection.indexOf('|');
        return selection.substring(startsAt, endsAt);
    }

}
