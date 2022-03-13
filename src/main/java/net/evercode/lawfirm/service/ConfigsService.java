package net.evercode.lawfirm.service;

import net.evercode.lawfirm.domain.Configs;
import net.evercode.lawfirm.repository.ConfigsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Configs}.
 */
@Service
@Transactional
public class ConfigsService {

    private final Logger log = LoggerFactory.getLogger(ConfigsService.class);

    private final ConfigsRepository configsRepository;

    public ConfigsService(ConfigsRepository configsRepository) {
        this.configsRepository = configsRepository;
    }

    /**
     * Save a configs.
     *
     * @param configs the entity to save.
     * @return the persisted entity.
     */
    public Configs save(Configs configs) {
        log.debug("Request to save Configs : {}", configs);
        return configsRepository.save(configs);
    }

    /**
     * Partially update a configs.
     *
     * @param configs the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Configs> partialUpdate(Configs configs) {
        log.debug("Request to partially update Configs : {}", configs);

        return configsRepository
            .findById(configs.getId())
            .map(existingConfigs -> {
                if (configs.getKey() != null) {
                    existingConfigs.setKey(configs.getKey());
                }
                if (configs.getValue() != null) {
                    existingConfigs.setValue(configs.getValue());
                }

                return existingConfigs;
            })
            .map(configsRepository::save);
    }

    /**
     * Get all the configs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Configs> findAll(Pageable pageable) {
        log.debug("Request to get all Configs");
        return configsRepository.findAll(pageable);
    }

    /**
     * Get one configs by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Configs> findOne(Long id) {
        log.debug("Request to get Configs : {}", id);
        return configsRepository.findById(id);
    }

    /**
     * Delete the configs by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Configs : {}", id);
        configsRepository.deleteById(id);
    }
}
