package com.example.demo.contrillers;


import com.example.demo.models.Exchange;
import com.example.demo.models.User;
import com.example.demo.service.ExchangeService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;
import java.util.*;


@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExchangeApiController {

    final ExchangeService exchangeService;
    final UserService userService;
    final ObjectMapper objectMapper;

    public ExchangeApiController(ExchangeService exchangeService, UserService userService, ObjectMapper objectMapper) {
        this.exchangeService = exchangeService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }


    @GetMapping("/exchange")
    public ObjectNode getExchange(HttpServletRequest request) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        String fromCurrency = request.getParameter("from");
        int value = Integer.parseInt(request.getParameter("value"));
        String toCurrency = request.getParameter("to");


        //Работа сс внешним API для получения ставки обмена валют
        URL currencyUrl = new URL("https://api.exchangeratesapi.io/latest?symbols=" + toCurrency + "&base=" + fromCurrency);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(currencyUrl.openConnection().getInputStream()));
        JSONObject jsonObject = (JSONObject) jsonParser.parse(bufferedReader);
        bufferedReader.close();

        // обрабатываем структуру в объекте
        JSONObject structure = (JSONObject) jsonObject.get("rates");
        double rates = (Double) structure.get(toCurrency);

        double queryResult = rates * value;

        //Получаем залогиненного пользователя
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authName = auth.getName();

        Exchange newExchange = new Exchange(fromCurrency, toCurrency, (value * getUSDRate(fromCurrency)) , queryResult);
        exchangeService.addEx(newExchange, userService.findByUsername(authName));





        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("id", newExchange.getId());
        objectNode.put("result", queryResult);



        return objectNode;
   }


   @GetMapping("/stats/{type}")
   public ObjectNode getUserOneExMoreN(HttpServletRequest request, @PathVariable String type) {
       ObjectNode objectNode = objectMapper.createObjectNode();
       List<String> users = new ArrayList<>();




       //Это условие надо, что бы, при необходимости, ввести новый параметр GET запроса с ключом: "more"
       // (который устанавливает рамки выборки пользователей)
       if (type.equals("oneEx") || type.equals("sumEx")) {
           int value = Integer.parseInt(request.getParameter("more"));

           //Проходимся по всему списку зарегистрированных пользователей
           for (User user : userService.allUsers()) {
               double sumOfEx = 0.0;

               //У каждого пользователя проходимся по его списку Exchanges
               for (Exchange exchange : user.getExchanges()) {

                   //Если в параметр с ключом: "type" было передано значение: "oneEx",то у каждого объекта Exchange
                   // получаем его VolumeOfCurrency,если оно больше или равно значению переданного пользователем в
                   //параметр с ключом: "more", то записываем пользователя в список users.
                   if (type.equals("oneEx")) {
                       double volumeEx = exchange.getVolumeOfCurrency();

                       if (volumeEx >= value) {
                           users.add(user.getUsername());
                           break;
                       }
                       //Если в параметр с ключом: "type" было передано значение: "sumEx",то у каждого объекта Exchange
                       // получаем его VolumeOfCurrency и суммируем их в одной переменной, если оно больше значения переданного пользователем в
                       //параметр с ключом: "more", то записываем пользователя в список users.
                   } else if (type.equals("sumEx")) {
                       sumOfEx += exchange.getVolumeOfCurrency();

                       if (sumOfEx > value) {
                           users.add(user.getUsername());
                           break;
                       }
                   }
               }

           }
           objectNode.putPOJO("Users", users);

       } else if (type.equals("popCur")){

           //Список всех волют в которую конвертировали пользователи
           List<String> list = new ArrayList<>();
           for (Exchange ex: exchangeService.allExchanges()) {
               list.add(ex.getIntoCurrency());
           }

           //Подсчет количества каждой уникальной валюты
           Map<String, Integer> hm = new HashMap<>();
           Integer count;
           for (String s : list) {
               count = hm.get(s);
               hm.put(s, count == null ? 1 : count + 1);

           }

           //Сортируем по убыванию
           hm = sortByValues(hm);


           objectNode.putPOJO("currency", hm);
       }




       return objectNode;
   }


   /*Метод возвращает rate вводимой пользователем валюты к ДОЛЛАРУ.
   Для того, что бы объем валюты,вводимая пользователем, всегда сохранялась в БД в долларах
   для дальнейшей работы (отражение статистики в долларах).
    */
   public double getUSDRate (String fromCurrency) throws IOException, ParseException {
       JSONParser jsonParser = new JSONParser();
       URL currencyUrl = new URL("https://api.exchangeratesapi.io/latest?symbols=USD" +"&base=" + fromCurrency);
       BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(currencyUrl.openConnection().getInputStream()));
       JSONObject jsonObject = (JSONObject) jsonParser.parse(bufferedReader);
       bufferedReader.close();

       // обрабатываем структуру в объекте
       JSONObject structure = (JSONObject) jsonObject.get("rates");

       return (double) structure.get("USD");
   }

   //Метод по сортировки Map
    public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
        Comparator<K> valueComparator = (k1, k2) -> map.get(k2).compareTo(map.get(k1));
        Map<K, V> sortedByValues = new TreeMap<>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }

}

