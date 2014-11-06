package fraud.api.v1.velocity

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Related metrics aggregation configuration
 */
@ToString
@EqualsAndHashCode
class AggregationConfig implements Serializable {
    Aggregation aggregation;
    String secondaryMetric
}
