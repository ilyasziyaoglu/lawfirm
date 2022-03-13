package net.evercode.lawfirm.web.rest;

import net.evercode.lawfirm.IntegrationTest;
import net.evercode.lawfirm.domain.Employees;
import net.evercode.lawfirm.domain.ServicePoints;
import net.evercode.lawfirm.domain.Services;
import net.evercode.lawfirm.repository.EmployeesRepository;
import net.evercode.lawfirm.service.EmployeesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link EmployeesResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EmployeesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SURNAME = "AAAAAAAAAA";
    private static final String UPDATED_SURNAME = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_STORY = "AAAAAAAAAA";
    private static final String UPDATED_STORY = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER = 1;
    private static final Integer UPDATED_ORDER = 2;
    private static final Integer SMALLER_ORDER = 1 - 1;

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/employees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EmployeesRepository employeesRepository;

    @Mock
    private EmployeesRepository employeesRepositoryMock;

    @Mock
    private EmployeesService employeesServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEmployeesMockMvc;

    private Employees employees;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employees createEntity(EntityManager em) {
        Employees employees = new Employees()
            .name(DEFAULT_NAME)
            .surname(DEFAULT_SURNAME)
            .title(DEFAULT_TITLE)
            .story(DEFAULT_STORY)
            .order(DEFAULT_ORDER)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
        return employees;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employees createUpdatedEntity(EntityManager em) {
        Employees employees = new Employees()
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .title(UPDATED_TITLE)
            .story(UPDATED_STORY)
            .order(UPDATED_ORDER)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        return employees;
    }

    @BeforeEach
    public void initTest() {
        employees = createEntity(em);
    }

    @Test
    @Transactional
    void createEmployees() throws Exception {
        int databaseSizeBeforeCreate = employeesRepository.findAll().size();
        // Create the Employees
        restEmployeesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employees)))
            .andExpect(status().isCreated());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeCreate + 1);
        Employees testEmployees = employeesList.get(employeesList.size() - 1);
        assertThat(testEmployees.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEmployees.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testEmployees.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testEmployees.getStory()).isEqualTo(DEFAULT_STORY);
        assertThat(testEmployees.getOrder()).isEqualTo(DEFAULT_ORDER);
        assertThat(testEmployees.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testEmployees.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createEmployeesWithExistingId() throws Exception {
        // Create the Employees with an existing ID
        employees.setId(1L);

        int databaseSizeBeforeCreate = employeesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmployeesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employees)))
            .andExpect(status().isBadRequest());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeesRepository.findAll().size();
        // set the field null
        employees.setName(null);

        // Create the Employees, which fails.

        restEmployeesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employees)))
            .andExpect(status().isBadRequest());

        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSurnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeesRepository.findAll().size();
        // set the field null
        employees.setSurname(null);

        // Create the Employees, which fails.

        restEmployeesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employees)))
            .andExpect(status().isBadRequest());

        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeesRepository.findAll().size();
        // set the field null
        employees.setTitle(null);

        // Create the Employees, which fails.

        restEmployeesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employees)))
            .andExpect(status().isBadRequest());

        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrderIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeesRepository.findAll().size();
        // set the field null
        employees.setOrder(null);

        // Create the Employees, which fails.

        restEmployeesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employees)))
            .andExpect(status().isBadRequest());

        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEmployees() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList
        restEmployeesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employees.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].story").value(hasItem(DEFAULT_STORY)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEmployeesWithEagerRelationshipsIsEnabled() throws Exception {
        when(employeesServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEmployeesMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(employeesServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEmployeesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(employeesServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEmployeesMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(employeesServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getEmployees() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get the employees
        restEmployeesMockMvc
            .perform(get(ENTITY_API_URL_ID, employees.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(employees.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.surname").value(DEFAULT_SURNAME))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.story").value(DEFAULT_STORY))
            .andExpect(jsonPath("$.order").value(DEFAULT_ORDER))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    void getEmployeesByIdFiltering() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        Long id = employees.getId();

        defaultEmployeesShouldBeFound("id.equals=" + id);
        defaultEmployeesShouldNotBeFound("id.notEquals=" + id);

        defaultEmployeesShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEmployeesShouldNotBeFound("id.greaterThan=" + id);

        defaultEmployeesShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEmployeesShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEmployeesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where name equals to DEFAULT_NAME
        defaultEmployeesShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the employeesList where name equals to UPDATED_NAME
        defaultEmployeesShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where name not equals to DEFAULT_NAME
        defaultEmployeesShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the employeesList where name not equals to UPDATED_NAME
        defaultEmployeesShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where name in DEFAULT_NAME or UPDATED_NAME
        defaultEmployeesShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the employeesList where name equals to UPDATED_NAME
        defaultEmployeesShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where name is not null
        defaultEmployeesShouldBeFound("name.specified=true");

        // Get all the employeesList where name is null
        defaultEmployeesShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByNameContainsSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where name contains DEFAULT_NAME
        defaultEmployeesShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the employeesList where name contains UPDATED_NAME
        defaultEmployeesShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where name does not contain DEFAULT_NAME
        defaultEmployeesShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the employeesList where name does not contain UPDATED_NAME
        defaultEmployeesShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesBySurnameIsEqualToSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where surname equals to DEFAULT_SURNAME
        defaultEmployeesShouldBeFound("surname.equals=" + DEFAULT_SURNAME);

        // Get all the employeesList where surname equals to UPDATED_SURNAME
        defaultEmployeesShouldNotBeFound("surname.equals=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllEmployeesBySurnameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where surname not equals to DEFAULT_SURNAME
        defaultEmployeesShouldNotBeFound("surname.notEquals=" + DEFAULT_SURNAME);

        // Get all the employeesList where surname not equals to UPDATED_SURNAME
        defaultEmployeesShouldBeFound("surname.notEquals=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllEmployeesBySurnameIsInShouldWork() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where surname in DEFAULT_SURNAME or UPDATED_SURNAME
        defaultEmployeesShouldBeFound("surname.in=" + DEFAULT_SURNAME + "," + UPDATED_SURNAME);

        // Get all the employeesList where surname equals to UPDATED_SURNAME
        defaultEmployeesShouldNotBeFound("surname.in=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllEmployeesBySurnameIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where surname is not null
        defaultEmployeesShouldBeFound("surname.specified=true");

        // Get all the employeesList where surname is null
        defaultEmployeesShouldNotBeFound("surname.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesBySurnameContainsSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where surname contains DEFAULT_SURNAME
        defaultEmployeesShouldBeFound("surname.contains=" + DEFAULT_SURNAME);

        // Get all the employeesList where surname contains UPDATED_SURNAME
        defaultEmployeesShouldNotBeFound("surname.contains=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllEmployeesBySurnameNotContainsSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where surname does not contain DEFAULT_SURNAME
        defaultEmployeesShouldNotBeFound("surname.doesNotContain=" + DEFAULT_SURNAME);

        // Get all the employeesList where surname does not contain UPDATED_SURNAME
        defaultEmployeesShouldBeFound("surname.doesNotContain=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where title equals to DEFAULT_TITLE
        defaultEmployeesShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the employeesList where title equals to UPDATED_TITLE
        defaultEmployeesShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEmployeesByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where title not equals to DEFAULT_TITLE
        defaultEmployeesShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the employeesList where title not equals to UPDATED_TITLE
        defaultEmployeesShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEmployeesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultEmployeesShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the employeesList where title equals to UPDATED_TITLE
        defaultEmployeesShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEmployeesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where title is not null
        defaultEmployeesShouldBeFound("title.specified=true");

        // Get all the employeesList where title is null
        defaultEmployeesShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByTitleContainsSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where title contains DEFAULT_TITLE
        defaultEmployeesShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the employeesList where title contains UPDATED_TITLE
        defaultEmployeesShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEmployeesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where title does not contain DEFAULT_TITLE
        defaultEmployeesShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the employeesList where title does not contain UPDATED_TITLE
        defaultEmployeesShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllEmployeesByStoryIsEqualToSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where story equals to DEFAULT_STORY
        defaultEmployeesShouldBeFound("story.equals=" + DEFAULT_STORY);

        // Get all the employeesList where story equals to UPDATED_STORY
        defaultEmployeesShouldNotBeFound("story.equals=" + UPDATED_STORY);
    }

    @Test
    @Transactional
    void getAllEmployeesByStoryIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where story not equals to DEFAULT_STORY
        defaultEmployeesShouldNotBeFound("story.notEquals=" + DEFAULT_STORY);

        // Get all the employeesList where story not equals to UPDATED_STORY
        defaultEmployeesShouldBeFound("story.notEquals=" + UPDATED_STORY);
    }

    @Test
    @Transactional
    void getAllEmployeesByStoryIsInShouldWork() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where story in DEFAULT_STORY or UPDATED_STORY
        defaultEmployeesShouldBeFound("story.in=" + DEFAULT_STORY + "," + UPDATED_STORY);

        // Get all the employeesList where story equals to UPDATED_STORY
        defaultEmployeesShouldNotBeFound("story.in=" + UPDATED_STORY);
    }

    @Test
    @Transactional
    void getAllEmployeesByStoryIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where story is not null
        defaultEmployeesShouldBeFound("story.specified=true");

        // Get all the employeesList where story is null
        defaultEmployeesShouldNotBeFound("story.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByStoryContainsSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where story contains DEFAULT_STORY
        defaultEmployeesShouldBeFound("story.contains=" + DEFAULT_STORY);

        // Get all the employeesList where story contains UPDATED_STORY
        defaultEmployeesShouldNotBeFound("story.contains=" + UPDATED_STORY);
    }

    @Test
    @Transactional
    void getAllEmployeesByStoryNotContainsSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where story does not contain DEFAULT_STORY
        defaultEmployeesShouldNotBeFound("story.doesNotContain=" + DEFAULT_STORY);

        // Get all the employeesList where story does not contain UPDATED_STORY
        defaultEmployeesShouldBeFound("story.doesNotContain=" + UPDATED_STORY);
    }

    @Test
    @Transactional
    void getAllEmployeesByOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where order equals to DEFAULT_ORDER
        defaultEmployeesShouldBeFound("order.equals=" + DEFAULT_ORDER);

        // Get all the employeesList where order equals to UPDATED_ORDER
        defaultEmployeesShouldNotBeFound("order.equals=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllEmployeesByOrderIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where order not equals to DEFAULT_ORDER
        defaultEmployeesShouldNotBeFound("order.notEquals=" + DEFAULT_ORDER);

        // Get all the employeesList where order not equals to UPDATED_ORDER
        defaultEmployeesShouldBeFound("order.notEquals=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllEmployeesByOrderIsInShouldWork() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where order in DEFAULT_ORDER or UPDATED_ORDER
        defaultEmployeesShouldBeFound("order.in=" + DEFAULT_ORDER + "," + UPDATED_ORDER);

        // Get all the employeesList where order equals to UPDATED_ORDER
        defaultEmployeesShouldNotBeFound("order.in=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllEmployeesByOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where order is not null
        defaultEmployeesShouldBeFound("order.specified=true");

        // Get all the employeesList where order is null
        defaultEmployeesShouldNotBeFound("order.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where order is greater than or equal to DEFAULT_ORDER
        defaultEmployeesShouldBeFound("order.greaterThanOrEqual=" + DEFAULT_ORDER);

        // Get all the employeesList where order is greater than or equal to UPDATED_ORDER
        defaultEmployeesShouldNotBeFound("order.greaterThanOrEqual=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllEmployeesByOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where order is less than or equal to DEFAULT_ORDER
        defaultEmployeesShouldBeFound("order.lessThanOrEqual=" + DEFAULT_ORDER);

        // Get all the employeesList where order is less than or equal to SMALLER_ORDER
        defaultEmployeesShouldNotBeFound("order.lessThanOrEqual=" + SMALLER_ORDER);
    }

    @Test
    @Transactional
    void getAllEmployeesByOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where order is less than DEFAULT_ORDER
        defaultEmployeesShouldNotBeFound("order.lessThan=" + DEFAULT_ORDER);

        // Get all the employeesList where order is less than UPDATED_ORDER
        defaultEmployeesShouldBeFound("order.lessThan=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllEmployeesByOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        // Get all the employeesList where order is greater than DEFAULT_ORDER
        defaultEmployeesShouldNotBeFound("order.greaterThan=" + DEFAULT_ORDER);

        // Get all the employeesList where order is greater than SMALLER_ORDER
        defaultEmployeesShouldBeFound("order.greaterThan=" + SMALLER_ORDER);
    }

    @Test
    @Transactional
    void getAllEmployeesByServicePointIsEqualToSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);
        ServicePoints servicePoint;
        if (TestUtil.findAll(em, ServicePoints.class).isEmpty()) {
            servicePoint = ServicePointsResourceIT.createEntity(em);
            em.persist(servicePoint);
            em.flush();
        } else {
            servicePoint = TestUtil.findAll(em, ServicePoints.class).get(0);
        }
        em.persist(servicePoint);
        em.flush();
        employees.setServicePoint(servicePoint);
        employeesRepository.saveAndFlush(employees);
        Long servicePointId = servicePoint.getId();

        // Get all the employeesList where servicePoint equals to servicePointId
        defaultEmployeesShouldBeFound("servicePointId.equals=" + servicePointId);

        // Get all the employeesList where servicePoint equals to (servicePointId + 1)
        defaultEmployeesShouldNotBeFound("servicePointId.equals=" + (servicePointId + 1));
    }

    @Test
    @Transactional
    void getAllEmployeesByServicesIsEqualToSomething() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);
        Services services;
        if (TestUtil.findAll(em, Services.class).isEmpty()) {
            services = ServicesResourceIT.createEntity(em);
            em.persist(services);
            em.flush();
        } else {
            services = TestUtil.findAll(em, Services.class).get(0);
        }
        em.persist(services);
        em.flush();
        employees.addServices(services);
        employeesRepository.saveAndFlush(employees);
        Long servicesId = services.getId();

        // Get all the employeesList where services equals to servicesId
        defaultEmployeesShouldBeFound("servicesId.equals=" + servicesId);

        // Get all the employeesList where services equals to (servicesId + 1)
        defaultEmployeesShouldNotBeFound("servicesId.equals=" + (servicesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEmployeesShouldBeFound(String filter) throws Exception {
        restEmployeesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employees.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].story").value(hasItem(DEFAULT_STORY)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));

        // Check, that the count call also returns 1
        restEmployeesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEmployeesShouldNotBeFound(String filter) throws Exception {
        restEmployeesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEmployeesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEmployees() throws Exception {
        // Get the employees
        restEmployeesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEmployees() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();

        // Update the employees
        Employees updatedEmployees = employeesRepository.findById(employees.getId()).get();
        // Disconnect from session so that the updates on updatedEmployees are not directly saved in db
        em.detach(updatedEmployees);
        updatedEmployees
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .title(UPDATED_TITLE)
            .story(UPDATED_STORY)
            .order(UPDATED_ORDER)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restEmployeesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEmployees.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEmployees))
            )
            .andExpect(status().isOk());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
        Employees testEmployees = employeesList.get(employeesList.size() - 1);
        assertThat(testEmployees.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEmployees.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testEmployees.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testEmployees.getStory()).isEqualTo(UPDATED_STORY);
        assertThat(testEmployees.getOrder()).isEqualTo(UPDATED_ORDER);
        assertThat(testEmployees.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testEmployees.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingEmployees() throws Exception {
        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();
        employees.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, employees.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employees))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEmployees() throws Exception {
        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();
        employees.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employees))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEmployees() throws Exception {
        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();
        employees.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employees)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEmployeesWithPatch() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();

        // Update the employees using partial update
        Employees partialUpdatedEmployees = new Employees();
        partialUpdatedEmployees.setId(employees.getId());

        partialUpdatedEmployees
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .story(UPDATED_STORY)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restEmployeesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmployees.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmployees))
            )
            .andExpect(status().isOk());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
        Employees testEmployees = employeesList.get(employeesList.size() - 1);
        assertThat(testEmployees.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEmployees.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testEmployees.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testEmployees.getStory()).isEqualTo(UPDATED_STORY);
        assertThat(testEmployees.getOrder()).isEqualTo(DEFAULT_ORDER);
        assertThat(testEmployees.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testEmployees.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateEmployeesWithPatch() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();

        // Update the employees using partial update
        Employees partialUpdatedEmployees = new Employees();
        partialUpdatedEmployees.setId(employees.getId());

        partialUpdatedEmployees
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .title(UPDATED_TITLE)
            .story(UPDATED_STORY)
            .order(UPDATED_ORDER)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restEmployeesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmployees.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmployees))
            )
            .andExpect(status().isOk());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
        Employees testEmployees = employeesList.get(employeesList.size() - 1);
        assertThat(testEmployees.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEmployees.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testEmployees.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testEmployees.getStory()).isEqualTo(UPDATED_STORY);
        assertThat(testEmployees.getOrder()).isEqualTo(UPDATED_ORDER);
        assertThat(testEmployees.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testEmployees.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingEmployees() throws Exception {
        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();
        employees.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, employees.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(employees))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEmployees() throws Exception {
        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();
        employees.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(employees))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEmployees() throws Exception {
        int databaseSizeBeforeUpdate = employeesRepository.findAll().size();
        employees.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeesMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(employees))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Employees in the database
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEmployees() throws Exception {
        // Initialize the database
        employeesRepository.saveAndFlush(employees);

        int databaseSizeBeforeDelete = employeesRepository.findAll().size();

        // Delete the employees
        restEmployeesMockMvc
            .perform(delete(ENTITY_API_URL_ID, employees.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Employees> employeesList = employeesRepository.findAll();
        assertThat(employeesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
