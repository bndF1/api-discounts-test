package com.bnd.io.discounts.service.impl;

import com.bnd.io.discounts.service.CustomOrderService;
import com.bnd.io.discounts.domain.CustomOrder;
import com.bnd.io.discounts.repository.CustomOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link CustomOrder}.
 */
@Service
@Transactional
public class CustomOrderServiceImpl implements CustomOrderService {

    private final Logger log = LoggerFactory.getLogger(CustomOrderServiceImpl.class);

    private final CustomOrderRepository customOrderRepository;

    public CustomOrderServiceImpl(CustomOrderRepository customOrderRepository) {
        this.customOrderRepository = customOrderRepository;
    }

    /**
     * Save a customOrder.
     *
     * @param customOrder the entity to save.
     * @return the persisted entity.
     */
    @Override
    public CustomOrder save(CustomOrder customOrder) {
        log.debug("Request to save CustomOrder : {}", customOrder);
        return customOrderRepository.save(customOrder);
    }

    /**
     * Get all the customOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CustomOrder> findAll(Pageable pageable) {
        log.debug("Request to get all CustomOrders");
        return customOrderRepository.findAll(pageable);
    }


    /**
     * Get one customOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CustomOrder> findOne(Long id) {
        log.debug("Request to get CustomOrder : {}", id);
        return customOrderRepository.findById(id);
    }

    /**
     * Delete the customOrder by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete CustomOrder : {}", id);
        customOrderRepository.deleteById(id);
    }
}
