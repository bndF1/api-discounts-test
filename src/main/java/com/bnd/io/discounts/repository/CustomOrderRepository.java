package com.bnd.io.discounts.repository;

import com.bnd.io.discounts.domain.CustomOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Spring Data repository for the CustomOrder entity. */
@Repository
public interface CustomOrderRepository extends JpaRepository<CustomOrder, Long> {}
