package net.evercode.lawfirm.web.rest;

import net.evercode.lawfirm.IntegrationTest;
import net.evercode.lawfirm.domain.JobApplications;
import net.evercode.lawfirm.repository.JobApplicationsRepository;
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
 * Integration tests for the {@link JobApplicationsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JobApplicationsResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SURNAME = "AAAAAAAAAA";
    private static final String UPDATED_SURNAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_AREA = "AAAAAAAAAA";
    private static final String UPDATED_AREA = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_CV = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_CV = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_CV_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_CV_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/job-applications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private JobApplicationsRepository jobApplicationsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJobApplicationsMockMvc;

    private JobApplications jobApplications;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JobApplications createEntity(EntityManager em) {
        JobApplications jobApplications = new JobApplications()
            .name(DEFAULT_NAME)
            .surname(DEFAULT_SURNAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .area(DEFAULT_AREA)
            .message(DEFAULT_MESSAGE)
            .cv(DEFAULT_CV)
            .cvContentType(DEFAULT_CV_CONTENT_TYPE);
        return jobApplications;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JobApplications createUpdatedEntity(EntityManager em) {
        JobApplications jobApplications = new JobApplications()
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .area(UPDATED_AREA)
            .message(UPDATED_MESSAGE)
            .cv(UPDATED_CV)
            .cvContentType(UPDATED_CV_CONTENT_TYPE);
        return jobApplications;
    }

    @BeforeEach
    public void initTest() {
        jobApplications = createEntity(em);
    }

    @Test
    @Transactional
    void createJobApplications() throws Exception {
        int databaseSizeBeforeCreate = jobApplicationsRepository.findAll().size();
        // Create the JobApplications
        restJobApplicationsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobApplications))
            )
            .andExpect(status().isCreated());

        // Validate the JobApplications in the database
        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeCreate + 1);
        JobApplications testJobApplications = jobApplicationsList.get(jobApplicationsList.size() - 1);
        assertThat(testJobApplications.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testJobApplications.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testJobApplications.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testJobApplications.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testJobApplications.getArea()).isEqualTo(DEFAULT_AREA);
        assertThat(testJobApplications.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testJobApplications.getCv()).isEqualTo(DEFAULT_CV);
        assertThat(testJobApplications.getCvContentType()).isEqualTo(DEFAULT_CV_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createJobApplicationsWithExistingId() throws Exception {
        // Create the JobApplications with an existing ID
        jobApplications.setId(1L);

        int databaseSizeBeforeCreate = jobApplicationsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJobApplicationsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobApplications))
            )
            .andExpect(status().isBadRequest());

        // Validate the JobApplications in the database
        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobApplicationsRepository.findAll().size();
        // set the field null
        jobApplications.setName(null);

        // Create the JobApplications, which fails.

        restJobApplicationsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobApplications))
            )
            .andExpect(status().isBadRequest());

        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSurnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobApplicationsRepository.findAll().size();
        // set the field null
        jobApplications.setSurname(null);

        // Create the JobApplications, which fails.

        restJobApplicationsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobApplications))
            )
            .andExpect(status().isBadRequest());

        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobApplicationsRepository.findAll().size();
        // set the field null
        jobApplications.setEmail(null);

        // Create the JobApplications, which fails.

        restJobApplicationsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobApplications))
            )
            .andExpect(status().isBadRequest());

        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobApplicationsRepository.findAll().size();
        // set the field null
        jobApplications.setPhone(null);

        // Create the JobApplications, which fails.

        restJobApplicationsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobApplications))
            )
            .andExpect(status().isBadRequest());

        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAreaIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobApplicationsRepository.findAll().size();
        // set the field null
        jobApplications.setArea(null);

        // Create the JobApplications, which fails.

        restJobApplicationsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobApplications))
            )
            .andExpect(status().isBadRequest());

        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllJobApplications() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList
        restJobApplicationsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobApplications.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].area").value(hasItem(DEFAULT_AREA)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].cvContentType").value(hasItem(DEFAULT_CV_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cv").value(hasItem(Base64Utils.encodeToString(DEFAULT_CV))));
    }

    @Test
    @Transactional
    void getJobApplications() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get the jobApplications
        restJobApplicationsMockMvc
            .perform(get(ENTITY_API_URL_ID, jobApplications.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(jobApplications.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.surname").value(DEFAULT_SURNAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.area").value(DEFAULT_AREA))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.cvContentType").value(DEFAULT_CV_CONTENT_TYPE))
            .andExpect(jsonPath("$.cv").value(Base64Utils.encodeToString(DEFAULT_CV)));
    }

    @Test
    @Transactional
    void getJobApplicationsByIdFiltering() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        Long id = jobApplications.getId();

        defaultJobApplicationsShouldBeFound("id.equals=" + id);
        defaultJobApplicationsShouldNotBeFound("id.notEquals=" + id);

        defaultJobApplicationsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultJobApplicationsShouldNotBeFound("id.greaterThan=" + id);

        defaultJobApplicationsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultJobApplicationsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where name equals to DEFAULT_NAME
        defaultJobApplicationsShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the jobApplicationsList where name equals to UPDATED_NAME
        defaultJobApplicationsShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where name not equals to DEFAULT_NAME
        defaultJobApplicationsShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the jobApplicationsList where name not equals to UPDATED_NAME
        defaultJobApplicationsShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where name in DEFAULT_NAME or UPDATED_NAME
        defaultJobApplicationsShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the jobApplicationsList where name equals to UPDATED_NAME
        defaultJobApplicationsShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where name is not null
        defaultJobApplicationsShouldBeFound("name.specified=true");

        // Get all the jobApplicationsList where name is null
        defaultJobApplicationsShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllJobApplicationsByNameContainsSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where name contains DEFAULT_NAME
        defaultJobApplicationsShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the jobApplicationsList where name contains UPDATED_NAME
        defaultJobApplicationsShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where name does not contain DEFAULT_NAME
        defaultJobApplicationsShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the jobApplicationsList where name does not contain UPDATED_NAME
        defaultJobApplicationsShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllJobApplicationsBySurnameIsEqualToSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where surname equals to DEFAULT_SURNAME
        defaultJobApplicationsShouldBeFound("surname.equals=" + DEFAULT_SURNAME);

        // Get all the jobApplicationsList where surname equals to UPDATED_SURNAME
        defaultJobApplicationsShouldNotBeFound("surname.equals=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllJobApplicationsBySurnameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where surname not equals to DEFAULT_SURNAME
        defaultJobApplicationsShouldNotBeFound("surname.notEquals=" + DEFAULT_SURNAME);

        // Get all the jobApplicationsList where surname not equals to UPDATED_SURNAME
        defaultJobApplicationsShouldBeFound("surname.notEquals=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllJobApplicationsBySurnameIsInShouldWork() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where surname in DEFAULT_SURNAME or UPDATED_SURNAME
        defaultJobApplicationsShouldBeFound("surname.in=" + DEFAULT_SURNAME + "," + UPDATED_SURNAME);

        // Get all the jobApplicationsList where surname equals to UPDATED_SURNAME
        defaultJobApplicationsShouldNotBeFound("surname.in=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllJobApplicationsBySurnameIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where surname is not null
        defaultJobApplicationsShouldBeFound("surname.specified=true");

        // Get all the jobApplicationsList where surname is null
        defaultJobApplicationsShouldNotBeFound("surname.specified=false");
    }

    @Test
    @Transactional
    void getAllJobApplicationsBySurnameContainsSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where surname contains DEFAULT_SURNAME
        defaultJobApplicationsShouldBeFound("surname.contains=" + DEFAULT_SURNAME);

        // Get all the jobApplicationsList where surname contains UPDATED_SURNAME
        defaultJobApplicationsShouldNotBeFound("surname.contains=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllJobApplicationsBySurnameNotContainsSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where surname does not contain DEFAULT_SURNAME
        defaultJobApplicationsShouldNotBeFound("surname.doesNotContain=" + DEFAULT_SURNAME);

        // Get all the jobApplicationsList where surname does not contain UPDATED_SURNAME
        defaultJobApplicationsShouldBeFound("surname.doesNotContain=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where email equals to DEFAULT_EMAIL
        defaultJobApplicationsShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the jobApplicationsList where email equals to UPDATED_EMAIL
        defaultJobApplicationsShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByEmailIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where email not equals to DEFAULT_EMAIL
        defaultJobApplicationsShouldNotBeFound("email.notEquals=" + DEFAULT_EMAIL);

        // Get all the jobApplicationsList where email not equals to UPDATED_EMAIL
        defaultJobApplicationsShouldBeFound("email.notEquals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultJobApplicationsShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the jobApplicationsList where email equals to UPDATED_EMAIL
        defaultJobApplicationsShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where email is not null
        defaultJobApplicationsShouldBeFound("email.specified=true");

        // Get all the jobApplicationsList where email is null
        defaultJobApplicationsShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllJobApplicationsByEmailContainsSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where email contains DEFAULT_EMAIL
        defaultJobApplicationsShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the jobApplicationsList where email contains UPDATED_EMAIL
        defaultJobApplicationsShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where email does not contain DEFAULT_EMAIL
        defaultJobApplicationsShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the jobApplicationsList where email does not contain UPDATED_EMAIL
        defaultJobApplicationsShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where phone equals to DEFAULT_PHONE
        defaultJobApplicationsShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the jobApplicationsList where phone equals to UPDATED_PHONE
        defaultJobApplicationsShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByPhoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where phone not equals to DEFAULT_PHONE
        defaultJobApplicationsShouldNotBeFound("phone.notEquals=" + DEFAULT_PHONE);

        // Get all the jobApplicationsList where phone not equals to UPDATED_PHONE
        defaultJobApplicationsShouldBeFound("phone.notEquals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultJobApplicationsShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the jobApplicationsList where phone equals to UPDATED_PHONE
        defaultJobApplicationsShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where phone is not null
        defaultJobApplicationsShouldBeFound("phone.specified=true");

        // Get all the jobApplicationsList where phone is null
        defaultJobApplicationsShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    void getAllJobApplicationsByPhoneContainsSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where phone contains DEFAULT_PHONE
        defaultJobApplicationsShouldBeFound("phone.contains=" + DEFAULT_PHONE);

        // Get all the jobApplicationsList where phone contains UPDATED_PHONE
        defaultJobApplicationsShouldNotBeFound("phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where phone does not contain DEFAULT_PHONE
        defaultJobApplicationsShouldNotBeFound("phone.doesNotContain=" + DEFAULT_PHONE);

        // Get all the jobApplicationsList where phone does not contain UPDATED_PHONE
        defaultJobApplicationsShouldBeFound("phone.doesNotContain=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByAreaIsEqualToSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where area equals to DEFAULT_AREA
        defaultJobApplicationsShouldBeFound("area.equals=" + DEFAULT_AREA);

        // Get all the jobApplicationsList where area equals to UPDATED_AREA
        defaultJobApplicationsShouldNotBeFound("area.equals=" + UPDATED_AREA);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByAreaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where area not equals to DEFAULT_AREA
        defaultJobApplicationsShouldNotBeFound("area.notEquals=" + DEFAULT_AREA);

        // Get all the jobApplicationsList where area not equals to UPDATED_AREA
        defaultJobApplicationsShouldBeFound("area.notEquals=" + UPDATED_AREA);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByAreaIsInShouldWork() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where area in DEFAULT_AREA or UPDATED_AREA
        defaultJobApplicationsShouldBeFound("area.in=" + DEFAULT_AREA + "," + UPDATED_AREA);

        // Get all the jobApplicationsList where area equals to UPDATED_AREA
        defaultJobApplicationsShouldNotBeFound("area.in=" + UPDATED_AREA);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByAreaIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where area is not null
        defaultJobApplicationsShouldBeFound("area.specified=true");

        // Get all the jobApplicationsList where area is null
        defaultJobApplicationsShouldNotBeFound("area.specified=false");
    }

    @Test
    @Transactional
    void getAllJobApplicationsByAreaContainsSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where area contains DEFAULT_AREA
        defaultJobApplicationsShouldBeFound("area.contains=" + DEFAULT_AREA);

        // Get all the jobApplicationsList where area contains UPDATED_AREA
        defaultJobApplicationsShouldNotBeFound("area.contains=" + UPDATED_AREA);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByAreaNotContainsSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where area does not contain DEFAULT_AREA
        defaultJobApplicationsShouldNotBeFound("area.doesNotContain=" + DEFAULT_AREA);

        // Get all the jobApplicationsList where area does not contain UPDATED_AREA
        defaultJobApplicationsShouldBeFound("area.doesNotContain=" + UPDATED_AREA);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where message equals to DEFAULT_MESSAGE
        defaultJobApplicationsShouldBeFound("message.equals=" + DEFAULT_MESSAGE);

        // Get all the jobApplicationsList where message equals to UPDATED_MESSAGE
        defaultJobApplicationsShouldNotBeFound("message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByMessageIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where message not equals to DEFAULT_MESSAGE
        defaultJobApplicationsShouldNotBeFound("message.notEquals=" + DEFAULT_MESSAGE);

        // Get all the jobApplicationsList where message not equals to UPDATED_MESSAGE
        defaultJobApplicationsShouldBeFound("message.notEquals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where message in DEFAULT_MESSAGE or UPDATED_MESSAGE
        defaultJobApplicationsShouldBeFound("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE);

        // Get all the jobApplicationsList where message equals to UPDATED_MESSAGE
        defaultJobApplicationsShouldNotBeFound("message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where message is not null
        defaultJobApplicationsShouldBeFound("message.specified=true");

        // Get all the jobApplicationsList where message is null
        defaultJobApplicationsShouldNotBeFound("message.specified=false");
    }

    @Test
    @Transactional
    void getAllJobApplicationsByMessageContainsSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where message contains DEFAULT_MESSAGE
        defaultJobApplicationsShouldBeFound("message.contains=" + DEFAULT_MESSAGE);

        // Get all the jobApplicationsList where message contains UPDATED_MESSAGE
        defaultJobApplicationsShouldNotBeFound("message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllJobApplicationsByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        // Get all the jobApplicationsList where message does not contain DEFAULT_MESSAGE
        defaultJobApplicationsShouldNotBeFound("message.doesNotContain=" + DEFAULT_MESSAGE);

        // Get all the jobApplicationsList where message does not contain UPDATED_MESSAGE
        defaultJobApplicationsShouldBeFound("message.doesNotContain=" + UPDATED_MESSAGE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultJobApplicationsShouldBeFound(String filter) throws Exception {
        restJobApplicationsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobApplications.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].area").value(hasItem(DEFAULT_AREA)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].cvContentType").value(hasItem(DEFAULT_CV_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cv").value(hasItem(Base64Utils.encodeToString(DEFAULT_CV))));

        // Check, that the count call also returns 1
        restJobApplicationsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultJobApplicationsShouldNotBeFound(String filter) throws Exception {
        restJobApplicationsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restJobApplicationsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingJobApplications() throws Exception {
        // Get the jobApplications
        restJobApplicationsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewJobApplications() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        int databaseSizeBeforeUpdate = jobApplicationsRepository.findAll().size();

        // Update the jobApplications
        JobApplications updatedJobApplications = jobApplicationsRepository.findById(jobApplications.getId()).get();
        // Disconnect from session so that the updates on updatedJobApplications are not directly saved in db
        em.detach(updatedJobApplications);
        updatedJobApplications
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .area(UPDATED_AREA)
            .message(UPDATED_MESSAGE)
            .cv(UPDATED_CV)
            .cvContentType(UPDATED_CV_CONTENT_TYPE);

        restJobApplicationsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedJobApplications.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedJobApplications))
            )
            .andExpect(status().isOk());

        // Validate the JobApplications in the database
        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeUpdate);
        JobApplications testJobApplications = jobApplicationsList.get(jobApplicationsList.size() - 1);
        assertThat(testJobApplications.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testJobApplications.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testJobApplications.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testJobApplications.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testJobApplications.getArea()).isEqualTo(UPDATED_AREA);
        assertThat(testJobApplications.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testJobApplications.getCv()).isEqualTo(UPDATED_CV);
        assertThat(testJobApplications.getCvContentType()).isEqualTo(UPDATED_CV_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingJobApplications() throws Exception {
        int databaseSizeBeforeUpdate = jobApplicationsRepository.findAll().size();
        jobApplications.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJobApplicationsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, jobApplications.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jobApplications))
            )
            .andExpect(status().isBadRequest());

        // Validate the JobApplications in the database
        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchJobApplications() throws Exception {
        int databaseSizeBeforeUpdate = jobApplicationsRepository.findAll().size();
        jobApplications.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobApplicationsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jobApplications))
            )
            .andExpect(status().isBadRequest());

        // Validate the JobApplications in the database
        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJobApplications() throws Exception {
        int databaseSizeBeforeUpdate = jobApplicationsRepository.findAll().size();
        jobApplications.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobApplicationsMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobApplications))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the JobApplications in the database
        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateJobApplicationsWithPatch() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        int databaseSizeBeforeUpdate = jobApplicationsRepository.findAll().size();

        // Update the jobApplications using partial update
        JobApplications partialUpdatedJobApplications = new JobApplications();
        partialUpdatedJobApplications.setId(jobApplications.getId());

        partialUpdatedJobApplications
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .email(UPDATED_EMAIL)
            .message(UPDATED_MESSAGE)
            .cv(UPDATED_CV)
            .cvContentType(UPDATED_CV_CONTENT_TYPE);

        restJobApplicationsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJobApplications.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJobApplications))
            )
            .andExpect(status().isOk());

        // Validate the JobApplications in the database
        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeUpdate);
        JobApplications testJobApplications = jobApplicationsList.get(jobApplicationsList.size() - 1);
        assertThat(testJobApplications.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testJobApplications.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testJobApplications.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testJobApplications.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testJobApplications.getArea()).isEqualTo(DEFAULT_AREA);
        assertThat(testJobApplications.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testJobApplications.getCv()).isEqualTo(UPDATED_CV);
        assertThat(testJobApplications.getCvContentType()).isEqualTo(UPDATED_CV_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateJobApplicationsWithPatch() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        int databaseSizeBeforeUpdate = jobApplicationsRepository.findAll().size();

        // Update the jobApplications using partial update
        JobApplications partialUpdatedJobApplications = new JobApplications();
        partialUpdatedJobApplications.setId(jobApplications.getId());

        partialUpdatedJobApplications
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .area(UPDATED_AREA)
            .message(UPDATED_MESSAGE)
            .cv(UPDATED_CV)
            .cvContentType(UPDATED_CV_CONTENT_TYPE);

        restJobApplicationsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJobApplications.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJobApplications))
            )
            .andExpect(status().isOk());

        // Validate the JobApplications in the database
        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeUpdate);
        JobApplications testJobApplications = jobApplicationsList.get(jobApplicationsList.size() - 1);
        assertThat(testJobApplications.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testJobApplications.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testJobApplications.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testJobApplications.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testJobApplications.getArea()).isEqualTo(UPDATED_AREA);
        assertThat(testJobApplications.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testJobApplications.getCv()).isEqualTo(UPDATED_CV);
        assertThat(testJobApplications.getCvContentType()).isEqualTo(UPDATED_CV_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingJobApplications() throws Exception {
        int databaseSizeBeforeUpdate = jobApplicationsRepository.findAll().size();
        jobApplications.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJobApplicationsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, jobApplications.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jobApplications))
            )
            .andExpect(status().isBadRequest());

        // Validate the JobApplications in the database
        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJobApplications() throws Exception {
        int databaseSizeBeforeUpdate = jobApplicationsRepository.findAll().size();
        jobApplications.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobApplicationsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jobApplications))
            )
            .andExpect(status().isBadRequest());

        // Validate the JobApplications in the database
        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJobApplications() throws Exception {
        int databaseSizeBeforeUpdate = jobApplicationsRepository.findAll().size();
        jobApplications.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobApplicationsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jobApplications))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the JobApplications in the database
        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteJobApplications() throws Exception {
        // Initialize the database
        jobApplicationsRepository.saveAndFlush(jobApplications);

        int databaseSizeBeforeDelete = jobApplicationsRepository.findAll().size();

        // Delete the jobApplications
        restJobApplicationsMockMvc
            .perform(delete(ENTITY_API_URL_ID, jobApplications.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<JobApplications> jobApplicationsList = jobApplicationsRepository.findAll();
        assertThat(jobApplicationsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
