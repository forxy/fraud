package fraud.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import common.exceptions.ServiceException
import common.api.EntityPage
import common.api.SortDirection
import fraud.db.dao.ITransactionDAO
import fraud.exceptions.FraudServiceEvent
import fraud.api.v1.check.Transaction

/**
 * Implementation class for FraudService business logic
 */
class FraudCheckService implements IFraudCheckService {

    static final int DEFAULT_PAGE_SIZE = 10

    ITransactionDAO transactionDAO

    @Override
    Boolean check(final Transaction transaction) {
        transactionDAO.save(transaction)
        false
    }

    List<Transaction> getAllTransactions() {
        transactionDAO.findAll().collect()
    }

    @Override
    EntityPage<Transaction> getTransactions(final Integer page, final Integer size, final SortDirection sortDirection,
                                            final String sortedBy, final Transaction filter) {
        if (page >= 1) {
            int pageSize = size == null ? DEFAULT_PAGE_SIZE : size
            PageRequest pageRequest
            if (sortDirection != null && sortedBy != null) {
                Sort.Direction dir = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC
                pageRequest = new PageRequest(page - 1, pageSize, dir, sortedBy)
            } else {
                pageRequest = new PageRequest(page - 1, pageSize)
            }
            final Page<Transaction> p = transactionDAO.findAll(pageRequest, filter)
            new EntityPage<>(p.getContent(), p.getSize(), p.getNumber(), p.getTotalElements())
        } else {
            throw new ServiceException(FraudServiceEvent.InvalidPageNumber, page)
        }
    }

    @Override
    Transaction getTransaction(final String transactionID) {
        Transaction transaction = transactionDAO.findOne(transactionID)
        if (transaction == null) {
            throw new ServiceException(FraudServiceEvent.TransactionNotFound, transactionID)
        }
        transaction
    }
}
