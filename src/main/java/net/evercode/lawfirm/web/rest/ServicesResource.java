package net.evercode.lawfirm.web.rest;

import net.evercode.lawfirm.domain.Services;
import net.evercode.lawfirm.repository.ServicesRepository;
import net.evercode.lawfirm.service.ServicesService;
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
 * REST controller for managing {@link net.evercode.lawfirm.domain.Services}.
 */
@RestController
@RequestMapping("/api")
public class ServicesResource {

    private final Logger log = LoggerFactory.getLogger(ServicesResource.class);

    private static final String ENTITY_NAME = "services";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ServicesService servicesService;

    private final ServicesRepository servicesRepository;

    public ServicesResource(ServicesService servicesService, ServicesRepository servicesRepository) {
        this.servicesService = servicesService;
        this.servicesRepository = servicesRepository;
    }

    /**
     * {@code POST  /services} : Create a new services.
     *
     * @param services the services to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new services, or with status {@code 400 (Bad Request)} if the services has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/services")
    public ResponseEntity<Services> createServices(@Valid @RequestBody Services services) throws URISyntaxException {
        log.debug("REST request to save Services : {}", services);
        if (services.getId() != null) {
            throw new BadRequestAlertException("A new services cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Services result = servicesService.save(services);
        return ResponseEntity
            .created(new URI("/api/services/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /services/:id} : Updates an existing services.
     *
     * @param id the id of the services to save.
     * @param services the services to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated services,
     * or with status {@code 400 (Bad Request)} if the services is not valid,
     * or with status {@code 500 (Internal Server Error)} if the services couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/services/{id}")
    public ResponseEntity<Services> updateServices(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Services services
    ) throws URISyntaxException {
        log.debug("REST request to update Services : {}, {}", id, services);
        if (services.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, services.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!servicesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Services result = servicesService.save(services);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, services.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /services/:id} : Partial updates given fields of an existing services, field will ignore if it is null
     *
     * @param id the id of the services to save.
     * @param services the services to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated services,
     * or with status {@code 400 (Bad Request)} if the services is not valid,
     * or with status {@code 404 (Not Found)} if the services is not found,
     * or with status {@code 500 (Internal Server Error)} if the services couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/services/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Services> partialUpdateServices(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Services services
    ) throws URISyntaxException {
        log.debug("REST request to partial update Services partially : {}, {}", id, services);
        if (services.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, services.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!servicesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Services> result = servicesService.partialUpdate(services);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, services.getId().toString())
        );
    }

    /**
     * {@code GET  /services} : get all the services.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of services in body.
     */
    @GetMapping("/services")
    public List<Services> getAllServices() {
        log.debug("REST request to get all Services");
        return servicesService.findAll();
    }

    /**
     * {@code GET  /services/:id} : get the "id" services.
     *
     * @param id the id of the services to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the services, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/services/{id}")
    public ResponseEntity<Services> getServices(@PathVariable Long id) {
        log.debug("REST request to get Services : {}", id);
        Optional<Services> services = servicesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(services);
    }

    /**
     * {@code DELETE  /services/:id} : delete the "id" services.
     *
     * @param id the id of the services to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteServices(@PathVariable Long id) {
        log.debug("REST request to delete Services : {}", id);
        servicesService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
