
package com.example.demo.models;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "t_exch")
public class Exchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromCurrency;
    private String intoCurrency;
    //В БД сохраняется в ДОЛЛАРАХ используя метод gesUSDRate (@)
    private double volumeOfCurrency;
    private double exchangeResult;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;


    public Exchange(String fromCurrency, String intoCurrency, double volumeOfCurrency, double exchangeResult) {
        this.fromCurrency = fromCurrency;
        this.intoCurrency = intoCurrency;
        this.volumeOfCurrency = volumeOfCurrency;
        this.exchangeResult = exchangeResult;
    }

    public Exchange() {
    }

    public double getExchangeResult() {
        return exchangeResult;
    }

    public void setExchangeResult(double exchangeResult) {
        this.exchangeResult = exchangeResult;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getIntoCurrency() {
        return intoCurrency;
    }

    public void setIntoCurrency(String intoCurrency) {
        this.intoCurrency = intoCurrency;
    }

    public double getVolumeOfCurrency() {
        return volumeOfCurrency;
    }

    public void setVolumeOfCurrency(int volumeOfCurrency) {
        this.volumeOfCurrency = volumeOfCurrency;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
