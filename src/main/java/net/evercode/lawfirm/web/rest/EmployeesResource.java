package net.evercode.lawfirm.web.rest;

import net.evercode.lawfirm.domain.Employees;
import net.evercode.lawfirm.repository.EmployeesRepository;
import net.evercode.lawfirm.service.EmployeesQueryService;
import net.evercode.lawfirm.service.EmployeesService;
import net.evercode.lawfirm.service.criteria.EmployeesCriteria;
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
 * REST controller for managing {@link net.evercode.lawfirm.domain.Employees}.
 */
@RestController
@RequestMapping("/api")
public class EmployeesResource {

    private final Logger log = LoggerFactory.getLogger(EmployeesResource.class);

    private static final String ENTITY_NAME = "employees";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EmployeesService employeesService;

    private final EmployeesRepository employeesRepository;

    private final EmployeesQueryService employeesQueryService;

    public EmployeesResource(
        EmployeesService employeesService,
        EmployeesRepository employeesRepository,
        EmployeesQueryService employeesQueryService
    ) {
        this.employeesService = employeesService;
        this.employeesRepository = employeesRepository;
        this.employeesQueryService = employeesQueryService;
    }

    /**
     * {@code POST  /employees} : Create a new employees.
     *
     * @param employees the employees to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new employees, or with status {@code 400 (Bad Request)} if the employees has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/employees")
    public ResponseEntity<Employees> createEmployees(@Valid @RequestBody Employees employees) throws URISyntaxException {
        log.debug("REST request to save Employees : {}", employees);
        if (employees.getId() != null) {
            throw new BadRequestAlertException("A new employees cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Employees result = employeesService.save(employees);
        return ResponseEntity
            .created(new URI("/api/employees/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /employees/:id} : Updates an existing employees.
     *
     * @param id the id of the employees to save.
     * @param employees the employees to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated employees,
     * or with status {@code 400 (Bad Request)} if the employees is not valid,
     * or with status {@code 500 (Internal Server Error)} if the employees couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/employees/{id}")
    public ResponseEntity<Employees> updateEmployees(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Employees employees
    ) throws URISyntaxException {
        log.debug("REST request to update Employees : {}, {}", id, employees);
        if (employees.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, employees.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!employeesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Employees result = employeesService.save(employees);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, employees.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /employees/:id} : Partial updates given fields of an existing employees, field will ignore if it is null
     *
     * @param id the id of the employees to save.
     * @param employees the employees to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated employees,
     * or with status {@code 400 (Bad Request)} if the employees is not valid,
     * or with status {@code 404 (Not Found)} if the employees is not found,
     * or with status {@code 500 (Internal Server Error)} if the employees couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/employees/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Employees> partialUpdateEmployees(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Employees employees
    ) throws URISyntaxException {
        log.debug("REST request to partial update Employees partially : {}, {}", id, employees);
        if (employees.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, employees.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!employeesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Employees> result = employeesService.partialUpdate(employees);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, employees.getId().toString())
        );
    }

    /**
     * {@code GET  /employees} : get all the employees.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of employees in body.
     */
    @GetMapping("/employees")
    public ResponseEntity<List<Employees>> getAllEmployees(EmployeesCriteria criteria) {
        log.debug("REST request to get Employees by criteria: {}", criteria);
        List<Employees> entityList = employeesQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /employees/count} : count all the employees.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/employees/count")
    public ResponseEntity<Long> countEmployees(EmployeesCriteria criteria) {
        log.debug("REST request to count Employees by criteria: {}", criteria);
        return ResponseEntity.ok().body(employeesQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /employees/:id} : get the "id" employees.
     *
     * @param id the id of the employees to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employees, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/employees/{id}")
    public ResponseEntity<Employees> getEmployees(@PathVariable Long id) {
        log.debug("REST request to get Employees : {}", id);
        Optional<Employees> employees = employeesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(employees);
    }

    /**
     * {@code DELETE  /employees/:id} : delete the "id" employees.
     *
     * @param id the id of the employees to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployees(@PathVariable Long id) {
        log.debug("REST request to delete Employees : {}", id);
        employeesService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
