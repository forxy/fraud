package fraud.api.v1.derog;

import javax.persistence.Column

/**
 * Base list item definition
 */
class ListPartitionKey implements Serializable {
    @Column (name = "type")
    String type;
    @Column (name = "value")
    String value;
}
