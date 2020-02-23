package pl.michal.pacholczyk.restconsumer.restclient.nbpconnector;

import pl.michal.pacholczyk.restconsumer.common.dto.CurrencyDto;
import pl.michal.pacholczyk.restconsumer.common.dto.GoldPriceDto;
import pl.michal.pacholczyk.restconsumer.common.enums.CurrencyCode;
import pl.michal.pacholczyk.restconsumer.common.enums.TableCode;

import java.time.LocalDate;
import java.util.List;

public interface NbpConnector {

    List<GoldPriceDto> getCurrentGoldPrice();

    List<GoldPriceDto> getGoldPriceBetweenDates(LocalDate startDate, LocalDate endDate);

    CurrencyDto getCurrencyExchangeRate(CurrencyCode currencyCode, TableCode tableCode);

    CurrencyDto getCurrencyExchangeRateFromGivenPeriod(CurrencyCode currencyCode, TableCode tableCode, LocalDate startDate, LocalDate endDate);

}
