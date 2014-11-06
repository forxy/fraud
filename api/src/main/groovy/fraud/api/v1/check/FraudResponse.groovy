package fraud.api.v1.check

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString;

@ToString
@EqualsAndHashCode
class FraudResponse {
    Boolean isFraud;
    Double probability;
    Double threshold;
    Long transactionID;
}
