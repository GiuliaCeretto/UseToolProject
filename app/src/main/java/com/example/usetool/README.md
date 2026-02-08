STRUTTURA APPLICAZIONE

app/src/main/java/com/example/usetool/
├── data/                       # Gestione della persistenza e dell'origine dati
│   ├── dto/                    # Classi speculari al JSON del server (ExpertDTO, ToolDTO)
│   ├── network/                # Accesso tecnico al DB 
│   └── repository/             # Mediatore e logica di mapping: prende i dati dal DataSource e li consegna al ViewModel trasformati in oggetti del model
│
├── model/                      # Domain Model: le entità "pulite" dell'app (User, Tool)
│
├── navigation/                 # Gestione dei percorsi e del NavController
│   ├── NavGraph.kt             # Definizione del grafo delle schermate
│   └── NavRoutes.kt            # Costanti delle rotte (es. "home", "profile/{id}")
│
└── ui/                         # Tutto ciò che riguarda l'interfaccia grafica
├── component/              # Widget riutilizzabili e stateless (Buttons, Cards)
├── screens/                # Schermate principali (HomeScreen, ToolScreen)
└── viewmodel/              # Logica di presentazione e gestione dello stato