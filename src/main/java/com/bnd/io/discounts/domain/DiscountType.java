package com.bnd.io.discounts.domain;

import com.bnd.io.discounts.domain.enums.DiscountOperation;
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

  @NotNull
  @Column(name = "discount", nullable = false)
  private Double discount;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "discount_operation", nullable = false)
  private DiscountOperation discountOperation;
}
