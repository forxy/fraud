package fraud.api.v1.check.payment

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import fraud.api.v1.check.Entity

@ToString
@EqualsAndHashCode(callSuper = true)
class Amount extends Entity {
    BigDecimal value;
    BigDecimal usdValue;
    String currency;
}
