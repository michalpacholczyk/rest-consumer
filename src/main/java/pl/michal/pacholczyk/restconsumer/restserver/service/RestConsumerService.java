package pl.michal.pacholczyk.restconsumer.restserver.service;

import pl.michal.pacholczyk.restconsumer.common.dto.*;

import java.time.LocalDate;

public interface RestConsumerService {

    AnalyzeExchangeRateResponseDto analyzeUsdExchangeRatesBetweenDates(LocalDate startDate);

    GoldPriceDto getHighestGoldPriceBetweenDates(LocalDate startDate, LocalDate endDate);

    GoldPriceInForeignCurrencyDto getCurrentGoldPriceInForeignCurrency(String currencyCode);

    ExchangeRateDto findBestDayToBuyCurrency(String currencyCode, LocalDate startDate, LocalDate endDate);
}
