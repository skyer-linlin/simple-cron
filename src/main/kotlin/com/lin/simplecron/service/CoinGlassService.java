package com.lin.simplecron.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.lin.simplecron.domain.CoinglassFundingRate;
import com.lin.simplecron.repository.CoinglassFundingRateRepository;
import com.lin.simplecron.vo.CexFundingRateVO;
import com.lin.simplecron.vo.CoinGlassFundingRateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final String COINGLASS_API_URL = "https://fapi.coinglass.com/api/index?sort=oi&sortWay=desc&symbol=BTC&type=1";

    @Qualifier("ProxyRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CoinglassFundingRateRepository rateRepository;

    @Autowired
    private ObjectMapper objectMapper;


    public List<CoinglassFundingRate> fetchFundingRate() {
        LocalTime now = LocalTime.now();
        LocalTime nowTime = LocalTime.of(now.getHour(), now.getMinute(), now.getSecond());
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), nowTime);
        ResponseEntity<String> entity = restTemplate.getForEntity(COINGLASS_API_URL, String.class);
        String jsonStr = entity.getBody();
        List<CoinglassFundingRate> list = fundingRateVo2Domain(jsonStr, dateTime);
        return list;
    }

    public void saveCurrentFundingRate() {
        List<CoinglassFundingRate> list = fetchFundingRate();
        rateRepository.saveAll(list);
        log.info("保存 {} 条资金费率数据", list.size());
    }

    private List<CoinglassFundingRate> fundingRateVo2Domain(String jsonStr, LocalDateTime dateTime) {
        ArrayList<CoinGlassFundingRateVO> voList;
        List<CoinglassFundingRate> list = Lists.newArrayList();
        ;
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonStr)
                .get("data")
                .get("funding")
                .get("list");
            String fundingRateJsonStr = jsonNode.toString();
            voList = objectMapper.readValue(fundingRateJsonStr, new TypeReference<>() {
            });
            list = voList.stream().map(vo -> {
                CoinglassFundingRate cfr = new CoinglassFundingRate();
                cfr.setSymbol(vo.getSymbol())
                    .setBinance(Optional.ofNullable(vo.getBinance()).map(CexFundingRateVO::getRate).orElse(null))
                    .setBitmex(Optional.ofNullable(vo.getBitmex()).map(CexFundingRateVO::getRate).orElse(null))
                    .setOkex(Optional.ofNullable(vo.getOkex()).map(CexFundingRateVO::getRate).orElse(null))
                    .setBybit(Optional.ofNullable(vo.getBybit()).map(CexFundingRateVO::getRate).orElse(null))
                    .setFtx(Optional.ofNullable(vo.getFTX()).map(CexFundingRateVO::getRate).orElse(null))
                    .setBitfinex(Optional.ofNullable(vo.getBitfinex()).map(CexFundingRateVO::getRate).orElse(null))
                    .setDeribit(Optional.ofNullable(vo.getDeribit()).map(CexFundingRateVO::getRate).orElse(null))
                    .setBitget(Optional.ofNullable(vo.getBitget()).map(CexFundingRateVO::getRate).orElse(null))
                    .setCoinex(Optional.ofNullable(vo.getCoinEx()).map(CexFundingRateVO::getRate).orElse(null));
                cfr.setDatetime(dateTime);
                return cfr;
            }).toList();
        } catch (JsonProcessingException e) {
            log.error("Error parsing json");
            e.printStackTrace();
        }
        return list;
    }

    public String fetchTime() {
        return rateRepository.fetchTime();
    }

    public Long countFundingRate() {
        return rateRepository.count();
    }
}
