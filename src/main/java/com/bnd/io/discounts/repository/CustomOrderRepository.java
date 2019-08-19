package com.bnd.io.discounts.repository;

import com.bnd.io.discounts.domain.CustomOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Spring Data repository for the CustomOrder entity. */
@Repository
public interface CustomOrderRepository extends JpaRepository<CustomOrder, Long> {

  @Override
  @EntityGraph(attributePaths = {"products"})
  Page<CustomOrder> findAll(Pageable pageable);

  @Override
  @EntityGraph(attributePaths = {"products"})
  Optional<CustomOrder> findById(Long id);
}
