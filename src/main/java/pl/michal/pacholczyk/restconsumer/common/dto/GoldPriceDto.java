package pl.michal.pacholczyk.restconsumer.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoldPriceDto {

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate data;

    private BigDecimal cena;
}
