package net.evercode.lawfirm.service;

import net.evercode.lawfirm.domain.Properties;
import net.evercode.lawfirm.domain.Properties_;
import net.evercode.lawfirm.repository.PropertiesRepository;
import net.evercode.lawfirm.service.criteria.PropertiesCriteria;
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
 * Service for executing complex queries for {@link Properties} entities in the database.
 * The main input is a {@link PropertiesCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Properties} or a {@link Page} of {@link Properties} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PropertiesQueryService extends QueryService<Properties> {

    private final Logger log = LoggerFactory.getLogger(PropertiesQueryService.class);

    private final PropertiesRepository propertiesRepository;

    public PropertiesQueryService(PropertiesRepository propertiesRepository) {
        this.propertiesRepository = propertiesRepository;
    }

    /**
     * Return a {@link List} of {@link Properties} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Properties> findByCriteria(PropertiesCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Properties> specification = createSpecification(criteria);
        return propertiesRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Properties} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Properties> findByCriteria(PropertiesCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Properties> specification = createSpecification(criteria);
        return propertiesRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PropertiesCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Properties> specification = createSpecification(criteria);
        return propertiesRepository.count(specification);
    }

    /**
     * Function to convert {@link PropertiesCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Properties> createSpecification(PropertiesCriteria criteria) {
        Specification<Properties> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Properties_.id));
            }
            if (criteria.getKey() != null) {
                specification = specification.and(buildStringSpecification(criteria.getKey(), Properties_.key));
            }
            if (criteria.getValue() != null) {
                specification = specification.and(buildStringSpecification(criteria.getValue(), Properties_.value));
            }
            if (criteria.getLanguage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLanguage(), Properties_.language));
            }
        }
        return specification;
    }
}
