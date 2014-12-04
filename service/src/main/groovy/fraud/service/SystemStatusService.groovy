package fraud.service

import common.status.ISystemStatusService
import common.status.api.ComponentStatus
import common.status.api.StatusType
import common.status.api.SystemStatus
import common.support.SystemProperties
import fraud.db.dao.ITransactionDAO

/**
 * Business logic to prepare system status
 */
class SystemStatusService implements ISystemStatusService {

    ITransactionDAO transactionDAO

    @Override
    SystemStatus getStatus() {
        StatusType systemStatusType = null
        List<ComponentStatus> componentStatuses = []
        if (transactionDAO != null) {
            componentStatuses.add(transactionDAO.status)
        } else {
            systemStatusType = StatusType.RED
        }

        // @formatter:off
        new SystemStatus(
                SystemProperties.serviceName,
                SystemProperties.hostAddress,
                SystemProperties.serviceVersion,
                systemStatusType != null ? systemStatusType : getTheWorstStatus(componentStatuses),
                componentStatuses)
        // @formatter:on
    }

    private static StatusType getTheWorstStatus(final List<ComponentStatus> componentStatuses) {
        StatusType theWorstStatus = StatusType.GREEN
        componentStatuses.each {
            if (it.status.ordinal() > theWorstStatus.ordinal()) {
                theWorstStatus = it.status
            }
        }
        theWorstStatus
    }
}
