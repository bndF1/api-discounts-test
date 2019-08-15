package com.bnd.io.discounts.domain;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "discount_type")
@Data
public class DiscountType implements Serializable {

  private static final long serialVersionUID = 1L;

  @OneToMany(mappedBy = "discountType")
  private final Set<Coupon> coupons = new HashSet<>();

  @NotNull
  @Column(name = "discount_type_code", nullable = false)
  private final String discountTypeCode;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  private Long id;

  @Column(name = "description")
  private String description;

  @Builder(
      builderMethodName = "discountTypeBuilder",
      builderClassName = "DiscountTypeBuilderClassName",
      toBuilder = true)
  public DiscountType(
      final Long id, @NotNull final String discountTypeCode, final String description) {
    this.id = id;
    this.discountTypeCode = discountTypeCode;
    this.description = description;
  }

  @Builder()
  public DiscountType(final String discountTypeCode) {
    this.discountTypeCode = discountTypeCode;
  }
}
