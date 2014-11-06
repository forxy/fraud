package fraud.service

import common.pojo.EntityPage
import common.pojo.SortDirection
import fraud.api.v1.check.Transaction

/**
 * Entry point into fraud service business logic
 */
interface IFraudCheckService {

    Boolean check(final Transaction transaction)

    Iterable<Transaction> getAllTransactions()

    EntityPage<Transaction> getTransactions(final Integer page, final Integer size, final SortDirection sortDirection,
                                            final String sortedBy, final Transaction filter)

    Transaction getTransaction(final String clientID)
}
