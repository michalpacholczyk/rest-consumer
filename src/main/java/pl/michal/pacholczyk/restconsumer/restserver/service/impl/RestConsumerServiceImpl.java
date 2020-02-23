package pl.michal.pacholczyk.restconsumer.restserver.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.michal.pacholczyk.restconsumer.common.dto.*;
import pl.michal.pacholczyk.restconsumer.common.enums.CurrencyCode;
import pl.michal.pacholczyk.restconsumer.common.enums.TableCode;
import pl.michal.pacholczyk.restconsumer.common.exception.CustomInternalException;
import pl.michal.pacholczyk.restconsumer.common.exception.CustomRequestException;
import pl.michal.pacholczyk.restconsumer.restclient.nbpconnector.NbpConnector;
import pl.michal.pacholczyk.restconsumer.restserver.service.RestConsumerService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RestConsumerServiceImpl implements RestConsumerService {

    private static final LocalDate GOLD_PRICE_FROM_DATE = LocalDate.of(2013, 1, 2);

    private NbpConnector nbpConnector;

    @Override
    public AnalyzeExchangeRateResponseDto analyzeUsdExchangeRatesBetweenDates(LocalDate startDate) {

        LocalDate endDate = LocalDate.now();
        validateDates(startDate, endDate);

        CurrencyDto currencyDto = nbpConnector.getCurrencyExchangeRateFromGivenPeriod(CurrencyCode.USD, TableCode.C, startDate, endDate);

        if (!currencyDto.getRates().isEmpty()) {
            List<ExchangeRateDto> exchangeRateDtoList = currencyDto.getRates();
            ExchangeRateDto startDateExchangeRateDto = exchangeRateDtoList.get(0);
            ExchangeRateDto endDateExchangeRateDto = exchangeRateDtoList.get(exchangeRateDtoList.size() - 1);

            AnalyzeExchangeRateResponseDto resultDto = new AnalyzeExchangeRateResponseDto();
            resultDto.setStartDate(startDate);
            resultDto.setEndDate(endDate);
            resultDto.setCode(CurrencyCode.USD);
            try {
                resultDto.setAskPriceDiff(endDateExchangeRateDto.getAsk().subtract(startDateExchangeRateDto.getAsk(), new MathContext(4)));
                resultDto.setBidPriceDiff(endDateExchangeRateDto.getBid().subtract(startDateExchangeRateDto.getBid(), new MathContext(4)));
                resultDto.setRates(exchangeRateDtoList);
            } catch (Throwable T) {
                throw new CustomInternalException();
            }
            return resultDto;
        } else {
            throw new CustomInternalException();
        }
    }

    @Override
    public GoldPriceDto getHighestGoldPriceBetweenDates(LocalDate startDate, LocalDate endDate) {
        validateDates(startDate, endDate);
        List<GoldPriceDto> goldPriceDtoList = nbpConnector.getGoldPriceBetweenDates(startDate, endDate);
        return goldPriceDtoList.stream().max(Comparator.comparing(GoldPriceDto::getCena)).get();
    }

    @Override
    public GoldPriceInForeignCurrencyDto getCurrentGoldPriceInForeignCurrency(String currencyCode) {
        CurrencyCode code = validateAndParseCurrencyCode(currencyCode);
        CurrencyDto currencyDto = nbpConnector.getCurrencyExchangeRate(code, TableCode.A);
        List<GoldPriceDto> currentGoldPriceList = nbpConnector.getCurrentGoldPrice();
        GoldPriceDto currentGoldPrice;
        ExchangeRateDto exchangeRateDto;
        BigDecimal calculatedPrice;

        if (currencyCode.isEmpty() || currencyDto.getRates().isEmpty()) {
            throw new CustomInternalException();
        } else {
            try {
                currentGoldPrice = currentGoldPriceList.get(0);
                exchangeRateDto = currencyDto.getRates().get(0);
                calculatedPrice = currentGoldPrice.getCena().divide(exchangeRateDto.getMid(), 3, RoundingMode.CEILING);
            } catch (Throwable T) {
                throw new CustomInternalException();
            }
        }

        GoldPriceInForeignCurrencyDto result = new GoldPriceInForeignCurrencyDto();
        result.setCode(code);
        result.setData(currentGoldPrice.getData());
        result.setCena(calculatedPrice);

        return result;
    }

    @Override
    public ExchangeRateDto findBestDayToBuyCurrency(String currencyCode, LocalDate startDate, LocalDate endDate) {
        validateDates(startDate, endDate);
        CurrencyCode code = validateAndParseCurrencyCode(currencyCode);
        CurrencyDto currencyDto = nbpConnector.getCurrencyExchangeRateFromGivenPeriod(code, TableCode.C, startDate, endDate);

        return currencyDto.getRates().stream()
                .max(Comparator.comparing(ExchangeRateDto::getAsk))
                .get();
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
            throw new CustomRequestException("Given date cannot be in future!");
        }
        if (startDate.isAfter(endDate)) {
            throw new CustomRequestException("Start date cannot be after end date!");
        }
        if (startDate.isEqual(LocalDate.now().minusDays(1))) {
            throw new CustomRequestException("Start date must be at least 2 days before current date!");
        }
        if (startDate.isEqual(endDate)) {
            throw new CustomRequestException("Start date cannot be current date!");
        }
        if (endDate.minusYears(1).isAfter(startDate)) {
            throw new CustomRequestException("Request period cannot be longer than 1 year!");
        }
        if (startDate.isBefore(GOLD_PRICE_FROM_DATE)) {
            throw new CustomRequestException("Start date cannot be before " + GOLD_PRICE_FROM_DATE.toString() + "!" );
        }
    }

    private CurrencyCode validateAndParseCurrencyCode(String givenStr) {
        Optional<CurrencyCode> code = CurrencyCode.parseCode(givenStr);
        if (code.isPresent()) {
            return code.get();
        } else {
            throw new CustomRequestException("Given currency code invalid, please use ISO 4217 standard!");
        }

    }
}
