# Opis projektu
Aplikacja to usługa sieciowa wystawiająca publiczne API, 
służące do operowania na danych dostarczanych przez zewnętrzy serwer (http://api.nbp.pl/en.html).
Aplikacja została zbudowana z wykorzystaniem frameworku Spring / Spring Boot. 
Kod aplikacji został pokryty testami jednostkowymi i itegracyjnymi.

### Opis API
Web service posiada 4 końcówki.
Odpowiedzi zwracane są w formacie JSON. Lista końcówek::
* <b>/currency/analyze/usd</b>
* <b>/currency/{currency}/{startDate}/{endDate}</b>
* <b>/gold-price/{currency}</b>
* <b>/top-gold-price/{startDate}/{endDate}</b>

API zostało szczegółowo udokumentowane przy pomocy narzędzia <b>Swagger</b>, pod końcówką 
```
/swagger-ui.html
```
znajduje się opis poszczególnych end-pointów 
oraz typów dozolonych zapytań Http i parametrów. Z poziomu Swaggera można również przetestować API 
(przycisk "Try it out" w sekcji żadania)

### Uruchamianie
Usługa została wykonana z użyciem narzędzia automatyzującego budowę Maven. 
Aby zbudować kod należy z pomiomu lokalizacji projektu wywołać komendę:
```
mvm clean install
```
a następnie uruchomić serwer przy pomocy komendy
```
mvn spring-boot:run
```
Serwer staruje domyślnie na porcie sieciowym <b>8080</b>
