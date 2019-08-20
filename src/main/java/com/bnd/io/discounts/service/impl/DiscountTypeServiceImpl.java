package com.bnd.io.discounts.service.impl;

import com.bnd.io.discounts.service.DiscountTypeService;
import com.bnd.io.discounts.domain.DiscountType;
import com.bnd.io.discounts.repository.DiscountTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link DiscountType}.
 */
@Service
@Transactional
public class DiscountTypeServiceImpl implements DiscountTypeService {

    private final Logger log = LoggerFactory.getLogger(DiscountTypeServiceImpl.class);

    private final DiscountTypeRepository discountTypeRepository;

    public DiscountTypeServiceImpl(DiscountTypeRepository discountTypeRepository) {
        this.discountTypeRepository = discountTypeRepository;
    }

    /**
     * Save a discountType.
     *
     * @param discountType the entity to save.
     * @return the persisted entity.
     */
    @Override
    public DiscountType save(DiscountType discountType) {
        log.debug("Request to save DiscountType : {}", discountType);
        return discountTypeRepository.save(discountType);
    }

    /**
     * Get all the discountTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DiscountType> findAll(Pageable pageable) {
        log.debug("Request to get all DiscountTypes");
        return discountTypeRepository.findAll(pageable);
    }


    /**
     * Get one discountType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<DiscountType> findOne(Long id) {
        log.debug("Request to get DiscountType : {}", id);
        return discountTypeRepository.findById(id);
    }

    /**
     * Delete the discountType by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete DiscountType : {}", id);
        discountTypeRepository.deleteById(id);
    }
}
