package com.bnd.io.discounts.service.impl;

import com.bnd.io.discounts.domain.CustomOrder;
import com.bnd.io.discounts.domain.Product;
import com.bnd.io.discounts.repository.CustomOrderRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
  @InjectMocks CustomOrderServiceImpl customOrderServiceImpl;

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
    final Product shirt =
        Product.builder().code("CAMISA").price(new EasyRandom().nextDouble()).build();
    final Product suit =
        Product.builder().code("TRAJE").price(new EasyRandom().nextDouble()).build();

    final CustomOrder customOrder =
        CustomOrder.builder().products(new HashSet<>(Arrays.asList(shirt, suit))).build();

    final CustomOrder updatedOrder =
        this.customOrderServiceImpl.calculateOrderDiscount(customOrder);
    assertThat(updatedOrder.getPrice()).isEqualTo(shirt.getPrice() + suit.getPrice());
  }
}
