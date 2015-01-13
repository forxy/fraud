package fraud.api.v1.velocity

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Table

/**
 * Contains raw data set for different velocity metrics
 */
@Table(name = "history")
@ToString
@EqualsAndHashCode
class History implements Serializable {
    @EmbeddedId
    HistoryCompositeKey key

    @ToString
    @EqualsAndHashCode
    static class HistoryCompositeKey implements Serializable {
        @EmbeddedId
        PartitionKey id
        @Column(name="transaction_id", columnDefinition="timeuuid")
        UUID transactionID
    }
}
