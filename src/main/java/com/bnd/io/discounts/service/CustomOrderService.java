package com.bnd.io.discounts.service;

import com.bnd.io.discounts.domain.CustomOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/** Service Interface for managing {@link CustomOrder}. */
public interface CustomOrderService {

  /**
   * Save a customOrder.
   *
   * @param customOrder the entity to save.
   * @return the persisted entity.
   */
  CustomOrder save(CustomOrder customOrder);

  /**
   * Get all the customOrders.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<CustomOrder> findAll(Pageable pageable);

  /**
   * Get the "id" customOrder.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Optional<CustomOrder> findOne(Long id);

  /**
   * Delete the "id" customOrder.
   *
   * @param id the id of the entity.
   */
  void delete(Long id);
}
