package com.bnd.io.discounts.service;

import com.bnd.io.discounts.domain.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/** Service Interface for managing {@link Coupon}. */
public interface CouponService {

  /**
   * Save a coupon.
   *
   * @param coupon the entity to save.
   * @return the persisted entity.
   */
  Coupon save(Coupon coupon);

  /**
   * Get all the coupons.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<Coupon> findAll(Pageable pageable);

  /**
   * Get the "id" coupon.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Optional<Coupon> findOne(Long id);

  /**
   * Delete the "id" coupon.
   *
   * @param id the id of the entity.
   */
  void delete(Long id);

  Optional<Coupon> findByCouponCodeAndActiveIsTrue(String couponCode);

  Optional<Coupon> findByCouponCode(String couponCode);
}
