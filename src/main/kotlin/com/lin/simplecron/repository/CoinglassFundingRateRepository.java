package com.lin.simplecron.repository;

import com.lin.simplecron.domain.CoinglassFundingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * com.lin.simplecron.repository
 *
 * @author quanlinlin
 * @date 2022/11/4 01:06
 * @since
 */
@Repository
public interface CoinglassFundingRateRepository extends JpaRepository<CoinglassFundingRate, Integer> {

    @Query(value = "select now()", nativeQuery = true)
    String fetchTime();
}
