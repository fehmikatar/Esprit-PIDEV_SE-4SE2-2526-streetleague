# Esprit-PIDEV_SE-4SE2-2526-streetleague

<div align="center">
  <img src="https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit">
</div>

---

## 📖 Description

**StreetLeague** est une plateforme innovante dédiée à la gestion du sport amateur. L'application permet d'organiser des compétitions, de gérer la réservation de terrains (SportSpaces) et de récolter des feedbacks de la communauté. Ce repository contient l'**API Backend** (Spring Boot) qui alimente l'ensemble de l'écosystème StreetLeague.

## 👥 Membres de l'Équipe & Répartition
- **Fehmi Katar** : Gestion des Terrains (SportSpace), Feedbacks, Réservations, Gestion des Utilisateurs et Authentification (JWT).
- 
## 🏗️ Structure du Projet

L'architecture est modulaire et divisée de manière académique :
* 📂 **`restcontrollers/`** : Points d'entrée de l'API REST (Endpoints).
* 📂 **`services/`** : Logique métier applicative et traitements complexes.
* 📂 **`repositories/`** : DAL (Data Access Layer) via Spring Data JPA.
* 📂 **`entities/`** : Modèles de la base de données.
* 📂 **`dto/`** : Objets de transferts de données (Isoler la BDD de l'API).
* 📂 **`security/`** : Configuration Spring Security & implémentation JWT.

## 🛠️ Instructions de lancement

### Pré-requis
- Java 17 ou 21
- Maven (intégré via `mvnw`)
- Base de données MySQL lancée en local (port `3306`)

### Déploiement Local
1. **Cloner le repository**
   ```bash
   git clone https://github.com/ehmikatar/Esprit-PIDEV_SE-4SE2-2526-streetleague.git
   cd Esprit-PIDEV_SE-4SE2-2526-streetleague
   ```
2. **Compiler et exécuter les tests Unitaires (JUnit 5)**
   ```bash
   mvn clean verify
   ```
3. **Lancer le serveur Spring Boot**
   ```bash
   mvn spring-boot:run
   ```
Le serveur s'exécute par défaut sur le port `:8080`.
L'interface de documentation Swagger est accessible via : `http://localhost:8080/swagger-ui.html`

## 🛑 Fichier `.gitignore`
Le dépôt intègre un fichier `.gitignore` strict et pré-configuré excluant :
- Les fichiers binaires de compilation (`/target`)
- Les logs et traces d'exécution
- L'historique d'IDE (`.idea/`, `.vscode/`, `.eclipse/`)

---
**Module de Projet Intégré (PI)** - École Supérieure Privée d'Ingénierie et de Technologies (ESPRIT)
