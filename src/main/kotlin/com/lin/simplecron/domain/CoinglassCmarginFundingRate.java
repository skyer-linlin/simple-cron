package com.lin.simplecron.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author quanlinlin
 * @description coinglass_funding_rate
 * @date 2022-11-04
 */
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Accessors(chain = true)
@Table(name = "coinglass_cmargin_funding_rate")
public class CoinglassCmarginFundingRate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * symbol
     */
    @Column(name = "symbol")
    private String symbol;

    /**
     * binance
     */
    @Column(name = "binance")
    private Double binance;

    /**
     * okex
     */
    @Column(name = "okex")
    private Double okex;

    /**
     * bitmex
     */
    @Column(name = "bitmex")
    private Double bitmex;

    /**
     * bybit
     */
    @Column(name = "bybit")
    private Double bybit;

    /**
     * deribit
     */
    @Column(name = "deribit")
    private Double deribit;

    /**
     * coinex
     */
    @Column(name = "coinex")
    private Double coinex;

    /**
     * datetime
     */
    @Column(name = "datetime")
    private LocalDateTime datetime;

    /**
     * 币本位价格
     */
    @Column(name = "c_price")
    private Double cPrice;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        CoinglassCmarginFundingRate that = (CoinglassCmarginFundingRate) o;
        return symbol != null && Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}