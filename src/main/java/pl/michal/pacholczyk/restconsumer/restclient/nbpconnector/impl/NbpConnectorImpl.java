package pl.michal.pacholczyk.restconsumer.restclient.nbpconnector.impl;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.michal.pacholczyk.restconsumer.common.dto.CurrencyDto;
import pl.michal.pacholczyk.restconsumer.common.dto.GoldPriceDto;
import pl.michal.pacholczyk.restconsumer.common.enums.CurrencyCode;
import pl.michal.pacholczyk.restconsumer.common.enums.TableCode;
import pl.michal.pacholczyk.restconsumer.common.exception.CustomInternalException;
import pl.michal.pacholczyk.restconsumer.restclient.configuration.NbpRestProperties;
import pl.michal.pacholczyk.restconsumer.restclient.nbpconnector.NbpConnector;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class NbpConnectorImpl implements NbpConnector {

    private static final String FORMAT = "format";
    private static final String JSON = "json";

    private WebClient webClient;
    private NbpRestProperties restProperties;

    @Override
    public List<GoldPriceDto> getCurrentGoldPrice() {
        return webClient.get()
                .uri(builder -> builder
                        .path(restProperties.getGoldPriceBaseURI())
                        .queryParam(FORMAT, JSON)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, resp -> Mono.error(new CustomInternalException()))
                .onStatus(HttpStatus::is5xxServerError, resp -> Mono.error(new CustomInternalException()))
                .bodyToFlux(GoldPriceDto.class)
                .collectList()
                .block();
    }

    @Override
    public List<GoldPriceDto> getGoldPriceBetweenDates(LocalDate startDate, LocalDate endDate) {
        return webClient.get()
                .uri(builder -> builder
                        .path(restProperties.getGoldPriceBaseURI())
                        .path("/{startDate}/{endDate}")
                        .queryParam(FORMAT, JSON)
                        .build(startDate, endDate))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, resp -> Mono.error(new CustomInternalException()))
                .onStatus(HttpStatus::is5xxServerError, resp -> Mono.error(new CustomInternalException()))
                .bodyToFlux(GoldPriceDto.class)
                .collectList()
                .block();
    }

    @Override
    public CurrencyDto getCurrencyExchangeRate(CurrencyCode currencyCode, TableCode tableCode) {
        return webClient.get()
                .uri(builder -> builder
                        .path(restProperties.getExchangeRatesBaseURI())
                        .path("/{table}/{code}")
                        .queryParam(FORMAT, JSON)
                        .build(tableCode.toString(), currencyCode.toString()))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, resp -> Mono.error(new CustomInternalException()))
                .onStatus(HttpStatus::is5xxServerError, resp -> Mono.error(new CustomInternalException()))
                .bodyToMono(CurrencyDto.class)
                .block();
    }

    @Override
    public CurrencyDto getCurrencyExchangeRateFromGivenPeriod(CurrencyCode currencyCode, TableCode tableCode, LocalDate startDate, LocalDate endDate) {
        return webClient.get()
                .uri(builder -> builder
                        .path(restProperties.getExchangeRatesBaseURI())
                        .path("/{table}/{code}/{startDate}/{endDate}")
                        .queryParam(FORMAT, JSON)
                        .build(tableCode, currencyCode, startDate, endDate))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, resp -> Mono.error(new CustomInternalException()))
                .onStatus(HttpStatus::is5xxServerError, resp -> Mono.error(new CustomInternalException()))
                .bodyToMono(CurrencyDto.class)
                .block();
    }

}
