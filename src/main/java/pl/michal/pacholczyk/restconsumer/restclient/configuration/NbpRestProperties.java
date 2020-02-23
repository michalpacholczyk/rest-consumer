package pl.michal.pacholczyk.restconsumer.restclient.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.springframework.web.util.UriComponentsBuilder.newInstance;

@Getter
@Setter
@ConfigurationProperties(prefix = "connector.nbp")
public class NbpRestProperties {

    private String scheme;
    private String host;
    private String port;
    private String contextPath;
    private String goldPriceBaseURI;
    private String exchangeRatesBaseURI;

    public String getBaseURL() {
        return newInstance().scheme(scheme).host(host).port(port).path(contextPath)
                .build().encode().toUri().toString();
    }

}
