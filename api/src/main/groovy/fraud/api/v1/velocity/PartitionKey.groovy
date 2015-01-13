package fraud.api.v1.velocity

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Column

/**
 * Velocity metrics cassandra partition key
 */
@ToString
@EqualsAndHashCode
class PartitionKey implements Serializable {
    @Column(name = "metric_type")
    String metricType
    @Column(name = "metric_value")
    String metricValue
}
