package net.evercode.lawfirm.service;

import net.evercode.lawfirm.domain.Config;
import net.evercode.lawfirm.repository.ConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Config}.
 */
@Service
@Transactional
public class ConfigService {

    private final Logger log = LoggerFactory.getLogger(ConfigService.class);

    private final ConfigRepository configRepository;

    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    /**
     * Save a config.
     *
     * @param config the entity to save.
     * @return the persisted entity.
     */
    public Config save(Config config) {
        log.debug("Request to save Config : {}", config);
        return configRepository.save(config);
    }

    /**
     * Partially update a config.
     *
     * @param config the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Config> partialUpdate(Config config) {
        log.debug("Request to partially update Config : {}", config);

        return configRepository
            .findById(config.getId())
            .map(existingConfig -> {
                if (config.getKey() != null) {
                    existingConfig.setKey(config.getKey());
                }
                if (config.getValue() != null) {
                    existingConfig.setValue(config.getValue());
                }

                return existingConfig;
            })
            .map(configRepository::save);
    }

    /**
     * Get all the configs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Config> findAll(Pageable pageable) {
        log.debug("Request to get all Configs");
        return configRepository.findAll(pageable);
    }

    /**
     * Get one config by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Config> findOne(Long id) {
        log.debug("Request to get Config : {}", id);
        return configRepository.findById(id);
    }

    /**
     * Delete the config by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Config : {}", id);
        configRepository.deleteById(id);
    }

    public Config findByKey(String key) {
        log.debug("Request to get Config : {}", key);
        Optional<Config> config = configRepository.findByKey(key);
        if (config.isEmpty()) {
            log.error("Config not found by key: {}", key);
            throw new RuntimeException("Config not found by key: " + key);
        }
        return config.get();
    }
}
