package com.lin.simplecron.repository;

import com.lin.simplecron.domain.CoinglassCmarginFundingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

public interface CoinglassCmarginFundingRateRepository extends JpaRepository<CoinglassCmarginFundingRate, Integer> {
    @Transactional
    @Modifying
    @Query(value = "delete from CoinglassCmarginFundingRate c where c.symbol not in ?1 and c.datetime < ?2")
    Integer deleteAllBySymbolNotInAndDatetimeBefore(HashSet<String> ignoreSymbolSets, LocalDateTime ustartDate);
}