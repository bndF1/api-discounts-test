package com.bnd.io.discounts.repository;

import com.bnd.io.discounts.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/** Spring Data repository for the Coupon entity. */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

  Optional<Coupon> findByCouponCodeAndActiveIsTrue(String code);

  Optional<Coupon> findByCouponCode(String code);

  Set<Coupon> findByDiscountType_DiscountTypeCodeEqualsIgnoreCaseAndActiveIsTrue(
      String discountTypeCode);
}
