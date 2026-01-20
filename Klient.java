import java.io.*;
import java.net.*;
import java.util.*;

public class Klient {
    public static void main(String[] args) {
        for (int i = 1; i <= 4; i++) {
            final int id = i;
            new Thread(() -> startujKlienta(id)).start();
        }
    }

    public static void startujKlienta(int clientId) {
        try (Socket socket = new Socket("localhost", 9999);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(clientId);

            String status = (String) in.readObject();
            System.out.println("Klient " + clientId + ": Status połączenia -> " + status);

            if (!status.equals("OK")) return;

            String[] klasyDoPobrania = {"Kot", "Pies", "Ptak", "Smok"};
            
            for (String nazwa : klasyDoPobrania) {
                out.writeObject(nazwa);
                
                try {
                    Object odpowiedz = in.readObject();

                    List<?> lista = (List<?>) odpowiedz;

                    System.out.println("Klient " + clientId + " odebrał " + nazwa + ":");
                    lista.stream().forEach(obj -> System.out.println("   [ID:" + clientId + "] " + obj));

                } catch (ClassCastException e) {
                    System.out.println("Klient " + clientId + ": BŁĄD RZUTOWANIA! Otrzymano nieprawidłowy typ danych.");
                }
                
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            System.err.println("Klient " + clientId + " błąd: " + e.getMessage());
        }
    }
}