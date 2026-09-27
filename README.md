# 🚀 TechPulse – Tech News & Repository App

**TechPulse** ist eine moderne Android-Anwendung, die Entwickler-News von **Dev.to** und beliebte Repositories von **GitHub** in einer performanten, benutzerfreundlichen App vereint.

---

## 📸 Screenshots

| Haupt-Feed | Repository Suche | Lesezeichen |
|:---------------------------:|:----------------:|:--------------------:|
| *(<img width="250" alt="Screenshot_20260924_155454_TechPulse" src="https://github.com/user-attachments/assets/9e12c2c0-ddcc-4a2d-b256-4729560237fe" />)* | *(<img width="250" alt="7931" src="https://github.com/user-attachments/assets/2c13aa5e-c6da-4450-a9ac-4915348994df" />)* | *(<img width="250" alt="7935" src="https://github.com/user-attachments/assets/103ac143-b309-4586-9d0a-003068a5b8db" />)* |

---

## ✨ Features

* **📱 Dynamischer Haupt-Feed:** Neueste Tech-Artikel von Dev.to mit flüssigem **Endless Scrolling (Pagination)**.
* **🔍 GitHub Repository Search:** Suche nach Repositories mit Sortier- und Filteroptionen.
* **🔖 Lesezeichen-System:** Speichern von Artikeln und Repositories in Firestore mit strenger Typ-Trennung (`POST` vs. `REPOSITORY`).
* **❤️ Interaktionen:** Live-Updates von Likes und Kommentaren synchronisiert mit der Cloud.
* **⚡ Nahtlose Performance:** Caching-Mechanismen und optimierter Memory-Footprint beim Paging.

---

## 📂 Projektstruktur

```text
com.example.techpulse
├── data
│   ├── local          # Room Database & Firestore Documents
│   ├── mapper         # Transformationen zwischen DTOs, Entities & Domain Models
│   ├── remote         # Retrofit API-Interfaces (TechPulseApi) & DTOs
│   └── repository     # Single Source of Truth Repository-Implementierungen
├── domain
│   ├── model          # Core Domain Models (Post, Article, Repository)
│   └── usecase        # Geschäftslogik und Anwendungsfälle
└── ui
    ├── components     # Wiederverwendbare Compose-Komponenten (PostCard, TopBar)
    ├── navigation     # NavHost und Route-Definitionen
    └── presentation   # ViewModels & Screens (Feed, Bookmarks, RepoDetail)
```

---

## 📐 Architektur & Datenfluss

Die App folgt den Prinzipien der Clean Architecture mit einer klaren Schichtentrennung:

```text
[ API (Dev.to / GitHub) ]   [ Local DB (Room / Firestore) ]
            │                              │
            └──────────────┬───────────────┘
                           ▼
                 [ Repository Layer ]
                           │
                           ▼ (Post / Repository Domain Models)
                  [ ViewModel Layer ]
                           │
                           ▼ (FeedUiState)
                   [ UI (Compose) ]
```

---

## ⚡ Resilience & Offline Experience

* **Seamles Error Handling:** Unterscheidung zwischen Netzwerkfehlern und leeren Server-Antworten mit benutzerfreundlichen Error-States (`FeedUiState.Error`).
* **Retry-Mechanismus:** Möglichkeit zum manuellen Erneuten Laden über die UI im Fehlerfall.

---

## 🔮 Roadmap & Ausblick

- [x] Endless Scrolling für Dev.to Artikel
- [x] Lesezeichen-Synchronisierung mit Firebase Firestore
- [ ] Push-Benachrichtigungen bei neuen Top-Artikeln
- [ ] Offline-First Unterstützung mit Room-Caching für den Feed
- [ ] Dark / Light Theme Toggle

---

## 👤 Autor

**Dein Name**
* **GitHub:** [Magnus98Sebastian](https://github.com/Magnus98Sebastian)
* **LinkedIn:** [Sebastian Magnus](https://www.linkedin.com/in/sebastian-magnus-2ba39a3a4/)
