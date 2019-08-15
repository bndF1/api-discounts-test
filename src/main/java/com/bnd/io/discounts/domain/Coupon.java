package com.bnd.io.discounts.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "coupon")
@Data
public class Coupon implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  private Long id;

  @Column(name = "coupon_code")
  private String couponCode;

  @Column(name = "active")
  private Boolean active;

  @ManyToOne
  @JsonIgnoreProperties("coupons")
  private DiscountType discountType;

  @Builder(
      builderMethodName = "couponBuilder",
      builderClassName = "CouponBuilderClassName",
      toBuilder = true)
  public Coupon(final Long id, final String couponCode, final Boolean active, final DiscountType discountType) {
    this.id = id;
    this.couponCode = couponCode;
    this.active = active;
    this.discountType = discountType;
  }

  @Builder()
  public Coupon() {}
}
