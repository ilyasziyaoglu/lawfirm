package net.evercode.lawfirm.service;

import net.evercode.lawfirm.common.utils.FileUtils;
import net.evercode.lawfirm.domain.Employees;
import net.evercode.lawfirm.repository.EmployeesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Employees}.
 */
@Service
@Transactional
public class EmployeesService {

    private final Logger log = LoggerFactory.getLogger(EmployeesService.class);

    private final EmployeesRepository employeesRepository;
    private final FileUtils fileUtils;

    public EmployeesService(EmployeesRepository employeesRepository, FileUtils fileUtils) {
        this.employeesRepository = employeesRepository;
        this.fileUtils = fileUtils;
    }

    /**
     * Save a employees.
     *
     * @param employees the entity to save.
     * @return the persisted entity.
     */
    public Employees save(Employees employees) {
        log.debug("Request to save Employees : {}", employees);
        fileUtils.saveFile(employees.getImage(), "images", employees.getImageContentType());
        return employeesRepository.save(employees);
    }

    /**
     * Partially update a employees.
     *
     * @param employees the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Employees> partialUpdate(Employees employees) {
        log.debug("Request to partially update Employees : {}", employees);

        return employeesRepository
            .findById(employees.getId())
            .map(existingEmployees -> {
                if (employees.getName() != null) {
                    existingEmployees.setName(employees.getName());
                }
                if (employees.getSurname() != null) {
                    existingEmployees.setSurname(employees.getSurname());
                }
                if (employees.getTitle() != null) {
                    existingEmployees.setTitle(employees.getTitle());
                }
                if (employees.getStory() != null) {
                    existingEmployees.setStory(employees.getStory());
                }
                if (employees.getOrder() != null) {
                    existingEmployees.setOrder(employees.getOrder());
                }
                if (employees.getImage() != null) {
                    // TODO: implement also image upload
                    existingEmployees.setImage(employees.getImage());
                }
                if (employees.getImageContentType() != null) {
                    existingEmployees.setImageContentType(employees.getImageContentType());
                }

                return existingEmployees;
            })
            .map(employeesRepository::save);
    }

    /**
     * Get all the employees.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Employees> findAll() {
        log.debug("Request to get all Employees");
        return employeesRepository.findAllWithEagerRelationships();
    }

    /**
     * Get all the employees with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Employees> findAllWithEagerRelationships(Pageable pageable) {
        return employeesRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one employees by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Employees> findOne(Long id) {
        log.debug("Request to get Employees : {}", id);
        return employeesRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the employees by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Employees : {}", id);
        employeesRepository.findTopById(id).ifPresent(employees -> {
            // TODO: delete also image file
            employeesRepository.deleteById(id);
        });
    }
}
