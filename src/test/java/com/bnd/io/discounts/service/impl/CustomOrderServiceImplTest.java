package com.bnd.io.discounts.service.impl;

import com.bnd.io.discounts.domain.CustomOrder;
import com.bnd.io.discounts.domain.Product;
import com.bnd.io.discounts.exceptions.ApiExceptions;
import com.bnd.io.discounts.exceptions.DeactivatedCouponException;
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
  void calculateOrderDiscountWithGivenCouponAndItIsDeactivated() {
    final EasyRandomParameters parameters =
        new EasyRandomParameters().excludeField(FieldPredicates.named("customOrder"));
    final EasyRandom easyRandom = new EasyRandom(parameters);

    final Product shirt = easyRandom.nextObject(Product.class);
    final Product suit = easyRandom.nextObject(Product.class);

    final CustomOrder customOrder =
        CustomOrder.builder()
            .products(new HashSet<>(Arrays.asList(shirt, suit)))
            .couponCode("NON_ACTIVE")
            .build();

    when(this.couponService.findByCouponCodeAndActiveIsTrue(any())).thenReturn(Optional.empty());

    final Exception exception =
        assertThrows(
            DeactivatedCouponException.class,
            () -> this.customOrderServiceImpl.calculateOrderDiscount(customOrder));
    assertThat(exception.getMessage()).isEqualTo(String.valueOf(ApiExceptions.DEACTIVATED_COUPON));
  }
}
