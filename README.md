# Programowanie-zaawansowane---WSB-Merito
Projekt z programowania zaawansowanego na uczelnię WSB Merito Warszawa. 

1. Opis Projektu
Aplikacja realizuje system wymiany zserializowanych obiektów w architekturze klient-serwer. Serwer zarządza mapą obiektów i obsługuje wielu klientów równocześnie w osobnych wątkach, kontrolując limit aktywnych połączeń. Klient komunikuje się z serwerem, pobiera kolekcje danych i przetwarza je przy użyciu strumieni.

2. Architektura Klas (Modele)
Aplikacja definiuje trzy klasy modeli danych, z których każda implementuje interfejs `Serializable`:
* Kot: pola `String imie`, `int wiek`.
* Pies: pola `String rasa`, `boolean czyGroźny`.
* Ptak: pola `String gatunek`, `double predkoscLotu`.

Wszystkie klasy posiadają komplet metod: konstruktory, `toString()` oraz `equals()`.

3. Implementacja Serwera
Serwer po uruchomieniu wykonuje następujące kroki:
1.  Inicjalizacja: Tworzy po 4 obiekty każdej klasy i umieszcza je w `HashMap` pod kluczami typu `Klasa_Numer` (np. `Kot_1`, `Kot_2`).
2.  Obsługa połączeń: W pętli `while(true)` oczekuje na klientów przy użyciu `ServerSocket`. Każde połączenie delegowane jest do osobnej instancji klasy `ObslugaKlienta` (implementującej `Runnable`).
3.  Zarządzanie limitem (MAX_CLIENTS): Serwer sprawdza liczbę aktywnych wątków. Jeśli limit zostanie przekroczony, wysyła status `REFUSED` i zamyka gniazdo.
4.  Logowani: Serwer rejestruje ID klienta oraz wypisuje w konsoli informacje o przesyłanych obiektach.
5.  Symulacja błędów: Jeśli klient poprosi o nieistniejącą klasę, serwer wysyła obiekt typu `String` zamiast kolekcji, co pozwala przetestować obsługę wyjątków po stronie klienta.

4. Implementacja Klienta
Klient realizuje następujący scenariusz:
1.  Handshake: Łączy się z serwerem i przesyła swoje numeryczne ID.
2.  Weryfikacja statusu: Kończy działanie przy statusie `REFUSED` lub przechodzi do zapytań przy statusie `OK`.
3.  Pętla zapytań: Kilkukrotnie prosi o kolekcje obiektów konkretnych klas.
4.  Przetwarzanie danych:
    Odbiera zserializowaną kolekcję.
    Używa **Java Stream API (`.stream().forEach()`) do wypisania obiektów na konsoli wraz ze swoim ID.
5.  Obsługa błędów rzutowania: Zawiera blok `try-catch` dla `ClassCastException`, aby poprawnie obsłużyć sytuację, w której serwer przesyła obiekt innego typu niż oczekiwana kolekcja.
