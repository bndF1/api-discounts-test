package com.bnd.io.discounts.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "discount_type")
public class DiscountType implements Serializable {

  private static final long serialVersionUID = 1L;

  @OneToMany(mappedBy = "discountType")
  private final Set<Coupon> coupons = new HashSet<>();

  @NotNull
  @Column(name = "discount_type_code", nullable = false)
  private String discountTypeCode;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  private Long id;

  @Column(name = "description")
  private String description;

  //  @Builder(
  //      builderMethodName = "discountTypeBuilder",
  //      builderClassName = "DiscountTypeBuilderClassName",
  //      toBuilder = true)
  //  public DiscountType(
  //      final Long id, @NotNull final String discountTypeCode, final String description) {
  //    this.id = id;
  //    this.discountTypeCode = discountTypeCode;
  //    this.description = description;
  //  }
  //
  //  @Builder()
  //  public DiscountType(final String discountTypeCode) {
  //    this.discountTypeCode = discountTypeCode;
  //  }
}
