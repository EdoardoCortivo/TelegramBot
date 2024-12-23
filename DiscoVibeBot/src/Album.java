import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Album {
    private String autore;
    private String titolo;
    private String formato;
    private String prezzo;
    private String Immagine;
    private String Venditore;

    public Album(String prezzo, String autore, String titolo, String formato, String Immagine, String Venditore) {
        this.prezzo = prezzo;
        this.autore = autore;
        this.titolo = titolo;
        this.formato = formato;
        this.Immagine = Immagine;
        this.Venditore = Venditore;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public String getVenditore() {
        return Venditore;
    }

    public void setVenditore(String venditore) {
        Venditore = venditore;
    }

    public String getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(String prezzo) {
        this.prezzo = prezzo;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getImmagine() {
        return Immagine;
    }

    public void setImmagine(String immagine) {
        Immagine = immagine;
    }

    @Override
    public String toString() {
        return "Fornitore " + Venditore + ": \n" +
                "🎵 Album: " + titolo + "\n" +
                "🧑‍🎤 Artista: " + autore + "\n" +
                "💿 Formato: " + formato + "\n" +
                "💰 Prezzo: " + prezzo + "\n" +
                "";
    }

    public String stringa() {
        return Immagine;
    }

    public static Album fromString(String s) {
        // Rimuoviamo "Album{" e "}"
        s = s.replace("Album{", "").replace("}", "");

        // Separiamo la stringa usando il carattere '\''
        String[] parts = s.split("'");

        if (parts.length != 12) {
            throw new IllegalArgumentException("La stringa non è nel formato corretto.");
        }

        // Creiamo e restituiamo un nuovo oggetto Album
        return new Album(
                parts[0],  // autore
                parts[1],  // titolo
                parts[2],  // formato
                parts[3],  // prezzo
                parts[4],  // Immagine
                parts[5]   // Venditore
        );
    }

    public Album() {
    }
}