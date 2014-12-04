package fraud.db.dao.cassandra

import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.mapping.EntityTypeParser
import com.datastax.driver.mapping.meta.EntityFieldMetaData
import com.datastax.driver.mapping.meta.EntityTypeMetadata
import common.status.api.ComponentStatus
import fraud.db.dao.IDerogDAO
import fraud.api.v1.derog.BlackListItem
import fraud.api.v1.derog.ListPartitionKey

import static com.datastax.driver.core.querybuilder.QueryBuilder.gt
import static com.datastax.driver.core.querybuilder.QueryBuilder.token

/**
 * BlackLists DAO implementation
 */
class DerogDAO extends BaseCassandraDAO implements IDerogDAO {

    @Override
    boolean isInBlackList(ListPartitionKey key) {
        BlackListItem item = mappingSession.get(BlackListItem.class, key)
        item != null && item.isActive
    }

    @Override
    List<BlackListItem> getList(final String type, final String value, int limit) {
        EntityTypeMetadata emeta = EntityTypeParser.getEntityMetadata(BlackListItem.class)
        if (type != null && value != null) {
            EntityFieldMetaData typeMeta = emeta.getFieldMetadata("type")
            EntityFieldMetaData valueMeta = emeta.getFieldMetadata("value")
            return mappingSession.getByQuery(BlackListItem.class,
                    QueryBuilder.select().all().from(mappingSession.keyspace, emeta.tableName)
                            .where(gt(token(typeMeta.columnName, valueMeta.columnName),
                            token(type, value))).limit(limit))
        } else {
            return mappingSession.getByQuery(BlackListItem.class,
                    QueryBuilder.select().all().from(mappingSession.keyspace, emeta.tableName).limit(limit))
        }
    }

    @Override
    BlackListItem get(final ListPartitionKey id) {
        mappingSession.get(BlackListItem.class, id)
    }

    @Override
    void save(BlackListItem item) {
        mappingSession.save(item)
    }

    @Override
    void delete(BlackListItem item) {
        mappingSession.delete(item)
    }

    @Override
    ComponentStatus getStatus() {
        new ComponentStatus()
    }
}
