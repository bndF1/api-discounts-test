package com.bnd.io.discounts.service.impl;

import com.bnd.io.discounts.domain.Coupon;
import com.bnd.io.discounts.domain.CustomOrder;
import com.bnd.io.discounts.domain.Product;
import com.bnd.io.discounts.exceptions.ApiExceptions;
import com.bnd.io.discounts.exceptions.CouponException;
import com.bnd.io.discounts.repository.CustomOrderRepository;
import com.bnd.io.discounts.service.CouponService;
import com.bnd.io.discounts.service.CustomOrderService;
import com.bnd.io.discounts.service.strategy.Strategy;
import com.bnd.io.discounts.service.strategy.StrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/** Service Implementation for managing {@link CustomOrder}. */
@Service
@Transactional
public class CustomOrderServiceImpl implements CustomOrderService {

  private final Logger log = LoggerFactory.getLogger(CustomOrderServiceImpl.class);

  private final CustomOrderRepository customOrderRepository;

  private final CouponService couponService;

  private final StrategyFactory strategyFactory;

  public CustomOrderServiceImpl(
      final CustomOrderRepository customOrderRepository,
      final CouponService couponService,
      final StrategyFactory strategyFactory) {
    this.customOrderRepository = customOrderRepository;
    this.couponService = couponService;
    this.strategyFactory = strategyFactory;
  }

  /**
   * Save a customOrder.
   *
   * @param customOrder the entity to save.
   * @return the persisted entity.
   */
  @Override
  public CustomOrder save(final CustomOrder customOrder) {
    log.debug("Request to save CustomOrder : {}", customOrder);
    return customOrderRepository.save(customOrder);
  }

  /**
   * Get all the customOrders.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  @Override
  @Transactional(readOnly = true)
  public Page<CustomOrder> findAll(final Pageable pageable) {
    log.debug("Request to get all CustomOrders");
    return customOrderRepository.findAll(pageable);
  }

  /**
   * Get one customOrder by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Override
  @Transactional(readOnly = true)
  public Optional<CustomOrder> findOne(final Long id) {
    log.debug("Request to get CustomOrder : {}", id);
    return customOrderRepository.findById(id);
  }

  /**
   * Delete the customOrder by id.
   *
   * @param id the id of the entity.
   */
  @Override
  public void delete(final Long id) {
    log.debug("Request to delete CustomOrder : {}", id);
    customOrderRepository.deleteById(id);
  }

  @Override
  public CustomOrder calculateOrderDiscount(final CustomOrder order) {
    calculatePriceForGivenProducts(order);
    if (order.getCouponCode() == null || order.getCouponCode().trim().equals("")) {
      return order;
    }
    calculateOrderPriceWithGivenCoupon(order);
    return order;
  }

  private void calculatePriceForGivenProducts(final CustomOrder order) {
    final double orderPrice = order.getProducts().stream().mapToDouble(Product::getPrice).sum();
    order.setPrice(orderPrice);
  }

  private void calculateOrderPriceWithGivenCoupon(final CustomOrder order) {
    final Optional<Coupon> byCouponCode =
        this.couponService.findByCouponCode(order.getCouponCode());

    if (byCouponCode.isEmpty()) {
      throw new CouponException(ApiExceptions.COUPON_NOT_FOUND);
    }

    final Coupon coupon =
        byCouponCode
            .filter(Coupon::getActive)
            .orElseThrow(() -> new CouponException(ApiExceptions.DEACTIVATED_COUPON));

    final double orderPrice = calculatePrice(order, coupon);
    order.setPrice(orderPrice);
  }

  private double calculatePrice(final CustomOrder order, final Coupon coupon) {
    final double orderPrice = calculatePriceByStrategy(order, coupon);
    return Math.max(orderPrice, 0d);
  }

  private double calculatePriceByStrategy(final CustomOrder customOrder, final Coupon coupon) {
    final Strategy strategy =
        this.strategyFactory.getStrategy(coupon.getDiscountType().getDiscountOperation());
    return strategy.calculatePrice(customOrder, coupon.getDiscountType().getDiscount());
  }
}
