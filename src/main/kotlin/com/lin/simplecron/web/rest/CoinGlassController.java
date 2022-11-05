package com.lin.simplecron.web.rest;

import cn.hutool.core.lang.Pair;
import com.lin.simplecron.domain.CoinglassCmarginFundingRate;
import com.lin.simplecron.domain.CoinglassUmarginFundingRate;
import com.lin.simplecron.service.CoinGlassService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * com.lin.simplecron.web.rest
 *
 * @author quanlinlin
 * @date 2022/11/3 21:16
 * @since
 */
@RestController
@RequestMapping("/api/coinGlass")
@Tag(name = "coinGlass 信息接口", description = "从 coinglass 网站抓取信息,可以监控触发,筛选,排序等")
public class CoinGlassController {
    @Autowired
    private CoinGlassService coinGlassService;

    @GetMapping("/fundingRate/current")
    public ResponseEntity<Pair<List<CoinglassUmarginFundingRate>, List<CoinglassCmarginFundingRate>>> fetchFundingRate() {
        return ResponseEntity.ok(coinGlassService.fetchFundingRate());
    }

    @PostMapping("/fundingRate/save")
    public ResponseEntity<LocalDateTime> saveFundingRate() {
        coinGlassService.saveCurrentFundingRate();
        return ResponseEntity.ok(LocalDateTime.now());
    }

    @GetMapping("/fundingrate/count")
    public ResponseEntity<Pair<Long, Long>> countFundingRate() {
        return ResponseEntity.ok(coinGlassService.countFundingRate());
    }

    @GetMapping("/time")
    public ResponseEntity<String> fetchTime() {
        return ResponseEntity.ok(coinGlassService.fetchTime());
    }
}
