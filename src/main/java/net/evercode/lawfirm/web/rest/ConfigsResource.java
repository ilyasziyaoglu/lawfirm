package net.evercode.lawfirm.web.rest;

import net.evercode.lawfirm.domain.Config;
import net.evercode.lawfirm.repository.ConfigsRepository;
import net.evercode.lawfirm.service.ConfigsQueryService;
import net.evercode.lawfirm.service.ConfigsService;
import net.evercode.lawfirm.service.criteria.ConfigsCriteria;
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
 * REST controller for managing {@link Config}.
 */
@RestController
@RequestMapping("/api")
public class ConfigsResource {

    private final Logger log = LoggerFactory.getLogger(ConfigsResource.class);

    private static final String ENTITY_NAME = "configs";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConfigsService configsService;

    private final ConfigsRepository configsRepository;

    private final ConfigsQueryService configsQueryService;

    public ConfigsResource(ConfigsService configsService, ConfigsRepository configsRepository, ConfigsQueryService configsQueryService) {
        this.configsService = configsService;
        this.configsRepository = configsRepository;
        this.configsQueryService = configsQueryService;
    }

    /**
     * {@code POST  /configs} : Create a new configs.
     *
     * @param config the configs to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new configs, or with status {@code 400 (Bad Request)} if the configs has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/configs")
    public ResponseEntity<Config> createConfigs(@Valid @RequestBody Config config) throws URISyntaxException {
        log.debug("REST request to save Configs : {}", config);
        if (config.getId() != null) {
            throw new BadRequestAlertException("A new configs cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Config result = configsService.save(config);
        return ResponseEntity
            .created(new URI("/api/configs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /configs/:id} : Updates an existing configs.
     *
     * @param id the id of the configs to save.
     * @param config the configs to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated configs,
     * or with status {@code 400 (Bad Request)} if the configs is not valid,
     * or with status {@code 500 (Internal Server Error)} if the configs couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/configs/{id}")
    public ResponseEntity<Config> updateConfigs(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Config config
    ) throws URISyntaxException {
        log.debug("REST request to update Configs : {}, {}", id, config);
        if (config.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, config.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!configsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Config result = configsService.save(config);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, config.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /configs/:id} : Partial updates given fields of an existing configs, field will ignore if it is null
     *
     * @param id the id of the configs to save.
     * @param config the configs to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated configs,
     * or with status {@code 400 (Bad Request)} if the configs is not valid,
     * or with status {@code 404 (Not Found)} if the configs is not found,
     * or with status {@code 500 (Internal Server Error)} if the configs couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/configs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Config> partialUpdateConfigs(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Config config
    ) throws URISyntaxException {
        log.debug("REST request to partial update Configs partially : {}, {}", id, config);
        if (config.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, config.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!configsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Config> result = configsService.partialUpdate(config);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, config.getId().toString())
        );
    }

    /**
     * {@code GET  /configs} : get all the configs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of configs in body.
     */
    @GetMapping("/configs")
    public ResponseEntity<List<Config>> getAllConfigs(
        ConfigsCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Configs by criteria: {}", criteria);
        Page<Config> page = configsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /configs/count} : count all the configs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/configs/count")
    public ResponseEntity<Long> countConfigs(ConfigsCriteria criteria) {
        log.debug("REST request to count Configs by criteria: {}", criteria);
        return ResponseEntity.ok().body(configsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /configs/:id} : get the "id" configs.
     *
     * @param id the id of the configs to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the configs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/configs/{id}")
    public ResponseEntity<Config> getConfigs(@PathVariable Long id) {
        log.debug("REST request to get Configs : {}", id);
        Optional<Config> configs = configsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(configs);
    }

    /**
     * {@code DELETE  /configs/:id} : delete the "id" configs.
     *
     * @param id the id of the configs to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/configs/{id}")
    public ResponseEntity<Void> deleteConfigs(@PathVariable Long id) {
        log.debug("REST request to delete Configs : {}", id);
        configsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
