package com.bnd.io.discounts.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/** A Product. */
@Data
@Entity
@Table(name = "product")
@Builder(toBuilder = true)
public class Product implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  private final Long id;

  @NotNull
  @Column(name = "code", nullable = false)
  private final String code;

  @NotNull
  @Column(name = "price", nullable = false)
  private final Double price;

  @ManyToOne
  @JsonIgnoreProperties("products")
  private final CustomOrder customOrder;
}
