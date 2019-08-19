package com.bnd.io.discounts.web.rest;

import com.bnd.io.discounts.domain.DiscountType;
import com.bnd.io.discounts.repository.DiscountTypeRepository;
import com.bnd.io.discounts.service.DiscountTypeService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class DiscountTypeResourceIntTest {

  @Autowired private DiscountTypeService discountTypeService;
  @Autowired private DiscountTypeRepository discountTypeRepository;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    this.deleteAll();
  }

  private void deleteAll() {
    this.discountTypeRepository.deleteAll();
  }

  private String writeContent(final DiscountType discountType) throws JsonProcessingException {
    return objectMapper.writeValueAsString(discountType);
  }

  @Test
  @Transactional
  public void createDiscountType() throws Exception {
    final DiscountType discountType = createDiscountTypeObject();

    final int databaseSizeBeforeCreate = discountTypeRepository.findAll().size();

    // Create the DiscountType
    mockMvc
        .perform(
            post("/api/discount-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeContent(discountType)))
        .andExpect(status().isCreated());

    final List<DiscountType> discountTypeList = discountTypeRepository.findAll();
    assertThat(discountTypeList).hasSize(databaseSizeBeforeCreate + 1);
    final DiscountType testDiscountType = discountTypeList.get(discountTypeList.size() - 1);
    assertThat(testDiscountType.getDiscountTypeCode())
        .isEqualTo(discountType.getDiscountTypeCode());
    assertThat(testDiscountType.getDescription()).isEqualTo(discountType.getDescription());
  }

  private DiscountType createDiscountTypeObject() {
    return new EasyRandom(new EasyRandomParameters().excludeField(FieldPredicates.named("id")))
        .nextObject(DiscountType.class);
  }

  @Test
  @Transactional
  public void createDiscountTypeWithExistingId() throws Exception {
    final DiscountType discountType = createDiscountTypeObject();
    final int databaseSizeBeforeCreate = discountTypeRepository.findAll().size();

    discountType.setId(1L);

    mockMvc
        .perform(
            post("/api/discount-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeContent(discountType)))
        .andExpect(status().isBadRequest());

    final List<DiscountType> discountTypeList = discountTypeRepository.findAll();
    assertThat(discountTypeList).hasSize(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  public void checkDiscountTypeCodeIsRequired() throws Exception {
    final DiscountType discountType = createDiscountTypeObject();
    discountTypeRepository.saveAndFlush(discountType);
    discountType.setDiscountTypeCode(null);

    mockMvc
        .perform(
            post("/api/discount-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeContent(discountType)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Transactional
  public void getAllDiscountTypes() throws Exception {
    final DiscountType discountType = createDiscountTypeObject();
    discountTypeRepository.saveAndFlush(discountType);

    mockMvc
        .perform(get("/api/discount-types?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$.[*].id").value(hasItem(discountType.getId().intValue())))
        .andExpect(
            jsonPath("$.[*].discountTypeCode").value(hasItem(discountType.getDiscountTypeCode())))
        .andExpect(jsonPath("$.[*].description").value(hasItem(discountType.getDescription())));
  }

  @Test
  @Transactional
  public void getDiscountType() throws Exception {
    final DiscountType discountType = createDiscountTypeObject();
    discountTypeRepository.saveAndFlush(discountType);

    mockMvc
        .perform(get("/api/discount-types/{id}", discountType.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$.id").value(discountType.getId().intValue()))
        .andExpect(jsonPath("$.discountTypeCode").value(discountType.getDiscountTypeCode()))
        .andExpect(jsonPath("$.description").value(discountType.getDescription()));
  }

  @Test
  @Transactional
  public void getNonExistingDiscountType() throws Exception {
    // Get the discountType
    mockMvc
        .perform(get("/api/discount-types/{id}", Long.MAX_VALUE))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  public void updateDiscountType() throws Exception {
    final DiscountType discountType = createDiscountTypeObject();
    discountTypeRepository.saveAndFlush(discountType);

    final int databaseSizeBeforeUpdate = discountTypeRepository.findAll().size();
    final DiscountType updatedDiscountType =
        discountTypeRepository.findById(discountType.getId()).get();

    final String updatedDescription = new EasyRandom().nextObject(String.class);
    updatedDiscountType.setDescription(updatedDescription);

    mockMvc
        .perform(
            put("/api/discount-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeContent(updatedDiscountType)))
        .andExpect(status().isOk());

    final List<DiscountType> discountTypeList = discountTypeRepository.findAll();
    assertThat(discountTypeList).hasSize(databaseSizeBeforeUpdate);
    final DiscountType testDiscountType = discountTypeList.get(discountTypeList.size() - 1);
    assertThat(testDiscountType.getDescription()).isEqualTo(updatedDescription);
  }

  @Test
  @Transactional
  public void updateNonExistingDiscountType() throws Exception {
    final DiscountType discountType = createDiscountTypeObject();

    final int databaseSizeBeforeUpdate = discountTypeRepository.findAll().size();

    mockMvc
        .perform(
            put("/api/discount-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeContent(discountType)))
        .andExpect(status().isBadRequest());

    final List<DiscountType> discountTypeList = discountTypeRepository.findAll();
    assertThat(discountTypeList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  public void deleteDiscountType() throws Exception {
    final DiscountType discountType = createDiscountTypeObject();
    discountTypeRepository.saveAndFlush(discountType);

    final int databaseSizeBeforeDelete = discountTypeRepository.findAll().size();

    mockMvc
        .perform(
            delete("/api/discount-types/{id}", discountType.getId())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    final List<DiscountType> discountTypeList = discountTypeRepository.findAll();
    assertThat(discountTypeList).hasSize(databaseSizeBeforeDelete - 1);
  }
}
