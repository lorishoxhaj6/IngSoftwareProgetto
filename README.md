# Diabetes Management System (DMS)

[cite_start]Sistema clinico informativo per la gestione e il monitoraggio di pazienti affetti da diabete di tipo 2. Il software facilita l'interazione tra **Paziente** e **Diabetologo**, consentendo il tracciamento costante di parametri vitali, aderenza terapeutica e sintomatologia[cite: 3, 4].

## 📄 Documentazione Ufficiale
Per un'analisi dettagliata dei requisiti, dei diagrammi UML e delle scelte implementative, consultare la:
👉 ****

---

## 🚀 Caratteristiche Principali

Il sistema si concentra sul monitoraggio proattivo e sulla prevenzione di crisi glicemiche:
* [cite_start]**Monitoraggio Glicemico**: Registrazione giornaliera dei livelli di glucosio con sistema di alert automatico in caso di parametri fuori norma[cite: 3].
* [cite_start]**Gestione Terapie**: I diabetologi possono prescrivere farmaci e dosaggi; i pazienti registrano l'assunzione in tempo reale[cite: 8, 7].
* **Aderenza Terapeutica**: Monitoraggio automatico delle assunzioni. [cite_start]Se il sistema rileva tre giornate consecutive di mancata assunzione, genera un alert visibile al medico[cite: 26].
* [cite_start]**Diario Clinico**: Segnalazione di sintomi, patologie concomitanti e note cliniche aggiornabili dai medici[cite: 5, 13].
* [cite_start]**Tracciabilità**: Sistema di logging integrato che registra quale medico ha effettuato modifiche ai dati in tempo reale[cite: 26].

## 🏗️ Architettura e Design Pattern

[cite_start]Il progetto segue una progettazione orientata agli oggetti con una netta separazione delle responsabilità attraverso i seguenti pattern[cite: 18]:

### Pattern Architetturali
* [cite_start]**Model-View-Controller (MVC)**: Separa la logica applicativa (Controller), la struttura dei dati (Model) e l'interfaccia utente (View)[cite: 18].
* **Data Access Object (DAO)**: Isola la logica di persistenza (SQL) dal resto dell'applicazione. [cite_start]Implementato per entità come `Patient`, `Measurement`, `Prescription`, `Intake` e `Symptom`[cite: 21].
* **Facade**: 
    * [cite_start]**Clinic Facade**: Punto di accesso unificato per le funzionalità cliniche generali[cite: 21].
    * [cite_start]**Alert Service**: Gestore centralizzato per la generazione e validazione degli avvisi clinici[cite: 21].

## 📂 Casi d'Uso Principali

### Lato Paziente
1. [cite_start]**Registrazione Glicemia**: Inserimento e modifica delle misurazioni giornaliere prima e dopo i pasti[cite: 3].
2. [cite_start]**Segnalazione Sintomi**: Inserimento di sintomi predefiniti o descrizioni testuali di patologie e terapie concomitanti[cite: 5].
3. [cite_start]**Registro Farmaci**: Registrazione dell'assunzione di farmaci specificando data, farmaco e dose[cite: 7].

### Lato Diabetologo
1. [cite_start]**Gestione Terapia**: Specifica di farmaco, dosaggio e istruzioni per i pazienti assegnati[cite: 8].
2. [cite_start]**Visualizzazione Dati**: Monitoraggio andamento glicemico, sintomi segnalati e farmaci assunti dal paziente[cite: 12].
3. [cite_start]**Aggiornamento Cartella**: Inserimento di note o segnalazioni cliniche aggiornate[cite: 13].

## 🛠️ Sviluppo e Qualità

* [cite_start]**Metodologia**: Approccio agile e incrementale con tecniche di **Pair Designing** e **Pair Programming**[cite: 17].
* [cite_start]**Testing Automatizzato**: Utilizzo di **JUnit** per la validazione dello strato di persistenza (`JdbcPatientDao` e `JdbcMeasurementDao`)[cite: 24].
* [cite_start]**Validazione Utente**: Test di usabilità condotti su utenti non esperti per verificare l'intuitività dell'interfaccia[cite: 27].

---
**Sviluppatori**: [Loris Hoxhaj, Andrew Bregoli, Lorenzo Oceano]  
*Progetto realizzato per il corso di Ingegneria del Software*
