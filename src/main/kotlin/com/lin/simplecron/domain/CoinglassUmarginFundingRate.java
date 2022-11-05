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
@Table(name = "coinglass_umargin_funding_rate")
public class CoinglassUmarginFundingRate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
     * bitmex
     */
    @Column(name = "bitmex")
    private Double bitmex;

    /**
     * okex
     */
    @Column(name = "okex")
    private Double okex;

    /**
     * bybit
     */
    @Column(name = "bybit")
    private Double bybit;

    /**
     * ftx
     */
    @Column(name = "ftx")
    private Double ftx;

    /**
     * bitfinex
     */
    @Column(name = "gate")
    private Double gate;

    /**
     * deribit
     */
    @Column(name = "deribit")
    private Double deribit;

    /**
     * bitget
     */
    @Column(name = "bitget")
    private Double bitget;

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

    @Column(name = "u_price")
    private Double uPrice;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        CoinglassUmarginFundingRate that = (CoinglassUmarginFundingRate) o;
        return symbol != null && Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}