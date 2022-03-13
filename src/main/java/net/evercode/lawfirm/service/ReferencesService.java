package net.evercode.lawfirm.service;

import net.evercode.lawfirm.domain.References;
import net.evercode.lawfirm.repository.ReferencesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link References}.
 */
@Service
@Transactional
public class ReferencesService {

    private final Logger log = LoggerFactory.getLogger(ReferencesService.class);

    private final ReferencesRepository referencesRepository;

    public ReferencesService(ReferencesRepository referencesRepository) {
        this.referencesRepository = referencesRepository;
    }

    /**
     * Save a references.
     *
     * @param references the entity to save.
     * @return the persisted entity.
     */
    public References save(References references) {
        log.debug("Request to save References : {}", references);
        return referencesRepository.save(references);
    }

    /**
     * Partially update a references.
     *
     * @param references the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<References> partialUpdate(References references) {
        log.debug("Request to partially update References : {}", references);

        return referencesRepository
            .findById(references.getId())
            .map(existingReferences -> {
                if (references.getName() != null) {
                    existingReferences.setName(references.getName());
                }
                if (references.getOrder() != null) {
                    existingReferences.setOrder(references.getOrder());
                }
                if (references.getImage() != null) {
                    existingReferences.setImage(references.getImage());
                }
                if (references.getImageContentType() != null) {
                    existingReferences.setImageContentType(references.getImageContentType());
                }

                return existingReferences;
            })
            .map(referencesRepository::save);
    }

    /**
     * Get all the references.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<References> findAll(Pageable pageable) {
        log.debug("Request to get all References");
        return referencesRepository.findAll(pageable);
    }

    /**
     * Get one references by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<References> findOne(Long id) {
        log.debug("Request to get References : {}", id);
        return referencesRepository.findById(id);
    }

    /**
     * Delete the references by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete References : {}", id);
        referencesRepository.deleteById(id);
    }
}
