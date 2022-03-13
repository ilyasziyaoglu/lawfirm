package net.evercode.lawfirm.web.rest;

import net.evercode.lawfirm.domain.ServicePoints;
import net.evercode.lawfirm.repository.ServicePointsRepository;
import net.evercode.lawfirm.service.ServicePointsService;
import net.evercode.lawfirm.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link net.evercode.lawfirm.domain.ServicePoints}.
 */
@RestController
@RequestMapping("/api")
public class ServicePointsResource {

    private final Logger log = LoggerFactory.getLogger(ServicePointsResource.class);

    private static final String ENTITY_NAME = "servicePoints";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ServicePointsService servicePointsService;

    private final ServicePointsRepository servicePointsRepository;

    public ServicePointsResource(ServicePointsService servicePointsService, ServicePointsRepository servicePointsRepository) {
        this.servicePointsService = servicePointsService;
        this.servicePointsRepository = servicePointsRepository;
    }

    /**
     * {@code POST  /service-points} : Create a new servicePoints.
     *
     * @param servicePoints the servicePoints to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new servicePoints, or with status {@code 400 (Bad Request)} if the servicePoints has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/service-points")
    public ResponseEntity<ServicePoints> createServicePoints(@Valid @RequestBody ServicePoints servicePoints) throws URISyntaxException {
        log.debug("REST request to save ServicePoints : {}", servicePoints);
        if (servicePoints.getId() != null) {
            throw new BadRequestAlertException("A new servicePoints cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ServicePoints result = servicePointsService.save(servicePoints);
        return ResponseEntity
            .created(new URI("/api/service-points/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /service-points/:id} : Updates an existing servicePoints.
     *
     * @param id the id of the servicePoints to save.
     * @param servicePoints the servicePoints to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated servicePoints,
     * or with status {@code 400 (Bad Request)} if the servicePoints is not valid,
     * or with status {@code 500 (Internal Server Error)} if the servicePoints couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/service-points/{id}")
    public ResponseEntity<ServicePoints> updateServicePoints(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ServicePoints servicePoints
    ) throws URISyntaxException {
        log.debug("REST request to update ServicePoints : {}, {}", id, servicePoints);
        if (servicePoints.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, servicePoints.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!servicePointsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ServicePoints result = servicePointsService.save(servicePoints);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, servicePoints.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /service-points/:id} : Partial updates given fields of an existing servicePoints, field will ignore if it is null
     *
     * @param id the id of the servicePoints to save.
     * @param servicePoints the servicePoints to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated servicePoints,
     * or with status {@code 400 (Bad Request)} if the servicePoints is not valid,
     * or with status {@code 404 (Not Found)} if the servicePoints is not found,
     * or with status {@code 500 (Internal Server Error)} if the servicePoints couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/service-points/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ServicePoints> partialUpdateServicePoints(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ServicePoints servicePoints
    ) throws URISyntaxException {
        log.debug("REST request to partial update ServicePoints partially : {}, {}", id, servicePoints);
        if (servicePoints.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, servicePoints.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!servicePointsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ServicePoints> result = servicePointsService.partialUpdate(servicePoints);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, servicePoints.getId().toString())
        );
    }

    /**
     * {@code GET  /service-points} : get all the servicePoints.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of servicePoints in body.
     */
    @GetMapping("/service-points")
    public List<ServicePoints> getAllServicePoints() {
        log.debug("REST request to get all ServicePoints");
        return servicePointsService.findAll();
    }

    /**
     * {@code GET  /service-points/:id} : get the "id" servicePoints.
     *
     * @param id the id of the servicePoints to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the servicePoints, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/service-points/{id}")
    public ResponseEntity<ServicePoints> getServicePoints(@PathVariable Long id) {
        log.debug("REST request to get ServicePoints : {}", id);
        Optional<ServicePoints> servicePoints = servicePointsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(servicePoints);
    }

    /**
     * {@code DELETE  /service-points/:id} : delete the "id" servicePoints.
     *
     * @param id the id of the servicePoints to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/service-points/{id}")
    public ResponseEntity<Void> deleteServicePoints(@PathVariable Long id) {
        log.debug("REST request to delete ServicePoints : {}", id);
        servicePointsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
