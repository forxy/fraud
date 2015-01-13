package fraud.api.v1.velocity

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Velocity configuration
 */
@Document(collection = "velocityConfig")
@ToString
@EqualsAndHashCode
class VelocityConfig implements Serializable {
    @Id
    String id
    Set<String> primaryMetrics
    Long period
    Long expiresIn
    Set<AggregationConfig> aggregationConfigs
    Date createDate
    String createdBy
    Date updateDate
    String updatedBy
}
