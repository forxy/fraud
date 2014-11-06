package fraud.service.velocity

import common.pojo.EntityPage
import common.pojo.SortDirection
import fraud.api.v1.velocity.VelocityConfig

/**
 * Entry point into auth service business logic
 */
interface IVelocityConfigService {

    Iterable<VelocityConfig> getAllVelocityConfigs()

    EntityPage<VelocityConfig> getVelocityConfigs(final Integer page, final Integer size, final SortDirection sortDirection,
                                                  final String sortedBy, final VelocityConfig filter)

    VelocityConfig getVelocityConfig(final String id)

    void updateVelocityConfig(final String id, final VelocityConfig velocityConfig)

    void createVelocityConfig(final VelocityConfig velocityConfig)

    void deleteVelocityConfig(final String id)
}
