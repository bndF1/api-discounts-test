package com.bnd.io.discounts.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode()
@ToString(callSuper = true)
@Table(name = "custom_order")
public class CustomOrder implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  private Long id;

  @OneToOne
  @JoinColumn(unique = true)
  private Coupon coupon;

  @OneToMany(mappedBy = "customOrder")
  private Set<Product> products = new HashSet<>();

  @Builder(
      builderMethodName = "customOrderBuilder",
      builderClassName = "CustomOrderBuilderClassName",
      toBuilder = true)
  public CustomOrder(final Long id, final Coupon coupon, final Set<Product> products) {
    this.id = id;
    this.coupon = coupon;
    this.products = products;
  }

  @Builder()
  public CustomOrder() {}
}
