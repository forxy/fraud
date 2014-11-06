package fraud.api.v1.check.person

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString;
import fraud.api.v1.check.Entity;

@ToString
@EqualsAndHashCode(callSuper = true)
class Telephone extends Entity {
    String areaCode;
    String countryAccessCode;
    String phoneNumber;
    Type type;

    enum Type {
        Home,
        Mobile,
        Business
    }
}
