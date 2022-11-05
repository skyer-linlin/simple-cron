package com.lin.simplecron.repository;

import com.lin.simplecron.domain.CoinglassUmarginFundingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CoinglassUmarginFundingRateRepository extends JpaRepository<CoinglassUmarginFundingRate, Integer> {
    @Query(value = "select now()", nativeQuery = true)
    String fetchTime();
}