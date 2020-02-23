package pl.michal.pacholczyk.restconsumer.restserver.controller;

import io.swagger.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.michal.pacholczyk.restconsumer.common.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Validated
public interface RestGateway {

    @ApiOperation(value = "Service analyze USD exchange rates (ask and bid) between given and current date",
            response = AnalyzeExchangeRateResponseDto.class)
    @PostMapping("/currency/analyze/usd")
    AnalyzeExchangeRateResponseDto analyzeUsdExchangeRatesBetweenDates(
            @ApiParam(required = true,
                    name = "Start date",
                    value = "Start date in format yyyy-MM-dd, cannot be current date (at least to days before current date), before 2013-01-02 or in a future.")
            @NotNull @RequestBody @Valid @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate);

    @ApiOperation(value = "Get highest gold price between given dates.", response = GoldPriceDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startDate", value = "Start date in format yyyy-MM-dd, cannot be before 2013-01-02 or in future", required = true, paramType = "path", defaultValue="2020-01-01"),
            @ApiImplicitParam(name = "endDate", value = "End date in format yyyy-MM-dd, cannot be in future, before startDate and ealier than 1 year after startDate,", required = true, paramType = "path", defaultValue="2020-02-01")
    })
    @GetMapping("/top-gold-price/{startDate}/{endDate}")
    GoldPriceDto getHighestGoldPriceBetweenDates(@PathVariable("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                 @PathVariable("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate);

    @ApiOperation(value = "Get current gold price in given USD, CAD, EUR, CHF currency.", response = GoldPriceInForeignCurrencyDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currency", value = "Currency code in ISO 4217 standard, available option are USD, CAD, EUR, CHF (case insensitive)", required = true, paramType = "path", defaultValue="USD")
    })
    @GetMapping("/gold-price/{currency}")
    GoldPriceInForeignCurrencyDto getCurrentGoldPriceInForeignCurrency(@PathVariable("currency") @Size(max = 3, message = "Currency code cannot be longer tha 3 chars!") String currencyCode);

    @ApiOperation(value = "Get date with lowest ask exchange rate from given period.", response = ExchangeRateDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currency", value = "Currency code in ISO 4217 standard, available option are USD, CAD, EUR, CHF (case insensitive)", required = true, paramType = "path", defaultValue="USD"),
            @ApiImplicitParam(name = "startDate", value = "Start date in format yyyy-MM-dd, cannot be before 2013-01-02 or in future", required = true, paramType = "path", defaultValue="2020-01-01"),
            @ApiImplicitParam(name = "endDate", value = "End date in format yyyy-MM-dd, cannot be in future, before startDate and ealier than 1 year after startDate,", required = true, paramType = "path", defaultValue="2020-02-01")
    })
    @GetMapping("/currency/{currency}/{startDate}/{endDate}")
    ExchangeRateDto findBestDayToBuyCurrency(@PathVariable("currency") @Size(max = 3, message = "Currency code cannot be longer tha 3 chars!") String currencyCode,
                                                   @PathVariable("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                   @PathVariable("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate);

}
