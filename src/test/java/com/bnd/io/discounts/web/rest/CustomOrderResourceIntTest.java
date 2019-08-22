package com.bnd.io.discounts.web.rest;

import com.bnd.io.discounts.domain.Coupon;
import com.bnd.io.discounts.domain.CustomOrder;
import com.bnd.io.discounts.domain.DiscountType;
import com.bnd.io.discounts.domain.Product;
import com.bnd.io.discounts.repository.CouponRepository;
import com.bnd.io.discounts.repository.CustomOrderRepository;
import com.bnd.io.discounts.repository.DiscountTypeRepository;
import com.bnd.io.discounts.repository.ProductRepository;
import com.bnd.io.discounts.service.CustomOrderService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class CustomOrderResourceIntTest {

  @Autowired private CustomOrderService customOrderService;
  @Autowired private CustomOrderResource customOrderResource;
  @Autowired private CustomOrderRepository customOrderRepository;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ProductRepository productRepository;
  @Autowired private CouponRepository couponRepository;
  @Autowired private DiscountTypeRepository discountTypeRepository;

  @Test
  @Transactional
  void calculateDiscountFromGivenOrder() throws Exception {
    final CustomOrder order = new EasyRandom().nextObject(CustomOrder.class);
    mockMvc
        .perform(
            post("/api/calculate-order-discount")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(order)))
        .andExpect(status().is2xxSuccessful());
  }

  @BeforeEach
  private void setUp() {
    this.deleteAll();
  }

  private void deleteAll() {
    this.customOrderRepository.deleteAll();
  }

  @Test
  @Transactional
  void testCreateCustomOrderFailOnIdGiven() throws Exception {
    final CustomOrder order = new EasyRandom().nextObject(CustomOrder.class);
    mockMvc
        .perform(
            post("/api/custom-order")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(order)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  @Transactional
  public void testCreateCustomOrderShouldWork() throws Exception {
    final EasyRandomParameters parameters = new EasyRandomParameters();
    parameters
        .excludeField(FieldPredicates.named("id"))
        .excludeField(FieldPredicates.ofType(HashSet.class))
        .excludeField(FieldPredicates.ofType(Coupon.class));
    final CustomOrder order = new EasyRandom(parameters).nextObject(CustomOrder.class);

    final MockHttpServletResponse response =
        mockMvc
            .perform(
                post("/api/custom-orders")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(order)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse();

    final List<CustomOrder> orderList = this.customOrderRepository.findAll();

    assertThat(orderList.isEmpty()).isFalse();
    assertThat(
            orderList.contains(
                objectMapper.readValue(response.getContentAsString(), CustomOrder.class)))
        .isTrue();
  }

  @Test
  @Transactional
  public void testUpdateCustomOrder() throws Exception {
    final EasyRandomParameters parameters = new EasyRandomParameters();
    parameters.excludeField(FieldPredicates.named("id"));

    final EasyRandomParameters easyRandomParameters =
        new EasyRandomParameters()
            .excludeField(FieldPredicates.named("id"))
            .excludeField(FieldPredicates.ofType(CustomOrder.class));
    final Set<Product> products =
        new EasyRandom(easyRandomParameters).objects(Product.class, 2).collect(Collectors.toSet());
    this.productRepository.saveAll(products);

    final DiscountType discountType = new EasyRandom().nextObject(DiscountType.class);
    final DiscountType storedDiscountType = this.discountTypeRepository.saveAndFlush(discountType);

    final Coupon coupon1 =
        new EasyRandom()
            .nextObject(Coupon.class)
            .toBuilder()
            .discountType(storedDiscountType)
            .build();
    final Coupon coupon2 =
        new EasyRandom()
            .nextObject(Coupon.class)
            .toBuilder()
            .discountType(storedDiscountType)
            .build();
    final Coupon originalCoupon = this.couponRepository.saveAndFlush(coupon1);
    final Coupon updatedCoupon = this.couponRepository.saveAndFlush(coupon2);

    final CustomOrder order =
        new EasyRandom(parameters)
            .nextObject(CustomOrder.class)
            .toBuilder()
            .coupon(originalCoupon)
            .products(products)
            .build();
    final CustomOrder storedOrder = this.customOrderRepository.saveAndFlush(order);
    storedOrder.setCoupon(updatedCoupon);

    final MockHttpServletResponse response =
        mockMvc
            .perform(
                put("/api/custom-orders")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(storedOrder)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    final CustomOrder orderMapped =
        objectMapper.readValue(response.getContentAsString(), CustomOrder.class);
    assertThat(orderMapped.getCoupon().getCouponCode()).isEqualTo(updatedCoupon.getCouponCode());
  }

  @Test
  void testGetAllCustomOrders() throws Exception {
    final EasyRandomParameters easyRandomParameters =
        new EasyRandomParameters()
            .excludeField(FieldPredicates.named("id"))
            .excludeField(FieldPredicates.ofType(CustomOrder.class));
    final Set<Product> products =
        new EasyRandom(easyRandomParameters).objects(Product.class, 2).collect(Collectors.toSet());

    this.productRepository.saveAll(products);

    final List<CustomOrder> orderList =
        new EasyRandom(
                easyRandomParameters
                    .excludeField(FieldPredicates.ofType(Coupon.class))
                    .excludeField(FieldPredicates.ofType(HashSet.class)))
            .objects(CustomOrder.class, 5)
            .peek(customOrder -> customOrder.setProducts(products))
            .collect(Collectors.toList());
    this.customOrderRepository.saveAll(orderList);

    mockMvc
        .perform(get("/api/custom-orders?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[*].id").value(hasItem(orderList.get(0).getId().intValue())));
  }

  @Test
  void testGetCustomOrder() throws Exception {
    final EasyRandomParameters easyRandomParameters =
        new EasyRandomParameters()
            .excludeField(FieldPredicates.named("id"))
            .excludeField(FieldPredicates.ofType(CustomOrder.class));

    final Set<Product> products =
        new EasyRandom(easyRandomParameters).objects(Product.class, 2).collect(Collectors.toSet());
    this.productRepository.saveAll(products);

    final CustomOrder order =
        new EasyRandom(
                new EasyRandomParameters()
                    .excludeField(FieldPredicates.named("id"))
                    .excludeField(FieldPredicates.ofType(Coupon.class)))
            .nextObject(CustomOrder.class);
    order.setProducts(products);
    final CustomOrder storedOrder = this.customOrderRepository.saveAndFlush(order);

    mockMvc
        .perform(
            get("/api/custom-orders/{id}", storedOrder.getId()).contentType("application/json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(storedOrder.getId().intValue()));
  }

  @Test
  @Transactional
  public void getNonExistingCustomOrder() throws Exception {
    mockMvc
        .perform(get("/api/custom-orders/{id}", Long.MAX_VALUE))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteCustomOrder() throws Exception {
    final CustomOrder order =
        new EasyRandom(
                new EasyRandomParameters()
                    .excludeField(FieldPredicates.named("id"))
                    .excludeField(FieldPredicates.ofType(HashSet.class))
                    .excludeField(FieldPredicates.ofType(Coupon.class)))
            .nextObject(CustomOrder.class);
    final CustomOrder storedOrder = this.customOrderRepository.saveAndFlush(order);
    final int databaseSizeBeforeDelete = customOrderRepository.findAll().size();

    mockMvc
        .perform(delete("/api/custom-orders/{id}", storedOrder.getId()))
        .andExpect(status().isNoContent());

    final List<CustomOrder> orderList = customOrderRepository.findAll();
    assertThat(orderList).hasSize(databaseSizeBeforeDelete - 1);
  }
}
