package fraud.api.v1.derog

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

/**
 * Bypass item
 */
@Entity
@Table(name = "whitelist")
@ToString
@EqualsAndHashCode
class WhiteListItem {
    @EmbeddedId
    ListPartitionKey key;
    @Column(name = "is_active")
    Boolean isActive;
    @Column(name = "create_date")
    Date createDate;
    @Column(name = "created_by")
    String createdBy;
    @Column(name = "update_date")
    Date updateDate;
    @Column(name = "updated_by")
    String updatedBy;
}
