package net.evercode.lawfirm.service;

import net.evercode.lawfirm.domain.ServicePoints;
import net.evercode.lawfirm.repository.ServicePointsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link ServicePoints}.
 */
@Service
@Transactional
public class ServicePointsService {

    private final Logger log = LoggerFactory.getLogger(ServicePointsService.class);

    private final ServicePointsRepository servicePointsRepository;

    public ServicePointsService(ServicePointsRepository servicePointsRepository) {
        this.servicePointsRepository = servicePointsRepository;
    }

    /**
     * Save a servicePoints.
     *
     * @param servicePoints the entity to save.
     * @return the persisted entity.
     */
    public ServicePoints save(ServicePoints servicePoints) {
        log.debug("Request to save ServicePoints : {}", servicePoints);
        return servicePointsRepository.save(servicePoints);
    }

    /**
     * Partially update a servicePoints.
     *
     * @param servicePoints the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ServicePoints> partialUpdate(ServicePoints servicePoints) {
        log.debug("Request to partially update ServicePoints : {}", servicePoints);

        return servicePointsRepository
            .findById(servicePoints.getId())
            .map(existingServicePoints -> {
                if (servicePoints.getName() != null) {
                    existingServicePoints.setName(servicePoints.getName());
                }
                if (servicePoints.getEmail() != null) {
                    existingServicePoints.setEmail(servicePoints.getEmail());
                }
                if (servicePoints.getPhone() != null) {
                    existingServicePoints.setPhone(servicePoints.getPhone());
                }
                if (servicePoints.getAddress() != null) {
                    existingServicePoints.setAddress(servicePoints.getAddress());
                }
                if (servicePoints.getMapUrl() != null) {
                    existingServicePoints.setMapUrl(servicePoints.getMapUrl());
                }
                if (servicePoints.getLatitude() != null) {
                    existingServicePoints.setLatitude(servicePoints.getLatitude());
                }
                if (servicePoints.getLongitude() != null) {
                    existingServicePoints.setLongitude(servicePoints.getLongitude());
                }

                return existingServicePoints;
            })
            .map(servicePointsRepository::save);
    }

    /**
     * Get all the servicePoints.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ServicePoints> findAll() {
        log.debug("Request to get all ServicePoints");
        return servicePointsRepository.findAll();
    }

    /**
     * Get one servicePoints by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ServicePoints> findOne(Long id) {
        log.debug("Request to get ServicePoints : {}", id);
        return servicePointsRepository.findById(id);
    }

    /**
     * Delete the servicePoints by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ServicePoints : {}", id);
        servicePointsRepository.deleteById(id);
    }
}
