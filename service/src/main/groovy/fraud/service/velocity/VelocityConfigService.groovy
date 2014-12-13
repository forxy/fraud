package fraud.service.velocity

import common.exceptions.ServiceException
import common.api.EntityPage
import common.api.SortDirection
import fraud.db.dao.IVelocityConfigDAO
import fraud.exceptions.FraudEvent
import fraud.api.v1.velocity.VelocityConfig
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/**
 * Implementation class for VelocityConfigService business logic
 */
class VelocityConfigService implements IVelocityConfigService {

    static final int DEFAULT_PAGE_SIZE = 10

    IVelocityConfigDAO velocityConfigDAO

    List<VelocityConfig> getAllVelocityConfigs() {
        velocityConfigDAO.findAll().collect()
    }

    @Override
    EntityPage<VelocityConfig> getVelocityConfigs(
            final Integer page, final Integer size, final SortDirection sortDirection,
            final String sortedBy, final VelocityConfig filter) {
        if (page >= 1) {
            int pageSize = size == null ? DEFAULT_PAGE_SIZE : size
            PageRequest pageRequest
            if (sortDirection != null && sortedBy != null) {
                Sort.Direction dir = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC
                pageRequest = new PageRequest(page - 1, pageSize, dir, sortedBy)
            } else {
                pageRequest = new PageRequest(page - 1, pageSize)
            }
            final Page<VelocityConfig> p = velocityConfigDAO.findAll(pageRequest, filter)
            new EntityPage<>(p.getContent(), p.getSize(), p.getNumber(), p.getTotalElements())
        } else {
            throw new ServiceException(FraudEvent.InvalidPageNumber, page)
        }
    }

    @Override
    VelocityConfig getVelocityConfig(final String id) {
        VelocityConfig config = velocityConfigDAO.findOne(id)
        if (config == null) {
            throw new ServiceException(FraudEvent.VelocityConfigNotFound, id)
        }
        config
    }

    @Override
    void updateVelocityConfig(final String id, final VelocityConfig config) {
        VelocityConfig existingConfig = velocityConfigDAO.findOne(id)
        if (existingConfig) {
            config.id = id
            velocityConfigDAO.save(config)
        } else {
            throw new ServiceException(FraudEvent.VelocityConfigNotFound, id)
        }
    }

    @Override
    void createVelocityConfig(final VelocityConfig config) {
        config.setCreateDate(new Date())
        if (!velocityConfigDAO.exists(config.primaryMetrics)) {
            velocityConfigDAO.save(config)
        } else {
            throw new ServiceException(FraudEvent.VelocityConfigAlreadyExists, config.primaryMetrics)
        }
    }

    @Override
    void deleteVelocityConfig(final String id) {
        if (velocityConfigDAO.findOne(id)) {
            velocityConfigDAO.delete(id)
        } else {
            throw new ServiceException(FraudEvent.VelocityConfigNotFound, id)
        }
    }
}
