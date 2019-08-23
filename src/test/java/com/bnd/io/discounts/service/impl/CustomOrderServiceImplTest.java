package com.bnd.io.discounts.service.impl;

import com.bnd.io.discounts.domain.Coupon;
import com.bnd.io.discounts.domain.CustomOrder;
import com.bnd.io.discounts.domain.Product;
import com.bnd.io.discounts.repository.CustomOrderRepository;
import com.bnd.io.discounts.service.CouponService;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CustomOrderServiceImplTest {
  @Mock Logger log;

  @Mock CustomOrderRepository customOrderRepository;

  @Mock CouponService couponService;

  @Spy @InjectMocks CustomOrderServiceImpl customOrderServiceImpl;

  @Test
  void testFindAll() {
    when(customOrderRepository.findAll(any(Pageable.class))).thenReturn(null);

    final Page<CustomOrder> result = customOrderServiceImpl.findAll(null);
    Assertions.assertEquals(null, result);
  }

  @Test
  void testFindOne() {
    when(customOrderRepository.findById(anyLong())).thenReturn(null);

    final Optional<CustomOrder> result = customOrderServiceImpl.findOne(Long.valueOf(1));
    Assertions.assertEquals(null, result);
  }

  @Test
  void testDelete() {
    customOrderServiceImpl.delete(Long.valueOf(1));
  }

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
            .couponCode(coupon.getCouponCode())
            .build();

    when(this.couponService.findByCouponCode(any())).thenReturn(Optional.of(coupon));

    final CustomOrder updatedOrder =
        this.customOrderServiceImpl.calculateOrderDiscount(customOrder);
    assertThat(updatedOrder.getPrice()).isEqualTo(shirt.getPrice() + suit.getPrice());
  }
}
