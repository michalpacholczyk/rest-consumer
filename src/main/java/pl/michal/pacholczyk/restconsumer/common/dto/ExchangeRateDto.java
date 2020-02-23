package pl.michal.pacholczyk.restconsumer.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class ExchangeRateDto {

    @JsonFormat(pattern="yyyy-MM-dd")
    @JsonProperty("effectiveDate")
    private LocalDate data;

    private BigDecimal mid;

    private BigDecimal bid;

    private BigDecimal ask;
}
