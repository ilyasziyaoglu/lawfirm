package net.evercode.lawfirm.service;

import net.evercode.lawfirm.domain.JobApplications;
import net.evercode.lawfirm.repository.JobApplicationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link JobApplications}.
 */
@Service
@Transactional
public class JobApplicationsService {

    private final Logger log = LoggerFactory.getLogger(JobApplicationsService.class);

    private final JobApplicationsRepository jobApplicationsRepository;

    public JobApplicationsService(JobApplicationsRepository jobApplicationsRepository) {
        this.jobApplicationsRepository = jobApplicationsRepository;
    }

    /**
     * Save a jobApplications.
     *
     * @param jobApplications the entity to save.
     * @return the persisted entity.
     */
    public JobApplications save(JobApplications jobApplications) {
        log.debug("Request to save JobApplications : {}", jobApplications);
        return jobApplicationsRepository.save(jobApplications);
    }

    /**
     * Partially update a jobApplications.
     *
     * @param jobApplications the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<JobApplications> partialUpdate(JobApplications jobApplications) {
        log.debug("Request to partially update JobApplications : {}", jobApplications);

        return jobApplicationsRepository
            .findById(jobApplications.getId())
            .map(existingJobApplications -> {
                if (jobApplications.getName() != null) {
                    existingJobApplications.setName(jobApplications.getName());
                }
                if (jobApplications.getSurname() != null) {
                    existingJobApplications.setSurname(jobApplications.getSurname());
                }
                if (jobApplications.getEmail() != null) {
                    existingJobApplications.setEmail(jobApplications.getEmail());
                }
                if (jobApplications.getPhone() != null) {
                    existingJobApplications.setPhone(jobApplications.getPhone());
                }
                if (jobApplications.getArea() != null) {
                    existingJobApplications.setArea(jobApplications.getArea());
                }
                if (jobApplications.getMessage() != null) {
                    existingJobApplications.setMessage(jobApplications.getMessage());
                }
                if (jobApplications.getCv() != null) {
                    existingJobApplications.setCv(jobApplications.getCv());
                }
                if (jobApplications.getCvContentType() != null) {
                    existingJobApplications.setCvContentType(jobApplications.getCvContentType());
                }

                return existingJobApplications;
            })
            .map(jobApplicationsRepository::save);
    }

    /**
     * Get all the jobApplications.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<JobApplications> findAll(Pageable pageable) {
        log.debug("Request to get all JobApplications");
        return jobApplicationsRepository.findAll(pageable);
    }

    /**
     * Get one jobApplications by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<JobApplications> findOne(Long id) {
        log.debug("Request to get JobApplications : {}", id);
        return jobApplicationsRepository.findById(id);
    }

    /**
     * Delete the jobApplications by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete JobApplications : {}", id);
        jobApplicationsRepository.deleteById(id);
    }
}
