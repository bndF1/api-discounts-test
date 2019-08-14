package com.bnd.io.discounts.domain;

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

  @Builder(
      builderMethodName = "couponBuilder",
      builderClassName = "CouponBuilderClassName",
      toBuilder = true)
  public Coupon(final Long id, final String couponCode, final Boolean active) {
    this.id = id;
    this.couponCode = couponCode;
    this.active = active;
  }

  @Builder()
  public Coupon() {}
}
