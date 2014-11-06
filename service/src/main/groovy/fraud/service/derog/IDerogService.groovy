package fraud.service.derog

import fraud.api.v1.derog.BlackListItem

/**
 * Black lists manipulation logic API
 */
interface IDerogService {

    List<BlackListItem> getMoreItemsFrom(final String type, final String value)

    List<BlackListItem> getMoreItemsFrom(final String type, final String value, final int limit)

    BlackListItem get(final String type, final String value)

    boolean isInBlackList(final String type, final String value)

    void add(final BlackListItem item)

    void update(final BlackListItem item)

    void delete(final BlackListItem item)
}
