package com.bnd.io.discounts.service.impl;

import com.bnd.io.discounts.domain.Coupon;
import com.bnd.io.discounts.domain.CustomOrder;
import com.bnd.io.discounts.domain.DiscountType;
import com.bnd.io.discounts.domain.Product;
import com.bnd.io.discounts.domain.enums.DiscountOperation;
import com.bnd.io.discounts.exceptions.ApiExceptions;
import com.bnd.io.discounts.exceptions.CouponException;
import com.bnd.io.discounts.repository.CustomOrderRepository;
import com.bnd.io.discounts.service.CouponService;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CustomOrderServiceImplTest {
  @Mock Logger log;

  @Mock CustomOrderRepository customOrderRepository;

  @Mock CouponService couponService;

  @Spy @InjectMocks CustomOrderServiceImpl customOrderServiceImpl;

  @Test
  void calculateOrderDiscountWithoutCoupon() {

    final EasyRandomParameters parameters =
        new EasyRandomParameters().excludeField(FieldPredicates.named("customOrder"));
    final EasyRandom easyRandom = new EasyRandom(parameters);

    final Product shirt = easyRandom.nextObject(Product.class);
    final Product suit = easyRandom.nextObject(Product.class);

    final CustomOrder customOrder =
        CustomOrder.builder().products(new HashSet<>(Arrays.asList(shirt, suit))).build();

    final CustomOrder updatedOrder =
        this.customOrderServiceImpl.calculateOrderDiscount(customOrder);
    assertThat(updatedOrder.getPrice()).isEqualTo(shirt.getPrice() + suit.getPrice());
  }

  @Test
  void calculateOrderDiscountWithGivenCouponAndItIsNotFound() {
    final EasyRandomParameters parameters =
        new EasyRandomParameters().excludeField(FieldPredicates.named("customOrder"));
    final EasyRandom easyRandom = new EasyRandom(parameters);

    final Product shirt = easyRandom.nextObject(Product.class);
    final Product suit = easyRandom.nextObject(Product.class);

    final CustomOrder customOrder =
        CustomOrder.builder()
            .products(new HashSet<>(Arrays.asList(shirt, suit)))
            .couponCode("NOT_EXISTENT")
            .build();

    when(this.couponService.findByCouponCode(any())).thenReturn(Optional.empty());

    final Exception exception =
        assertThrows(
            CouponException.class,
            () -> this.customOrderServiceImpl.calculateOrderDiscount(customOrder));
    assertThat(exception.getMessage()).isEqualTo(String.valueOf(ApiExceptions.COUPON_NOT_FOUND));
  }

  @Test
  void calculateOrderDiscountWithGivenCouponAndItIsDeactivated() {
    final EasyRandomParameters parameters =
        new EasyRandomParameters().excludeField(FieldPredicates.named("customOrder"));
    final EasyRandom easyRandom = new EasyRandom(parameters);

    final Product shirt = easyRandom.nextObject(Product.class);
    final Product suit = easyRandom.nextObject(Product.class);

    final Coupon coupon = easyRandom.nextObject(Coupon.class);
    coupon.setActive(Boolean.FALSE);

    final CustomOrder customOrder =
        CustomOrder.builder()
            .products(new HashSet<>(Arrays.asList(shirt, suit)))
            .couponCode("NON_ACTIVE")
            .build();

    when(this.couponService.findByCouponCode(any())).thenReturn(Optional.of(coupon));

    final Exception exception =
        assertThrows(
            CouponException.class,
            () -> this.customOrderServiceImpl.calculateOrderDiscount(customOrder));
    assertThat(exception.getMessage()).isEqualTo(String.valueOf(ApiExceptions.DEACTIVATED_COUPON));
  }

  @Test
  void calculateOrderDiscountWithGivenCouponAndItIsFreeCoupon() {
    final EasyRandomParameters parameters =
        new EasyRandomParameters().excludeField(FieldPredicates.named("customOrder"));
    final EasyRandom easyRandom = new EasyRandom(parameters);

    final Product shirt = easyRandom.nextObject(Product.class);
    final Product suit = easyRandom.nextObject(Product.class);

    final Coupon coupon = easyRandom.nextObject(Coupon.class);
    coupon.setActive(Boolean.TRUE);

    final DiscountType discountType = easyRandom.nextObject(DiscountType.class);
    discountType.setDiscount(100d);
    discountType.setDiscountOperation(DiscountOperation.PERCENT);

    coupon.setDiscountType(discountType);

    final CustomOrder customOrder =
        CustomOrder.builder()
            .products(new HashSet<>(Arrays.asList(shirt, suit)))
            .couponCode("CUPON-A")
            .build();

    when(this.couponService.findByCouponCode(any())).thenReturn(Optional.of(coupon));

    final CustomOrder customOrderWithDiscount =
        this.customOrderServiceImpl.calculateOrderDiscount(customOrder);
    assertThat(customOrderWithDiscount.getPrice()).isEqualTo(0d);
  }

  @Test
  void calculateOrderDiscountWithGivenCouponAndItIsFixedRateCoupon() {
    final EasyRandomParameters parameters =
        new EasyRandomParameters().excludeField(FieldPredicates.named("customOrder"));
    final EasyRandom easyRandom = new EasyRandom(parameters);

    final Product shirt = easyRandom.nextObject(Product.class);
    shirt.setPrice(3d);
    final Product suit = easyRandom.nextObject(Product.class);
    suit.setPrice(12d);

    final Coupon coupon = easyRandom.nextObject(Coupon.class);
    coupon.setActive(Boolean.TRUE);

    final DiscountType discountType = easyRandom.nextObject(DiscountType.class);
    discountType.setDiscount(10d);
    discountType.setDiscountOperation(DiscountOperation.DIRECT);

    coupon.setDiscountType(discountType);

    final CustomOrder customOrder =
        CustomOrder.builder()
            .products(new HashSet<>(Arrays.asList(shirt, suit)))
            .couponCode("CUPON-D")
            .build();

    when(this.couponService.findByCouponCode(any())).thenReturn(Optional.of(coupon));

    final CustomOrder customOrderWithDiscount =
        this.customOrderServiceImpl.calculateOrderDiscount(customOrder);
    assertThat(customOrderWithDiscount.getPrice()).isEqualTo(5d);
  }

  @Test
  void calculateOrderDiscountWithGivenCouponAndItIsPercentRateCoupon() {
    final EasyRandomParameters parameters =
        new EasyRandomParameters().excludeField(FieldPredicates.named("customOrder"));
    final EasyRandom easyRandom = new EasyRandom(parameters);

    final Product bag = easyRandom.nextObject(Product.class);
    bag.setPrice(100d);

    final Coupon coupon = easyRandom.nextObject(Coupon.class);
    coupon.setActive(Boolean.TRUE);

    final DiscountType discountType = easyRandom.nextObject(DiscountType.class);
    discountType.setDiscount(5d);
    discountType.setDiscountOperation(DiscountOperation.PERCENT);

    coupon.setDiscountType(discountType);

    final CustomOrder customOrder =
        CustomOrder.builder()
            .products(new HashSet<>(Collections.singletonList(bag)))
            .couponCode("CUPON-E")
            .build();

    when(this.couponService.findByCouponCode(any())).thenReturn(Optional.of(coupon));

    final CustomOrder customOrderWithDiscount =
        this.customOrderServiceImpl.calculateOrderDiscount(customOrder);
    assertThat(customOrderWithDiscount.getPrice()).isEqualTo(95d);
  }
}
