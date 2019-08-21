package com.bnd.io.discounts.service.impl;

import com.bnd.io.discounts.domain.Coupon;
import com.bnd.io.discounts.repository.CouponRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
class CouponServiceImplTest {
  @Mock Logger log;
  @Mock CouponRepository couponRepository;
  @InjectMocks CouponServiceImpl couponServiceImpl;

  @Test
  void testFindAll() {
    final Page<Coupon> result = couponServiceImpl.findAll(null);
    Assertions.assertNull(result);
  }

  @Test
  void testFindOne() {
    final Optional<Coupon> result = couponServiceImpl.findOne(Long.valueOf(1));
    Assertions.assertEquals(Optional.empty(), result);
  }

  @Test
  void testDelete() {
    couponServiceImpl.delete(Long.valueOf(1));
  }
}
