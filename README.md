## Haskell JavaFX MARC Parser

**MARC** je skraćenica za Machine-Readable Cataloging i predstavlja kolekciju različitih standarda za predstavljanje metapodataka knjiga, časopisa, elektronskih izvora,... Metapodaci o jednoj knjizi predstavljaju se jednim bibliografskim zapisom koji se sastoji od skupa polja i potpolja i još nekih elemenata. Zapisi se mogu pojaviti u različitim reprezentacijama. Parser koji se implementira u okviru projekta treba da omogući parsiranje različitih varijanti MARC formata i različitih načina reprezentacije tih formata.

Ulaz u parser je fajl koji sadrži jedan ili više MARC zapisa kreiranih prema nekom od MARC formata, a rezultat parsiranja je JSON fajl koji sadrži struktuirane MARC zapise u json formatu.
(Primeri podrzanih MARC formata se nalaze u folderu Files for parsing).
Alternativni ulaz u parser je jedna linija MARC teksta koja nakon parsiranja rezultira u json ispisu prepoznatog elementa MARC zapisa


### Elementi zapisa:

#### Tipovi polja:
- Leader
- Kontrolno polje (ControlField)
- Data polje (DataField)

#### Ceo MARC zapis:
- Leader (Opciono)
- Lista kontrolnih polja (Opciono)
- Lista data polja

### Struktura elemenata zapisa:
**Leader**:
- field::String

**ControlField**:
- tag::String
- field::String

**DataField**:
- tag::String
- subfields::List<SubField>

    **SubField**:
    - tag::String
    - field::String
    - ind1::String
    - ind2::String


**Primer jedne linije MARC zapisa za sva 3 tipa**:
- **Leader**: LDR 01142cam22003014500
- **ControlField**: 003 DLC
- **DataField**: 020 ## $a0152038655 :$c$15.95

### Opis rada projekta
JavaFX prozor omogucava unos tekstualnog fajla ili jedne linije MARC teksta, zatim
unos salje u JSON formatu ka Haskell REST serveru (na portu 3000). Haskell server parsira unos, i vraca rezultat u istom tom json formatu, koji u telu poruke sadrzi parsiran JSON MARC fajl. Parsiran fajl se zatim prikazuje u JavaFX prozoru.


**Format JSON poruke** koja sluzi za komunikaciju sa haskell serverom je JSON fajl (JSON Message) sa strukturom:

**JSON Message**:
```json
{"name":"String", "mbody":"String"}
```


### REST putanje haskell servera:

**POST  ::  /parse/line** 

- Prima JSON Message sa tekstom unosa jedne linije MARC zapisa u mbody polju
- Vraca JSON Message sa parsiranom linijom ulaza u mbody polju

**POST  ::  /parse/file** 

- Prima JSON Message sa tekstom celog MARC zapisa u mbody polju
- Vraca JSON Message sa parsiranim JSON MARC zapisom u mbody polju


#### Primer /parse/line ulaza i izlaza:
**Ulaz**: 
```json
    {"name":"ParseLine",
    "mbody":"LDR 01142cam22003014500\n"}
```
**Izlaz**:  
```json
    {"name": "PARSED MARCLine"
    "mbody": "{'type':'LEADER', 'data':{'leader': '01142cam22003014500'}"}
```
#### Primer /parse/file ulaza i izlaza:
**Ulaz**: 
```json
    {"name":"ParseFile",
    "mbody":"LDR 01142cam22003014500\n"
            "003 DLC\n"
            "005 19930521155141.9\n"
            "008 920219s1993 caua j 000 0 eng 010 ## $a92005291\n"
            "020 ## $a0152038655 :$c$15.95\n"}
```
**Izlaz**:  
```json
    {"name": "PARSED MARC"
    "mbody": MARC JSON}
```
**MARC JSON**:
```json
    {"leader": "01142cam22003014500",
    "fields": [{"001":"92005291"},
    {"003":"DLC"},
    {"005":"19930521155141.9"},
    {"008":"920219s1993 caua j 000 0 eng 010 ## $a92005291"},
    {"020":{"subfields":[{"a":"0152038655 :"},
    {"c":""},
    {"1":"5.95"}],
    "ind1":" ",
    "ind2":" "}}]}
```

#### Dodatni Komentari:
- JavaFX deo projekta se naziva FXForm, koristi Maven build tool
- Haskell deo projekta koristi cabal kao build tool, projekat se startuje komandom: cabal run
- Haskell deo projekta sadrzi modul Parser koji vrsi sve funkcije parsiranja i modul Main koji se bavi funkcijama REST servera

#### Primer rada FXForm-a

#### Prazan prozor

<img src="Resources/Empty Window.png" title="Empty Window">

#### Parsiranje linije

<img src="Resources/Line Parsing.png" title="Line Parsing">

#### Parsiranje fajla

<img src="Resources/File Parsing.png" title="File Parsing">