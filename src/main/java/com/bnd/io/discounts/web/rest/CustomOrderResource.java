package com.bnd.io.discounts.web.rest;

import com.bnd.io.discounts.domain.CustomOrder;
import com.bnd.io.discounts.service.CustomOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/** REST controller for managing {@link com.bnd.io.discounts.domain.CustomOrder}. */
@RestController
@RequestMapping("/api")
public class CustomOrderResource {

  private static final String ENTITY_NAME = "ORDER";
  private final Logger log = LoggerFactory.getLogger(CustomOrderResource.class);
  private final CustomOrderService customOrderService;

  public CustomOrderResource(final CustomOrderService customOrderService) {
    this.customOrderService = customOrderService;
  }

  /**
   * {@code POST /custom-orders} : Create a new customOrder.
   *
   * @param customOrder the customOrder to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new
   *     customOrder, or with status {@code 400 (Bad Request)} if the customOrder has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/custom-orders")
  public ResponseEntity<CustomOrder> createCustomOrder(@RequestBody final CustomOrder customOrder)
      throws URISyntaxException {
    log.debug("REST request to save CustomOrder : {}", customOrder);
    if (customOrder.getId() != null) {
      return ResponseEntity.badRequest()
          .header(ENTITY_NAME, "id_exists", "A new {} cannot already have an ID", ENTITY_NAME)
          .body(null);
    }
    final CustomOrder result = customOrderService.save(customOrder);
    return ResponseEntity.created(new URI("/api/custom-orders/" + result.getId())).body(result);
  }

  /**
   * {@code PUT /custom-orders} : Updates an existing customOrder.
   *
   * @param customOrder the customOrder to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated
   *     customOrder, or with status {@code 400 (Bad Request)} if the customOrder is not valid, or
   *     with status {@code 500 (Internal Server Error)} if the customOrder couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/custom-orders")
  public ResponseEntity<CustomOrder> updateCustomOrder(@RequestBody final CustomOrder customOrder)
      throws URISyntaxException {
    log.debug("REST request to update CustomOrder : {}", customOrder);
    if (customOrder.getId() == null) {
      return ResponseEntity.badRequest().header(ENTITY_NAME, "id_null", "Invalid id").body(null);
    }
    final CustomOrder result = customOrderService.save(customOrder);
    return ResponseEntity.ok().body(result);
  }

  /**
   * {@code GET /custom-orders} : get all the customOrders.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customOrders in
   *     body.
   */
  @GetMapping("/custom-orders")
  public ResponseEntity<List<CustomOrder>> getAllCustomOrders(final Pageable pageable) {
    log.debug("REST request to get a page of CustomOrders");
    final Page<CustomOrder> page = customOrderService.findAll(pageable);
    return ResponseEntity.ok().body(page.getContent());
  }

  /**
   * {@code GET /custom-orders/:id} : get the "id" customOrder.
   *
   * @param id the id of the customOrder to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customOrder,
   *     or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/custom-orders/{id}")
  public ResponseEntity<CustomOrder> getCustomOrder(@PathVariable final Long id) {
    log.debug("REST request to get CustomOrder : {}", id);
    final Optional<CustomOrder> customOrder = customOrderService.findOne(id);
    return ResponseEntity.of(customOrder);
  }

  /**
   * {@code DELETE /custom-orders/:id} : delete the "id" customOrder.
   *
   * @param id the id of the customOrder to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/custom-orders/{id}")
  public ResponseEntity<Void> deleteCustomOrder(@PathVariable final Long id) {
    log.debug("REST request to delete CustomOrder : {}", id);
    customOrderService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/calculate-order-discount")
  public ResponseEntity<CustomOrder> calculateOrderDiscount(
      @RequestBody @Valid final CustomOrder order) {
    log.debug("REST request to calculate discount for CustomOrder : {}", order);
    final CustomOrder updatedOrder = this.customOrderService.calculateOrderDiscount(order);
    return ResponseEntity.ok().body(updatedOrder);
  }

  @ExceptionHandler(RuntimeException.class)
  public final ResponseEntity<Exception> handleAllExceptions(final RuntimeException ex) {
    return new ResponseEntity<>(ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
