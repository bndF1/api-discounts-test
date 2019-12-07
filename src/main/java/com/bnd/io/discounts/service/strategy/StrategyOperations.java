package com.bnd.io.discounts.service.strategy;

import com.bnd.io.discounts.domain.CustomOrder;

public abstract class StrategyOperations implements Strategy {

  public StrategyOperations() {}

  public static class StrategyOperationPercent extends StrategyOperations {
    @Override
    public double calculatePrice(final CustomOrder customOrder, final double discountValue) {
      return customOrder.getPrice() - (customOrder.getPrice() * (discountValue / 100));
    }
  }

  public static class StrategyOperationDirect extends StrategyOperations {
    @Override
    public double calculatePrice(final CustomOrder customOrder, final double discountValue) {
      return customOrder.getPrice() - discountValue;
    }
  }
}
