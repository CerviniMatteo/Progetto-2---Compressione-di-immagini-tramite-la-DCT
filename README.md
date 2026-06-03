# Assignment sulla compressione d'immagini con la DCT bidimensionale

Progetto Java per lo studio della Trasformata Discreta Coseno bidimensionale  (DCT2) e del suo utilizzo nella compressione delle immagini.
Il progetto java si trova all'interno della cartella `code` 
Il progetto contiene due parti:

1. **Parte 1 — benchmark DCT**
   confronta l’implementazione DCT personalizzata con quella della libreria JTransforms ed esporta i risultati temporali in un file CSV.
2. **Parte 2 — compressione d'immagini**
   permette di scegliere un’immagine, configurare i parametri di compressione e comprimere l’immagine con un flusso DCT basato su blocchi.

## Cosa fa l’applicazione
- **Parte 1**: Avvia un benchmark sulle seguenti dimensioni di matrice:
  ```text
  8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192
  ```
  per confrontare i tempi di esecuzione della DCT custom e di JTransforms.
  Per ottenere misurazioni accurate, vengono effettuate 3 iterazioni di warmup della JVM. Le misure effettive vengono effettuate su 5 iterazioni,  esportando la media dei risultati ottenuti in ogni iterazione nel file `plots/times_vs_size.csv`.
- **Parte 2**: Apre la GUI di compressione delle immagini, che permette di:
	- selezionare un'immagine
	- inserire i parametri di compressione
	- ottenere un'anteprima affiancata dell’immagine originale e di quella compressa
	
	Il file compresso viene salvato in formato BMP con nome:
	```text
		code/output/<nome-originale>_compressed.bmp
	```
## Requisiti

- Java 17
- Maven

## Dipendenze
- EJML
- JTransforms
- OpenCSV
- Apache Commons Math
- Commons Logging
- FlatLaf

Tutte le dipendenze sono dichiarate in `pom.xml` e gestite da Maven.

## Build

Dalla cartella root della repository:

```bash
cd code
mvn clean package
```

## Esecuzione

Avvia l’applicazione con Maven a partire dalla cartella root della repository:

```bash
cd code
mvn exec:java
```