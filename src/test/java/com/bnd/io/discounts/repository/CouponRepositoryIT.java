package com.bnd.io.discounts.repository;

import com.bnd.io.discounts.domain.Coupon;
import com.bnd.io.discounts.domain.DiscountType;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CouponRepositoryIT {

  private final String NON_EXISTING_COUPON = "NON_EXISTING";
  @Autowired private CouponRepository couponRepository;
  @Autowired private DiscountTypeRepository discountTypeRepository;
  private List<Coupon> coupons;

  @BeforeEach
  void setUp() {
    final EasyRandom easyRandom =
        new EasyRandom(new EasyRandomParameters().excludeField(FieldPredicates.named("id")));

    final DiscountType discountType = easyRandom.nextObject(DiscountType.class);
    this.discountTypeRepository.saveAndFlush(discountType);

    final Coupon couponActiveByDefault = easyRandom.nextObject(Coupon.class);
    couponActiveByDefault.setDiscountType(discountType);
    couponActiveByDefault.setActive(Boolean.TRUE);

    final Coupon couponDeactivatedByDefault = easyRandom.nextObject(Coupon.class);
    couponDeactivatedByDefault.setDiscountType(discountType);
    couponDeactivatedByDefault.setActive(Boolean.FALSE);

    final List<Coupon> couponList =
        easyRandom
            .objects(Coupon.class, 4)
            .peek(
                coupon -> {
                  this.discountTypeRepository.saveAndFlush(coupon.getDiscountType());
                })
            .collect(Collectors.toList());

    couponList.addAll(Arrays.asList(couponActiveByDefault, couponDeactivatedByDefault));
    this.coupons = couponRepository.saveAll(couponList);
  }

  @AfterEach
  void tearDown() {
    this.couponRepository.deleteAll();
  }

  @Test
  @Transactional
  void findAllCouponsTest() {
    final List<Coupon> couponsList = this.couponRepository.findAll();
    assertThat(couponsList.size()).isEqualTo(6);
    assertThat(couponsList.containsAll(this.coupons)).isTrue();
  }

  @Test
  @Transactional
  void findByCouponCodeAndActiveIsTrueTest() {
    final Optional<Coupon> activeOptionalCoupon =
        this.coupons.stream().filter(Coupon::getActive).findAny();

    final Optional<Coupon> optionalCoupon =
        this.couponRepository.findByCouponCodeAndActiveIsTrue(
            activeOptionalCoupon.get().getCouponCode());
    assertThat(optionalCoupon.isPresent()).isTrue();
    assertThat(optionalCoupon.get().getCouponCode())
        .isEqualTo(activeOptionalCoupon.get().getCouponCode());
  }

  @Test
  @Transactional
  void findByCouponCodeAndActiveIsTrueNonExistingTest() {
    final Optional<Coupon> optionalCoupon =
        this.couponRepository.findByCouponCodeAndActiveIsTrue(NON_EXISTING_COUPON);
    assertThat(optionalCoupon.isPresent()).isFalse();
  }

  @Test
  @Transactional
  void findByCouponCodeNonExistingTest() {
    final Optional<Coupon> optionalCoupon =
        this.couponRepository.findByCouponCode(NON_EXISTING_COUPON);
    assertThat(optionalCoupon.isPresent()).isFalse();
  }

  @Test
  @Transactional
  void findByCouponCodeExistingTest() {
    final Optional<Coupon> activeOptionalCoupon =
        this.coupons.stream().filter(Coupon::getActive).findAny();

    final Optional<Coupon> optionalCoupon =
        this.couponRepository.findByCouponCode(activeOptionalCoupon.get().getCouponCode());
    assertThat(optionalCoupon.isPresent()).isTrue();
    assertThat(optionalCoupon.get().getCouponCode())
        .isEqualTo(activeOptionalCoupon.get().getCouponCode());
    assertThat(optionalCoupon.get().getActive()).isTrue();
  }

  @Test
  @Transactional
  void findByCouponCodeExistingNonActiveTest() {
    final Optional<Coupon> nonActiveOptionalCoupon =
        this.coupons.stream().filter(coupon -> !coupon.getActive()).findAny();

    final Optional<Coupon> optionalCoupon =
        this.couponRepository.findByCouponCode(nonActiveOptionalCoupon.get().getCouponCode());

    assertThat(optionalCoupon.isPresent()).isTrue();
    assertThat(optionalCoupon.get().getCouponCode())
        .isEqualTo(nonActiveOptionalCoupon.get().getCouponCode());
    assertThat(optionalCoupon.get().getActive()).isFalse();
  }
}
