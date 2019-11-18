## API Capability Set

In every API of a certain size a number of capabilities are used to provide a particular set of functionality.

[![Build status](https://travis-ci.org/openapi-tools/api-capabilities.svg?branch=master)](https://travis-ci.org/openapi-tools/api-capabilities)
[![Coverage Status](https://codecov.io/gh/openapi-tools/api-capabilities/coverage.svg?branch=master)](https://codecov.io/gh/openapi-tools/api-capabilities)
[![Technical Debt](https://sonarcloud.io/api/badges/measure?key=io.openapitools.api%3Acapabilities&metric=sqale_debt_ratio)](https://sonarcloud.io/dashboard?id=io.openapitools.api3%capabilities)

This example contains the following capabilities:

- Selection: `select`
  - selects objects by attribute value(s)
- Sorting: `sort`
  - sorts objects descending or ascending by attribute(s)
- Temporal: `interval`
  - limits objects to those within a certain time frame
- Pagination: `elements`
  - specifies objects within in a range
- Filtering: `filter`
  - excludes or includes objects based on attribute(s)
- Composition: `embed`
  - includes "related" objects and projections into the response

These capabilities may be applied individually to endpoints in APIs.

The user of the API endpoint can see what capabilities are supported at each endpoint by looking for tags like select, sort, paginate etc.
The Swagger tags are used here to achieve an easy way to show the capabilities in each endpoint as can be seen below in the Swagger
specification.

Another perspective that is often seen in APIs is the use of technical keys (potentially UUIDs) which are semantically poor, but often seen as
a necessity for sensitive keys such as social security numbers. In order to avoid having such sensitive information leaked to logs etc. there is
a need for bringing these keys into the body of a request and a non-sensitive key is going to help.
The problem with an UUID'ish key is that the developer experience is not optimal. Therefore it would be a nice thing to get some form of
consensus on a derived capability like :

Sensitive Semantic ID deconstruction

- generation of non-sensitive semantic key for objects that has a sensitive semantical key in the form of something that has a better developer
  experience than e.g. UUIDs can offer.

### Selection API Capability

Selection by criteria is done using a Query Parameter called `select`.

The syntax is:

    select="<attribute>::<value>|<atribute>::<value>|..."

#### Simple Example

    https://banking.services.sample-bank.dk/accounts?select="balance::100"

selects accounts having an exact balance of 100.

#### Selecting an interval

    https://banking.services.sample-bank.dk/accounts?select="balance::100+|balance::1000-"

selects accounts having a balance between 100 and 1000 (both inclusive).

#### Selecting multiple objects

    https://banking.services.sample-bank.dk/accounts?select="no::123456789|no::234567890"

selects the two accounts having account numbers "123456789" and "234567890".

#### Selecting with wildcards

    https://banking.services.sample-bank.dk/accounts?select="name::savings*|name::*loan*"

selects accounts having a name starting with "savings" or containing "loan"

### Sorting API Capability

Sorting is done using a `sort` Query Parameter. Sort order can be either ascending (default) or descending.

The syntax is:

    sort="<attribute>::+/-|<attribute>::+/-|..."

#### Simple Example

    https://banking.services.sample-bank.dk/accounts?sort=balance

sorts accounts by ascending balance.

#### Sorting on two properties

    https://banking.services.sample-bank.dk/accounts?sort=balance|lastUpdate::-

sorts accounts by ascending balance and descending lastUpdate.

### Temporal API Capability

Temporal aspects are handled using the `interval` Query Parameter.

The syntax is:

    interval="<now/from/to/at/::+/-/#d/#/now>|<now/from/to/at/::+/-/#d/#>"

#### Example

    https://banking.services.sample-bank.dk/accounts/1234-56789/transactions?interval="from::-14d|to::now"

returns the transactions from a specific account within the last 14 days.

#### More examples

    https://banking.services.sample-bank.dk/accounts/1234-56789/transactions?interval="from::1476449846|to::now"
    https://banking.services.sample-bank.dk/accounts/1234-56789/transactions?interval="from::1476449846"
    https://banking.services.sample-bank.dk/accounts/1234-56789/transactions?interval="at::1476449846"

All three return the transactions from a specific account within the last day assuming it is friday the 14th of October 2016 UTC time.
To be absolutely clear about the time it is milliseconds from the epoch of 1970-01-01T00:00:00Z.

### Pagination API Capability

Pagination of responses is obtained by using the Query parameter `elements` which signals the initial element and the last element to be part of
the response.

The syntax is:

    elements="<startingFrom>|<endingAt>"

both inclusive.

The maximum element count is 500.

#### Example:

    https://banking.services.sample-bank.dk/accounts/1234-56789/transactions?elements="10|30"

returns elements 10 to 30.

    https://banking.services.sample-bank.dk/accounts/1234-56789/transactions?elements="10|10"
    https://banking.services.sample-bank.dk/accounts/1234-56789/transactions?elements="10"
    
both returns element 10.

### Filtering API Capability

The Query parameters `filter` is used for requesting a dynamic projection. The service is not obliged to be able to support this, but may return
the standard projection of the objects given for that endpoint. This can be used for discovery of what projections service consumers would like
to have and help evolving the API to stay relevant and aligned with the consumers use of the service.

The syntax is:

    filter="<attribute>::+/-|<attribute>::+/-"

* `+` means include
* `-` means exclude

#### Example of exclude

    https://banking.services.sample-bank.dk/accounts/1234-56789?filter="balance::-|name::-"

ideally returns an account object without balance and name attributes.

The service may however choose not to support this and return a complete object and not this sparse dynamic view.

#### Example of include

    https://banking.services.sample-bank.dk/accounts/1234-56789?filter="balance::+|name::+"

ideally returns an account object with only balance and name attributes.

The service may however choose not to support this and return a complete object and not this sparse dynamic view.

### Composition API Capability

Composition is about enabling the service consumers to compose objects. The query parameter `embed` signals that the consumer wants a certain
related object included as a part of the response if possible.

The syntax is:

    embed="<concept>::<projection>|<concept>::<projection>|..."

#### Example:

    https://banking.services.sample-bank.dk/accounts/1234-56789?embed="transaction::list|owner::sparse"

ideally returns a json response including `_links` and `_embeddded` objects inside the response containing either a map or array of transactions
with links in the `_links` object and the desired projection in the `_embedded` object for both owner and transactions.

The service can choose to return just the accounts including links to transactions under the `_links` object as this is allowed by HAL.
The query parameter `embed` can be used for evolving the service to match the desires of consumers - if many consumers have the same wishes for
what to embed, the service provider should consider including more in the responses and endure the added coupling between this service and the
service that delivers the embedded information. This coupling should of course not be synchronous.
