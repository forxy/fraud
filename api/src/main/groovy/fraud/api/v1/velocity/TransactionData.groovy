package fraud.api.v1.velocity

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Table

/**
 * Contains raw data set for different velocity metrics
 */
@Table(name = "transaction")
@ToString
@EqualsAndHashCode
class TransactionData implements Serializable {
    @EmbeddedId
    TransactionCompositeKey key;
    @Column(name = "data")
    List<String> data;

    @ToString
    @EqualsAndHashCode
    static class TransactionCompositeKey implements Serializable {
        @Column(name = "transaction_id", columnDefinition = "timeuuid")
        UUID transactionID;
        @Column(name = "data_type")
        String dataType;
    }
}
