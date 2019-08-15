package com.bnd.io.discounts.repository;

import com.bnd.io.discounts.domain.CustomOrder;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the CustomOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomOrderRepository extends JpaRepository<CustomOrder, Long> {

}
