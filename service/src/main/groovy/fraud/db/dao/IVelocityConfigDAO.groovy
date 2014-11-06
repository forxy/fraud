package fraud.db.dao

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import common.status.ISystemStatusComponent
import fraud.api.v1.velocity.VelocityConfig

/**
 * Data Access Object for Forxy database to manipulate VelocityConfigs.
 */
interface IVelocityConfigDAO extends ISystemStatusComponent {

    Iterable<VelocityConfig> findAll(final Sort sort)

    Page<VelocityConfig> findAll(final Pageable pageable)

    Page<VelocityConfig> findAll(final Pageable pageable, final VelocityConfig filter)

    Iterable<VelocityConfig> findAll()

    VelocityConfig findOne(final String id)

    boolean exists(final Set<String> primaryMetrics)

    VelocityConfig save(final VelocityConfig velocityConfig);

    void delete(final String id)

    Long count()
}

