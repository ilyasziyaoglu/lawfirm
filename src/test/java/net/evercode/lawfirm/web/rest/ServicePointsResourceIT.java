package net.evercode.lawfirm.web.rest;

import net.evercode.lawfirm.IntegrationTest;
import net.evercode.lawfirm.domain.ServicePoints;
import net.evercode.lawfirm.repository.ServicePointsRepository;
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
 * Integration tests for the {@link ServicePointsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ServicePointsResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_MAP_URL = "AAAAAAAAAA";
    private static final String UPDATED_MAP_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_LATITUDE = 1;
    private static final Integer UPDATED_LATITUDE = 2;

    private static final Integer DEFAULT_LONGITUDE = 1;
    private static final Integer UPDATED_LONGITUDE = 2;

    private static final String ENTITY_API_URL = "/api/service-points";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ServicePointsRepository servicePointsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restServicePointsMockMvc;

    private ServicePoints servicePoints;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServicePoints createEntity(EntityManager em) {
        ServicePoints servicePoints = new ServicePoints()
            .name(DEFAULT_NAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .address(DEFAULT_ADDRESS)
            .mapUrl(DEFAULT_MAP_URL)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE);
        return servicePoints;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServicePoints createUpdatedEntity(EntityManager em) {
        ServicePoints servicePoints = new ServicePoints()
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .mapUrl(UPDATED_MAP_URL)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);
        return servicePoints;
    }

    @BeforeEach
    public void initTest() {
        servicePoints = createEntity(em);
    }

    @Test
    @Transactional
    void createServicePoints() throws Exception {
        int databaseSizeBeforeCreate = servicePointsRepository.findAll().size();
        // Create the ServicePoints
        restServicePointsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(servicePoints)))
            .andExpect(status().isCreated());

        // Validate the ServicePoints in the database
        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeCreate + 1);
        ServicePoints testServicePoints = servicePointsList.get(servicePointsList.size() - 1);
        assertThat(testServicePoints.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testServicePoints.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testServicePoints.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testServicePoints.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testServicePoints.getMapUrl()).isEqualTo(DEFAULT_MAP_URL);
        assertThat(testServicePoints.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testServicePoints.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void createServicePointsWithExistingId() throws Exception {
        // Create the ServicePoints with an existing ID
        servicePoints.setId(1L);

        int databaseSizeBeforeCreate = servicePointsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restServicePointsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(servicePoints)))
            .andExpect(status().isBadRequest());

        // Validate the ServicePoints in the database
        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicePointsRepository.findAll().size();
        // set the field null
        servicePoints.setName(null);

        // Create the ServicePoints, which fails.

        restServicePointsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(servicePoints)))
            .andExpect(status().isBadRequest());

        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicePointsRepository.findAll().size();
        // set the field null
        servicePoints.setEmail(null);

        // Create the ServicePoints, which fails.

        restServicePointsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(servicePoints)))
            .andExpect(status().isBadRequest());

        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicePointsRepository.findAll().size();
        // set the field null
        servicePoints.setPhone(null);

        // Create the ServicePoints, which fails.

        restServicePointsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(servicePoints)))
            .andExpect(status().isBadRequest());

        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicePointsRepository.findAll().size();
        // set the field null
        servicePoints.setAddress(null);

        // Create the ServicePoints, which fails.

        restServicePointsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(servicePoints)))
            .andExpect(status().isBadRequest());

        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllServicePoints() throws Exception {
        // Initialize the database
        servicePointsRepository.saveAndFlush(servicePoints);

        // Get all the servicePointsList
        restServicePointsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(servicePoints.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].mapUrl").value(hasItem(DEFAULT_MAP_URL)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)));
    }

    @Test
    @Transactional
    void getServicePoints() throws Exception {
        // Initialize the database
        servicePointsRepository.saveAndFlush(servicePoints);

        // Get the servicePoints
        restServicePointsMockMvc
            .perform(get(ENTITY_API_URL_ID, servicePoints.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(servicePoints.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.mapUrl").value(DEFAULT_MAP_URL))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE));
    }

    @Test
    @Transactional
    void getNonExistingServicePoints() throws Exception {
        // Get the servicePoints
        restServicePointsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewServicePoints() throws Exception {
        // Initialize the database
        servicePointsRepository.saveAndFlush(servicePoints);

        int databaseSizeBeforeUpdate = servicePointsRepository.findAll().size();

        // Update the servicePoints
        ServicePoints updatedServicePoints = servicePointsRepository.findById(servicePoints.getId()).get();
        // Disconnect from session so that the updates on updatedServicePoints are not directly saved in db
        em.detach(updatedServicePoints);
        updatedServicePoints
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .mapUrl(UPDATED_MAP_URL)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);

        restServicePointsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedServicePoints.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedServicePoints))
            )
            .andExpect(status().isOk());

        // Validate the ServicePoints in the database
        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeUpdate);
        ServicePoints testServicePoints = servicePointsList.get(servicePointsList.size() - 1);
        assertThat(testServicePoints.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testServicePoints.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testServicePoints.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testServicePoints.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testServicePoints.getMapUrl()).isEqualTo(UPDATED_MAP_URL);
        assertThat(testServicePoints.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testServicePoints.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void putNonExistingServicePoints() throws Exception {
        int databaseSizeBeforeUpdate = servicePointsRepository.findAll().size();
        servicePoints.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServicePointsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, servicePoints.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(servicePoints))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServicePoints in the database
        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchServicePoints() throws Exception {
        int databaseSizeBeforeUpdate = servicePointsRepository.findAll().size();
        servicePoints.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicePointsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(servicePoints))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServicePoints in the database
        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamServicePoints() throws Exception {
        int databaseSizeBeforeUpdate = servicePointsRepository.findAll().size();
        servicePoints.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicePointsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(servicePoints)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ServicePoints in the database
        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateServicePointsWithPatch() throws Exception {
        // Initialize the database
        servicePointsRepository.saveAndFlush(servicePoints);

        int databaseSizeBeforeUpdate = servicePointsRepository.findAll().size();

        // Update the servicePoints using partial update
        ServicePoints partialUpdatedServicePoints = new ServicePoints();
        partialUpdatedServicePoints.setId(servicePoints.getId());

        partialUpdatedServicePoints.phone(UPDATED_PHONE).address(UPDATED_ADDRESS).mapUrl(UPDATED_MAP_URL);

        restServicePointsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServicePoints.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedServicePoints))
            )
            .andExpect(status().isOk());

        // Validate the ServicePoints in the database
        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeUpdate);
        ServicePoints testServicePoints = servicePointsList.get(servicePointsList.size() - 1);
        assertThat(testServicePoints.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testServicePoints.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testServicePoints.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testServicePoints.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testServicePoints.getMapUrl()).isEqualTo(UPDATED_MAP_URL);
        assertThat(testServicePoints.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testServicePoints.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void fullUpdateServicePointsWithPatch() throws Exception {
        // Initialize the database
        servicePointsRepository.saveAndFlush(servicePoints);

        int databaseSizeBeforeUpdate = servicePointsRepository.findAll().size();

        // Update the servicePoints using partial update
        ServicePoints partialUpdatedServicePoints = new ServicePoints();
        partialUpdatedServicePoints.setId(servicePoints.getId());

        partialUpdatedServicePoints
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .mapUrl(UPDATED_MAP_URL)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);

        restServicePointsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServicePoints.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedServicePoints))
            )
            .andExpect(status().isOk());

        // Validate the ServicePoints in the database
        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeUpdate);
        ServicePoints testServicePoints = servicePointsList.get(servicePointsList.size() - 1);
        assertThat(testServicePoints.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testServicePoints.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testServicePoints.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testServicePoints.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testServicePoints.getMapUrl()).isEqualTo(UPDATED_MAP_URL);
        assertThat(testServicePoints.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testServicePoints.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void patchNonExistingServicePoints() throws Exception {
        int databaseSizeBeforeUpdate = servicePointsRepository.findAll().size();
        servicePoints.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServicePointsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, servicePoints.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(servicePoints))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServicePoints in the database
        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchServicePoints() throws Exception {
        int databaseSizeBeforeUpdate = servicePointsRepository.findAll().size();
        servicePoints.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicePointsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(servicePoints))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServicePoints in the database
        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamServicePoints() throws Exception {
        int databaseSizeBeforeUpdate = servicePointsRepository.findAll().size();
        servicePoints.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicePointsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(servicePoints))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ServicePoints in the database
        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteServicePoints() throws Exception {
        // Initialize the database
        servicePointsRepository.saveAndFlush(servicePoints);

        int databaseSizeBeforeDelete = servicePointsRepository.findAll().size();

        // Delete the servicePoints
        restServicePointsMockMvc
            .perform(delete(ENTITY_API_URL_ID, servicePoints.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ServicePoints> servicePointsList = servicePointsRepository.findAll();
        assertThat(servicePointsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
