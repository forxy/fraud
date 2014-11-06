package fraud.db.dao

import common.status.ISystemStatusComponent
import fraud.api.v1.derog.BlackListItem
import fraud.api.v1.derog.ListPartitionKey

/**
 * Data Access Object for fraud database to manipulate BlackLists.
 */
interface IDerogDAO extends ISystemStatusComponent {

    boolean isInBlackList(ListPartitionKey key)

    List<BlackListItem> getList(final String type, final String value, final int limit)

    BlackListItem get(final ListPartitionKey id)

    void save(final BlackListItem item)

    void delete(final BlackListItem item)
}

