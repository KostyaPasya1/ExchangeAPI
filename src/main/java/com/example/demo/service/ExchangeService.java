
package com.example.demo.service;


import com.example.demo.models.Exchange;
import com.example.demo.models.User;
import com.example.demo.repository.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ExchangeService {

    @Autowired
    ExchangeRepository exchangeRepository;

    public boolean addEx (Exchange exchange, User user) {

        exchange.setUser(user);
        exchangeRepository.save(exchange);


        return true;
    }

    public List<Exchange> allExchanges(){
        return exchangeRepository.findAll();
    }





}
