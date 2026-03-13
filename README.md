# Diabetes Management System (DMS)

Sistema clinico informativo per la gestione e il monitoraggio di pazienti affetti da diabete di tipo 2. Il software facilita l'interazione tra **Paziente** e **Diabetologo**, consentendo il tracciamento costante di parametri vitali, aderenza terapeutica e sintomatologia.

## 📄 Documentazione Ufficiale
Per un'analisi dettagliata dei requisiti, dei diagrammi UML e delle scelte implementative, consultare la:
👉 **[Relazione Tecnica (PDF)](RELAZIONE_PROGETTO_INGEGNERIA_DEL_SOFTWARE_.pdf)**

---

## Caratteristiche Principali

Il sistema si concentra sul monitoraggio proattivo e sulla prevenzione di crisi glicemiche:
* **Monitoraggio Glicemico**: Registrazione giornaliera dei livelli di glucosio con sistema di alert automatico in caso di parametri fuori norma.
* **Gestione Terapie**: I diabetologi possono prescrivere farmaci e dosaggi; i pazienti registrano l'assunzione in tempo reale.
* **Aderenza Terapeutica**: Monitoraggio automatico delle assunzioni. Se il sistema rileva tre giornate consecutive di mancata assunzione, genera un alert visibile al medico.
* **Diario Clinico**: Segnalazione di sintomi, patologie concomitanti e note cliniche aggiornabili dai medici.
* **Tracciabilità**: Sistema di logging integrato che registra quale medico ha effettuato modifiche ai dati in tempo reale.

##  Architettura e Design Pattern

Il progetto segue una progettazione orientata agli oggetti con una netta separazione delle responsabilità attraverso i seguenti pattern:

### Pattern Architetturali
* **Model-View-Controller (MVC)**: Separa la logica applicativa (Controller), la struttura dei dati (Model) e l'interfaccia utente (View).
* **Data Access Object (DAO)**: Isola la logica di persistenza (SQL) dal resto dell'applicazione. Implementato per entità come `Patient`, `Measurement`, `Prescription`, `Intake` e `Symptom`[cite: 21].
* **Facade**: 
    * **Clinic Facade**: Punto di accesso unificato per le funzionalità cliniche generali.
    * **Alert Service**: Gestore centralizzato per la generazione e validazione degli avvisi clinici.

## 📂 Casi d'Uso Principali

### Lato Paziente
1. **Registrazione Glicemia**: Inserimento e modifica delle misurazioni giornaliere prima e dopo i pasti.
2. **Segnalazione Sintomi**: Inserimento di sintomi predefiniti o descrizioni testuali di patologie e terapie concomitanti.
3. **Registro Farmaci**: Registrazione dell'assunzione di farmaci specificando data, farmaco e dose.

### Lato Diabetologo
1. **Gestione Terapia**: Specifica di farmaco, dosaggio e istruzioni per i pazienti assegnati.
2. **Visualizzazione Dati**: Monitoraggio andamento glicemico, sintomi segnalati e farmaci assunti dal paziente.
3. **Aggiornamento Cartella**: Inserimento di note o segnalazioni cliniche aggiornate.

## 🛠️ Sviluppo e Qualità

* **Metodologia**: Approccio agile e incrementale con tecniche di **Pair Designing** e **Pair Programming**.
* **Testing Automatizzato**: Utilizzo di **JUnit** per la validazione dello strato di persistenza (`JdbcPatientDao` e `JdbcMeasurementDao`).
* **Validazione Utente**: Test di usabilità condotti su utenti non esperti per verificare l'intuitività dell'interfaccia.

---
**Sviluppatori**: [Loris Hoxhaj, Andrew Bregoli, Lorenzo Oceano]  
*Progetto realizzato per il corso di Ingegneria del Software*
