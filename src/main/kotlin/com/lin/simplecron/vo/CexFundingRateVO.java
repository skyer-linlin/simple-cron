package com.lin.simplecron.vo;

import lombok.Data;

/**
 * com.lin.simplecron.vo
 *
 * @author quanlinlin
 * @date 2022/11/4 19:58
 * @since
 */
@Data
public class CexFundingRateVO {
    private Double rate;
    private Double predictedRate;
    private Long nextFundingTime;
    private String exchangeName;
    private Integer l;
    private String exchangeLogo;
    private Integer status;
}
