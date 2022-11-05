package com.lin.simplecron.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

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
    @JsonProperty("symbolLogo")
    private String symbolLogo;

    @JsonProperty("uMarginList")
    private List<CexFundingRateVO> uMarginList;

    @JsonProperty("cMarginList")
    private List<CexFundingRateVO> cMarginList;

    @JsonProperty("uPrice")
    private Double uPrice;

    @JsonProperty("cPrice")
    private Double cPrice;

}
