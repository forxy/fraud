package fraud.util

import fraud.api.v1.velocity.VelocityConfig

/**
 * DBCache public API
 */
interface IDBCache {

    void invalidate()

    List<VelocityConfig> getConfigs()
}
