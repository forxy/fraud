package fraud.api.v1.check.location

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
class Address extends Location {
    String addressLine;
}
