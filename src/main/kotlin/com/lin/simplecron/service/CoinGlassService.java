package com.lin.simplecron.service;

import cn.hutool.core.lang.Pair;
import com.google.common.collect.Lists;
import com.lin.simplecron.config.Constants;
import com.lin.simplecron.domain.CoinglassCmarginFundingRate;
import com.lin.simplecron.domain.CoinglassUmarginFundingRate;
import com.lin.simplecron.repository.CoinglassCmarginFundingRateRepository;
import com.lin.simplecron.repository.CoinglassUmarginFundingRateRepository;
import com.lin.simplecron.vo.CexFundingRateVO;
import com.lin.simplecron.vo.CoinGlassFundingRateVO;
import com.lin.simplecron.vo.CoinGlassResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/**
 * com.lin.simplecron.service
 *
 * @author quanlinlin
 * @date 2022/11/3 21:12
 * @since
 */
@Service
@Slf4j
public class CoinGlassService {

    private final String COINGLASS_API_URL_PREFIX = "https://fapi.coinglass.com/api/";
    private final String COINGLASS_API_URL = "https://fapi.coinglass.com/api/fundingRate/v2/home";

    @Qualifier("ProxyRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CoinglassUmarginFundingRateRepository umarginFundingRateRepository;

    @Autowired
    private CoinglassCmarginFundingRateRepository cmarginFundingRateRepository;

    public Pair<List<CoinglassUmarginFundingRate>, List<CoinglassCmarginFundingRate>> fetchFundingRate() {
        LocalTime now = LocalTime.now();
        LocalTime nowTime = LocalTime.of(now.getHour(), now.getMinute(), now.getSecond());
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), nowTime);
        ParameterizedTypeReference<CoinGlassResponse<List<CoinGlassFundingRateVO>>> reference
            = new ParameterizedTypeReference<>() {
        };
        CoinGlassResponse<List<CoinGlassFundingRateVO>> response = sendHttpRequest(reference);
        List<CoinGlassFundingRateVO> list = response.getData();
        List<CoinglassUmarginFundingRate> umarginList = Lists.newArrayList();
        List<CoinglassCmarginFundingRate> cmarginList = Lists.newArrayList();
        for (CoinGlassFundingRateVO vo : list) {
            CoinglassCmarginFundingRate cmarginFundingRate = vo2CMargin(vo, dateTime);
            if (!Objects.isNull(cmarginFundingRate.getBinance())
                || !Objects.isNull(cmarginFundingRate.getOkex())) { // 币安 ok 都没有收的币本位合约就算了
                cmarginList.add(cmarginFundingRate);
            }
            umarginList.add(vo2UMargin(vo, dateTime));
        }

        return Pair.of(umarginList, cmarginList);
    }

    private CoinglassUmarginFundingRate vo2UMargin(CoinGlassFundingRateVO vo, LocalDateTime dateTime) {
        CoinglassUmarginFundingRate umarginFundingRate = new CoinglassUmarginFundingRate();
        umarginFundingRate.setDatetime(dateTime);
        umarginFundingRate.setUPrice(vo.getUPrice());
        umarginFundingRate.setSymbol(vo.getSymbol());
        for (CexFundingRateVO cexFundingRateVO : vo.getUMarginList()) {
            switch (Constants.Cex.nameOf(cexFundingRateVO.getExchangeName())) {
                case BINANCE -> umarginFundingRate.setBinance(cexFundingRateVO.getRate());
                case BITMEX -> umarginFundingRate.setBitmex(cexFundingRateVO.getRate());
                case OKEX -> umarginFundingRate.setOkex(cexFundingRateVO.getRate());
                case BYBIT -> umarginFundingRate.setBybit(cexFundingRateVO.getRate());
                case FTX -> umarginFundingRate.setFtx(cexFundingRateVO.getRate());
                case GATE -> umarginFundingRate.setGate(cexFundingRateVO.getRate());
                case DERIBIT -> umarginFundingRate.setDeribit(cexFundingRateVO.getRate());
                case BITGET -> umarginFundingRate.setBitget(cexFundingRateVO.getRate());
                case COIN_EX -> umarginFundingRate.setCoinex(cexFundingRateVO.getRate());
                default -> {
                }
            }
        }
        return umarginFundingRate;
    }

    private CoinglassCmarginFundingRate vo2CMargin(CoinGlassFundingRateVO vo, LocalDateTime dateTime) {
        CoinglassCmarginFundingRate cmarginFundingRate = new CoinglassCmarginFundingRate();
        cmarginFundingRate.setSymbol(vo.getSymbol());
        cmarginFundingRate.setDatetime(dateTime);
        cmarginFundingRate.setCPrice(vo.getCPrice());
        for (CexFundingRateVO cexFundingRateVO : vo.getCMarginList()) {
            switch (Constants.Cex.nameOf(cexFundingRateVO.getExchangeName())) {
                case BINANCE -> cmarginFundingRate.setBinance(cexFundingRateVO.getRate());
                case BITMEX -> cmarginFundingRate.setBitmex(cexFundingRateVO.getRate());
                case OKEX -> cmarginFundingRate.setOkex(cexFundingRateVO.getRate());
                case BYBIT -> cmarginFundingRate.setBybit(cexFundingRateVO.getRate());
                case FTX -> cmarginFundingRate.setFtx(cexFundingRateVO.getRate());
                case GATE -> cmarginFundingRate.setGate(cexFundingRateVO.getRate());
                case DERIBIT -> cmarginFundingRate.setDeribit(cexFundingRateVO.getRate());
                case BITGET -> cmarginFundingRate.setBitget(cexFundingRateVO.getRate());
                case COIN_EX -> cmarginFundingRate.setCoinex(cexFundingRateVO.getRate());
                default -> {
                }
            }
        }
        return cmarginFundingRate;
    }

    @NotNull
    private CoinGlassResponse<List<CoinGlassFundingRateVO>> sendHttpRequest(ParameterizedTypeReference<CoinGlassResponse<List<CoinGlassFundingRateVO>>> reference) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", "application/json, text/plain, */*");
        httpHeaders.set("Accept-Language", "zh-CN,zh;q=0.9");
        httpHeaders.set("Connection", "keep-alive");
        ResponseEntity<CoinGlassResponse<List<CoinGlassFundingRateVO>>> response =
            restTemplate.exchange(COINGLASS_API_URL, HttpMethod.GET, new HttpEntity<>(httpHeaders), reference);
        return response.getBody();
    }

    public void saveCurrentFundingRate() {
        Pair<List<CoinglassUmarginFundingRate>, List<CoinglassCmarginFundingRate>> pair = fetchFundingRate();
        umarginFundingRateRepository.saveAll(pair.getKey());
        log.info("保存 {} 条 U 本位合约资金费率数据", pair.getKey().size());
        cmarginFundingRateRepository.saveAll(pair.getValue());
        log.info("保存 {} 条 币 本位合约资金费率数据", pair.getValue().size());
    }

    public String fetchTime() {
        return umarginFundingRateRepository.fetchTime();
    }

    public Pair<Long, Long> countFundingRate() {
        return Pair.of(umarginFundingRateRepository.count(), cmarginFundingRateRepository.count());
    }
}
