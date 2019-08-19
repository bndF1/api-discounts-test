package com.bnd.io.discounts.repository;

import com.bnd.io.discounts.domain.DiscountType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the DiscountType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DiscountTypeRepository extends JpaRepository<DiscountType, Long> {

}
