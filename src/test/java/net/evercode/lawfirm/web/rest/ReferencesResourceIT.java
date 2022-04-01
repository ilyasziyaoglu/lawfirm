package net.evercode.lawfirm.web.rest;

import net.evercode.lawfirm.IntegrationTest;
import net.evercode.lawfirm.domain.References;
import net.evercode.lawfirm.repository.ReferencesRepository;
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
 * Integration tests for the {@link ReferencesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReferencesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER = 1;
    private static final Integer UPDATED_ORDER = 2;

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/references";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReferencesRepository referencesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReferencesMockMvc;

    private References references;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static References createEntity(EntityManager em) {
        References references = new References()
            .name(DEFAULT_NAME)
            .order(DEFAULT_ORDER)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
        return references;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static References createUpdatedEntity(EntityManager em) {
        References references = new References()
            .name(UPDATED_NAME)
            .order(UPDATED_ORDER)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        return references;
    }

    @BeforeEach
    public void initTest() {
        references = createEntity(em);
    }

    @Test
    @Transactional
    void createReferences() throws Exception {
        int databaseSizeBeforeCreate = referencesRepository.findAll().size();
        // Create the References
        restReferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(references)))
            .andExpect(status().isCreated());

        // Validate the References in the database
        List<References> referencesList = referencesRepository.findAll();
        assertThat(referencesList).hasSize(databaseSizeBeforeCreate + 1);
        References testReferences = referencesList.get(referencesList.size() - 1);
        assertThat(testReferences.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testReferences.getOrder()).isEqualTo(DEFAULT_ORDER);
        assertThat(testReferences.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testReferences.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createReferencesWithExistingId() throws Exception {
        // Create the References with an existing ID
        references.setId(1L);

        int databaseSizeBeforeCreate = referencesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(references)))
            .andExpect(status().isBadRequest());

        // Validate the References in the database
        List<References> referencesList = referencesRepository.findAll();
        assertThat(referencesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = referencesRepository.findAll().size();
        // set the field null
        references.setName(null);

        // Create the References, which fails.

        restReferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(references)))
            .andExpect(status().isBadRequest());

        List<References> referencesList = referencesRepository.findAll();
        assertThat(referencesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrderIsRequired() throws Exception {
        int databaseSizeBeforeTest = referencesRepository.findAll().size();
        // set the field null
        references.setOrder(null);

        // Create the References, which fails.

        restReferencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(references)))
            .andExpect(status().isBadRequest());

        List<References> referencesList = referencesRepository.findAll();
        assertThat(referencesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReferences() throws Exception {
        // Initialize the database
        referencesRepository.saveAndFlush(references);

        // Get all the referencesList
        restReferencesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(references.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    void getReferences() throws Exception {
        // Initialize the database
        referencesRepository.saveAndFlush(references);

        // Get the references
        restReferencesMockMvc
            .perform(get(ENTITY_API_URL_ID, references.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(references.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.order").value(DEFAULT_ORDER))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    void getNonExistingReferences() throws Exception {
        // Get the references
        restReferencesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewReferences() throws Exception {
        // Initialize the database
        referencesRepository.saveAndFlush(references);

        int databaseSizeBeforeUpdate = referencesRepository.findAll().size();

        // Update the references
        References updatedReferences = referencesRepository.findById(references.getId()).get();
        // Disconnect from session so that the updates on updatedReferences are not directly saved in db
        em.detach(updatedReferences);
        updatedReferences.name(UPDATED_NAME).order(UPDATED_ORDER).image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restReferencesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedReferences.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedReferences))
            )
            .andExpect(status().isOk());

        // Validate the References in the database
        List<References> referencesList = referencesRepository.findAll();
        assertThat(referencesList).hasSize(databaseSizeBeforeUpdate);
        References testReferences = referencesList.get(referencesList.size() - 1);
        assertThat(testReferences.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testReferences.getOrder()).isEqualTo(UPDATED_ORDER);
        assertThat(testReferences.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testReferences.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingReferences() throws Exception {
        int databaseSizeBeforeUpdate = referencesRepository.findAll().size();
        references.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReferencesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, references.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(references))
            )
            .andExpect(status().isBadRequest());

        // Validate the References in the database
        List<References> referencesList = referencesRepository.findAll();
        assertThat(referencesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReferences() throws Exception {
        int databaseSizeBeforeUpdate = referencesRepository.findAll().size();
        references.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferencesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(references))
            )
            .andExpect(status().isBadRequest());

        // Validate the References in the database
        List<References> referencesList = referencesRepository.findAll();
        assertThat(referencesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReferences() throws Exception {
        int databaseSizeBeforeUpdate = referencesRepository.findAll().size();
        references.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferencesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(references)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the References in the database
        List<References> referencesList = referencesRepository.findAll();
        assertThat(referencesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReferencesWithPatch() throws Exception {
        // Initialize the database
        referencesRepository.saveAndFlush(references);

        int databaseSizeBeforeUpdate = referencesRepository.findAll().size();

        // Update the references using partial update
        References partialUpdatedReferences = new References();
        partialUpdatedReferences.setId(references.getId());

        partialUpdatedReferences.order(UPDATED_ORDER);

        restReferencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReferences.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReferences))
            )
            .andExpect(status().isOk());

        // Validate the References in the database
        List<References> referencesList = referencesRepository.findAll();
        assertThat(referencesList).hasSize(databaseSizeBeforeUpdate);
        References testReferences = referencesList.get(referencesList.size() - 1);
        assertThat(testReferences.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testReferences.getOrder()).isEqualTo(UPDATED_ORDER);
        assertThat(testReferences.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testReferences.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateReferencesWithPatch() throws Exception {
        // Initialize the database
        referencesRepository.saveAndFlush(references);

        int databaseSizeBeforeUpdate = referencesRepository.findAll().size();

        // Update the references using partial update
        References partialUpdatedReferences = new References();
        partialUpdatedReferences.setId(references.getId());

        partialUpdatedReferences.name(UPDATED_NAME).order(UPDATED_ORDER).image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restReferencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReferences.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReferences))
            )
            .andExpect(status().isOk());

        // Validate the References in the database
        List<References> referencesList = referencesRepository.findAll();
        assertThat(referencesList).hasSize(databaseSizeBeforeUpdate);
        References testReferences = referencesList.get(referencesList.size() - 1);
        assertThat(testReferences.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testReferences.getOrder()).isEqualTo(UPDATED_ORDER);
        assertThat(testReferences.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testReferences.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingReferences() throws Exception {
        int databaseSizeBeforeUpdate = referencesRepository.findAll().size();
        references.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReferencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, references.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(references))
            )
            .andExpect(status().isBadRequest());

        // Validate the References in the database
        List<References> referencesList = referencesRepository.findAll();
        assertThat(referencesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReferences() throws Exception {
        int databaseSizeBeforeUpdate = referencesRepository.findAll().size();
        references.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(references))
            )
            .andExpect(status().isBadRequest());

        // Validate the References in the database
        List<References> referencesList = referencesRepository.findAll();
        assertThat(referencesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReferences() throws Exception {
        int databaseSizeBeforeUpdate = referencesRepository.findAll().size();
        references.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferencesMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(references))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the References in the database
        List<References> referencesList = referencesRepository.findAll();
        assertThat(referencesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReferences() throws Exception {
        // Initialize the database
        referencesRepository.saveAndFlush(references);

        int databaseSizeBeforeDelete = referencesRepository.findAll().size();

        // Delete the references
        restReferencesMockMvc
            .perform(delete(ENTITY_API_URL_ID, references.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<References> referencesList = referencesRepository.findAll();
        assertThat(referencesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
