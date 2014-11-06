package fraud.service

import common.status.ISystemStatusService
import common.status.pojo.ComponentStatus
import common.status.pojo.StatusType
import common.status.pojo.SystemStatus
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
            componentStatuses.add(transactionDAO.getStatus())
        } else {
            systemStatusType = StatusType.RED
        }

        // @formatter:off
        new SystemStatus(
                SystemProperties.getServiceName(),
                SystemProperties.getHostAddress(),
                SystemProperties.getServiceVersion(),
                systemStatusType != null ? systemStatusType : getTheWorstStatus(componentStatuses),
                componentStatuses)
        // @formatter:on
    }

    private static StatusType getTheWorstStatus(final List<ComponentStatus> componentStatuses) {
        StatusType theWorstStatus = StatusType.GREEN
        for (ComponentStatus componentStatus : componentStatuses) {
            if (componentStatus.getStatus().ordinal() > theWorstStatus.ordinal()) {
                theWorstStatus = componentStatus.getStatus()
            }
        }
        theWorstStatus
    }
}
