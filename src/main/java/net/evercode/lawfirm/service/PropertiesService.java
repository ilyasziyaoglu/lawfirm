package net.evercode.lawfirm.service;

import net.evercode.lawfirm.domain.Properties;
import net.evercode.lawfirm.repository.PropertiesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Properties}.
 */
@Service
@Transactional
public class PropertiesService {

    private final Logger log = LoggerFactory.getLogger(PropertiesService.class);

    private final PropertiesRepository propertiesRepository;

    public PropertiesService(PropertiesRepository propertiesRepository) {
        this.propertiesRepository = propertiesRepository;
    }

    /**
     * Save a properties.
     *
     * @param properties the entity to save.
     * @return the persisted entity.
     */
    public Properties save(Properties properties) {
        log.debug("Request to save Properties : {}", properties);
        return propertiesRepository.save(properties);
    }

    /**
     * Partially update a properties.
     *
     * @param properties the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Properties> partialUpdate(Properties properties) {
        log.debug("Request to partially update Properties : {}", properties);

        return propertiesRepository
            .findById(properties.getId())
            .map(existingProperties -> {
                if (properties.getKey() != null) {
                    existingProperties.setKey(properties.getKey());
                }
                if (properties.getValue() != null) {
                    existingProperties.setValue(properties.getValue());
                }
                if (properties.getLanguage() != null) {
                    existingProperties.setLanguage(properties.getLanguage());
                }

                return existingProperties;
            })
            .map(propertiesRepository::save);
    }

    /**
     * Get all the properties.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Properties> findAll(Pageable pageable) {
        log.debug("Request to get all Properties");
        return propertiesRepository.findAll(pageable);
    }

    /**
     * Get one properties by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Properties> findOne(Long id) {
        log.debug("Request to get Properties : {}", id);
        return propertiesRepository.findById(id);
    }

    /**
     * Delete the properties by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Properties : {}", id);
        propertiesRepository.deleteById(id);
    }
}
