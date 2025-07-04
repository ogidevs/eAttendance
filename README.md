# eAttendance - Sistem za Evidenciju Prisustva

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen.svg)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-orange.svg)
![Maven](https://img.shields.io/badge/Maven-4.0-red.svg)
![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)

**eAttendance** je moderna web aplikacija razvijena u Spring Boot radnom okviru, dizajnirana da digitalizuje i automatizuje proces evidencije prisustva studenata na predavanjima i vežbama. Projekat zamenjuje zastarele, manuelne metode evidencije brzim, sigurnim i transparentnim digitalnim rešenjem.

Ovaj projekat je realizovan u sklopu predmeta **IT355 - Veb sistemi 1**.

## ✨ Ključne Funkcionalnosti

Aplikacija podržava tri različite korisničke uloge, svaka sa specifičnim setom funkcionalnosti:

### 👤 Administrator
*   **Potpuno upravljanje korisnicima:** CRUD (Create, Read, Update, Delete) operacije nad studentskim i profesorskim nalozima.
*   **Upravljanje predmetima:** Kreiranje, izmena i brisanje predmeta.
*   **Povezivanje entiteta:** Dodeljivanje profesora predmetima i upisivanje studenata, čime se postavlja osnova za funkcionisanje celog sistema.

### 👨‍🏫 Profesor
*   **Personalizovani panel:** Pregled samo onih predmeta i časova za koje je lično zadužen.
*   **Upravljanje časovima:** Jednostavno zakazivanje i brisanje časova.
*   **Generisanje sesije za prijavu:** Jednim klikom generiše **jedinstveni alfanumerički kod** i **QR kod** za prijavu, sa vremenski ograničenom validnošću.
*   **Monitoring u realnom vremenu:** Uvid u broj prijavljenih studenata i njihova imena za svaki održani čas.

### 🎓 Student
*   **Višestruki načini prijave:**
    1.  **Pregled aktivnih časova:** Dashboard dinamički prikazuje listu časova na koje se trenutno može prijaviti, omogućavajući prijavu jednim klikom.
    2.  **Skeniranje QR koda:** Brza i laka prijava skeniranjem QR koda kamerom mobilnog telefona.
    3.  **Manuelni unos koda:** Klasičan unos koda kao rezervna opcija.
*   **Istorija prisustva:** Detaljan pregled svih prethodnih uspešno evidentiranih prisustva.

## 🛠️ Tehnologije i Arhitektura

Projekat je izgrađen na modernim i pouzdanim tehnologijama, prateći najbolje prakse softverskog inženjeringa.

*   **Backend:**
    *   **Jezik:** Java 17
    *   **Radni okvir:** Spring Boot 3.x
*   **Frontend:**
    *   **Template Engine:** Thymeleaf
    *   **Stilizovanje:** Čist CSS3 za moderan i responzivan dizajn.
*   **Build & Upravljanje zavisnostima:**
    *   Apache Maven
*   **Skladištenje podataka:**
    *   U skladu sa zahtevima projekta, umesto relacione baze podataka, korišćen je **Application Scope (In-Memory Storage)**. Podaci se čuvaju u Java kolekcijama unutar Singleton Spring bean-ova i postoje samo dok je aplikacija pokrenuta.

### Arhitektura
Aplikacija striktno prati **MVC (Model-View-Controller)** arhitekturu, sa jasno razdvojenim slojevima:
*   **Model:** Čiste POJO klase sa Lombok anotacijama.
*   **Repository:** Sloj za apstrakciju pristupa podacima.
*   **Service:** Sloj koji sadrži svu biznis logiku.
*   **Controller:** Sloj za upravljanje HTTP zahtevima.

## 🚀 Pokretanje Projekta

Da biste pokrenuli projekat na svom lokalnom računaru, pratite sledeće korake:

### Preduslovi
*   **Java Development Kit (JDK)** - Verzija 17 ili novija.
*   **Apache Maven** - Verzija 3.8 ili novija.
*   **Git**

### Koraci
1.  **Klonirajte repozitorijum:**
    ```bash
    git clone https://github.com/ogidevs/eattendance.git
    ```

2.  **Pozicionirajte se u direktorijum projekta:**
    ```bash
    cd eattendance
    ```

3.  **Pokrenite aplikaciju pomoću Maven wrapper-a:**
    ```bash
    ./mvnw spring-boot:run
    ```
    (Na Windows sistemima koristite `mvnw.cmd spring-boot:run`)

4.  Aplikacija će biti dostupna na adresi `http://localhost:8080`.

## 📋 Testni Nalozi

Za testiranje aplikacije, možete koristiti sledeće predefinisane naloge koji se kreiraju pri svakom pokretanju:

| Uloga         | Korisničko ime   | Lozinka   |
|---------------|------------------|-----------|
| **Administrator** | `admin`          | `adminpass` |
| **Profesor**      | `prof.petrovic`  | `pass`    |
| **Student**       | `stud.markovic`  | `pass`    |
