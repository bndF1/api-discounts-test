package com.bnd.io.discounts.service.strategy;

import com.bnd.io.discounts.domain.enums.DiscountOperation;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@Component
public class StrategyFactory {

  private final Map<DiscountOperation, Strategy> strategies =
      new EnumMap<>(DiscountOperation.class);

  public StrategyFactory() {
    this.onInitStrategiesByOperation();
  }

  public Strategy getStrategy(final DiscountOperation discountOperation) {
    final DiscountOperation operation =
        Optional.ofNullable(discountOperation)
            .orElseThrow(() -> new IllegalArgumentException("Invalid discount op"));

    return this.strategies.get(operation);
  }

  private void onInitStrategiesByOperation() {
    this.strategies.put(DiscountOperation.DIRECT, new StrategyOperations.StrategyOperationDirect());
    this.strategies.put(
        DiscountOperation.PERCENT, new StrategyOperations.StrategyOperationPercent());
  }
}
