package pl.michal.pacholczyk.restconsumer.restserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.michal.pacholczyk.restconsumer.common.dto.AnalyzeExchangeRateResponseDto;
import pl.michal.pacholczyk.restconsumer.common.dto.ExchangeRateDto;
import pl.michal.pacholczyk.restconsumer.common.dto.GoldPriceDto;
import pl.michal.pacholczyk.restconsumer.common.dto.GoldPriceInForeignCurrencyDto;
import pl.michal.pacholczyk.restconsumer.common.enums.CurrencyCode;
import pl.michal.pacholczyk.restconsumer.restserver.controller.impl.RestGatewayController;
import pl.michal.pacholczyk.restconsumer.restserver.service.RestConsumerService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestGatewayController.class)
public class RestGatewayControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RestConsumerService service;

    @Test
    public void analyzeUsdExchangeRatesBetweenDatesEndpointTest() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();

        ExchangeRateDto mockRate1 = new ExchangeRateDto();
        mockRate1.setAsk(new BigDecimal("2.10"));
        mockRate1.setBid(new BigDecimal("2.15"));
        mockRate1.setData(startDate);

        ExchangeRateDto mockRate2 = new ExchangeRateDto();
        mockRate2.setAsk(new BigDecimal("2.15"));
        mockRate2.setBid(new BigDecimal("2.10"));
        mockRate2.setData(endDate);

        List<ExchangeRateDto> mockList = Arrays.asList(mockRate1, mockRate2);

        AnalyzeExchangeRateResponseDto resultDto = new AnalyzeExchangeRateResponseDto();
        resultDto.setStartDate(startDate);
        resultDto.setEndDate(endDate);
        resultDto.setCode(CurrencyCode.USD);
        resultDto.setAskPriceDiff(mockRate2.getAsk().subtract(mockRate1.getAsk()));
        resultDto.setBidPriceDiff(mockRate2.getBid().subtract(mockRate1.getBid()));
        resultDto.setRates(mockList);

        given(service.analyzeUsdExchangeRatesBetweenDates(startDate)).willReturn(resultDto);

        mvc.perform(post("/currency/analyze/usd")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(startDate.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.askPriceDiff", is(0.05)))
                .andExpect(jsonPath("$.bidPriceDiff", is(-0.05)));
    }

    @Test
    public void getHighestGoldPriceBetweenDatesEndpointTest() throws Exception {

        BigDecimal cena = new BigDecimal("2.15");
        LocalDate data = LocalDate.of(2020,01, 15);
        LocalDate startDate = LocalDate.of(2020,01, 01);
        LocalDate endDate = LocalDate.of(2020,01, 30);

        GoldPriceDto mockDto = new GoldPriceDto();
        mockDto.setCena(cena);
        mockDto.setData(data);

        given(service.getHighestGoldPriceBetweenDates(startDate, endDate)).willReturn(mockDto);

        mvc.perform(get("/top-gold-price/{startDate}/{endDate}", startDate, endDate)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cena", is(2.15)))
                .andExpect(jsonPath("$.data", is("2020-01-15")));
    }

    @Test
    public void getCurrentGoldPriceInForeignCurrencyEndpointTest() throws Exception {

        BigDecimal cena = new BigDecimal("2.15");
        LocalDate data = LocalDate.of(2020,01, 15);

        GoldPriceInForeignCurrencyDto mockDto = new GoldPriceInForeignCurrencyDto();
        mockDto.setCode(CurrencyCode.USD);
        mockDto.setData(data);
        mockDto.setCena(cena);

        given(service.getCurrentGoldPriceInForeignCurrency(CurrencyCode.USD.toString())).willReturn(mockDto);

        mvc.perform(get("/gold-price/{currency}", CurrencyCode.USD.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(CurrencyCode.USD.toString())))
                .andExpect(jsonPath("$.cena", is(2.15)))
                .andExpect(jsonPath("$.data", is("2020-01-15")));
    }

    @Test
    public void findBestDayToBuyCurrencyEndpointTest() throws Exception {

        BigDecimal cena = new BigDecimal("2.15");
        LocalDate data = LocalDate.of(2020,01, 15);
        LocalDate startDate = LocalDate.of(2020,01, 01);
        LocalDate endDate = LocalDate.of(2020,01, 30);

        ExchangeRateDto mockDto = new ExchangeRateDto();
        mockDto.setAsk(cena);
        mockDto.setData(data);

        given(service.findBestDayToBuyCurrency(CurrencyCode.USD.toString(), startDate, endDate)).willReturn(mockDto);

        mvc.perform(get("/currency/{currency}/{startDate}/{endDate}", CurrencyCode.USD.toString(), startDate, endDate)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ask", is(2.15)))
                .andExpect(jsonPath("$.effectiveDate", is("2020-01-15")));
    }

}
