package fraud.api.v1.velocity

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Table

/**
 * Velocity metric row definition
 */
@Table(name = "metric")
@ToString
@EqualsAndHashCode
class Metric implements Serializable {
    @EmbeddedId
    MetricCompositeKey key;
    @Column(name = "aggregated_value")
    Double aggregatedValue;

    @ToString
    @EqualsAndHashCode
    static class MetricCompositeKey implements Serializable {
        @EmbeddedId
        PartitionKey id;
        @Column(name = "secondary_metric")
        String secondaryMetric;
        @Column(name = "aggregation_type")
        Aggregation aggregationType;
    }
}
