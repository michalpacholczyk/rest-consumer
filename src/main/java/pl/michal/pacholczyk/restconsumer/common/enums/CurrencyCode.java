package pl.michal.pacholczyk.restconsumer.common.enums;

import java.util.Optional;

public enum CurrencyCode {
    USD, CAD, EUR, CHF;

    public static Optional<CurrencyCode> parseCode(String givenStr) {
        for (CurrencyCode code : CurrencyCode.values()) {
            if (code.toString().equals(givenStr.toUpperCase())) {
                return Optional.ofNullable(code);
            }
        }
        return Optional.empty();
    }
}
