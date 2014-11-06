package fraud.db.dao

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import common.status.ISystemStatusComponent
import fraud.api.v1.check.Transaction

/**
 * Data Access Object for Fraud database to manipulate Frauds.
 */
interface ITransactionDAO extends PagingAndSortingRepository<Transaction, String>, ISystemStatusComponent {

    Page<Transaction> findAll(final Pageable pageable, final Transaction filter)
}

