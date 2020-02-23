package pl.michal.pacholczyk.restconsumer.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import pl.michal.pacholczyk.restconsumer.common.enums.CurrencyCode;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoldPriceInForeignCurrencyDto extends GoldPriceDto {

    private CurrencyCode code;
}
