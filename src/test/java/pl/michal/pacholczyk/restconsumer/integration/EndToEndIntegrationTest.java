package pl.michal.pacholczyk.restconsumer.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.MatchType;
import org.mockserver.model.Header;
import org.mockserver.model.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.michal.pacholczyk.restconsumer.common.enums.CurrencyCode;

import java.time.LocalDate;

import static io.netty.handler.codec.rtsp.RtspHeaderNames.CONTENT_TYPE;
import static org.hamcrest.core.Is.is;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class EndToEndIntegrationTest {

    private static final int MOCK_PORT = 1080;
    private static final String LOCALHOST = "127.0.0.1";
    private static final String CUURENCY_EXCHANGE_RATE_URI = "/api/exchangerates/rates/A/USD";
    private static final String CUURENCY_EXCHANGE_RATE_BETWEEN_DATES_BASE_URI = "/api/exchangerates/rates/C/USD/";
    private static final String CUURENCY_EXCHANGE_RATE_BETWEEN_DATES_URI = "/api/exchangerates/rates/C/USD/2020-02-19/2020-02-21";
    private static final String GOLD_PRICE_URI = "/api/cenyzlota";
    private static final String GOLD_PRICE_BETWEEN_DATES_URI = "/api/cenyzlota/2020-02-19/2020-02-21";
    private static final LocalDate START_DATE = LocalDate.of(2020,02, 19);
    private static final LocalDate END_DATE = LocalDate.of(2020,02, 21);

    private static final Parameter FORMAT_JSON = new Parameter("format", "json");

    @Autowired
    private MockMvc mvc;

    private static ClientAndServer mockServer;

    @BeforeClass
    public static void startServer() {
        mockServer = startClientAndServer(1080);
    }

    @AfterClass
    public static void stopServer() {
        mockServer.stop();
    }

    @Test
    public void analyzeUsdExchangeRatesBetweenDatesEndToEndTest() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        LocalDate startDate = LocalDate.now().minusDays(2);

        analyzeUsdExchangeRatesBetweenDates_mockServerResponse(startDate);

        mvc.perform(post("/currency/analyze/usd")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(startDate.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.askPriceDiff", is(0.05)))
                .andExpect(jsonPath("$.bidPriceDiff", is(-0.05)));
    }

    @Test
    public void getHighestGoldPriceBetweenDatesEndToEndTest() throws Exception {

        getGoldPriceBetweenDates_mockServerResponse();

        mvc.perform(get("/top-gold-price/{startDate}/{endDate}", START_DATE, END_DATE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cena", is(215.00)))
                .andExpect(jsonPath("$.data", is("2020-02-20")));
    }

    @Test
    public void getCurrentGoldPriceInForeignCurrencyEndToEndTest() throws Exception {

        getCurrentGoldPrice_mockServerResponse();
        getCurrencyExchangeRate_mockServerResponse();

        mvc.perform(get("/gold-price/{currency}", CurrencyCode.USD.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(CurrencyCode.USD.toString())))
                .andExpect(jsonPath("$.cena", is(0.50)))
                .andExpect(jsonPath("$.data", is("2020-02-21")));
    }

    @Test
    public void findBestDayToBuyCurrencyEndToEndTest() throws Exception {

        getCurrencyExchangeRateFromGivenPeriod_mockServerResponse();

        mvc.perform(get("/currency/{currency}/{startDate}/{endDate}", CurrencyCode.USD.toString(), START_DATE, END_DATE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ask", is(2.15)))
                .andExpect(jsonPath("$.effectiveDate", is("2020-02-20")));
    }

    private void analyzeUsdExchangeRatesBetweenDates_mockServerResponse(LocalDate startDate) {
        new MockServerClient(LOCALHOST, MOCK_PORT).when(
                request()
                        .withPath(CUURENCY_EXCHANGE_RATE_BETWEEN_DATES_BASE_URI + startDate.toString() + "/" + LocalDate.now().toString())
                        .withQueryStringParameter(FORMAT_JSON)
        ).respond(
                response()
                        .withHeaders(new Header(CONTENT_TYPE.toString(), "application/json"))
                        .withBody(json("{"
                                        + "\"table\":\"C\","
                                        + "\"currency\":\"dolar amerykański\","
                                        + "\"code\":\"USD\","
                                        + "\"rates\":"
                                        + "["
                                        + "{"
                                        + "\"no\":\"034/C/NBP/2020\","
                                            + "\"effectiveDate\": \"" + startDate.toString() + "\","
                                            + "\"bid\":2.15,"
                                            + "\"ask\":2.10"
                                        + "},"
                                        + "{"
                                            + "\"no\":\"035/C/NBP/2020\","
                                            + "\"effectiveDate\": \"" + startDate.plusDays(1).toString() + "\","
                                            + "\"bid\":3.9200,"
                                            + "\"ask\":2.15"
                                        + "},"
                                        + "{"
                                            + "\"no\":\"036/C/NBP/2020\","
                                            + "\"effectiveDate\": \"" + startDate.plusDays(2).toString() + "\","
                                            + "\"bid\":2.10,"
                                            + "\"ask\":2.15"
                                        + "}"
                                        + "]"
                                        + "}",
                                MatchType.STRICT
                        ))
        );
    }

    private void getCurrentGoldPrice_mockServerResponse() {
        new MockServerClient(LOCALHOST, MOCK_PORT).when(
                request()
                        .withPath(GOLD_PRICE_URI)
                        .withQueryStringParameter(FORMAT_JSON)
        ).respond(
                response()
                        .withHeaders(new Header(CONTENT_TYPE.toString(), "application/json"))
                        .withBody(json("{" + System.lineSeparator() +
                                        "    \"data\": \"2020-02-21\"," + System.lineSeparator() +
                                        "    \"cena\": 2.00" + System.lineSeparator() +
                                        "}",
                                MatchType.STRICT
                        ))
        );
    }

    private void getGoldPriceBetweenDates_mockServerResponse() {
        new MockServerClient(LOCALHOST, MOCK_PORT).when(
                request()
                        .withPath(GOLD_PRICE_BETWEEN_DATES_URI)
                        .withQueryStringParameter(FORMAT_JSON)
        ).respond(
                response()
                        .withHeaders(new Header(CONTENT_TYPE.toString(), "application/json"))
                        .withBody(json("["
                                        + "{"
                                            + "\"data\":\"2020-02-19\","
                                            + "\"cena\":200.00"
                                        + "},"
                                        + "{"
                                            + "\"data\":\"2020-02-20\","
                                            + "\"cena\":215.00"
                                        + "},"
                                        + "{"
                                            + "\"data\":\"2020-02-21\","
                                            + "\"cena\":210.00"
                                        + "}"
                                        + "]",
                                MatchType.STRICT
                        ))
        );
    }

    private void getCurrencyExchangeRate_mockServerResponse() {
        new MockServerClient(LOCALHOST, MOCK_PORT).when(
                request()
                        .withPath(CUURENCY_EXCHANGE_RATE_URI)
                        .withQueryStringParameter(FORMAT_JSON)
        ).respond(
                response()
                        .withHeaders(new Header(CONTENT_TYPE.toString(), "application/json"))
                        .withBody(json("{"
                                        + "\"table\":\"A\","
                                        + "\"currency\":\"dolar amerykański\","
                                        + "\"code\":\"USD\","
                                        + "\"rates\":[{"
                                            + "\"no\":\"036/A/NBP/2020\","
                                            + "\"effectiveDate\":\"2020-02-21\","
                                            + "\"mid\":4.00"
                                        + "}]"
                                        + "}",
                                MatchType.STRICT
                        ))
        );
    }

    private void getCurrencyExchangeRateFromGivenPeriod_mockServerResponse() {
        new MockServerClient(LOCALHOST, MOCK_PORT).when(
                request()
                        .withPath(CUURENCY_EXCHANGE_RATE_BETWEEN_DATES_URI)
                        .withQueryStringParameter(FORMAT_JSON)
        ).respond(
                response()
                        .withHeaders(new Header(CONTENT_TYPE.toString(), "application/json"))
                        .withBody(json("{"
                                        + "\"table\":\"C\","
                                        + "\"currency\":\"dolar amerykański\","
                                        + "\"code\":\"USD\","
                                        + "\"rates\":"
                                        + "["
                                        + "{"
                                        + "\"no\":\"034/C/NBP/2020\","
                                            + "\"effectiveDate\":\"2020-02-19\","
                                            + "\"bid\":3.9148,"
                                            + "\"ask\":1.9938"
                                        + "},"
                                        + "{"
                                            + "\"no\":\"035/C/NBP/2020\","
                                            + "\"effectiveDate\":\"2020-02-20\","
                                            + "\"bid\":3.9200,"
                                            + "\"ask\":2.15"
                                        + "},"
                                        + "{"
                                            + "\"no\":\"036/C/NBP/2020\","
                                            + "\"effectiveDate\":\"2020-02-21\","
                                            + "\"bid\":3.9145,"
                                            + "\"ask\":1.9935"
                                        + "}"
                                        + "]"
                                        + "}",
                                MatchType.STRICT
                        ))
        );
    }

}
