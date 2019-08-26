package com.bnd.io.discounts.service.impl;

import com.bnd.io.discounts.domain.Coupon;
import com.bnd.io.discounts.repository.CouponRepository;
import com.bnd.io.discounts.service.CouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/** Service Implementation for managing {@link Coupon}. */
@Service
@Transactional
public class CouponServiceImpl implements CouponService {

  private final Logger log = LoggerFactory.getLogger(CouponServiceImpl.class);

  private final CouponRepository couponRepository;

  public CouponServiceImpl(final CouponRepository couponRepository) {
    this.couponRepository = couponRepository;
  }

  /**
   * Save a coupon.
   *
   * @param coupon the entity to save.
   * @return the persisted entity.
   */
  @Override
  public Coupon save(final Coupon coupon) {
    log.debug("Request to save Coupon : {}", coupon);
    return couponRepository.save(coupon);
  }

  /**
   * Get all the coupons.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  @Override
  @Transactional(readOnly = true)
  public Page<Coupon> findAll(final Pageable pageable) {
    log.debug("Request to get all Coupons");
    return couponRepository.findAll(pageable);
  }

  /**
   * Get one coupon by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Override
  @Transactional(readOnly = true)
  public Optional<Coupon> findOne(final Long id) {
    log.debug("Request to get Coupon : {}", id);
    return couponRepository.findById(id);
  }

  /**
   * Delete the coupon by id.
   *
   * @param id the id of the entity.
   */
  @Override
  public void delete(final Long id) {
    log.debug("Request to delete Coupon : {}", id);
    couponRepository.deleteById(id);
  }

  @Override
  public Optional<Coupon> findByCouponCodeAndActiveIsTrue(final String couponCode) {
    return this.couponRepository.findByCouponCodeAndActiveIsTrue(couponCode);
  }

  @Override
  public Optional<Coupon> findByCouponCode(final String couponCode) {
    return this.couponRepository.findByCouponCode(couponCode);
  }

  @Override
  public Set<Coupon> findByDiscountType_discountTypeCodeEqualsIgnoreCaseAndActiveIsTrue(
      final String discountTypeCode) {
    return this.couponRepository.findByDiscountType_DiscountTypeCodeEqualsIgnoreCaseAndActiveIsTrue(
        discountTypeCode);
  }
}
