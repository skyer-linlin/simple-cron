package com.lin.simplecron.vo;

import lombok.Data;

/**
 * jiemofetch.domains
 *
 * @author quanlinlin
 * @date 2022/9/13 20:18
 * @since
 */
@Data
public class CoinGlassResponse<T> {
    private String code;
    private String msg;
    private T data;
    private Boolean success;
}
