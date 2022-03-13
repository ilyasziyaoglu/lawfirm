package net.evercode.lawfirm.web.rest;

import net.evercode.lawfirm.domain.JobApplications;
import net.evercode.lawfirm.repository.JobApplicationsRepository;
import net.evercode.lawfirm.service.JobApplicationsQueryService;
import net.evercode.lawfirm.service.JobApplicationsService;
import net.evercode.lawfirm.service.criteria.JobApplicationsCriteria;
import net.evercode.lawfirm.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link net.evercode.lawfirm.domain.JobApplications}.
 */
@RestController
@RequestMapping("/api")
public class JobApplicationsResource {

    private final Logger log = LoggerFactory.getLogger(JobApplicationsResource.class);

    private static final String ENTITY_NAME = "jobApplications";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JobApplicationsService jobApplicationsService;

    private final JobApplicationsRepository jobApplicationsRepository;

    private final JobApplicationsQueryService jobApplicationsQueryService;

    public JobApplicationsResource(
        JobApplicationsService jobApplicationsService,
        JobApplicationsRepository jobApplicationsRepository,
        JobApplicationsQueryService jobApplicationsQueryService
    ) {
        this.jobApplicationsService = jobApplicationsService;
        this.jobApplicationsRepository = jobApplicationsRepository;
        this.jobApplicationsQueryService = jobApplicationsQueryService;
    }

    /**
     * {@code POST  /job-applications} : Create a new jobApplications.
     *
     * @param jobApplications the jobApplications to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new jobApplications, or with status {@code 400 (Bad Request)} if the jobApplications has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/job-applications")
    public ResponseEntity<JobApplications> createJobApplications(@Valid @RequestBody JobApplications jobApplications)
        throws URISyntaxException {
        log.debug("REST request to save JobApplications : {}", jobApplications);
        if (jobApplications.getId() != null) {
            throw new BadRequestAlertException("A new jobApplications cannot already have an ID", ENTITY_NAME, "idexists");
        }
        JobApplications result = jobApplicationsService.save(jobApplications);
        return ResponseEntity
            .created(new URI("/api/job-applications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /job-applications/:id} : Updates an existing jobApplications.
     *
     * @param id the id of the jobApplications to save.
     * @param jobApplications the jobApplications to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jobApplications,
     * or with status {@code 400 (Bad Request)} if the jobApplications is not valid,
     * or with status {@code 500 (Internal Server Error)} if the jobApplications couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/job-applications/{id}")
    public ResponseEntity<JobApplications> updateJobApplications(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody JobApplications jobApplications
    ) throws URISyntaxException {
        log.debug("REST request to update JobApplications : {}, {}", id, jobApplications);
        if (jobApplications.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, jobApplications.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!jobApplicationsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        JobApplications result = jobApplicationsService.save(jobApplications);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, jobApplications.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /job-applications/:id} : Partial updates given fields of an existing jobApplications, field will ignore if it is null
     *
     * @param id the id of the jobApplications to save.
     * @param jobApplications the jobApplications to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jobApplications,
     * or with status {@code 400 (Bad Request)} if the jobApplications is not valid,
     * or with status {@code 404 (Not Found)} if the jobApplications is not found,
     * or with status {@code 500 (Internal Server Error)} if the jobApplications couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/job-applications/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<JobApplications> partialUpdateJobApplications(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody JobApplications jobApplications
    ) throws URISyntaxException {
        log.debug("REST request to partial update JobApplications partially : {}, {}", id, jobApplications);
        if (jobApplications.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, jobApplications.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!jobApplicationsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<JobApplications> result = jobApplicationsService.partialUpdate(jobApplications);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, jobApplications.getId().toString())
        );
    }

    /**
     * {@code GET  /job-applications} : get all the jobApplications.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of jobApplications in body.
     */
    @GetMapping("/job-applications")
    public ResponseEntity<List<JobApplications>> getAllJobApplications(
        JobApplicationsCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get JobApplications by criteria: {}", criteria);
        Page<JobApplications> page = jobApplicationsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /job-applications/count} : count all the jobApplications.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/job-applications/count")
    public ResponseEntity<Long> countJobApplications(JobApplicationsCriteria criteria) {
        log.debug("REST request to count JobApplications by criteria: {}", criteria);
        return ResponseEntity.ok().body(jobApplicationsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /job-applications/:id} : get the "id" jobApplications.
     *
     * @param id the id of the jobApplications to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the jobApplications, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/job-applications/{id}")
    public ResponseEntity<JobApplications> getJobApplications(@PathVariable Long id) {
        log.debug("REST request to get JobApplications : {}", id);
        Optional<JobApplications> jobApplications = jobApplicationsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(jobApplications);
    }

    /**
     * {@code DELETE  /job-applications/:id} : delete the "id" jobApplications.
     *
     * @param id the id of the jobApplications to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/job-applications/{id}")
    public ResponseEntity<Void> deleteJobApplications(@PathVariable Long id) {
        log.debug("REST request to delete JobApplications : {}", id);
        jobApplicationsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
