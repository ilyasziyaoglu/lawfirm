package net.evercode.lawfirm.service;

import net.evercode.lawfirm.domain.JobApplications;
import net.evercode.lawfirm.domain.JobApplications_;
import net.evercode.lawfirm.repository.JobApplicationsRepository;
import net.evercode.lawfirm.service.criteria.JobApplicationsCriteria;
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
 * Service for executing complex queries for {@link JobApplications} entities in the database.
 * The main input is a {@link JobApplicationsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link JobApplications} or a {@link Page} of {@link JobApplications} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class JobApplicationsQueryService extends QueryService<JobApplications> {

    private final Logger log = LoggerFactory.getLogger(JobApplicationsQueryService.class);

    private final JobApplicationsRepository jobApplicationsRepository;

    public JobApplicationsQueryService(JobApplicationsRepository jobApplicationsRepository) {
        this.jobApplicationsRepository = jobApplicationsRepository;
    }

    /**
     * Return a {@link List} of {@link JobApplications} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<JobApplications> findByCriteria(JobApplicationsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<JobApplications> specification = createSpecification(criteria);
        return jobApplicationsRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link JobApplications} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<JobApplications> findByCriteria(JobApplicationsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<JobApplications> specification = createSpecification(criteria);
        return jobApplicationsRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(JobApplicationsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<JobApplications> specification = createSpecification(criteria);
        return jobApplicationsRepository.count(specification);
    }

    /**
     * Function to convert {@link JobApplicationsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<JobApplications> createSpecification(JobApplicationsCriteria criteria) {
        Specification<JobApplications> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), JobApplications_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), JobApplications_.name));
            }
            if (criteria.getSurname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSurname(), JobApplications_.surname));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), JobApplications_.email));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), JobApplications_.phone));
            }
            if (criteria.getArea() != null) {
                specification = specification.and(buildStringSpecification(criteria.getArea(), JobApplications_.area));
            }
            if (criteria.getMessage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMessage(), JobApplications_.message));
            }
        }
        return specification;
    }
}
