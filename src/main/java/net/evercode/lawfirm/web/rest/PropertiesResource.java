package net.evercode.lawfirm.web.rest;

import net.evercode.lawfirm.domain.Properties;
import net.evercode.lawfirm.repository.PropertiesRepository;
import net.evercode.lawfirm.service.PropertiesQueryService;
import net.evercode.lawfirm.service.PropertiesService;
import net.evercode.lawfirm.service.criteria.PropertiesCriteria;
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
 * REST controller for managing {@link net.evercode.lawfirm.domain.Properties}.
 */
@RestController
@RequestMapping("/api")
public class PropertiesResource {

    private final Logger log = LoggerFactory.getLogger(PropertiesResource.class);

    private static final String ENTITY_NAME = "properties";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PropertiesService propertiesService;

    private final PropertiesRepository propertiesRepository;

    private final PropertiesQueryService propertiesQueryService;

    public PropertiesResource(
        PropertiesService propertiesService,
        PropertiesRepository propertiesRepository,
        PropertiesQueryService propertiesQueryService
    ) {
        this.propertiesService = propertiesService;
        this.propertiesRepository = propertiesRepository;
        this.propertiesQueryService = propertiesQueryService;
    }

    /**
     * {@code POST  /properties} : Create a new properties.
     *
     * @param properties the properties to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new properties, or with status {@code 400 (Bad Request)} if the properties has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/properties")
    public ResponseEntity<Properties> createProperties(@Valid @RequestBody Properties properties) throws URISyntaxException {
        log.debug("REST request to save Properties : {}", properties);
        if (properties.getId() != null) {
            throw new BadRequestAlertException("A new properties cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Properties result = propertiesService.save(properties);
        return ResponseEntity
            .created(new URI("/api/properties/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /properties/:id} : Updates an existing properties.
     *
     * @param id the id of the properties to save.
     * @param properties the properties to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated properties,
     * or with status {@code 400 (Bad Request)} if the properties is not valid,
     * or with status {@code 500 (Internal Server Error)} if the properties couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/properties/{id}")
    public ResponseEntity<Properties> updateProperties(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Properties properties
    ) throws URISyntaxException {
        log.debug("REST request to update Properties : {}, {}", id, properties);
        if (properties.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, properties.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!propertiesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Properties result = propertiesService.save(properties);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, properties.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /properties/:id} : Partial updates given fields of an existing properties, field will ignore if it is null
     *
     * @param id the id of the properties to save.
     * @param properties the properties to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated properties,
     * or with status {@code 400 (Bad Request)} if the properties is not valid,
     * or with status {@code 404 (Not Found)} if the properties is not found,
     * or with status {@code 500 (Internal Server Error)} if the properties couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/properties/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Properties> partialUpdateProperties(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Properties properties
    ) throws URISyntaxException {
        log.debug("REST request to partial update Properties partially : {}, {}", id, properties);
        if (properties.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, properties.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!propertiesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Properties> result = propertiesService.partialUpdate(properties);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, properties.getId().toString())
        );
    }

    /**
     * {@code GET  /properties} : get all the properties.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of properties in body.
     */
    @GetMapping("/properties")
    public ResponseEntity<List<Properties>> getAllProperties(
        PropertiesCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Properties by criteria: {}", criteria);
        Page<Properties> page = propertiesQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /properties/count} : count all the properties.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/properties/count")
    public ResponseEntity<Long> countProperties(PropertiesCriteria criteria) {
        log.debug("REST request to count Properties by criteria: {}", criteria);
        return ResponseEntity.ok().body(propertiesQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /properties/:id} : get the "id" properties.
     *
     * @param id the id of the properties to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the properties, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/properties/{id}")
    public ResponseEntity<Properties> getProperties(@PathVariable Long id) {
        log.debug("REST request to get Properties : {}", id);
        Optional<Properties> properties = propertiesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(properties);
    }

    /**
     * {@code DELETE  /properties/:id} : delete the "id" properties.
     *
     * @param id the id of the properties to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/properties/{id}")
    public ResponseEntity<Void> deleteProperties(@PathVariable Long id) {
        log.debug("REST request to delete Properties : {}", id);
        propertiesService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
