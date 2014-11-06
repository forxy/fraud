package fraud.api.v1.check.person

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
class Account extends Person {
    String login;
    String password;
    Statistics statistics;
}
