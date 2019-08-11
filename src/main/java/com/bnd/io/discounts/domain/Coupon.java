package com.bnd.io.discounts.domain;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "coupon")
@Builder(toBuilder = true)
@Data
public class Coupon implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  private final Long id;

  @Column(name = "coupon_code")
  private final String couponCode;

  @Column(name = "active")
  private final Boolean active;
}
