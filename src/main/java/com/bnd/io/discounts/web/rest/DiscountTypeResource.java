package com.bnd.io.discounts.web.rest;

import com.bnd.io.discounts.domain.DiscountType;
import com.bnd.io.discounts.service.DiscountTypeService;
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

/** REST controller for managing {@link com.bnd.io.discounts.domain.DiscountType}. */
@RestController
@RequestMapping("/api")
public class DiscountTypeResource {

  private static final String ENTITY_NAME = "DISCOUNTTYPE";
  private final Logger log = LoggerFactory.getLogger(DiscountTypeResource.class);
  private final DiscountTypeService discountTypeService;

  public DiscountTypeResource(final DiscountTypeService discountTypeService) {
    this.discountTypeService = discountTypeService;
  }

  /**
   * {@code POST /discount-types} : Create a new discountType.
   *
   * @param discountType the discountType to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new
   *     discountType, or with status {@code 400 (Bad Request)} if the discountType has already an
   *     ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/discount-types")
  public ResponseEntity<DiscountType> createDiscountType(
      @Valid @RequestBody final DiscountType discountType) throws URISyntaxException {
    log.debug("REST request to save DiscountType : {}", discountType);
    if (discountType.getId() != null) {
      return ResponseEntity.badRequest()
          .header(ENTITY_NAME, "id_exists", "A new {} cannot already have an ID", ENTITY_NAME)
          .body(null);
    }

    final DiscountType result = discountTypeService.save(discountType);
    return ResponseEntity.created(new URI("/api/discount-types/" + result.getId())).body(result);
  }

  /**
   * {@code PUT /discount-types} : Updates an existing discountType.
   *
   * @param discountType the discountType to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated
   *     discountType, or with status {@code 400 (Bad Request)} if the discountType is not valid, or
   *     with status {@code 500 (Internal Server Error)} if the discountType couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/discount-types")
  public ResponseEntity<DiscountType> updateDiscountType(
      @Valid @RequestBody final DiscountType discountType) throws URISyntaxException {
    log.debug("REST request to update DiscountType : {}", discountType);
    if (discountType.getId() == null) {
      return ResponseEntity.badRequest().header(ENTITY_NAME, "id_null", "Invalid id").body(null);
    }
    final DiscountType result = discountTypeService.save(discountType);
    return ResponseEntity.ok().body(result);
  }

  /**
   * {@code GET /discount-types} : get all the discountTypes.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of discountTypes
   *     in body.
   */
  @GetMapping("/discount-types")
  public ResponseEntity<List<DiscountType>> getAllDiscountTypes(final Pageable pageable) {
    log.debug("REST request to get a page of DiscountTypes");
    final Page<DiscountType> page = discountTypeService.findAll(pageable);
    return ResponseEntity.ok().body(page.getContent());
  }

  /**
   * {@code GET /discount-types/:id} : get the "id" discountType.
   *
   * @param id the id of the discountType to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the discountType,
   *     or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/discount-types/{id}")
  public ResponseEntity<DiscountType> getDiscountType(@PathVariable final Long id) {
    log.debug("REST request to get DiscountType : {}", id);
    final Optional<DiscountType> discountType = discountTypeService.findOne(id);
    return ResponseEntity.of(discountType);
  }

  /**
   * {@code DELETE /discount-types/:id} : delete the "id" discountType.
   *
   * @param id the id of the discountType to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/discount-types/{id}")
  public ResponseEntity<Void> deleteDiscountType(@PathVariable final Long id) {
    log.debug("REST request to delete DiscountType : {}", id);
    discountTypeService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
