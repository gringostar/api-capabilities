package io.openapitools.api.capabilities;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * A composition signals that a consumer of a given API resource
 * wants to have a given concept included in a projection.
 * <p>
 * Composition is about enabling the consumers of services, the Query Parameter
 * {@code embed} is used to signal to the service that the consumer would
 * like to have a certain assumed related object included as a part of the
 * response if possible.
 * <p>
 * The syntax is: {@code embed="<concept>::<projection>|<concept>::<projection>|..."}
 * <p>
 * Example:
 * {@code https://banking.services.sample-bank.dk/accounts/1234-56789?embed="transaction::list|owner::sparse"}
 * <p>
 * which ideally will return a json response including {@code _links} and {@code _embeddded} objects
 * inside the response containing either a map or array of transactions with links in the
 * {@code _links} object and the desired projection in the {@code _embedded} object
 * for both owner and transactions.
 * <p>
 * The service can choose to return just the accounts including links to transactions under
 * the {@code _links} object as this is allowed by HAL.
 * <p>
 * The Query Parameter can be used for evolving the service to match the desires of consumers
 * - if many consumers are having the same wishes for what to embed
 * - the owners of the service could start considering whether they want to include
 * more in the responses and endure the added coupling between this service and the
 * service that may deliver the embedded information.
 * <p>
 * This coupling should of course not be synchronous.
 */
public final class Composition {

    private static final Pattern REGEX = Pattern.compile("^(([a-zA-Z_0-9]+)?::([a-zA-Z_0-9]+))?((\\|[a-zA-Z_0-9]+)?::([a-zA-Z_0-9]+))*");
    private static final CapabilityParser<Composition> PARSER = new CapabilityParser<>(REGEX, Composition::parseToken);
    
    private final String concept;
    private final String projection;

    private Composition(String concept, String projection) {
        this.concept = concept;
        this.projection = projection;
    }

    String getConcept() {
        return this.concept;
    }

    String getProjection() {
        return this.projection;
    }
    /**
     * Signals to the server that a consumer would like a composition of objects in reponse.
     *
     * Delivers a set of Compositions back containing a number of attributes from the part that is within
     * the http:// ..../ some-resource?embed="value" that value may contain one or more concepts and projections
     * which is part of the resource attributes for the endpoint that the request is targeting
     * the format of the {@code select = "value"} where {@code "value"} is
     * {@code "concept::projection|otherconcept::thisprojection|..." } and so on.
     * <p>
     * The regexp is: {@code "^(([a-zA-Z_0-9]+)?::([a-zA-Z_0-9]+))?((\\|[a-zA-Z_0-9]+)?::([a-zA-Z_0-9]+))*"}
     *
     * @param embed the embed Query Parameter
     * @return a Set of concepts and projections
     */
    public static List<Composition> getEmbedded(String embed) {
        return PARSER.parse(embed);
    }
    
    private static Optional<Composition> parseToken(String token) {
        String concept = getConcept(token);
        if (!concept.isEmpty()) {
            return Optional.of(new Composition(concept, getProjection(token)));
        }
        return Optional.empty();                                            
    }

    private static String getConcept(String compositionPair) {
        int end = compositionPair.indexOf(':');
        if (end > 0) {
            return compositionPair.substring(0, end);
        }
        return "";
    }

    private static String getProjection(String compositionPair) {
        int startsAt = compositionPair.indexOf("::") + "::".length();
        int endsAt = compositionPair.indexOf('|');
        return compositionPair.substring(startsAt, endsAt > 0 ? endsAt : compositionPair.length());
    }

}
