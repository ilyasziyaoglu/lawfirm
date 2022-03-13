package net.evercode.lawfirm.service;

import net.evercode.lawfirm.domain.Services;
import net.evercode.lawfirm.repository.ServicesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Services}.
 */
@Service
@Transactional
public class ServicesService {

    private final Logger log = LoggerFactory.getLogger(ServicesService.class);

    private final ServicesRepository servicesRepository;

    public ServicesService(ServicesRepository servicesRepository) {
        this.servicesRepository = servicesRepository;
    }

    /**
     * Save a services.
     *
     * @param services the entity to save.
     * @return the persisted entity.
     */
    public Services save(Services services) {
        log.debug("Request to save Services : {}", services);
        return servicesRepository.save(services);
    }

    /**
     * Partially update a services.
     *
     * @param services the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Services> partialUpdate(Services services) {
        log.debug("Request to partially update Services : {}", services);

        return servicesRepository
            .findById(services.getId())
            .map(existingServices -> {
                if (services.getTitle() != null) {
                    existingServices.setTitle(services.getTitle());
                }
                if (services.getDescription() != null) {
                    existingServices.setDescription(services.getDescription());
                }
                if (services.getOrder() != null) {
                    existingServices.setOrder(services.getOrder());
                }
                if (services.getIcon() != null) {
                    existingServices.setIcon(services.getIcon());
                }
                if (services.getIconContentType() != null) {
                    existingServices.setIconContentType(services.getIconContentType());
                }

                return existingServices;
            })
            .map(servicesRepository::save);
    }

    /**
     * Get all the services.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Services> findAll() {
        log.debug("Request to get all Services");
        return servicesRepository.findAll();
    }

    /**
     * Get one services by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Services> findOne(Long id) {
        log.debug("Request to get Services : {}", id);
        return servicesRepository.findById(id);
    }

    /**
     * Delete the services by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Services : {}", id);
        servicesRepository.deleteById(id);
    }
}
