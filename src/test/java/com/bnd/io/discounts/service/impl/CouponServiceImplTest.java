package com.bnd.io.discounts.service.impl;

import com.bnd.io.discounts.domain.Coupon;
import com.bnd.io.discounts.repository.CouponRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CouponServiceImplTest {

    public static final String COUPON_CODE = "CODE";
    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponServiceImpl couponServiceImpl;

    private EasyRandom easyRandom;

    @BeforeEach
    public void setup() {
        easyRandom = new EasyRandom();
        final Coupon coupon = easyRandom.nextObject(Coupon.class);
        when(this.couponRepository.findByCouponCodeAndActiveIsTrue(COUPON_CODE))
                .thenReturn(Optional.of(coupon));
        when(this.couponRepository.findAllByDiscountType_DiscountTypeCodeEqualsIgnoreCaseAndActiveIsTrue(COUPON_CODE))
                .thenReturn(List.of(coupon));
        when(this.couponRepository.findByCouponCode(COUPON_CODE))
                .thenReturn(Optional.of(coupon));
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(couponRepository);
    }

    @Test
    void testFindAll() {
        final Page<Coupon> result = couponServiceImpl.findAll(Pageable.unpaged());
        assertNull(result);
        verify(couponRepository).findAll(any(Pageable.class));
    }

    @Test
    void testFindAllShouldNotReturnNull() {
        final Coupon coupon = easyRandom.nextObject(Coupon.class);
        when(this.couponRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(coupon)));
        final Page<Coupon> result = couponServiceImpl.findAll(Pageable.unpaged());
        assertNotNull(result);
        verify(couponRepository).findAll(any(Pageable.class));
    }

    @Test
    void testFindOne() {
        final Optional<Coupon> result = couponServiceImpl.findOne(1L);
        assertEquals(Optional.empty(), result);
        verify(this.couponRepository).findById(anyLong());
    }

    @Test
    void testFindOneShouldNotReturnEmpty() {
        final Coupon coupon = easyRandom.nextObject(Coupon.class);
        when(this.couponRepository.findById(any())).thenReturn(Optional.of(coupon));
        final Optional<Coupon> result = couponServiceImpl.findOne(1L);
        assertTrue(result.isPresent());
        verify(this.couponRepository).findById(anyLong());
    }

    @Test
    void testDelete() {
        couponServiceImpl.delete(1L);
        final Optional<Coupon> optionalCoupon = this.couponServiceImpl.findOne(1L);
        assertTrue(optionalCoupon.isEmpty());
        verify(couponRepository).deleteById(anyLong());
        verify(couponRepository).findById(anyLong());
    }

    @Test
    void saveShouldWorkProperly() {
        final Coupon coupon = easyRandom.nextObject(Coupon.class);
        when(this.couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        final Coupon savedCoupon = this.couponServiceImpl.save(coupon);
        assertNotNull(savedCoupon);
        verify(this.couponRepository).save(any(Coupon.class));
    }

    @Test
    void saveShouldThrowException() {
        final DataIntegrityViolationException exception = new DataIntegrityViolationException("Entity exists in database");
        final Coupon coupon = easyRandom.nextObject(Coupon.class);
        when(this.couponRepository.save(any(Coupon.class))).thenThrow(exception);



        DataIntegrityViolationException assertThrows = assertThrows(
                DataIntegrityViolationException.class,
                () -> this.couponServiceImpl.save(coupon)
        );

        verify(this.couponRepository).save(any(Coupon.class));
        assertNotNull(assertThrows);
        assertNotNull(assertThrows.getMessage());
        assertTrue(assertThrows.getMessage().contains("Entity exists in database"));
    }

    @Test
    void findByCouponCodeAndActiveIsTrueShouldFindProperCoupon() {
        final Optional<Coupon> optionalCoupon = this.couponServiceImpl.findByCouponCodeAndActiveIsTrue(COUPON_CODE);
        assertTrue(optionalCoupon.isPresent());
        verify(this.couponRepository).findByCouponCodeAndActiveIsTrue(anyString());
    }

    @Test
    void findByCouponCodeAndActiveIsTrueShouldNotFindAnyCoupon() {
        final Optional<Coupon> optionalCoupon = this.couponServiceImpl.findByCouponCodeAndActiveIsTrue("ANOTHER_CODE");
        assertTrue(optionalCoupon.isEmpty());
        verify(this.couponRepository).findByCouponCodeAndActiveIsTrue(anyString());
    }

    @Test
    void findAllByDiscountTypeCodeAndActiveIsTrueShouldWorkProperly() {
        final List<Coupon> coupons = this.couponServiceImpl.findAllByDiscountTypeCodeAndActiveIsTrue(COUPON_CODE);
        assertFalse(coupons.isEmpty());
        verify(this.couponRepository).findAllByDiscountType_DiscountTypeCodeEqualsIgnoreCaseAndActiveIsTrue(anyString());
    }

    @Test
    void findByCouponCodeShouldWorkProperly() {
        final Optional<Coupon> optionalCoupon = this.couponServiceImpl.findByCouponCode(COUPON_CODE);
        assertTrue(optionalCoupon.isPresent());
        verify(this.couponRepository).findByCouponCode(anyString());
    }
}
