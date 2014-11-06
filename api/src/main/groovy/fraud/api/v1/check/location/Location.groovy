package fraud.api.v1.check.location

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString;
import fraud.api.v1.check.Entity;

@ToString
@EqualsAndHashCode(callSuper = true)
class Location extends Entity {
    String city;
    String country;
    Integer postalCode;
}
