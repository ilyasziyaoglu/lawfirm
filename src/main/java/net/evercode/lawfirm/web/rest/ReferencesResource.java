package net.evercode.lawfirm.web.rest;

import net.evercode.lawfirm.domain.References;
import net.evercode.lawfirm.repository.ReferencesRepository;
import net.evercode.lawfirm.service.ReferencesService;
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
 * REST controller for managing {@link net.evercode.lawfirm.domain.References}.
 */
@RestController
@RequestMapping("/api")
public class ReferencesResource {

    private final Logger log = LoggerFactory.getLogger(ReferencesResource.class);

    private static final String ENTITY_NAME = "references";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReferencesService referencesService;

    private final ReferencesRepository referencesRepository;

    public ReferencesResource(ReferencesService referencesService, ReferencesRepository referencesRepository) {
        this.referencesService = referencesService;
        this.referencesRepository = referencesRepository;
    }

    /**
     * {@code POST  /references} : Create a new references.
     *
     * @param references the references to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new references, or with status {@code 400 (Bad Request)} if the references has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/references")
    public ResponseEntity<References> createReferences(@Valid @RequestBody References references) throws URISyntaxException {
        log.debug("REST request to save References : {}", references);
        if (references.getId() != null) {
            throw new BadRequestAlertException("A new references cannot already have an ID", ENTITY_NAME, "idexists");
        }
        References result = referencesService.save(references);
        return ResponseEntity
            .created(new URI("/api/references/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /references/:id} : Updates an existing references.
     *
     * @param id the id of the references to save.
     * @param references the references to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated references,
     * or with status {@code 400 (Bad Request)} if the references is not valid,
     * or with status {@code 500 (Internal Server Error)} if the references couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/references/{id}")
    public ResponseEntity<References> updateReferences(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody References references
    ) throws URISyntaxException {
        log.debug("REST request to update References : {}, {}", id, references);
        if (references.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, references.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!referencesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        References result = referencesService.save(references);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, references.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /references/:id} : Partial updates given fields of an existing references, field will ignore if it is null
     *
     * @param id the id of the references to save.
     * @param references the references to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated references,
     * or with status {@code 400 (Bad Request)} if the references is not valid,
     * or with status {@code 404 (Not Found)} if the references is not found,
     * or with status {@code 500 (Internal Server Error)} if the references couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/references/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<References> partialUpdateReferences(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody References references
    ) throws URISyntaxException {
        log.debug("REST request to partial update References partially : {}, {}", id, references);
        if (references.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, references.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!referencesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<References> result = referencesService.partialUpdate(references);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, references.getId().toString())
        );
    }

    /**
     * {@code GET  /references} : get all the references.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of references in body.
     */
    @GetMapping("/references")
    public ResponseEntity<List<References>> getAllReferences(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of References");
        Page<References> page = referencesService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /references/:id} : get the "id" references.
     *
     * @param id the id of the references to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the references, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/references/{id}")
    public ResponseEntity<References> getReferences(@PathVariable Long id) {
        log.debug("REST request to get References : {}", id);
        Optional<References> references = referencesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(references);
    }

    /**
     * {@code DELETE  /references/:id} : delete the "id" references.
     *
     * @param id the id of the references to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/references/{id}")
    public ResponseEntity<Void> deleteReferences(@PathVariable Long id) {
        log.debug("REST request to delete References : {}", id);
        referencesService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
