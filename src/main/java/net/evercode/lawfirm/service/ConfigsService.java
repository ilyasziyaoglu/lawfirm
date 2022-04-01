package net.evercode.lawfirm.service;

import com.github.dockerjava.api.exception.NotFoundException;
import net.evercode.lawfirm.domain.Config;
import net.evercode.lawfirm.repository.ConfigsRepository;
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
public class ConfigsService {

    private final Logger log = LoggerFactory.getLogger(ConfigsService.class);

    private final ConfigsRepository configsRepository;

    public ConfigsService(ConfigsRepository configsRepository) {
        this.configsRepository = configsRepository;
    }

    /**
     * Save a configs.
     *
     * @param config the entity to save.
     * @return the persisted entity.
     */
    public Config save(Config config) {
        log.debug("Request to save Configs : {}", config);
        return configsRepository.save(config);
    }

    /**
     * Partially update a configs.
     *
     * @param config the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Config> partialUpdate(Config config) {
        log.debug("Request to partially update Configs : {}", config);

        return configsRepository
            .findById(config.getId())
            .map(existingConfigs -> {
                if (config.getKey() != null) {
                    existingConfigs.setKey(config.getKey());
                }
                if (config.getValue() != null) {
                    existingConfigs.setValue(config.getValue());
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
    public Page<Config> findAll(Pageable pageable) {
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
    public Optional<Config> findOne(Long id) {
        log.debug("Request to get Configs : {}", id);
        return configsRepository.findById(id);
    }

    /**
     * Get one configs by key.
     *
     * @param key the key of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Config findTopByKey(String key) {
        log.debug("Request to get Config by key : {}", key);
        Optional<Config> optionalConfig = configsRepository.findTopByKey(key);
        if (optionalConfig.isEmpty()) {
            log.error("Configs not found! Key: {}", key);
            throw new NotFoundException("Configs not found! Key: " + key);
        }
        return optionalConfig.get();
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
