package fraud.db.dao.cassandra;

import com.datastax.driver.mapping.MappingSession;

/**
 * Base Cassandra DAO
 */
abstract class BaseCassandraDAO {

    MappingSession mappingSession
}
