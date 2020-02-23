package pl.michal.pacholczyk.restconsumer.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import pl.michal.pacholczyk.restconsumer.common.enums.CurrencyCode;
import pl.michal.pacholczyk.restconsumer.common.enums.TableCode;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyDto {

    private TableCode table;

    private String currency;

    private CurrencyCode code;

    private List<ExchangeRateDto> rates;

}
