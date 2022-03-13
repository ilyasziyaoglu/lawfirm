package net.evercode.lawfirm.web.rest;

import net.evercode.lawfirm.IntegrationTest;
import net.evercode.lawfirm.domain.Configs;
import net.evercode.lawfirm.repository.ConfigsRepository;
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
 * Integration tests for the {@link ConfigsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ConfigsResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/configs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ConfigsRepository configsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restConfigsMockMvc;

    private Configs configs;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Configs createEntity(EntityManager em) {
        Configs configs = new Configs().key(DEFAULT_KEY).value(DEFAULT_VALUE);
        return configs;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Configs createUpdatedEntity(EntityManager em) {
        Configs configs = new Configs().key(UPDATED_KEY).value(UPDATED_VALUE);
        return configs;
    }

    @BeforeEach
    public void initTest() {
        configs = createEntity(em);
    }

    @Test
    @Transactional
    void createConfigs() throws Exception {
        int databaseSizeBeforeCreate = configsRepository.findAll().size();
        // Create the Configs
        restConfigsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(configs)))
            .andExpect(status().isCreated());

        // Validate the Configs in the database
        List<Configs> configsList = configsRepository.findAll();
        assertThat(configsList).hasSize(databaseSizeBeforeCreate + 1);
        Configs testConfigs = configsList.get(configsList.size() - 1);
        assertThat(testConfigs.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testConfigs.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    void createConfigsWithExistingId() throws Exception {
        // Create the Configs with an existing ID
        configs.setId(1L);

        int databaseSizeBeforeCreate = configsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restConfigsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(configs)))
            .andExpect(status().isBadRequest());

        // Validate the Configs in the database
        List<Configs> configsList = configsRepository.findAll();
        assertThat(configsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = configsRepository.findAll().size();
        // set the field null
        configs.setKey(null);

        // Create the Configs, which fails.

        restConfigsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(configs)))
            .andExpect(status().isBadRequest());

        List<Configs> configsList = configsRepository.findAll();
        assertThat(configsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = configsRepository.findAll().size();
        // set the field null
        configs.setValue(null);

        // Create the Configs, which fails.

        restConfigsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(configs)))
            .andExpect(status().isBadRequest());

        List<Configs> configsList = configsRepository.findAll();
        assertThat(configsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllConfigs() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        // Get all the configsList
        restConfigsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(configs.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    void getConfigs() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        // Get the configs
        restConfigsMockMvc
            .perform(get(ENTITY_API_URL_ID, configs.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(configs.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    void getConfigsByIdFiltering() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        Long id = configs.getId();

        defaultConfigsShouldBeFound("id.equals=" + id);
        defaultConfigsShouldNotBeFound("id.notEquals=" + id);

        defaultConfigsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultConfigsShouldNotBeFound("id.greaterThan=" + id);

        defaultConfigsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultConfigsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllConfigsByKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        // Get all the configsList where key equals to DEFAULT_KEY
        defaultConfigsShouldBeFound("key.equals=" + DEFAULT_KEY);

        // Get all the configsList where key equals to UPDATED_KEY
        defaultConfigsShouldNotBeFound("key.equals=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllConfigsByKeyIsNotEqualToSomething() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        // Get all the configsList where key not equals to DEFAULT_KEY
        defaultConfigsShouldNotBeFound("key.notEquals=" + DEFAULT_KEY);

        // Get all the configsList where key not equals to UPDATED_KEY
        defaultConfigsShouldBeFound("key.notEquals=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllConfigsByKeyIsInShouldWork() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        // Get all the configsList where key in DEFAULT_KEY or UPDATED_KEY
        defaultConfigsShouldBeFound("key.in=" + DEFAULT_KEY + "," + UPDATED_KEY);

        // Get all the configsList where key equals to UPDATED_KEY
        defaultConfigsShouldNotBeFound("key.in=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllConfigsByKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        // Get all the configsList where key is not null
        defaultConfigsShouldBeFound("key.specified=true");

        // Get all the configsList where key is null
        defaultConfigsShouldNotBeFound("key.specified=false");
    }

    @Test
    @Transactional
    void getAllConfigsByKeyContainsSomething() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        // Get all the configsList where key contains DEFAULT_KEY
        defaultConfigsShouldBeFound("key.contains=" + DEFAULT_KEY);

        // Get all the configsList where key contains UPDATED_KEY
        defaultConfigsShouldNotBeFound("key.contains=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllConfigsByKeyNotContainsSomething() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        // Get all the configsList where key does not contain DEFAULT_KEY
        defaultConfigsShouldNotBeFound("key.doesNotContain=" + DEFAULT_KEY);

        // Get all the configsList where key does not contain UPDATED_KEY
        defaultConfigsShouldBeFound("key.doesNotContain=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllConfigsByValueIsEqualToSomething() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        // Get all the configsList where value equals to DEFAULT_VALUE
        defaultConfigsShouldBeFound("value.equals=" + DEFAULT_VALUE);

        // Get all the configsList where value equals to UPDATED_VALUE
        defaultConfigsShouldNotBeFound("value.equals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllConfigsByValueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        // Get all the configsList where value not equals to DEFAULT_VALUE
        defaultConfigsShouldNotBeFound("value.notEquals=" + DEFAULT_VALUE);

        // Get all the configsList where value not equals to UPDATED_VALUE
        defaultConfigsShouldBeFound("value.notEquals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllConfigsByValueIsInShouldWork() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        // Get all the configsList where value in DEFAULT_VALUE or UPDATED_VALUE
        defaultConfigsShouldBeFound("value.in=" + DEFAULT_VALUE + "," + UPDATED_VALUE);

        // Get all the configsList where value equals to UPDATED_VALUE
        defaultConfigsShouldNotBeFound("value.in=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllConfigsByValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        // Get all the configsList where value is not null
        defaultConfigsShouldBeFound("value.specified=true");

        // Get all the configsList where value is null
        defaultConfigsShouldNotBeFound("value.specified=false");
    }

    @Test
    @Transactional
    void getAllConfigsByValueContainsSomething() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        // Get all the configsList where value contains DEFAULT_VALUE
        defaultConfigsShouldBeFound("value.contains=" + DEFAULT_VALUE);

        // Get all the configsList where value contains UPDATED_VALUE
        defaultConfigsShouldNotBeFound("value.contains=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllConfigsByValueNotContainsSomething() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        // Get all the configsList where value does not contain DEFAULT_VALUE
        defaultConfigsShouldNotBeFound("value.doesNotContain=" + DEFAULT_VALUE);

        // Get all the configsList where value does not contain UPDATED_VALUE
        defaultConfigsShouldBeFound("value.doesNotContain=" + UPDATED_VALUE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultConfigsShouldBeFound(String filter) throws Exception {
        restConfigsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(configs.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));

        // Check, that the count call also returns 1
        restConfigsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultConfigsShouldNotBeFound(String filter) throws Exception {
        restConfigsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restConfigsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingConfigs() throws Exception {
        // Get the configs
        restConfigsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewConfigs() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        int databaseSizeBeforeUpdate = configsRepository.findAll().size();

        // Update the configs
        Configs updatedConfigs = configsRepository.findById(configs.getId()).get();
        // Disconnect from session so that the updates on updatedConfigs are not directly saved in db
        em.detach(updatedConfigs);
        updatedConfigs.key(UPDATED_KEY).value(UPDATED_VALUE);

        restConfigsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedConfigs.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedConfigs))
            )
            .andExpect(status().isOk());

        // Validate the Configs in the database
        List<Configs> configsList = configsRepository.findAll();
        assertThat(configsList).hasSize(databaseSizeBeforeUpdate);
        Configs testConfigs = configsList.get(configsList.size() - 1);
        assertThat(testConfigs.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testConfigs.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void putNonExistingConfigs() throws Exception {
        int databaseSizeBeforeUpdate = configsRepository.findAll().size();
        configs.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConfigsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, configs.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(configs))
            )
            .andExpect(status().isBadRequest());

        // Validate the Configs in the database
        List<Configs> configsList = configsRepository.findAll();
        assertThat(configsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchConfigs() throws Exception {
        int databaseSizeBeforeUpdate = configsRepository.findAll().size();
        configs.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConfigsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(configs))
            )
            .andExpect(status().isBadRequest());

        // Validate the Configs in the database
        List<Configs> configsList = configsRepository.findAll();
        assertThat(configsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamConfigs() throws Exception {
        int databaseSizeBeforeUpdate = configsRepository.findAll().size();
        configs.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConfigsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(configs)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Configs in the database
        List<Configs> configsList = configsRepository.findAll();
        assertThat(configsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateConfigsWithPatch() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        int databaseSizeBeforeUpdate = configsRepository.findAll().size();

        // Update the configs using partial update
        Configs partialUpdatedConfigs = new Configs();
        partialUpdatedConfigs.setId(configs.getId());

        partialUpdatedConfigs.key(UPDATED_KEY).value(UPDATED_VALUE);

        restConfigsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConfigs.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedConfigs))
            )
            .andExpect(status().isOk());

        // Validate the Configs in the database
        List<Configs> configsList = configsRepository.findAll();
        assertThat(configsList).hasSize(databaseSizeBeforeUpdate);
        Configs testConfigs = configsList.get(configsList.size() - 1);
        assertThat(testConfigs.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testConfigs.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateConfigsWithPatch() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        int databaseSizeBeforeUpdate = configsRepository.findAll().size();

        // Update the configs using partial update
        Configs partialUpdatedConfigs = new Configs();
        partialUpdatedConfigs.setId(configs.getId());

        partialUpdatedConfigs.key(UPDATED_KEY).value(UPDATED_VALUE);

        restConfigsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConfigs.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedConfigs))
            )
            .andExpect(status().isOk());

        // Validate the Configs in the database
        List<Configs> configsList = configsRepository.findAll();
        assertThat(configsList).hasSize(databaseSizeBeforeUpdate);
        Configs testConfigs = configsList.get(configsList.size() - 1);
        assertThat(testConfigs.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testConfigs.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingConfigs() throws Exception {
        int databaseSizeBeforeUpdate = configsRepository.findAll().size();
        configs.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConfigsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, configs.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(configs))
            )
            .andExpect(status().isBadRequest());

        // Validate the Configs in the database
        List<Configs> configsList = configsRepository.findAll();
        assertThat(configsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchConfigs() throws Exception {
        int databaseSizeBeforeUpdate = configsRepository.findAll().size();
        configs.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConfigsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(configs))
            )
            .andExpect(status().isBadRequest());

        // Validate the Configs in the database
        List<Configs> configsList = configsRepository.findAll();
        assertThat(configsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamConfigs() throws Exception {
        int databaseSizeBeforeUpdate = configsRepository.findAll().size();
        configs.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConfigsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(configs)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Configs in the database
        List<Configs> configsList = configsRepository.findAll();
        assertThat(configsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteConfigs() throws Exception {
        // Initialize the database
        configsRepository.saveAndFlush(configs);

        int databaseSizeBeforeDelete = configsRepository.findAll().size();

        // Delete the configs
        restConfigsMockMvc
            .perform(delete(ENTITY_API_URL_ID, configs.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Configs> configsList = configsRepository.findAll();
        assertThat(configsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
