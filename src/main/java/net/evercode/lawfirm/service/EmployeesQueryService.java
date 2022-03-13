package net.evercode.lawfirm.service;

import net.evercode.lawfirm.domain.Employees;
import net.evercode.lawfirm.domain.Employees_;
import net.evercode.lawfirm.domain.ServicePoints_;
import net.evercode.lawfirm.domain.Services_;
import net.evercode.lawfirm.repository.EmployeesRepository;
import net.evercode.lawfirm.service.criteria.EmployeesCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

import javax.persistence.criteria.JoinType;
import java.util.List;

/**
 * Service for executing complex queries for {@link Employees} entities in the database.
 * The main input is a {@link EmployeesCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Employees} or a {@link Page} of {@link Employees} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EmployeesQueryService extends QueryService<Employees> {

    private final Logger log = LoggerFactory.getLogger(EmployeesQueryService.class);

    private final EmployeesRepository employeesRepository;

    public EmployeesQueryService(EmployeesRepository employeesRepository) {
        this.employeesRepository = employeesRepository;
    }

    /**
     * Return a {@link List} of {@link Employees} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Employees> findByCriteria(EmployeesCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Employees> specification = createSpecification(criteria);
        return employeesRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Employees} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Employees> findByCriteria(EmployeesCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Employees> specification = createSpecification(criteria);
        return employeesRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EmployeesCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Employees> specification = createSpecification(criteria);
        return employeesRepository.count(specification);
    }

    /**
     * Function to convert {@link EmployeesCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Employees> createSpecification(EmployeesCriteria criteria) {
        Specification<Employees> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Employees_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Employees_.name));
            }
            if (criteria.getSurname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSurname(), Employees_.surname));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Employees_.title));
            }
            if (criteria.getStory() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStory(), Employees_.story));
            }
            if (criteria.getOrder() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOrder(), Employees_.order));
            }
            if (criteria.getServicePointId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getServicePointId(),
                            root -> root.join(Employees_.servicePoint, JoinType.LEFT).get(ServicePoints_.id)
                        )
                    );
            }
            if (criteria.getServicesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getServicesId(),
                            root -> root.join(Employees_.services, JoinType.LEFT).get(Services_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
