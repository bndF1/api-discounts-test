package com.bnd.io.discounts.web.rest;

import com.bnd.io.discounts.domain.Coupon;
import com.bnd.io.discounts.domain.DiscountType;
import com.bnd.io.discounts.repository.CouponRepository;
import com.bnd.io.discounts.repository.DiscountTypeRepository;
import com.bnd.io.discounts.service.CouponService;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class CouponResourceIT {
  @Autowired private CouponRepository couponRepository;
  @Autowired private DiscountTypeRepository discountTypeRepository;
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

  @Test
  @Transactional
  public void findActiveCouponsByDiscountCodeTest() throws Exception {
    final EasyRandomParameters parameters = new EasyRandomParameters();
    parameters.excludeField(FieldPredicates.named("id"));
    final EasyRandom easyRandom = new EasyRandom(parameters);

    final DiscountType discountType = easyRandom.nextObject(DiscountType.class);
    this.discountTypeRepository.saveAndFlush(discountType);

    final List<Coupon> couponList =
        easyRandom
            .objects(Coupon.class, 2)
            .map(
                coupon -> {
                  coupon.setDiscountType(discountType);
                  coupon.setActive(true);
                  return coupon;
                })
            .collect(Collectors.toList());

    final List<Coupon> storedCouponList = this.couponRepository.saveAll(couponList);

    final MockHttpServletResponse response =
        mockMvc
            .perform(
                get(
                        "/api/coupons-by-discount-code/{discountCode}",
                        discountType.getDiscountTypeCode())
                    .contentType("application/json"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    final List<Coupon> coupons =
        objectMapper.readValue(response.getContentAsString(), new TypeReference<List<Coupon>>() {});

    assertThat(coupons).hasSameElementsAs(storedCouponList);
  }

  @Test
  @Transactional
  public void getAllCoupons() throws Exception {
    // Initialize the database
    final EasyRandomParameters parameters = new EasyRandomParameters();
    parameters.excludeField(FieldPredicates.named("id"));

    final EasyRandom easyRandom = new EasyRandom(parameters);

    final DiscountType discountType = easyRandom.nextObject(DiscountType.class);
    final DiscountType storedDiscountType = this.discountTypeRepository.saveAndFlush(discountType);

    final Coupon coupon = easyRandom.nextObject(Coupon.class);
    coupon.setDiscountType(storedDiscountType);

    final Coupon storedCoupon = this.couponRepository.save(coupon);

    // Get all the couponList
    final MockHttpServletResponse response =
        mockMvc
            .perform(get("/api/coupons?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andReturn()
            .getResponse();

    final List<Coupon> coupons =
        objectMapper.readValue(response.getContentAsString(), new TypeReference<List<Coupon>>() {});

    assertThat(coupons).contains(storedCoupon);
  }
}
