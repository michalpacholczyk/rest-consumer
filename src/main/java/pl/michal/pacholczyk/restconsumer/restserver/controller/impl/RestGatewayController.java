package pl.michal.pacholczyk.restconsumer.restserver.controller.impl;

import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestController;
import pl.michal.pacholczyk.restconsumer.common.dto.*;
import pl.michal.pacholczyk.restconsumer.restserver.controller.RestGateway;
import pl.michal.pacholczyk.restconsumer.restserver.service.RestConsumerService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@RestController
@AllArgsConstructor
public class RestGatewayController implements RestGateway {

    private RestConsumerService restConsumerService;

    @Override
    public AnalyzeExchangeRateResponseDto analyzeUsdExchangeRatesBetweenDates(LocalDate startDate) {
        return restConsumerService.analyzeUsdExchangeRatesBetweenDates(startDate);
    }

    @Override
    public GoldPriceDto getHighestGoldPriceBetweenDates(LocalDate startDate, LocalDate endDate) {
        return restConsumerService.getHighestGoldPriceBetweenDates(startDate, endDate);
    }

    @Override
    public GoldPriceInForeignCurrencyDto getCurrentGoldPriceInForeignCurrency(String currencyCode) {
        return restConsumerService.getCurrentGoldPriceInForeignCurrency(currencyCode);
    }

    @Override
    public ExchangeRateDto findBestDayToBuyCurrency(String currencyCode, LocalDate startDate, LocalDate endDate) {
        return restConsumerService.findBestDayToBuyCurrency(currencyCode, startDate, endDate);
    }

}
