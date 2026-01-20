import java.io.Serializable;

class Kot implements Serializable {
    String imie;
    int wiek;

    public Kot(String imie, int wiek) { this.imie = imie; this.wiek = wiek; }
    public String toString() { return "Kot [imie=" + imie + ", wiek=" + wiek + "]"; }
    public boolean equals(Object o) { return (o instanceof Kot); } 
}

class Pies implements Serializable {
    String rasa;
    boolean szczeka;

    public Pies(String rasa, boolean szczeka) { this.rasa = rasa; this.szczeka = szczeka; }
    public String toString() { return "Pies [rasa=" + rasa + ", szczeka=" + szczeka + "]"; }
    public boolean equals(Object o) { return (o instanceof Pies); }
}

class Ptak implements Serializable {
    String gatunek;
    double rozpietoscSkrzydel;

    public Ptak(String gatunek, double roz) { this.gatunek = gatunek; this.rozpietoscSkrzydel = roz; }
    public String toString() { return "Ptak [gatunek=" + gatunek + ", skrzydla=" + rozpietoscSkrzydel + "]"; }
    public boolean equals(Object o) { return (o instanceof Ptak); }
}