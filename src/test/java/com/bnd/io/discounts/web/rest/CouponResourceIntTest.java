package com.bnd.io.discounts.web.rest;

import com.bnd.io.discounts.domain.Coupon;
import com.bnd.io.discounts.domain.DiscountType;
import com.bnd.io.discounts.repository.CouponRepository;
import com.bnd.io.discounts.service.CouponService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CouponResourceIntTest {
  @Autowired private CouponRepository couponRepository;
  @Autowired private CouponService couponService;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    this.deleteAll();
  }

  private void deleteAll() {
    this.couponRepository.deleteAll();
  }

  @Test
  @Transactional
  public void createCouponShouldNotWorkBecauseAlreadyExist() throws Exception {
    final Coupon coupon = new EasyRandom().nextObject(Coupon.class);
    mockMvc
        .perform(
            post("/api/coupons")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(coupon)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  @Transactional
  public void createCouponShouldWork() throws Exception {
    final EasyRandomParameters parameters = new EasyRandomParameters();
    parameters
        .excludeField(FieldPredicates.named("id"))
        .excludeField(FieldPredicates.ofType(DiscountType.class));
    final Coupon coupon = new EasyRandom(parameters).nextObject(Coupon.class);

    final MockHttpServletResponse response =
        mockMvc
            .perform(
                post("/api/coupons")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(coupon)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse();

    final List<Coupon> couponList = this.couponRepository.findAll();

    assertThat(couponList.isEmpty()).isFalse();
    assertThat(
            couponList.contains(
                objectMapper.readValue(response.getContentAsString(), Coupon.class)))
        .isTrue();
  }

  @Test
  @Transactional
  public void updateCouponShouldWork() throws Exception {
    final EasyRandomParameters parameters = new EasyRandomParameters();
    parameters.excludeField(FieldPredicates.named("id"));
    final Coupon coupon = new EasyRandom(parameters).nextObject(Coupon.class);
    final Coupon storedCoupon = this.couponRepository.save(coupon);
    storedCoupon.setActive(false);

    final MockHttpServletResponse response =
        mockMvc
            .perform(
                put("/api/coupons")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(storedCoupon)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    final Coupon mappedCoupon = objectMapper.readValue(response.getContentAsString(), Coupon.class);
    assertThat(mappedCoupon.getActive()).isFalse();
  }
}
