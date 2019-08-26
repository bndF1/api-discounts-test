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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CouponRepositoryIT {

  private final String NON_EXISTING_COUPON = "NON_EXISTING";
  @Autowired private CouponRepository couponRepository;
  @Autowired private DiscountTypeRepository discountTypeRepository;
  private List<Coupon> coupons;
  private List<DiscountType> discountTypes;

  @BeforeEach
  void setUp() {
    final EasyRandom easyRandom = getEasyRandom();

    final List<DiscountType> discountTypeList =
        easyRandom.objects(DiscountType.class, 5).collect(Collectors.toList());
    this.discountTypes = this.discountTypeRepository.saveAll(discountTypeList);

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
                  coupon.setDiscountType(discountType);
                })
            .collect(Collectors.toList());

    couponList.addAll(Arrays.asList(couponActiveByDefault, couponDeactivatedByDefault));
    this.coupons = couponRepository.saveAll(couponList);
  }

  private EasyRandom getEasyRandom() {
    return new EasyRandom(new EasyRandomParameters().excludeField(FieldPredicates.named("id")));
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

  @Test
  @Transactional
  void findAllByDiscountType_DiscountTypeCodeEqualsIgnoreCaseAndActiveIsTrueTest() {
    final EasyRandom easyRandom = getEasyRandom();

    final AtomicInteger size = new AtomicInteger(this.discountTypes.size() + 1);

    final List<Coupon> couponList =
        easyRandom
            .objects(Coupon.class, this.discountTypes.size())
            .peek(
                coupon -> {
                  final DiscountType discountType =
                      this.discountTypes.get(easyRandom.nextInt(size.decrementAndGet()));
                  coupon.setDiscountType(discountType);
                  coupon.setActive(true);
                })
            .collect(Collectors.toList());
    this.couponRepository.saveAll(couponList);

    final DiscountType randomDiscountType =
        this.discountTypes.get(easyRandom.nextInt(this.discountTypes.size()));

    final List<Coupon> filteredList =
        couponList.stream()
            .filter(
                coupon ->
                    coupon
                        .getDiscountType()
                        .getDiscountTypeCode()
                        .equals(randomDiscountType.getDiscountTypeCode()))
            .collect(Collectors.toList());

    final List<Coupon> activeCouponsByDiscountTypeCode =
        this.couponRepository.findAllByDiscountType_DiscountTypeCodeEqualsIgnoreCaseAndActiveIsTrue(
            randomDiscountType.getDiscountTypeCode());

    assertThat(activeCouponsByDiscountTypeCode).isNotEmpty();
    assertThat(activeCouponsByDiscountTypeCode.size()).isEqualTo(filteredList.size());
    assertThat(activeCouponsByDiscountTypeCode).containsExactlyElementsOf(filteredList);
    assertThat(activeCouponsByDiscountTypeCode).containsAll(filteredList);
  }

  @Test
  @Transactional
  void
      findAllByDiscountType_DiscountTypeCodeEqualsIgnoreCaseAndActiveIsTrueButThereAreNoActiveCouponsTest() {
    final EasyRandom easyRandom = getEasyRandom();

    final AtomicInteger size = new AtomicInteger(this.discountTypes.size() + 1);

    final List<Coupon> couponList =
        easyRandom
            .objects(Coupon.class, this.discountTypes.size())
            .peek(
                coupon -> {
                  final DiscountType discountType =
                      this.discountTypes.get(easyRandom.nextInt(size.decrementAndGet()));
                  coupon.setDiscountType(discountType);
                  coupon.setActive(false);
                })
            .collect(Collectors.toList());
    this.couponRepository.saveAll(couponList);

    final DiscountType randomDiscountType =
        this.discountTypes.get(easyRandom.nextInt(this.discountTypes.size()));

    final List<Coupon> activeCouponsByDiscountTypeCode =
        this.couponRepository.findAllByDiscountType_DiscountTypeCodeEqualsIgnoreCaseAndActiveIsTrue(
            randomDiscountType.getDiscountTypeCode());

    assertThat(activeCouponsByDiscountTypeCode).isEmpty();
  }
}
