public class Album {
    private String autore;
    private String titolo;
    private String formato;
    private String prezzo;
    private String Immagine;

    public Album(String prezzo, String autore, String titolo, String formato, String Immagine) {
        this.prezzo = prezzo;
        this.autore = autore;
        this.titolo = titolo;
        this.formato = formato;
        this.Immagine = Immagine;
    }

    public String getImmagine() {
        return Immagine;
    }

    public void setImmagine(String immagine) {
        Immagine = immagine;
    }

    @Override
    public String toString() {
        return "Ecco cosa ho trovato: \n" +
                "🎵 Album: " + titolo + "\n" +
                "🧑‍🎤 Artista: " + autore + "\n" +
                "💿 Formato: " + formato + "\n" +
                "💰 Prezzo: " + prezzo + "\n";

    }
}
