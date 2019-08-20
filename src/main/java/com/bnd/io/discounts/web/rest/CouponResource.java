package com.bnd.io.discounts.web.rest;

import com.bnd.io.discounts.domain.Coupon;
import com.bnd.io.discounts.service.CouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/** REST controller for managing {@link Coupon}. */
@RestController
@RequestMapping("/api")
public class CouponResource {

  private static final String ENTITY_NAME = "COUPON";
  private final Logger log = LoggerFactory.getLogger(CouponResource.class);
  private final CouponService couponService;

  public CouponResource(final CouponService couponService) {
    this.couponService = couponService;
  }

  /**
   * {@code POST /coupons} : Create a new coupon.
   *
   * @param coupon the coupon to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new
   *     coupon, or with status {@code 400 (Bad Request)} if the coupon has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/coupons")
  public ResponseEntity<Coupon> createCoupon(@RequestBody final Coupon coupon)
      throws URISyntaxException {
    log.debug("REST request to save Coupon : {}", coupon);
    if (coupon.getId() != null) {
      return ResponseEntity.badRequest()
          .header(ENTITY_NAME, "id_exists", "A new coupon cannot already have an ID")
          .body(null);
    }
    final Coupon result = couponService.save(coupon);
    return ResponseEntity.created(new URI("/api/coupons/" + result.getId())).body(result);
  }

  /**
   * {@code PUT /coupons} : Updates an existing coupon.
   *
   * @param coupon the coupon to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated
   *     coupon, or with status {@code 400 (Bad Request)} if the coupon is not valid, or with status
   *     {@code 500 (Internal Server Error)} if the coupon couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/coupons")
  public ResponseEntity<Coupon> updateCoupon(@RequestBody final Coupon coupon)
      throws URISyntaxException {
    log.debug("REST request to update Coupon : {}", coupon);
    if (coupon.getId() == null) {
      return ResponseEntity.badRequest().header(ENTITY_NAME, "id_null", "Invalid id").body(null);
    }
    final Coupon result = couponService.save(coupon);
    return ResponseEntity.ok().body(result);
  }

  /**
   * {@code GET /coupons} : get all the coupons.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of coupons in
   *     body.
   */
  @GetMapping("/coupons")
  public ResponseEntity<List<Coupon>> getAllCoupons(final Pageable pageable) {
    log.debug("REST request to get a page of Coupons");
    final Page<Coupon> page = couponService.findAll(pageable);
    return ResponseEntity.ok().body(page.getContent());
  }

  /**
   * {@code GET /coupons/:id} : get the "id" coupon.
   *
   * @param id the id of the coupon to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the coupon, or
   *     with status {@code 404 (Not Found)}.
   */
  @GetMapping("/coupons/{id}")
  public ResponseEntity<Coupon> getCoupon(@PathVariable final Long id) {
    log.debug("REST request to get Coupon : {}", id);
    final Optional<Coupon> coupon = couponService.findOne(id);
    return ResponseEntity.of(coupon);
  }

  /**
   * {@code DELETE /coupons/:id} : delete the "id" coupon.
   *
   * @param id the id of the coupon to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/coupons/{id}")
  public ResponseEntity<Void> deleteCoupon(@PathVariable final Long id) {
    log.debug("REST request to delete Coupon : {}", id);
    couponService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
