package com.bnd.io.discounts.web.rest;

import com.bnd.io.discounts.domain.Product;
import com.bnd.io.discounts.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/** REST controller for managing {@link com.bnd.io.discounts.domain.Product}. */
@RestController
@RequestMapping("/api")
public class ProductResource {

  private static final String ENTITY_NAME = "PRODUCT";
  private final Logger log = LoggerFactory.getLogger(ProductResource.class);
  private final ProductService productService;

  public ProductResource(final ProductService productService) {
    this.productService = productService;
  }

  /**
   * {@code POST /products} : Create a new product.
   *
   * @param product the product to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new
   *     product, or with status {@code 400 (Bad Request)} if the product has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/products")
  public ResponseEntity<Product> createProduct(@Valid @RequestBody final Product product)
      throws URISyntaxException {
    log.debug("REST request to save Product : {}", product);
    if (product.getId() != null) {
      return ResponseEntity.badRequest()
          .header(ENTITY_NAME, "id_exists", "A new {} cannot already have an ID", ENTITY_NAME)
          .body(null);
    }
    final Product result = productService.save(product);
    return ResponseEntity.created(new URI("/api/products/" + result.getId())).body(result);
  }

  /**
   * {@code PUT /products} : Updates an existing product.
   *
   * @param product the product to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated
   *     product, or with status {@code 400 (Bad Request)} if the product is not valid, or with
   *     status {@code 500 (Internal Server Error)} if the product couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/products")
  public ResponseEntity<Product> updateProduct(@Valid @RequestBody final Product product)
      throws URISyntaxException {
    log.debug("REST request to update Product : {}", product);
    if (product.getId() == null) {
      return ResponseEntity.badRequest().header(ENTITY_NAME, "id_null", "Invalid id").body(null);
    }
    final Product result = productService.save(product);
    return ResponseEntity.ok().body(result);
  }

  /**
   * {@code GET /products} : get all the products.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in
   *     body.
   */
  @GetMapping("/products")
  public ResponseEntity<List<Product>> getAllProducts(final Pageable pageable) {
    log.debug("REST request to get a page of Products");
    final Page<Product> page = productService.findAll(pageable);
    return ResponseEntity.ok().body(page.getContent());
  }

  /**
   * {@code GET /products/:id} : get the "id" product.
   *
   * @param id the id of the product to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the product, or
   *     with status {@code 404 (Not Found)}.
   */
  @GetMapping("/products/{id}")
  public ResponseEntity<Product> getProduct(@PathVariable final Long id) {
    log.debug("REST request to get Product : {}", id);
    final Optional<Product> product = productService.findOne(id);
    return ResponseEntity.of(product);
  }

  /**
   * {@code DELETE /products/:id} : delete the "id" product.
   *
   * @param id the id of the product to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/products/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable final Long id) {
    log.debug("REST request to delete Product : {}", id);
    productService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
