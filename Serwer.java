import java.io.*;
import java.net.*;
import java.util.*;

class Kot implements Serializable {
    String imie;
    int wiek;
    public Kot(String imie, int wiek) { this.imie = imie; this.wiek = wiek; }
    @Override
    public String toString() { return "Kot[imie=" + imie + ", wiek=" + wiek + "]"; }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Kot)) return false;
        Kot k = (Kot) o;
        return Objects.equals(imie, k.imie) && wiek == k.wiek;
    }
}

class Pies implements Serializable {
    String rasa;
    boolean czyGroźny;
    public Pies(String rasa, boolean czyGroźny) { this.rasa = rasa; this.czyGroźny = czyGroźny; }
    @Override
    public String toString() { return "Pies[rasa=" + rasa + ", groźny=" + czyGroźny + "]"; }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pies)) return false;
        Pies p = (Pies) o;
        return Objects.equals(rasa, p.rasa) && czyGroźny == p.czyGroźny;
    }
}

class Ptak implements Serializable {
    String gatunek;
    double predkoscLotu;
    public Ptak(String gatunek, double predkosc) { this.gatunek = gatunek; this.predkoscLotu = predkosc; }
    @Override
    public String toString() { return "Ptak[gatunek=" + gatunek + ", predkosc=" + predkoscLotu + "km/h]"; }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ptak)) return false;
        Ptak p = (Ptak) o;
        return Objects.equals(gatunek, p.gatunek) && predkoscLotu == p.predkoscLotu;
    }
}

public class Serwer {
    private static final int PORT = 9999;
    private static final int MAX_CLIENTS = 2;
    private static final Map<String, Object> mapaObiektow = new HashMap<>();
    private static int aktualnaLiczbaKlientow = 0;

    public static void main(String[] args) {
        inicjalizujMape();
        System.out.println("Serwer uruchomiony. Czekam na klientów...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ObslugaKlienta(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void inicjalizujMape() {
        for (int i = 1; i <= 4; i++) {
            mapaObiektow.put("Kot_" + i, new Kot("Kot_" + i, i + 2));
            mapaObiektow.put("Pies_" + i, new Pies("Rasa_" + i, i % 2 == 0));
            mapaObiektow.put("Ptak_" + i, new Ptak("Gatunek_" + i, 20.0 + i));
        }
    }

    public static synchronized boolean dodajKlienta() {
        if (aktualnaLiczbaKlientow < MAX_CLIENTS) {
            aktualnaLiczbaKlientow++;
            return true;
        }
        return false;
    }

    public static synchronized void usunKlienta() {
        aktualnaLiczbaKlientow--;
    }

    static class ObslugaKlienta implements Runnable {
        private Socket socket;
        public ObslugaKlienta(Socket s) { this.socket = s; }

        @Override
        public void run() {
            int idKlienta = -1;
            boolean czyZarejestrowany = false;

            try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                idKlienta = (int) in.readObject();

                if (dodajKlienta()) {
                    czyZarejestrowany = true;
                    out.writeObject("OK");
                    System.out.println("Serwer: Klient " + idKlienta + " ZAAKCEPTOWANY.");
                } else {
                    out.writeObject("REFUSED");
                    System.out.println("Serwer: Klient " + idKlienta + " ODRZUCONY (brak miejsc).");
                    return;
                }

                while (true) {
                    try {
                        String zapytanie = (String) in.readObject();
                    
                        Thread.sleep((long) (Math.random() * 800));

                        List<Object> kolekcja = new ArrayList<>();
                        for (Map.Entry<String, Object> entry : mapaObiektow.entrySet()) {
                            if (entry.getValue().getClass().getSimpleName().equalsIgnoreCase(zapytanie)) {
                                kolekcja.add(entry.getValue());
                            }
                        }

                        if (kolekcja.isEmpty()) {
                            out.writeObject("TO_JEST_BŁĘDNY_OBIEKT_STRING");
                            System.out.println("Serwer: Przesłano błąd do klienta " + idKlienta);
                        } else {
                            out.writeObject(kolekcja);
                            System.out.println("Serwer: Przesłano listę " + zapytanie + " do klienta " + idKlienta);
                        }

                    } catch (EOFException e) { break; }
                }

            } catch (Exception e) {
                System.out.println("Serwer: Rozłączono klienta " + idKlienta);
            } finally {
                if (czyZarejestrowany) usunKlienta();
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }
}