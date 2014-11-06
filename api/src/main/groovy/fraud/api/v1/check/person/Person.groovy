package fraud.api.v1.check.person

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import fraud.api.v1.check.Entity
import fraud.api.v1.check.location.Address

@ToString
@EqualsAndHashCode(callSuper = true)
class Person extends Entity {
    String prefixName;
    String firstName;
    String middleName;
    String lastName;
    String suffixName;
    Address address;
    String email;
    Character gender;
    Integer age;
    Date birthDate;
    List<Telephone> telephones;
}
