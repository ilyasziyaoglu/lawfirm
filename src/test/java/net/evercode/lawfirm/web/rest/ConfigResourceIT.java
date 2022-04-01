package net.evercode.lawfirm.web.rest;

import net.evercode.lawfirm.IntegrationTest;
import net.evercode.lawfirm.domain.Config;
import net.evercode.lawfirm.repository.ConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ConfigResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ConfigResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/configs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restConfigMockMvc;

    private Config config;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Config createEntity(EntityManager em) {
        Config config = new Config().key(DEFAULT_KEY).value(DEFAULT_VALUE);
        return config;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Config createUpdatedEntity(EntityManager em) {
        Config config = new Config().key(UPDATED_KEY).value(UPDATED_VALUE);
        return config;
    }

    @BeforeEach
    public void initTest() {
        config = createEntity(em);
    }

    @Test
    @Transactional
    void createConfig() throws Exception {
        int databaseSizeBeforeCreate = configRepository.findAll().size();
        // Create the Config
        restConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(config)))
            .andExpect(status().isCreated());

        // Validate the Config in the database
        List<Config> configList = configRepository.findAll();
        assertThat(configList).hasSize(databaseSizeBeforeCreate + 1);
        Config testConfig = configList.get(configList.size() - 1);
        assertThat(testConfig.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testConfig.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    void createConfigWithExistingId() throws Exception {
        // Create the Config with an existing ID
        config.setId(1L);

        int databaseSizeBeforeCreate = configRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(config)))
            .andExpect(status().isBadRequest());

        // Validate the Config in the database
        List<Config> configList = configRepository.findAll();
        assertThat(configList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = configRepository.findAll().size();
        // set the field null
        config.setKey(null);

        // Create the Config, which fails.

        restConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(config)))
            .andExpect(status().isBadRequest());

        List<Config> configList = configRepository.findAll();
        assertThat(configList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = configRepository.findAll().size();
        // set the field null
        config.setValue(null);

        // Create the Config, which fails.

        restConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(config)))
            .andExpect(status().isBadRequest());

        List<Config> configList = configRepository.findAll();
        assertThat(configList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllConfigs() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        // Get all the configList
        restConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(config.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    void getConfig() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        // Get the config
        restConfigMockMvc
            .perform(get(ENTITY_API_URL_ID, config.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(config.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    void getConfigsByIdFiltering() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        Long id = config.getId();

        defaultConfigShouldBeFound("id.equals=" + id);
        defaultConfigShouldNotBeFound("id.notEquals=" + id);

        defaultConfigShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultConfigShouldNotBeFound("id.greaterThan=" + id);

        defaultConfigShouldBeFound("id.lessThanOrEqual=" + id);
        defaultConfigShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllConfigsByKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        // Get all the configList where key equals to DEFAULT_KEY
        defaultConfigShouldBeFound("key.equals=" + DEFAULT_KEY);

        // Get all the configList where key equals to UPDATED_KEY
        defaultConfigShouldNotBeFound("key.equals=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllConfigsByKeyIsNotEqualToSomething() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        // Get all the configList where key not equals to DEFAULT_KEY
        defaultConfigShouldNotBeFound("key.notEquals=" + DEFAULT_KEY);

        // Get all the configList where key not equals to UPDATED_KEY
        defaultConfigShouldBeFound("key.notEquals=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllConfigsByKeyIsInShouldWork() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        // Get all the configList where key in DEFAULT_KEY or UPDATED_KEY
        defaultConfigShouldBeFound("key.in=" + DEFAULT_KEY + "," + UPDATED_KEY);

        // Get all the configList where key equals to UPDATED_KEY
        defaultConfigShouldNotBeFound("key.in=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllConfigsByKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        // Get all the configList where key is not null
        defaultConfigShouldBeFound("key.specified=true");

        // Get all the configList where key is null
        defaultConfigShouldNotBeFound("key.specified=false");
    }

    @Test
    @Transactional
    void getAllConfigsByKeyContainsSomething() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        // Get all the configList where key contains DEFAULT_KEY
        defaultConfigShouldBeFound("key.contains=" + DEFAULT_KEY);

        // Get all the configList where key contains UPDATED_KEY
        defaultConfigShouldNotBeFound("key.contains=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllConfigsByKeyNotContainsSomething() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        // Get all the configList where key does not contain DEFAULT_KEY
        defaultConfigShouldNotBeFound("key.doesNotContain=" + DEFAULT_KEY);

        // Get all the configList where key does not contain UPDATED_KEY
        defaultConfigShouldBeFound("key.doesNotContain=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllConfigsByValueIsEqualToSomething() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        // Get all the configList where value equals to DEFAULT_VALUE
        defaultConfigShouldBeFound("value.equals=" + DEFAULT_VALUE);

        // Get all the configList where value equals to UPDATED_VALUE
        defaultConfigShouldNotBeFound("value.equals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllConfigsByValueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        // Get all the configList where value not equals to DEFAULT_VALUE
        defaultConfigShouldNotBeFound("value.notEquals=" + DEFAULT_VALUE);

        // Get all the configList where value not equals to UPDATED_VALUE
        defaultConfigShouldBeFound("value.notEquals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllConfigsByValueIsInShouldWork() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        // Get all the configList where value in DEFAULT_VALUE or UPDATED_VALUE
        defaultConfigShouldBeFound("value.in=" + DEFAULT_VALUE + "," + UPDATED_VALUE);

        // Get all the configList where value equals to UPDATED_VALUE
        defaultConfigShouldNotBeFound("value.in=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllConfigsByValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        // Get all the configList where value is not null
        defaultConfigShouldBeFound("value.specified=true");

        // Get all the configList where value is null
        defaultConfigShouldNotBeFound("value.specified=false");
    }

    @Test
    @Transactional
    void getAllConfigsByValueContainsSomething() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        // Get all the configList where value contains DEFAULT_VALUE
        defaultConfigShouldBeFound("value.contains=" + DEFAULT_VALUE);

        // Get all the configList where value contains UPDATED_VALUE
        defaultConfigShouldNotBeFound("value.contains=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllConfigsByValueNotContainsSomething() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        // Get all the configList where value does not contain DEFAULT_VALUE
        defaultConfigShouldNotBeFound("value.doesNotContain=" + DEFAULT_VALUE);

        // Get all the configList where value does not contain UPDATED_VALUE
        defaultConfigShouldBeFound("value.doesNotContain=" + UPDATED_VALUE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultConfigShouldBeFound(String filter) throws Exception {
        restConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(config.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));

        // Check, that the count call also returns 1
        restConfigMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultConfigShouldNotBeFound(String filter) throws Exception {
        restConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restConfigMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingConfig() throws Exception {
        // Get the config
        restConfigMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewConfig() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        int databaseSizeBeforeUpdate = configRepository.findAll().size();

        // Update the config
        Config updatedConfig = configRepository.findById(config.getId()).get();
        // Disconnect from session so that the updates on updatedConfig are not directly saved in db
        em.detach(updatedConfig);
        updatedConfig.key(UPDATED_KEY).value(UPDATED_VALUE);

        restConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedConfig.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedConfig))
            )
            .andExpect(status().isOk());

        // Validate the Config in the database
        List<Config> configList = configRepository.findAll();
        assertThat(configList).hasSize(databaseSizeBeforeUpdate);
        Config testConfig = configList.get(configList.size() - 1);
        assertThat(testConfig.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testConfig.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void putNonExistingConfig() throws Exception {
        int databaseSizeBeforeUpdate = configRepository.findAll().size();
        config.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, config.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(config))
            )
            .andExpect(status().isBadRequest());

        // Validate the Config in the database
        List<Config> configList = configRepository.findAll();
        assertThat(configList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchConfig() throws Exception {
        int databaseSizeBeforeUpdate = configRepository.findAll().size();
        config.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(config))
            )
            .andExpect(status().isBadRequest());

        // Validate the Config in the database
        List<Config> configList = configRepository.findAll();
        assertThat(configList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamConfig() throws Exception {
        int databaseSizeBeforeUpdate = configRepository.findAll().size();
        config.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConfigMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(config)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Config in the database
        List<Config> configList = configRepository.findAll();
        assertThat(configList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateConfigWithPatch() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        int databaseSizeBeforeUpdate = configRepository.findAll().size();

        // Update the config using partial update
        Config partialUpdatedConfig = new Config();
        partialUpdatedConfig.setId(config.getId());

        partialUpdatedConfig.key(UPDATED_KEY).value(UPDATED_VALUE);

        restConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedConfig))
            )
            .andExpect(status().isOk());

        // Validate the Config in the database
        List<Config> configList = configRepository.findAll();
        assertThat(configList).hasSize(databaseSizeBeforeUpdate);
        Config testConfig = configList.get(configList.size() - 1);
        assertThat(testConfig.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testConfig.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateConfigWithPatch() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        int databaseSizeBeforeUpdate = configRepository.findAll().size();

        // Update the config using partial update
        Config partialUpdatedConfig = new Config();
        partialUpdatedConfig.setId(config.getId());

        partialUpdatedConfig.key(UPDATED_KEY).value(UPDATED_VALUE);

        restConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedConfig))
            )
            .andExpect(status().isOk());

        // Validate the Config in the database
        List<Config> configList = configRepository.findAll();
        assertThat(configList).hasSize(databaseSizeBeforeUpdate);
        Config testConfig = configList.get(configList.size() - 1);
        assertThat(testConfig.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testConfig.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingConfig() throws Exception {
        int databaseSizeBeforeUpdate = configRepository.findAll().size();
        config.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, config.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(config))
            )
            .andExpect(status().isBadRequest());

        // Validate the Config in the database
        List<Config> configList = configRepository.findAll();
        assertThat(configList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchConfig() throws Exception {
        int databaseSizeBeforeUpdate = configRepository.findAll().size();
        config.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(config))
            )
            .andExpect(status().isBadRequest());

        // Validate the Config in the database
        List<Config> configList = configRepository.findAll();
        assertThat(configList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamConfig() throws Exception {
        int databaseSizeBeforeUpdate = configRepository.findAll().size();
        config.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConfigMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(config)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Config in the database
        List<Config> configList = configRepository.findAll();
        assertThat(configList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteConfig() throws Exception {
        // Initialize the database
        configRepository.saveAndFlush(config);

        int databaseSizeBeforeDelete = configRepository.findAll().size();

        // Delete the config
        restConfigMockMvc
            .perform(delete(ENTITY_API_URL_ID, config.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Config> configList = configRepository.findAll();
        assertThat(configList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
