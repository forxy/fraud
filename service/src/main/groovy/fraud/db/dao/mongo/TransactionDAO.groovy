package fraud.db.dao.mongo

import org.apache.commons.lang.exception.ExceptionUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import common.status.api.ComponentStatus
import common.status.api.StatusType
import fraud.db.dao.ITransactionDAO
import fraud.api.v1.check.Transaction

/**
 * Mongo DB based data source for auths
 */
class TransactionDAO implements ITransactionDAO {

    MongoTemplate mongoTemplate

    @Override
    Iterable<Transaction> findAll(final Sort sort) {
        mongoTemplate.find(Query.query(new Criteria()).with(sort), Transaction.class)
    }

    @Override
    Page<Transaction> findAll(final Pageable pageable) {
        new PageImpl<>(
                mongoTemplate.find(Query.query(new Criteria()).with(pageable), Transaction.class),
                pageable, count()
        )
    }

    //@Override
    Page<Transaction> findAll(final Pageable pageable, final Transaction filter) {
        Query query = Query.query(new Criteria()).with(pageable)
        /*if (filter != null) {
            if (StringUtils.isNotEmpty(filter.getID())) {
                query.addCriteria(new Criteria('TransactionID').regex(filter.getTransactionID(), 'i'))
            }
            if (StringUtils.isNotEmpty(filter.getName())) {
                query.addCriteria(new Criteria('name').regex(filter.getName(), 'i'))
            }
            if (StringUtils.isNotEmpty(filter.getUpdatedBy())) {
                query.addCriteria(new Criteria('updatedBy').regex(filter.getUpdatedBy(), 'i'))
            }
            if (StringUtils.isNotEmpty(filter.getCreatedBy())) {
                query.addCriteria(new Criteria('createdBy').regex(filter.getCreatedBy(), 'i'))
            }
        }*/

        new PageImpl<>(mongoTemplate.find(query, Transaction.class), pageable, count())
    }

    @Override
    <S extends Transaction> S save(final S transaction) {
        mongoTemplate.save(transaction)
        transaction
    }

    @Override
    <S extends Transaction> Iterable<S> save(final Iterable<S> transaction) {
        throw null
    }

    @Override
    Transaction findOne(final String TransactionID) {
        mongoTemplate.findOne(Query.query(Criteria.where('TransactionID').is(TransactionID)), Transaction.class)
    }

    @Override
    boolean exists(final String TransactionID) {
        mongoTemplate.findOne(Query.query(Criteria.where('TransactionID').is(TransactionID)), Transaction.class) != null
    }

    @Override
    Iterable<Transaction> findAll() {
        mongoTemplate.findAll(Transaction.class)
    }

    @Override
    Iterable<Transaction> findAll(final Iterable<String> TransactionIDs) {
        mongoTemplate.find(Query.query(Criteria.where('TransactionID').in(TransactionIDs)), Transaction.class)
    }

    @Override
    long count() {
        mongoTemplate.count(null, Transaction.class)
    }

    @Override
    void delete(final String TransactionID) {
        mongoTemplate.remove(Query.query(Criteria.where('TransactionID').is(TransactionID)), Transaction.class)
    }

    @Override
    void delete(final Transaction Transaction) {
        mongoTemplate.remove(Transaction)
    }

    @Override
    void delete(final Iterable<? extends Transaction> Transactions) {
        for (Transaction Transaction : Transactions) {
            mongoTemplate.remove(Transaction)
        }
    }

    @Override
    void deleteAll() {
        mongoTemplate.remove(null, Transaction.class)
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
                mongoTemplate.count(null, Transaction.class)
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
        new ComponentStatus('Transaction DAO', location, statusType, null, ComponentStatus.ComponentType.DB,
                responseTime, null, exceptionMessage, exceptionDetails)
    }

    Object save(Object o) { return o }
}
