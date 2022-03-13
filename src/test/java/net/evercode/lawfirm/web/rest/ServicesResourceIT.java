package net.evercode.lawfirm.web.rest;

import net.evercode.lawfirm.IntegrationTest;
import net.evercode.lawfirm.domain.Services;
import net.evercode.lawfirm.repository.ServicesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ServicesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ServicesResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER = 1;
    private static final Integer UPDATED_ORDER = 2;

    private static final byte[] DEFAULT_ICON = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_ICON = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_ICON_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_ICON_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/services";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ServicesRepository servicesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restServicesMockMvc;

    private Services services;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Services createEntity(EntityManager em) {
        Services services = new Services()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .order(DEFAULT_ORDER)
            .icon(DEFAULT_ICON)
            .iconContentType(DEFAULT_ICON_CONTENT_TYPE);
        return services;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Services createUpdatedEntity(EntityManager em) {
        Services services = new Services()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .order(UPDATED_ORDER)
            .icon(UPDATED_ICON)
            .iconContentType(UPDATED_ICON_CONTENT_TYPE);
        return services;
    }

    @BeforeEach
    public void initTest() {
        services = createEntity(em);
    }

    @Test
    @Transactional
    void createServices() throws Exception {
        int databaseSizeBeforeCreate = servicesRepository.findAll().size();
        // Create the Services
        restServicesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(services)))
            .andExpect(status().isCreated());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeCreate + 1);
        Services testServices = servicesList.get(servicesList.size() - 1);
        assertThat(testServices.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testServices.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testServices.getOrder()).isEqualTo(DEFAULT_ORDER);
        assertThat(testServices.getIcon()).isEqualTo(DEFAULT_ICON);
        assertThat(testServices.getIconContentType()).isEqualTo(DEFAULT_ICON_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createServicesWithExistingId() throws Exception {
        // Create the Services with an existing ID
        services.setId(1L);

        int databaseSizeBeforeCreate = servicesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restServicesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(services)))
            .andExpect(status().isBadRequest());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicesRepository.findAll().size();
        // set the field null
        services.setTitle(null);

        // Create the Services, which fails.

        restServicesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(services)))
            .andExpect(status().isBadRequest());

        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicesRepository.findAll().size();
        // set the field null
        services.setDescription(null);

        // Create the Services, which fails.

        restServicesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(services)))
            .andExpect(status().isBadRequest());

        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrderIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicesRepository.findAll().size();
        // set the field null
        services.setOrder(null);

        // Create the Services, which fails.

        restServicesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(services)))
            .andExpect(status().isBadRequest());

        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllServices() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);

        // Get all the servicesList
        restServicesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(services.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)))
            .andExpect(jsonPath("$.[*].iconContentType").value(hasItem(DEFAULT_ICON_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].icon").value(hasItem(Base64Utils.encodeToString(DEFAULT_ICON))));
    }

    @Test
    @Transactional
    void getServices() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);

        // Get the services
        restServicesMockMvc
            .perform(get(ENTITY_API_URL_ID, services.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(services.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.order").value(DEFAULT_ORDER))
            .andExpect(jsonPath("$.iconContentType").value(DEFAULT_ICON_CONTENT_TYPE))
            .andExpect(jsonPath("$.icon").value(Base64Utils.encodeToString(DEFAULT_ICON)));
    }

    @Test
    @Transactional
    void getNonExistingServices() throws Exception {
        // Get the services
        restServicesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewServices() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);

        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();

        // Update the services
        Services updatedServices = servicesRepository.findById(services.getId()).get();
        // Disconnect from session so that the updates on updatedServices are not directly saved in db
        em.detach(updatedServices);
        updatedServices
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .order(UPDATED_ORDER)
            .icon(UPDATED_ICON)
            .iconContentType(UPDATED_ICON_CONTENT_TYPE);

        restServicesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedServices.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedServices))
            )
            .andExpect(status().isOk());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
        Services testServices = servicesList.get(servicesList.size() - 1);
        assertThat(testServices.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testServices.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testServices.getOrder()).isEqualTo(UPDATED_ORDER);
        assertThat(testServices.getIcon()).isEqualTo(UPDATED_ICON);
        assertThat(testServices.getIconContentType()).isEqualTo(UPDATED_ICON_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingServices() throws Exception {
        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();
        services.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServicesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, services.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(services))
            )
            .andExpect(status().isBadRequest());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchServices() throws Exception {
        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();
        services.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(services))
            )
            .andExpect(status().isBadRequest());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamServices() throws Exception {
        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();
        services.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(services)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateServicesWithPatch() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);

        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();

        // Update the services using partial update
        Services partialUpdatedServices = new Services();
        partialUpdatedServices.setId(services.getId());

        partialUpdatedServices.icon(UPDATED_ICON).iconContentType(UPDATED_ICON_CONTENT_TYPE);

        restServicesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServices.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedServices))
            )
            .andExpect(status().isOk());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
        Services testServices = servicesList.get(servicesList.size() - 1);
        assertThat(testServices.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testServices.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testServices.getOrder()).isEqualTo(DEFAULT_ORDER);
        assertThat(testServices.getIcon()).isEqualTo(UPDATED_ICON);
        assertThat(testServices.getIconContentType()).isEqualTo(UPDATED_ICON_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateServicesWithPatch() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);

        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();

        // Update the services using partial update
        Services partialUpdatedServices = new Services();
        partialUpdatedServices.setId(services.getId());

        partialUpdatedServices
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .order(UPDATED_ORDER)
            .icon(UPDATED_ICON)
            .iconContentType(UPDATED_ICON_CONTENT_TYPE);

        restServicesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServices.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedServices))
            )
            .andExpect(status().isOk());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
        Services testServices = servicesList.get(servicesList.size() - 1);
        assertThat(testServices.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testServices.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testServices.getOrder()).isEqualTo(UPDATED_ORDER);
        assertThat(testServices.getIcon()).isEqualTo(UPDATED_ICON);
        assertThat(testServices.getIconContentType()).isEqualTo(UPDATED_ICON_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingServices() throws Exception {
        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();
        services.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServicesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, services.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(services))
            )
            .andExpect(status().isBadRequest());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchServices() throws Exception {
        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();
        services.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(services))
            )
            .andExpect(status().isBadRequest());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamServices() throws Exception {
        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();
        services.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(services)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteServices() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);

        int databaseSizeBeforeDelete = servicesRepository.findAll().size();

        // Delete the services
        restServicesMockMvc
            .perform(delete(ENTITY_API_URL_ID, services.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
