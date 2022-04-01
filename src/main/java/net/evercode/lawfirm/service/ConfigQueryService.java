package net.evercode.lawfirm.service;

import net.evercode.lawfirm.domain.Config;
import net.evercode.lawfirm.domain.Config_;
import net.evercode.lawfirm.repository.ConfigRepository;
import net.evercode.lawfirm.service.criteria.ConfigCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

import java.util.List;

/**
 * Service for executing complex queries for {@link Config} entities in the database.
 * The main input is a {@link ConfigCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Config} or a {@link Page} of {@link Config} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ConfigQueryService extends QueryService<Config> {

    private final Logger log = LoggerFactory.getLogger(ConfigQueryService.class);

    private final ConfigRepository configRepository;

    public ConfigQueryService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    /**
     * Return a {@link List} of {@link Config} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Config> findByCriteria(ConfigCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Config> specification = createSpecification(criteria);
        return configRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Config} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Config> findByCriteria(ConfigCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Config> specification = createSpecification(criteria);
        return configRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ConfigCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Config> specification = createSpecification(criteria);
        return configRepository.count(specification);
    }

    /**
     * Function to convert {@link ConfigCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Config> createSpecification(ConfigCriteria criteria) {
        Specification<Config> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Config_.id));
            }
            if (criteria.getKey() != null) {
                specification = specification.and(buildStringSpecification(criteria.getKey(), Config_.key));
            }
            if (criteria.getValue() != null) {
                specification = specification.and(buildStringSpecification(criteria.getValue(), Config_.value));
            }
        }
        return specification;
    }
}
