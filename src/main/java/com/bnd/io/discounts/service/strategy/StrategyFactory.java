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

    return strategies.get(operation);
  }

  private void onInitStrategiesByOperation() {
    strategies.put(DiscountOperation.DIRECT, new StrategyOperations.StrategyOperationDirect());
    strategies.put(DiscountOperation.PERCENT, new StrategyOperations.StrategyOperationPercent());
  }
}
