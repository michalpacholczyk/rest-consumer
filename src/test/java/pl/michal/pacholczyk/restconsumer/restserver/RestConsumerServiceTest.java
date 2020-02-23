package pl.michal.pacholczyk.restconsumer.restserver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.michal.pacholczyk.restconsumer.common.dto.*;
import pl.michal.pacholczyk.restconsumer.common.enums.CurrencyCode;
import pl.michal.pacholczyk.restconsumer.common.enums.TableCode;
import pl.michal.pacholczyk.restconsumer.common.exception.CustomRequestException;
import pl.michal.pacholczyk.restconsumer.restclient.nbpconnector.NbpConnector;
import pl.michal.pacholczyk.restconsumer.restserver.service.impl.RestConsumerServiceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RestConsumerServiceTest {

    @Mock
    private NbpConnector nbpConnector;

    @InjectMocks
    private RestConsumerServiceImpl serviceUnderTest;

    @Test
    public void analyzeUsdExchangeRatesBetweenDatesTest() {

        //given
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate midDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();

        ExchangeRateDto mockRate1 = new ExchangeRateDto();
        mockRate1.setAsk(new BigDecimal("2.10"));
        mockRate1.setBid(new BigDecimal("2.15"));
        mockRate1.setData(startDate);

        ExchangeRateDto mockRate2 = new ExchangeRateDto();
        mockRate2.setAsk(new BigDecimal("2.15"));
        mockRate2.setBid(new BigDecimal("2.10"));
        mockRate2.setData(midDate);

        ExchangeRateDto mockRate3 = new ExchangeRateDto();
        mockRate3.setAsk(new BigDecimal("2.20"));
        mockRate3.setBid(new BigDecimal("2.30"));
        mockRate3.setData(endDate);

        CurrencyDto mockCurrency = new CurrencyDto();
        mockCurrency.setCode(CurrencyCode.USD);
        mockCurrency.setRates(Arrays.asList(mockRate1, mockRate2, mockRate3));

        given(nbpConnector.getCurrencyExchangeRateFromGivenPeriod(CurrencyCode.USD, TableCode.C, startDate, endDate)).willReturn(mockCurrency);

        //when
        AnalyzeExchangeRateResponseDto result = serviceUnderTest.analyzeUsdExchangeRatesBetweenDates(startDate);

        //then
        assertEquals(new BigDecimal("0.10"), result.getAskPriceDiff());
        assertEquals(new BigDecimal("0.15"), result.getBidPriceDiff());
        assertEquals(3, result.getRates().size());
        assertEquals(CurrencyCode.USD, result.getCode());
    }

    @Test
    public void analyzeUsdExchangeRatesBetweenDatesTest_shouldThrowException() {

        LocalDate startDate = LocalDate.now().plusDays(1);

        assertThrows(CustomRequestException.class, () -> {
            serviceUnderTest.analyzeUsdExchangeRatesBetweenDates(startDate);
        });
    }

    @Test
    public void getHighestGoldPriceBetweenDatesTest() {

        //given
        BigDecimal cena = new BigDecimal("2.15");
        LocalDate startDate = LocalDate.of(2020,01, 28);
        LocalDate data = LocalDate.of(2020,01, 29);
        LocalDate endDate = LocalDate.of(2020,01, 30);

        List<GoldPriceDto> mockList = new ArrayList<>();

        GoldPriceDto mockDto1 = new GoldPriceDto();
        mockDto1.setCena(new BigDecimal("2.00"));
        mockDto1.setData(startDate);

        GoldPriceDto mockDto2 = new GoldPriceDto();
        mockDto2.setCena(cena);
        mockDto2.setData(data);

        GoldPriceDto mockDto3 = new GoldPriceDto();
        mockDto3.setCena(new BigDecimal("2.10"));
        mockDto3.setData(endDate);

        mockList.add(mockDto1);
        mockList.add(mockDto2);
        mockList.add(mockDto3);

        given(nbpConnector.getGoldPriceBetweenDates(startDate, endDate)).willReturn(mockList);

        //when
        GoldPriceDto result = serviceUnderTest.getHighestGoldPriceBetweenDates(startDate, endDate);

        //then
        assertEquals(cena, result.getCena());
        assertEquals(data, result.getData());
    }

    @Test
    public void getHighestGoldPriceBetweenDatesTest_shouldThrowException() {

        LocalDate startDate = LocalDate.of(2020,01, 28);
        LocalDate endDate = LocalDate.of(2020,01, 29);

        assertThrows(CustomRequestException.class, () -> {
            serviceUnderTest.getHighestGoldPriceBetweenDates(endDate, startDate);
        });
    }

    @Test
    public void getCurrentGoldPriceInForeignCurrencyTest() {

        //given
        BigDecimal goldPriceInPLN = new BigDecimal("2.00");
        BigDecimal exchangeRate = new BigDecimal("3.00");
        BigDecimal goldPriceInForeignCurrency = goldPriceInPLN.divide(exchangeRate, 3, RoundingMode.CEILING);

        GoldPriceDto mockDto1 = new GoldPriceDto();
        mockDto1.setCena(goldPriceInPLN);
        mockDto1.setData(LocalDate.now());
        List<GoldPriceDto> mockList = Arrays.asList(mockDto1);

        ExchangeRateDto mockRate = new ExchangeRateDto();
        mockRate.setMid(exchangeRate);
        CurrencyDto mockCurrency = new CurrencyDto();
        mockCurrency.setCode(CurrencyCode.USD);
        mockCurrency.setRates(Arrays.asList(mockRate));

        given(nbpConnector.getCurrencyExchangeRate(CurrencyCode.USD, TableCode.A)).willReturn(mockCurrency);
        given(nbpConnector.getCurrentGoldPrice()).willReturn(mockList);

        //when
        GoldPriceInForeignCurrencyDto result = serviceUnderTest.getCurrentGoldPriceInForeignCurrency("USD");

        //then
        assertEquals(goldPriceInForeignCurrency, result.getCena());
        assertEquals(CurrencyCode.USD, result.getCode());
    }

    @Test
    public void findBestDayToBuyCurrencyTest() {

        //given
        BigDecimal bestExchangeRate = new BigDecimal("2.15");
        LocalDate startDate = LocalDate.of(2020,01, 28);
        LocalDate data = LocalDate.of(2020,01, 29);
        LocalDate endDate = LocalDate.of(2020,01, 30);

        ExchangeRateDto mockRate1 = new ExchangeRateDto();
        mockRate1.setAsk(new BigDecimal("2.10"));
        mockRate1.setData(startDate);

        ExchangeRateDto mockRate2 = new ExchangeRateDto();
        mockRate2.setAsk(bestExchangeRate);
        mockRate2.setData(data);

        ExchangeRateDto mockRate3 = new ExchangeRateDto();
        mockRate3.setAsk(new BigDecimal("2.10"));
        mockRate3.setData(endDate);

        CurrencyDto mockCurrency = new CurrencyDto();
        mockCurrency.setCode(CurrencyCode.USD);
        mockCurrency.setRates(Arrays.asList(mockRate1, mockRate2, mockRate3));

        given(nbpConnector.getCurrencyExchangeRateFromGivenPeriod(CurrencyCode.USD, TableCode.C, startDate, endDate)).willReturn(mockCurrency);

        //when
        ExchangeRateDto result = serviceUnderTest.findBestDayToBuyCurrency("USD", startDate, endDate);

        //then
        assertEquals(bestExchangeRate, result.getAsk());
        assertEquals(data, result.getData());
    }

    @Test
    public void findBestDayToBuyCurrencyTest_shouldThrowException() {

        LocalDate startDate = LocalDate.of(2020,01, 28);
        LocalDate endDate = LocalDate.of(2020,01, 29);

        assertThrows(CustomRequestException.class, () -> {
            serviceUnderTest.findBestDayToBuyCurrency("USDD", endDate, startDate);
        });
    }

}
