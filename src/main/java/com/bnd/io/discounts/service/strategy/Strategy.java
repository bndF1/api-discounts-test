package com.bnd.io.discounts.service.strategy;

import com.bnd.io.discounts.domain.CustomOrder;

public interface Strategy {

  double calculatePrice(CustomOrder customOrder, double discountValue);
}
