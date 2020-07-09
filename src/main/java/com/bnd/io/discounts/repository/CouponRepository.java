package com.bnd.io.discounts.repository;

import com.bnd.io.discounts.domain.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Spring Data repository for the Coupon entity. */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

  Optional<Coupon> findByCouponCodeAndActiveIsTrue(String code);

  Optional<Coupon> findByCouponCode(String code);

  @EntityGraph(attributePaths = {"discountType"})
  List<Coupon> findAllByDiscountType_DiscountTypeCodeEqualsIgnoreCaseAndActiveIsTrue(
      String discountTypeCode);

  @Override
  @EntityGraph(attributePaths = {"discountType"})
  Page<Coupon> findAll(Pageable pageable);

  @Override
  @EntityGraph(attributePaths = {"discountType"})
  Optional<Coupon> findById(Long aLong);
}
