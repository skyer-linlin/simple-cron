package com.lin.simplecron.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * com.lin.simplecron.vo
 *
 * @author quanlinlin
 * @date 2022/11/4 19:54
 * @since
 */
@Data
public class CoinGlassFundingRateVO {
    private String symbol;
    @JsonProperty("Huobi")
    private CexFundingRateVO huobi;
    @JsonProperty("CoinEx")
    private CexFundingRateVO coinEx;
    private CexFundingRateVO dYdX;
    @JsonProperty("Okex")
    private CexFundingRateVO okex;
    @JsonProperty("FTX")
    private CexFundingRateVO fTX;
    @JsonProperty("Bybit")
    private CexFundingRateVO bybit;
    @JsonProperty("Bitfinex")
    private CexFundingRateVO bitfinex;
    @JsonProperty("Deribit")
    private CexFundingRateVO deribit;
    @JsonProperty("Bitmex")
    private CexFundingRateVO bitmex;
    @JsonProperty("Bitget")
    private CexFundingRateVO bitget;
    @JsonProperty("Binance")
    private CexFundingRateVO binance;
    private String logo;

}
