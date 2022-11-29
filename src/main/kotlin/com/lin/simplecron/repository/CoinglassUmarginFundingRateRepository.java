package com.lin.simplecron.repository;

import com.lin.simplecron.domain.CoinglassUmarginFundingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface CoinglassUmarginFundingRateRepository extends JpaRepository<CoinglassUmarginFundingRate, Integer> {
    @Query(value = "select now()", nativeQuery = true)
    String fetchTime();


    @Transactional
    @Modifying
    @Query("delete from CoinglassUmarginFundingRate c where c.symbol not in ?1 and c.datetime < ?2")
    Integer deleteAllBySymbolNotInAndDatetimeBefore(Collection<String> symbol, LocalDateTime date);
}