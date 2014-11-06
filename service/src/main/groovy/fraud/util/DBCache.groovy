package fraud.util

import org.springframework.beans.factory.InitializingBean
import fraud.db.dao.IVelocityConfigDAO
import fraud.api.v1.velocity.VelocityConfig

/**
 * Shared storage for operational data
 */
class DBCache implements InitializingBean {

    IVelocityConfigDAO velocityConfigDAO

    List<VelocityConfig> configs

    void invalidate() {
        configs = velocityConfigDAO?.findAll()?.asList()
    }

    @Override
    void afterPropertiesSet() throws Exception {
        invalidate()
    }
}
