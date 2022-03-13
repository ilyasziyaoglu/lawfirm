package net.evercode.lawfirm.repository;

import net.evercode.lawfirm.domain.JobApplications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the JobApplications entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JobApplicationsRepository extends JpaRepository<JobApplications, Long>, JpaSpecificationExecutor<JobApplications> {}
