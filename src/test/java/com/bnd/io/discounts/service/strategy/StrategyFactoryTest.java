package com.bnd.io.discounts.service.strategy;

import com.bnd.io.discounts.domain.CustomOrder;
import com.bnd.io.discounts.domain.enums.DiscountOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrategyFactoryTest {
  @Mock Map<DiscountOperation, Strategy> strategies;
  @InjectMocks StrategyFactory strategyFactory;

  StrategyOperations.StrategyOperationDirect strategyOperationDirect =
      new StrategyOperations.StrategyOperationDirect();
  StrategyOperations.StrategyOperationPercent strategyOperationPercent =
      new StrategyOperations.StrategyOperationPercent();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void testGetStrategyWhenDiscountOperationPercent() {
    final Strategy result = strategyFactory.getStrategy(DiscountOperation.PERCENT);
    assertEquals(this.strategyOperationPercent.getClass(), result.getClass());
  }

  @Test
  void testGetStrategyWhenDiscountOperationDirect() {
    final Strategy result = strategyFactory.getStrategy(DiscountOperation.DIRECT);
    assertEquals(this.strategyOperationDirect.getClass(), result.getClass());
  }

  @Test
  void testCalculatePriceWhenDiscountOperationDirect() {
    final CustomOrder order = CustomOrder.builder().price(2).build();
    final double price = this.strategyOperationDirect.calculatePrice(order, 1);
    assertEquals(1, price);
  }

  @Test
  void testCalculatePriceWhenDiscountOperationPercent() {
    final CustomOrder order = CustomOrder.builder().price(2).build();
    final double price = this.strategyOperationPercent.calculatePrice(order, 1);
    assertEquals(1.98, price);
  }
}
