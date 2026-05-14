# Progetto di compressione immagini con DCT

Progetto Java per lo studio della Trasformata Discreta del Coseno bidimensionale (2D DCT) e del suo utilizzo nella compressione delle immagini.

Il repository contiene due parti strettamente correlate:

1. **Parte 1 — benchmark DCT**
   confronta l’implementazione DCT personalizzata con quella della libreria JTransforms ed esporta i risultati temporali.
2. **Parte 2 — compressione immagini con GUI**
   permette di scegliere un’immagine, configurare i parametri di compressione e comprimere l’immagine con un flusso DCT basato su blocchi.

## Cosa fa l’applicazione

L’applicazione parte da `com.example.Application`, che apre la finestra di scelta della parte da eseguire.

- **Parte 1** avvia un benchmark sulle seguenti dimensioni di matrice:
  `8, 16, 32, 64, 128, 256, 512, 1024, 2048`
- **Parte 2** apre la GUI di compressione immagini con:
  - selezione dell’immagine
  - inserimento dei parametri di compressione
  - anteprima affiancata dell’immagine originale e di quella compressa

## Struttura del progetto

- `src/main/java/com/example/Application.java` — punto di ingresso dell’applicazione
- `src/main/java/com/example/assignment/Part1.java` — logica del benchmark
- `src/main/java/com/example/assignment/Part2.java` — logica di compressione immagini
- `src/main/java/com/example/assignment/BenchmarkConstants.java` — etichette del benchmark, percorsi file e intestazioni CSV
- `src/main/java/com/example/lib/DCT2.java` — implementazione custom di DCT/IDCT basata su EJML
- `src/main/java/com/example/lib/utils/` — utility per CSV, immagini, grafici, timing e array
- `src/main/java/com/example/GUI/` — interfaccia grafica Swing e componenti di styling
- `output/` — grafici generati, file CSV, immagini copiate e file BMP compressi

## Flusso generale

### Avvio

1. `Application.main()` crea `PartChooserWindow`.
2. La finestra di scelta offre due azioni:
   - **Parte 1 — Benchmark DCT**
   - **Parte 2 — Compressione immagini con GUI**
3. La parte selezionata viene avviata dalla finestra di scelta.

### Flusso della Parte 1

Il benchmark:

- genera matrici quadrate casuali
- misura l’implementazione DCT custom e la DCT di JTransforms sugli stessi input
- registra i tempi di esecuzione e il rapporto `Library / MyDCT`
- crea un grafico intitolato `DCT2 Benchmark (My vs Library)`
- esporta un file CSV in `output/times_vs_size.csv`

### Flusso della Parte 2

La GUI di compressione:

- apre una finestra di selezione immagine
- carica l’immagine selezionata nell’applicazione
- apre una finestra per l’inserimento dei parametri `F` e `d`
- comprime l’immagine in background
- mostra le anteprime dell’immagine originale e di quella compressa affiancate
- salva l’immagine compressa in formato BMP in `output/`

## Dettagli del benchmark

### Dimensioni delle matrici

Il benchmark usa le seguenti dimensioni:

```text
8, 16, 32, 64, 128, 256, 512, 1024, 2048
```

### Misurazioni

Per ogni dimensione:

- viene generata una matrice casuale
- la matrice viene copiata in modo che entrambe le implementazioni ricevano gli stessi dati
- la DCT custom viene misurata su `EJML SimpleMatrix`
- la DCT della libreria viene misurata con `DoubleDCT_2D`
- il risultato viene convertito da secondi a millisecondi per il grafico e per l’esportazione CSV

### File di output del benchmark

- CSV: `output/times_vs_size.csv`
- Grafico: generato tramite `PlotUtils.plotDCTBenchmark(...)`

Il CSV contiene le colonne:

- `Size`
- `MyDCTTime (ms)`
- `LibDCTTime (ms)`
- `Ratio (Lib/My)`

## Dettagli della compressione

### Parametri

La finestra di compressione richiede due interi:

- `F` — dimensione del blocco
- `d` — parametro di cutoff delle frequenze

Regole di validazione:

- `F >= 0`
- `0 <= d <= 2F - 2`

### Algoritmo di compressione

Per ogni blocco `F x F` dell’immagine:

1. il blocco viene copiato in una matrice temporanea
2. viene convertito in `double[][]`
3. viene applicata la DCT 2D in avanti
4. vengono azzerati i coefficienti per cui `k + l >= d`
5. viene applicata la DCT 2D inversa
6. i valori vengono arrotondati a interi
7. il blocco viene traslato di 255 usando le utility del progetto
8. il blocco viene scritto nuovamente nel segnale immagine

### Regione effettivamente processata

Viene elaborata solo la più grande regione dell’immagine la cui larghezza e altezza siano multipli di `F`. I pixel di bordo fuori da quella regione restano invariati prima che venga restituita l’immagine compressa finale ritagliata.

### Output della compressione

Il file compresso viene salvato in formato BMP con questo schema di nome:

```text
output/<nome-originale>_compressed.bmp
```

## Finestre GUI

### `PartChooserWindow`

È la prima finestra mostrata dall’app.

- titolo: `Choose what assignment part to be run`
- offre due pulsanti:
  - **PART 1 Benchmark DCT**
  - **PART 2 GUI Image Compression**
- esegue il benchmark in un worker in background per non bloccare l’interfaccia

### `ImageCompressionWindow`

Interfaccia principale della compressione.

- titolo: `DCT Image Compression Tool`
- finestra massimizzata
- contiene:
  - pulsante **Choose Image**
  - pulsante **Compress Image**
  - pannello di anteprima **Original**
  - pannello di anteprima **Compressed**

### `ImagePicker`

Selettore di file usato per scegliere un’immagine.

- parte da una cartella Download se disponibile
- in alternativa usa `Downloads` / `Scaricati`
- preferisce una cartella `immagini` specifica del progetto dentro Download
- carica il file selezionato con `ImageIO`
- copia il file scelto in `output/`

### `CompressionCoefficientsPicker`

Finestra per inserire i parametri di compressione.

- titolo: `COMPRESSION FACTOR VALUES PICKER`
- richiede `F` e `d`
- mostra errori di validazione se i valori non sono corretti
- pubblica i valori scelti come coppia una volta superati i controlli

## Percorsi e nomi importanti

- CSV del benchmark: `output/times_vs_size.csv`
- Cartella di output generale: `output/`
- Le immagini sorgenti selezionate vengono copiate in `output/`
- Le immagini compresse vengono salvate come BMP in `output/`

## Requisiti

- Java 17
- Maven

## Dipendenze

Il progetto usa:

- EJML
- JTransforms
- XChart
- OpenCSV
- Apache Commons Math
- Commons Logging
- FlatLaf

Tutte le dipendenze sono dichiarate in `pom.xml`.

## Build

Dalla root del progetto:

```bash
mvn clean package
```

## Esecuzione

Avvia l’applicazione con Maven:

```bash
mvn exec:java
```

In alternativa, puoi eseguire direttamente `com.example.Application` dal tuo IDE.

## Note

- Il benchmark confronta l’implementazione custom e quella della libreria sugli stessi input.
- La compressione è basata su blocchi e usa JTransforms per la DCT in `Part2`.
- L’applicazione usa un tema Swing scuro tramite FlatLaf.
- Alcune finestre e i percorsi di selezione file dipendono dalla struttura della cartella Download locale.
