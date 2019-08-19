package com.bnd.io.discounts.service;

import com.bnd.io.discounts.domain.DiscountType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link DiscountType}.
 */
public interface DiscountTypeService {

    /**
     * Save a discountType.
     *
     * @param discountType the entity to save.
     * @return the persisted entity.
     */
    DiscountType save(DiscountType discountType);

    /**
     * Get all the discountTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DiscountType> findAll(Pageable pageable);


    /**
     * Get the "id" discountType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DiscountType> findOne(Long id);

    /**
     * Delete the "id" discountType.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
