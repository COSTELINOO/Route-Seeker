# Documentație Proiect Route-Seeker

## Contribuitori
- **Back-End:** [COSTELINOO](https://github.com/COSTELINOO)
- **Front-End:** [biancabaltag](https://github.com/biancabaltag)

## Cerința problemei

### Descriere generală
Route-Seeker reprezintă o aplicație destinată găsirii rutelor optime între diverse locații. Scopul principal al acestei aplicații este de a oferi utilizatorilor posibilitatea de a identifica cel mai eficient traseu pentru a ajunge la destinație în funcție de preferințele personale.

### Funcționalități principale
Aplicația trebuie să permită următoarele:

#### Căutarea rutelor
- Specificarea punctului de plecare și a destinației
- Adăugarea de puncte intermediare pe traseu
- Alegerea criteriului de optimizare (timp, distanță, cost)

#### Vizualizarea rezultatelor
- Afișarea traseului pe o hartă interactivă
- Furnizarea instrucțiunilor de navigare pas cu pas
- Estimarea timpului și distanței pentru ruta aleasă

#### Gestionarea conturilor de utilizator
- Crearea și administrarea conturilor
- Salvarea rutelor favorite pentru utilizare ulterioară
- Accesarea istoricului rutelor căutate anterior

#### Funcționalități suplimentare
- Sugestii pentru puncte de interes pe traseu
- Informații despre condițiile de trafic (dacă sunt disponibile)
- Posibilitatea de a partaja ruta cu alți utilizatori

### Beneficii pentru utilizatori
- Economisirea timpului prin identificarea celei mai rapide rute
- Reducerea costurilor de transport prin optimizarea traseelor
- Experiență îmbunătățită de călătorie prin sugestii de puncte de interes
- Acces rapid la rutele frecvent utilizate

### Utilizatori țintă
Aplicația se adresează:
- Persoanelor care călătoresc frecvent
- Profesioniștilor care necesită optimizarea traseelor (ex: curieri, livratori)
- Turiștilor care doresc să exploreze eficient diverse locații
- Oricărei persoane care are nevoie de asistență în găsirea unui traseu optim

### Rezultate așteptate
La finalizarea implementării, Route-Seeker trebuie să ofere o soluție intuitivă, eficientă și fiabilă pentru planificarea rutelor, permițând utilizatorilor să economisească timp și resurse atunci când călătoresc dintr-un punct în altul.

---

## Restricții și limitări

- **OOP Modeling** – 2 puncte: Modelarea corectă a claselor folosind principii de programare orientată pe obiect.
- **Advanced Programming (Streams, Lambdas, Complex Operations)** – 2 puncte: Utilizarea caracteristicilor avansate ale limbajului (Java Stream API, expresii lambda, operații funcționale sau algoritmi mai complicați).
- **Interface (Swing, JavaFX, Web App, REST API cu Spring, React/Angular)** – 5 puncte: Realizarea unei interfețe grafice sau web; poate include aplicații desktop (Swing, JavaFX) sau aplicații web moderne.
- **Database (SQL | noSQL & ORM | JDBC)** – 4 puncte: Integrarea cu baze de date relaționale sau NoSQL, utilizarea JDBC sau ORM (ex: Hibernate).
- **File Operations (Serialization)** – 2 puncte: Citire/scriere fișiere, serializare/deserializare obiecte.
- **Services (Networking, Sockets)** – 3 puncte: Comunicare între aplicații prin rețea: sockets, clienți/servicii.
- **Algorithm (Complexity)** – 2 puncte: Implementarea de algoritmi cu complexitate semnificativă sau optimizări.

---

## Videoclip de prezentare
[Link către videoclipul de prezentare a aplicației](https://drive.google.com/file/d/1APRKNDc_4-PsRituTiS3-bKVsHsaRNZT/view?usp=sharing)

## Capturi de ecran
<img width="1918" height="1002" alt="1" src="https://github.com/user-attachments/assets/f088f162-762f-497a-96ca-5a408c78a7c9" />
<img width="1917" height="993" alt="2" src="https://github.com/user-attachments/assets/3bfb35f3-e11a-42e1-981e-8f12ef8f26ed" />

<img width="1917" height="1016" alt="3" src="https://github.com/user-attachments/assets/622cac13-c85a-4ba2-be22-6ef3aa4731c9" />
<img width="1918" height="1002" alt="4" src="https://github.com/user-attachments/assets/d4f6e372-d2cc-4e0a-91f8-06bd22deede7" />
<img width="1917" height="986" alt="5" src="https://github.com/user-attachments/assets/f550f0f1-354e-4462-8285-e8c2b6790104" />
<img width="1918" height="1005" alt="6" src="https://github.com/user-attachments/assets/5a6bcf98-40ea-492b-8538-cf2cbe89186d" />
<img width="1918" height="1012" alt="7" src="https://github.com/user-attachments/assets/a57a87b7-096a-463b-84b1-4bc3649d0bb6" />
<img width="1918" height="1001" alt="8" src="https://github.com/user-attachments/assets/23914a1d-a2c3-4e90-bfc3-bd5ca4c55c82" />
<img width="1918" height="1011" alt="9" src="https://github.com/user-attachments/assets/418d13f5-6332-42ed-8ef5-33ac6083e591" />
<img width="1918" height="1016" alt="10" src="https://github.com/user-attachments/assets/e9f89f58-4e1b-4c5c-99b3-5baa43f701c4" />

# Documentație Back-End - Route-Seeker

## Cuprins

1. [Prezentare generală](#1-prezentare-generală)
2. [Arhitectura sistemului](#2-arhitectura-sistemului)
   - [2.1 Prezentare arhitecturală](#21-prezentare-arhitecturală)
   - [2.2 Diagrama componentelor](#22-diagrama-componentelor)
3. [Funcționalități principale](#3-funcționalități-principale)
   - [3.1 Gestionarea utilizatorilor](#31-gestionarea-utilizatorilor)
   - [3.2 Gestionarea rutelor](#32-gestionarea-rutelor)
   - [3.3 Gestionarea locațiilor](#33-gestionarea-locațiilor)
   - [3.4 Gestionarea conexiunilor](#34-gestionarea-conexiunilor)
   - [3.5 Gestionarea informațiilor](#35-gestionarea-informațiilor)
4. [Securitate](#4-securitate)
5. [Tehnologii utilizate](#5-tehnologii-utilizate)

---

## 1. Prezentare generală

Back-end-ul aplicației **Route-Seeker** este implementat folosind framework-ul Spring Boot și oferă o serie de API-uri REST pentru gestionarea utilizatorilor, rutelor, locațiilor, conexiunilor și informațiilor. Sistemul este construit pe o arhitectură stratificată și utilizează o bază de date PostgreSQL pentru stocarea informațiilor.

---

## 2. Arhitectura sistemului

### 2.1 Prezentare arhitecturală

Arhitectura back-end-ului este stratificată, fiind compusă din:
- **Controller**: Expune endpoint-uri REST pentru interacțiunea cu clientul.
- **Service**: Contine logica aplicației.
- **Repository**: Gestionează interacțiunile cu baza de date.
- **DTO și Mapper**: DTO-urile transferă date între straturi, iar mapper-ele transformă entitățile în DTO-uri și invers.

### 2.2 Diagrama componentelor

- **AuthController**: Gestionarea autentificării utilizatorilor.
- **CityController**: Gestionarea orașelor.
- **LocationController**: Gestionarea locațiilor.
- **ConnectionController**: Gestionarea conexiunilor.
- **InformationController**: Gestionarea informațiilor.
- **OperationController**: Operații avansate (ex: căutarea celei mai scurte rute).

---

## 3. Funcționalități principale

### 3.1 Gestionarea utilizatorilor

- **Autentificare și înregistrare utilizatori**:
  - Endpoint-uri: `/auth/login` și `/auth/register`
  - Gestionate de `AuthController`.
  - Generare de token JWT prin `JwtUtil`.

- **Validare token JWT**:
  - Implementată în `JwtFilter`.
  - Token-urile expirate sau invalide sunt tratate cu mesaje de eroare detaliate.

---

### 3.2 Gestionarea rutelor

- **Calcul rute optime**:
  - Implementate în `OperationController` și `OperationService`.
  - Suport pentru căutarea celei mai scurte, lungi sau ciclice rute între locații.

---

### 3.3 Gestionarea locațiilor

- **Adăugare, actualizare și ștergere locații**:
  - Endpoint-uri: `/locations`
  - Gestionate de `LocationController` și `LocationService`.
  - Validări pentru câmpuri obligatorii (ex: `idCity`, `name`, `pozX`, `pozY`).

- **Vizualizare locații**:
  - Listarea tuturor locațiilor sau căutarea după ID/nume.

---

### 3.4 Gestionarea conexiunilor

- **Adăugare și gestionare conexiuni între locații**:
  - Endpoint-uri: `/connections`
  - Gestionate de `ConnectionController` și `ConnectionService`.

---

### 3.5 Gestionarea informațiilor

- **Adăugare informații despre orașe și locații**:
  - Endpoint-uri: `/informations`
  - Gestionate de `InformationController` și `InformationService`.

---

## 4. Securitate

- **Autentificare JWT**:
  - Implementată cu `JwtUtil` și `JwtFilter`.
  - Token-urile sunt validate înainte de accesarea resurselor protejate.

- **Gestionarea erorilor**:
  - Clase dedicate pentru tratarea excepțiilor (`MyExeption`, `GlobalExceptionHandler`).

---

## 5. Tehnologii utilizate

- **Java 17**: Limbaj de programare principal.
- **Spring Boot**: Framework pentru dezvoltarea aplicației.
- **PostgreSQL**: Bază de date relațională.
- **Lombok**: Reducerea boilerplate-ului în cod.
- **JWT (JSON Web Tokens)**: Autentificare bazată pe token-uri.
- **Maven**: Gestionarea dependențelor și build-ului aplicației.
