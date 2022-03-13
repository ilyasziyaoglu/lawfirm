package net.evercode.lawfirm.service;

import net.evercode.lawfirm.domain.Configs;
import net.evercode.lawfirm.domain.Configs_;
import net.evercode.lawfirm.repository.ConfigsRepository;
import net.evercode.lawfirm.service.criteria.ConfigsCriteria;
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
 * Service for executing complex queries for {@link Configs} entities in the database.
 * The main input is a {@link ConfigsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Configs} or a {@link Page} of {@link Configs} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ConfigsQueryService extends QueryService<Configs> {

    private final Logger log = LoggerFactory.getLogger(ConfigsQueryService.class);

    private final ConfigsRepository configsRepository;

    public ConfigsQueryService(ConfigsRepository configsRepository) {
        this.configsRepository = configsRepository;
    }

    /**
     * Return a {@link List} of {@link Configs} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Configs> findByCriteria(ConfigsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Configs> specification = createSpecification(criteria);
        return configsRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Configs} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Configs> findByCriteria(ConfigsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Configs> specification = createSpecification(criteria);
        return configsRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ConfigsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Configs> specification = createSpecification(criteria);
        return configsRepository.count(specification);
    }

    /**
     * Function to convert {@link ConfigsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Configs> createSpecification(ConfigsCriteria criteria) {
        Specification<Configs> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Configs_.id));
            }
            if (criteria.getKey() != null) {
                specification = specification.and(buildStringSpecification(criteria.getKey(), Configs_.key));
            }
            if (criteria.getValue() != null) {
                specification = specification.and(buildStringSpecification(criteria.getValue(), Configs_.value));
            }
        }
        return specification;
    }
}
