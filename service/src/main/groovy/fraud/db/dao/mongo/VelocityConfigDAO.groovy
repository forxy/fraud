package fraud.db.dao.mongo

import common.status.pojo.ComponentStatus
import common.status.pojo.StatusType
import fraud.db.dao.IVelocityConfigDAO
import fraud.api.v1.velocity.VelocityConfig
import org.apache.commons.lang.exception.ExceptionUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

/**
 * Mongo DB based data source for velocity configurations
 */
class VelocityConfigDAO implements IVelocityConfigDAO {

    private MongoTemplate mongoTemplate

    @Override
    public Iterable<VelocityConfig> findAll(final Sort sort) {
        mongoTemplate.find(Query.query(new Criteria()).with(sort), VelocityConfig.class)
    }

    @Override
    public Page<VelocityConfig> findAll(final Pageable pageable) {
        new PageImpl<>(
                mongoTemplate.find(Query.query(new Criteria()).with(pageable), VelocityConfig.class),
                pageable, count()
        )
    }

    @Override
    public Page<VelocityConfig> findAll(final Pageable pageable, final VelocityConfig filter) {
        Query query = Query.query(new Criteria()).with(pageable)
        if (filter != null) {
            if (filter.primaryMetrics) {
                query.addCriteria(new Criteria('primaryMetrics').in(filter.primaryMetrics, 'i'))
            }
            if (filter.aggregationConfigs) {
                query.addCriteria(new Criteria('aggregationConfigs.secondaryMetric')
                        .in(filter.aggregationConfigs*.secondaryMetric, 'i'))
            }
            if (filter.updatedBy) {
                query.addCriteria(new Criteria('updatedBy').regex(filter.updatedBy, 'i'))
            }
            if (filter.createdBy) {
                query.addCriteria(new Criteria('createdBy').regex(filter.createdBy, 'i'))
            }
        }

        new PageImpl<>(mongoTemplate.find(query, VelocityConfig.class), pageable, count())
    }

    @Override
    public VelocityConfig save(final VelocityConfig velocityConfig) {
        mongoTemplate.save(velocityConfig)
        velocityConfig
    }

    @Override
    public VelocityConfig findOne(final String id) {
        mongoTemplate.findOne(Query.query(Criteria.where('id').is(id)), VelocityConfig.class)
    }

    @Override
    public boolean exists(final Set<String> primaryMetrics) {
        mongoTemplate.findOne(Query.query(Criteria.where('primaryMetrics').all(primaryMetrics)), VelocityConfig.class) != null
    }

    @Override
    public Iterable<VelocityConfig> findAll() {
        mongoTemplate.findAll(VelocityConfig.class)
    }

    @Override
    public void delete(final String id) {
        mongoTemplate.remove(Query.query(Criteria.where('id').is(id)), VelocityConfig.class)
    }

    @Override
    Long count() {
        mongoTemplate.count(null, VelocityConfig.class)
    }

    void setMongoTemplate(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate
    }

    @Override
    ComponentStatus getStatus() {
        String location = null
        StatusType statusType = StatusType.GREEN
        long responseTime = Long.MAX_VALUE
        String exceptionMessage = null
        String exceptionDetails = null
        if (mongoTemplate != null && mongoTemplate.getDb() != null && mongoTemplate.getDb().getMongo() != null) {
            location = mongoTemplate.getDb().getMongo().getConnectPoint()

            long timeStart = new Date().getTime()
            try {
                mongoTemplate.count(null, VelocityConfig.class)
            } catch (final Exception e) {
                exceptionMessage = e.getMessage()
                exceptionDetails = ExceptionUtils.getStackTrace(e)
                statusType = StatusType.RED
            } finally {
                responseTime = new Date().getTime() - timeStart
            }


        } else {
            statusType = StatusType.RED
        }
        new ComponentStatus('VelocityConfig DAO', location, statusType, null, ComponentStatus.ComponentType.DB,
                responseTime, null, exceptionMessage, exceptionDetails)
    }

    Object save(Object o) { return o }
}
