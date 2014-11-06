package fraud.db.dto

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Currency exchange information
 */
@Document
@ToString
@EqualsAndHashCode
class Currency {
    @Id
    String symbol
    Double usdRate
    Date updateDate
}
