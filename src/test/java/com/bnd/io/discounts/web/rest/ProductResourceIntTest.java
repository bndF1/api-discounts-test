package com.bnd.io.discounts.web.rest;

import com.bnd.io.discounts.domain.CustomOrder;
import com.bnd.io.discounts.domain.Product;
import com.bnd.io.discounts.repository.ProductRepository;
import com.bnd.io.discounts.service.ProductService;
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
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class ProductResourceIntTest {

  private static final String CONTENT_TYPE = "application/json";
  private Product product;
  @Autowired private ProductRepository productRepository;
  @Autowired private ProductService productService;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    this.deleteAll();
  }

  private void deleteAll() {
    this.productRepository.deleteAll();
  }

  private String writeContent(final Product product) throws JsonProcessingException {
    return objectMapper.writeValueAsString(product);
  }

  private Product createAnsSaveProductInRepository() {
    this.product = createProductObjectWithOutId();
    return this.productRepository.saveAndFlush(this.product);
  }

  @Test
  @Transactional
  public void createProduct() throws Exception {
    this.product = createProductObjectWithOutId();
    final int databaseSizeBeforeCreate = productRepository.findAll().size();

    mockMvc
        .perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeContent(this.product)))
        .andExpect(status().isCreated());

    final List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
    final Product testProduct = productList.get(productList.size() - 1);
    assertThat(testProduct.getCode()).isEqualTo(this.product.getCode());
    assertThat(testProduct.getPrice()).isEqualTo(this.product.getPrice());
  }

  private Product createProductObjectWithOutId() {
    return new EasyRandom(
            new EasyRandomParameters()
                .excludeField(FieldPredicates.named("id"))
                .excludeField(FieldPredicates.ofType(CustomOrder.class)))
        .nextObject(Product.class);
  }

  @Test
  @Transactional
  public void createProductWithExistingId() throws Exception {
    this.product = createAnsSaveProductInRepository();
    final int databaseSizeBeforeCreate = productRepository.findAll().size();
    // Create the Product with an existing ID

    final Product alreadyExistingProduct = createProductObjectWithOutId();
    alreadyExistingProduct.setId(this.product.getId());

    // An entity with an existing ID cannot be created, so this API call must fail
    mockMvc
        .perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeContent(alreadyExistingProduct)))
        .andExpect(status().isBadRequest());

    final List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  public void checkCodeIsRequired() throws Exception {
    this.product = createAnsSaveProductInRepository();
    final int databaseSizeBeforeTest = productRepository.findAll().size();
    // set the field null
    this.product.setCode(null);

    // Create the Product, which fails.
    mockMvc
        .perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeContent(this.product)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Transactional
  public void checkPriceIsRequired() throws Exception {
    this.product = createAnsSaveProductInRepository();
    final int databaseSizeBeforeTest = productRepository.findAll().size();
    // set the field null
    this.product.setPrice(null);

    // Create the Product, which fails.
    mockMvc
        .perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeContent(this.product)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Transactional
  public void getAllProducts() throws Exception {
    this.product = createAnsSaveProductInRepository();

    // Get all the productList
    mockMvc
        .perform(get("/api/products?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$.[*].id").value(hasItem(this.product.getId().intValue())))
        .andExpect(jsonPath("$.[*].code").value(hasItem(this.product.getCode())))
        .andExpect(jsonPath("$.[*].price").value(hasItem(this.product.getPrice())));
  }

  @Test
  @Transactional
  public void getProduct() throws Exception {

    this.product = createAnsSaveProductInRepository();

    // Get the product
    mockMvc
        .perform(get("/api/products/{id}", product.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$.id").value(this.product.getId().intValue()))
        .andExpect(jsonPath("$.code").value(this.product.getCode()))
        .andExpect(jsonPath("$.price").value(this.product.getPrice()));
  }

  @Test
  @Transactional
  public void getNonExistingProduct() throws Exception {
    mockMvc.perform(get("/api/products/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  public void updateProduct() throws Exception {
    this.product = createAnsSaveProductInRepository();

    final int databaseSizeBeforeUpdate = productRepository.findAll().size();

    // Update the product
    final Product storedProduct = productRepository.findById(this.product.getId()).get();
    final String updatedCode = "UPDATED-CODE";
    final Double updatedPrice = new EasyRandom().nextDouble();

    final Product productUpdated =
        storedProduct.toBuilder().price(updatedPrice).code(updatedCode).build();

    mockMvc
        .perform(
            put("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeContent(productUpdated)))
        .andExpect(status().isOk());

    // Validate the Product in the database
    final List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    final Product testProduct = productList.get(productList.size() - 1);
    assertThat(testProduct.getCode()).isEqualTo(updatedCode);
    assertThat(testProduct.getPrice()).isEqualTo(updatedPrice);
  }

  @Test
  @Transactional
  public void updateNonExistingProduct() throws Exception {
    final int databaseSizeBeforeUpdate = productRepository.findAll().size();
    mockMvc
        .perform(
            put("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeContent(this.product)))
        .andExpect(status().isBadRequest());

    final List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  public void deleteProduct() throws Exception {
    this.product = createAnsSaveProductInRepository();

    final int databaseSizeBeforeDelete = productRepository.findAll().size();

    // Delete the product
    mockMvc
        .perform(
            delete("/api/products/{id}", this.product.getId()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    // Validate the database contains one less item
    final List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);
  }
}
