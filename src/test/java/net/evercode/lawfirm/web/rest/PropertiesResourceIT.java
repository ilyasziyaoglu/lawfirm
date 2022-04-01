package net.evercode.lawfirm.web.rest;

import net.evercode.lawfirm.IntegrationTest;
import net.evercode.lawfirm.domain.Properties;
import net.evercode.lawfirm.repository.PropertiesRepository;
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
 * Integration tests for the {@link PropertiesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PropertiesResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_LANGUAGE = "AAAAAAAAAA";
    private static final String UPDATED_LANGUAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/properties";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PropertiesRepository propertiesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPropertiesMockMvc;

    private Properties properties;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Properties createEntity(EntityManager em) {
        Properties properties = new Properties().key(DEFAULT_KEY).value(DEFAULT_VALUE).language(DEFAULT_LANGUAGE);
        return properties;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Properties createUpdatedEntity(EntityManager em) {
        Properties properties = new Properties().key(UPDATED_KEY).value(UPDATED_VALUE).language(UPDATED_LANGUAGE);
        return properties;
    }

    @BeforeEach
    public void initTest() {
        properties = createEntity(em);
    }

    @Test
    @Transactional
    void createProperties() throws Exception {
        int databaseSizeBeforeCreate = propertiesRepository.findAll().size();
        // Create the Properties
        restPropertiesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(properties)))
            .andExpect(status().isCreated());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeCreate + 1);
        Properties testProperties = propertiesList.get(propertiesList.size() - 1);
        assertThat(testProperties.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testProperties.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testProperties.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
    }

    @Test
    @Transactional
    void createPropertiesWithExistingId() throws Exception {
        // Create the Properties with an existing ID
        properties.setId(1L);

        int databaseSizeBeforeCreate = propertiesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPropertiesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(properties)))
            .andExpect(status().isBadRequest());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertiesRepository.findAll().size();
        // set the field null
        properties.setKey(null);

        // Create the Properties, which fails.

        restPropertiesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(properties)))
            .andExpect(status().isBadRequest());

        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertiesRepository.findAll().size();
        // set the field null
        properties.setValue(null);

        // Create the Properties, which fails.

        restPropertiesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(properties)))
            .andExpect(status().isBadRequest());

        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProperties() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList
        restPropertiesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(properties.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE)));
    }

    @Test
    @Transactional
    void getProperties() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get the properties
        restPropertiesMockMvc
            .perform(get(ENTITY_API_URL_ID, properties.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(properties.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE));
    }

    @Test
    @Transactional
    void getPropertiesByIdFiltering() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        Long id = properties.getId();

        defaultPropertiesShouldBeFound("id.equals=" + id);
        defaultPropertiesShouldNotBeFound("id.notEquals=" + id);

        defaultPropertiesShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPropertiesShouldNotBeFound("id.greaterThan=" + id);

        defaultPropertiesShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPropertiesShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPropertiesByKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where key equals to DEFAULT_KEY
        defaultPropertiesShouldBeFound("key.equals=" + DEFAULT_KEY);

        // Get all the propertiesList where key equals to UPDATED_KEY
        defaultPropertiesShouldNotBeFound("key.equals=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllPropertiesByKeyIsNotEqualToSomething() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where key not equals to DEFAULT_KEY
        defaultPropertiesShouldNotBeFound("key.notEquals=" + DEFAULT_KEY);

        // Get all the propertiesList where key not equals to UPDATED_KEY
        defaultPropertiesShouldBeFound("key.notEquals=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllPropertiesByKeyIsInShouldWork() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where key in DEFAULT_KEY or UPDATED_KEY
        defaultPropertiesShouldBeFound("key.in=" + DEFAULT_KEY + "," + UPDATED_KEY);

        // Get all the propertiesList where key equals to UPDATED_KEY
        defaultPropertiesShouldNotBeFound("key.in=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllPropertiesByKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where key is not null
        defaultPropertiesShouldBeFound("key.specified=true");

        // Get all the propertiesList where key is null
        defaultPropertiesShouldNotBeFound("key.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByKeyContainsSomething() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where key contains DEFAULT_KEY
        defaultPropertiesShouldBeFound("key.contains=" + DEFAULT_KEY);

        // Get all the propertiesList where key contains UPDATED_KEY
        defaultPropertiesShouldNotBeFound("key.contains=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllPropertiesByKeyNotContainsSomething() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where key does not contain DEFAULT_KEY
        defaultPropertiesShouldNotBeFound("key.doesNotContain=" + DEFAULT_KEY);

        // Get all the propertiesList where key does not contain UPDATED_KEY
        defaultPropertiesShouldBeFound("key.doesNotContain=" + UPDATED_KEY);
    }

    @Test
    @Transactional
    void getAllPropertiesByValueIsEqualToSomething() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where value equals to DEFAULT_VALUE
        defaultPropertiesShouldBeFound("value.equals=" + DEFAULT_VALUE);

        // Get all the propertiesList where value equals to UPDATED_VALUE
        defaultPropertiesShouldNotBeFound("value.equals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllPropertiesByValueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where value not equals to DEFAULT_VALUE
        defaultPropertiesShouldNotBeFound("value.notEquals=" + DEFAULT_VALUE);

        // Get all the propertiesList where value not equals to UPDATED_VALUE
        defaultPropertiesShouldBeFound("value.notEquals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllPropertiesByValueIsInShouldWork() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where value in DEFAULT_VALUE or UPDATED_VALUE
        defaultPropertiesShouldBeFound("value.in=" + DEFAULT_VALUE + "," + UPDATED_VALUE);

        // Get all the propertiesList where value equals to UPDATED_VALUE
        defaultPropertiesShouldNotBeFound("value.in=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllPropertiesByValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where value is not null
        defaultPropertiesShouldBeFound("value.specified=true");

        // Get all the propertiesList where value is null
        defaultPropertiesShouldNotBeFound("value.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByValueContainsSomething() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where value contains DEFAULT_VALUE
        defaultPropertiesShouldBeFound("value.contains=" + DEFAULT_VALUE);

        // Get all the propertiesList where value contains UPDATED_VALUE
        defaultPropertiesShouldNotBeFound("value.contains=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllPropertiesByValueNotContainsSomething() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where value does not contain DEFAULT_VALUE
        defaultPropertiesShouldNotBeFound("value.doesNotContain=" + DEFAULT_VALUE);

        // Get all the propertiesList where value does not contain UPDATED_VALUE
        defaultPropertiesShouldBeFound("value.doesNotContain=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLanguageIsEqualToSomething() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where language equals to DEFAULT_LANGUAGE
        defaultPropertiesShouldBeFound("language.equals=" + DEFAULT_LANGUAGE);

        // Get all the propertiesList where language equals to UPDATED_LANGUAGE
        defaultPropertiesShouldNotBeFound("language.equals=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLanguageIsNotEqualToSomething() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where language not equals to DEFAULT_LANGUAGE
        defaultPropertiesShouldNotBeFound("language.notEquals=" + DEFAULT_LANGUAGE);

        // Get all the propertiesList where language not equals to UPDATED_LANGUAGE
        defaultPropertiesShouldBeFound("language.notEquals=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLanguageIsInShouldWork() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where language in DEFAULT_LANGUAGE or UPDATED_LANGUAGE
        defaultPropertiesShouldBeFound("language.in=" + DEFAULT_LANGUAGE + "," + UPDATED_LANGUAGE);

        // Get all the propertiesList where language equals to UPDATED_LANGUAGE
        defaultPropertiesShouldNotBeFound("language.in=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLanguageIsNullOrNotNull() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where language is not null
        defaultPropertiesShouldBeFound("language.specified=true");

        // Get all the propertiesList where language is null
        defaultPropertiesShouldNotBeFound("language.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByLanguageContainsSomething() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where language contains DEFAULT_LANGUAGE
        defaultPropertiesShouldBeFound("language.contains=" + DEFAULT_LANGUAGE);

        // Get all the propertiesList where language contains UPDATED_LANGUAGE
        defaultPropertiesShouldNotBeFound("language.contains=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLanguageNotContainsSomething() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList where language does not contain DEFAULT_LANGUAGE
        defaultPropertiesShouldNotBeFound("language.doesNotContain=" + DEFAULT_LANGUAGE);

        // Get all the propertiesList where language does not contain UPDATED_LANGUAGE
        defaultPropertiesShouldBeFound("language.doesNotContain=" + UPDATED_LANGUAGE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPropertiesShouldBeFound(String filter) throws Exception {
        restPropertiesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(properties.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE)));

        // Check, that the count call also returns 1
        restPropertiesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPropertiesShouldNotBeFound(String filter) throws Exception {
        restPropertiesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPropertiesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProperties() throws Exception {
        // Get the properties
        restPropertiesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProperties() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();

        // Update the properties
        Properties updatedProperties = propertiesRepository.findById(properties.getId()).get();
        // Disconnect from session so that the updates on updatedProperties are not directly saved in db
        em.detach(updatedProperties);
        updatedProperties.key(UPDATED_KEY).value(UPDATED_VALUE).language(UPDATED_LANGUAGE);

        restPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProperties.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProperties))
            )
            .andExpect(status().isOk());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);
        Properties testProperties = propertiesList.get(propertiesList.size() - 1);
        assertThat(testProperties.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testProperties.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProperties.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void putNonExistingProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();
        properties.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, properties.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(properties))
            )
            .andExpect(status().isBadRequest());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();
        properties.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertiesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(properties))
            )
            .andExpect(status().isBadRequest());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();
        properties.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertiesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(properties)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePropertiesWithPatch() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();

        // Update the properties using partial update
        Properties partialUpdatedProperties = new Properties();
        partialUpdatedProperties.setId(properties.getId());

        restPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProperties.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProperties))
            )
            .andExpect(status().isOk());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);
        Properties testProperties = propertiesList.get(propertiesList.size() - 1);
        assertThat(testProperties.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testProperties.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testProperties.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
    }

    @Test
    @Transactional
    void fullUpdatePropertiesWithPatch() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();

        // Update the properties using partial update
        Properties partialUpdatedProperties = new Properties();
        partialUpdatedProperties.setId(properties.getId());

        partialUpdatedProperties.key(UPDATED_KEY).value(UPDATED_VALUE).language(UPDATED_LANGUAGE);

        restPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProperties.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProperties))
            )
            .andExpect(status().isOk());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);
        Properties testProperties = propertiesList.get(propertiesList.size() - 1);
        assertThat(testProperties.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testProperties.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProperties.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void patchNonExistingProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();
        properties.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, properties.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(properties))
            )
            .andExpect(status().isBadRequest());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();
        properties.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(properties))
            )
            .andExpect(status().isBadRequest());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();
        properties.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertiesMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(properties))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProperties() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        int databaseSizeBeforeDelete = propertiesRepository.findAll().size();

        // Delete the properties
        restPropertiesMockMvc
            .perform(delete(ENTITY_API_URL_ID, properties.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
