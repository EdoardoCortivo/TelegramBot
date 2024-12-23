import java.sql.*;
import java.time.LocalDateTime;

public class AlbumDB {

    private Connection conn;

    public AlbumDB(String address, String port, String username, String password) {
        // Costruzione della stringa di connessione
        String dbConnectionString = "jdbc:mysql://" + address + ":" + port;

        try {
            // Connessione al server MySQL senza specificare il database
            conn = DriverManager.getConnection(dbConnectionString, username, password);

            // Verifica se il database 'Botdb' esiste
            if (!databaseExists("Botdb")) {
                // Crea il database Botdb se non esiste
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE Botdb");
                    System.out.println("Database 'Botdb' creato.");
                }
            }

            // Ora ci connettiamo al database 'Botdb'
            conn.close();  // Chiudiamo la connessione precedente senza il database
            conn = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/Botdb", username, password);

            // Creazione della tabella Albums se non esiste
            createTableIfNotExists();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Albums (" +
                "nome_artista VARCHAR(255) NOT NULL, " +
                "nome_album VARCHAR(255) NOT NULL, " +
                "formato VARCHAR(20) NOT NULL, " +
                "immagine VARCHAR(255) NOT NULL, " +
                "venditore VARCHAR(20) NOT NULL, " +
                "prezzo_attuale VARCHAR(255) NOT NULL, " +
                "prezzo_minimo VARCHAR(255) NOT NULL, " +
                "data_minimo DATE, " +
                "prezzo_massimo VARCHAR(255) NOT NULL, " +
                "data_massimo DATE, " +
                "PRIMARY KEY (nome_artista, nome_album, formato, venditore)" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("Tabella 'Albums' creata o già esistente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean databaseExists(String databaseName) {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SHOW DATABASES LIKE '" + databaseName + "'");
            return rs.next();  // Se il risultato contiene qualcosa, il database esiste
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

//---------------------------------------------------------------------------------------------------------------------------

    public String selectPrezzo(String what, String where, String where2 ,String is, String is2) {
        String result = "";
        try {
            if (!conn.isValid(5)) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        String query = "SELECT " + what + " FROM Albums WHERE " + where + " = ?" + " AND " + where2 + " = ? ";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, is);
            statement.setString(2, is2);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    result += rs.getString(i) + "";
                    if (rs.getString(i).length() < 8) result += "";
                }
                result += "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public String select(String what, String from, String where, String where2 ,String is, String is2) {
        String result = "";
        try {
            if (!conn.isValid(5)) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        String query = "SELECT " + what + " FROM " + from + " WHERE " + where + " = ?" + " AND " + where2 + " = ? ";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, is);
            statement.setString(2, is2);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    result += rs.getString(i) + "\t";
                    if (rs.getString(i).length() < 8) result += "\t";
                }
                result += "\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public String selectALL(String from, String where, String where2, String where3, String where4, String is, String is2, String is3, String is4) {
        String output = "";
        try {
            if (!conn.isValid(5)) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query = "SELECT * FROM " + from+ " WHERE " + where + " = ?" + " AND " + where2 + " = ? " + " AND " + where3+ " = ? " + " AND " + where4 + " = ? ";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, is);
            statement.setString(2, is2);
            statement.setString(3, is3);
            statement.setString(4, is4);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                // Estrai i dati dalle colonne del ResultSet
                String nomeArtista = rs.getString("nome_artista");
                String nomeAlbum = rs.getString("nome_album");
                String formato = rs.getString("formato");
                String immagine = rs.getString("immagine");
                String venditore = rs.getString("venditore");
                String prezzoAttuale = rs.getString("prezzo_attuale");
                String prezzoMax = rs.getString("prezzo_massimo");
                String prezzoMin = rs.getString("prezzo_minimo");
                String DataMax = rs.getString("data_massimo");
                String DataMin = rs.getString("data_minimo");

                output = String.format(
                        "Artista: %s\nAlbum: %s\nFormato: %s\nVenditore: %s\nPrezzo Attuale: %s\nPrezzo Massimo: %s\nPrezzo Minimo: %s\nData Massimo: %s\nData Minimo: %s",
                        nomeArtista, nomeAlbum, formato, venditore, prezzoAttuale, prezzoMax, prezzoMin, DataMax, DataMin
                );

                output += "____" + immagine;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return output;
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------\\

    public boolean insertAlbum(Album album) {
        try {
            if (!conn.isValid(5)) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        String query = "INSERT INTO Albums (nome_artista, nome_album, formato, prezzo_attuale, immagine, venditore, prezzo_minimo, prezzo_massimo, data_minimo, data_massimo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, album.getAutore());
            statement.setString(2, album.getTitolo());
            statement.setString(3, album.getFormato());
            statement.setString(4, album.getPrezzo());
            statement.setString(5, album.getImmagine());
            statement.setString(6, album.getVenditore());
            statement.setString(7, album.getPrezzo());
            statement.setString(8, album.getPrezzo());
            LocalDateTime D = LocalDateTime.now();
            statement.setDate(9, java.sql.Date.valueOf(D.toLocalDate()));
            statement.setDate(10, java.sql.Date.valueOf(D.toLocalDate()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteAlbum(Album album) {
        try {
            if (!conn.isValid(5)) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        String query = "DELETE FROM Albums WHERE autore = ? AND titolo = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, album.getAutore());
            statement.setString(2, album.getTitolo());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateAlbum(Album album) {
        String prezzo = selectPrezzo("prezzo_attuale","nome_artista", "nome_album", album.getAutore(), album.getTitolo());
        String prezzoMax = selectPrezzo("prezzo_massimo","nome_artista", "nome_album", album.getAutore(), album.getTitolo());
        String prezzoMin = selectPrezzo("prezzo_minimo","nome_artista", "nome_album", album.getAutore(), album.getTitolo());

        prezzo = prezzo.replace(" €", "");
        prezzoMax = prezzoMax.replace(" €", "");
        prezzoMin = prezzoMin.replace(" €", "");
        String Nprezzo = album.getPrezzo().replace(" €", "");

        prezzo = prezzo.replace(",", ".");
        prezzoMax = prezzoMax.replace(",", ".");
        prezzoMin = prezzoMin.replace(",", ".");
        Nprezzo = Nprezzo.replace(",", ".");

        double p = Double.parseDouble(prezzo);
        double pM = Double.parseDouble(prezzoMax);
        double pm = Double.parseDouble(prezzoMin);
        double pp = Double.parseDouble(Nprezzo);
        LocalDateTime D = LocalDateTime.now();

        try {
            if (!conn.isValid(5)) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if(pp<p){
            if(pp<pm) {
                MyTelegramBot.Notify(album);
                prezzoMin = album.getPrezzo();
                String query = "UPDATE Albums SET prezzo_attuale = ?, prezzo_minimo = ?, data_minimo = ? WHERE nome_artista = ? AND nome_album = ?";
                try {
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, album.getPrezzo());  // Imposta il prezzo attuale
                    statement.setString(2, prezzoMin);          // Imposta il prezzo minimo
                    statement.setDate(3, java.sql.Date.valueOf(D.toLocalDate()));  // Imposta la data minima
                    statement.setString(4, album.getAutore());  // Imposta l'autore
                    statement.setString(5, album.getTitolo());  // Imposta il titolo

                    // Esegui l'aggiornamento
                    statement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;  // Restituisce false in caso di errore
                }
            }
        }
        else{
            if(pp>pM) {
                prezzoMax = album.getPrezzo();
                String query = "UPDATE Albums SET prezzo_attuale = ?, prezzo_minimo = ?, data_minimo = ? WHERE nome_artista = ? AND nome_album = ?";
                try {
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, album.getPrezzo());  // Imposta il prezzo attuale
                    statement.setString(2, prezzoMin);          // Imposta il prezzo minimo
                    statement.setDate(3, java.sql.Date.valueOf(D.toLocalDate()));  // Imposta la data minima
                    statement.setString(4, album.getAutore());  // Imposta l'autore
                    statement.setString(5, album.getTitolo());  // Imposta il titolo

                    // Esegui l'aggiornamento
                    statement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;  // Restituisce false in caso di errore
                }
            }
        }
        return true;
    }
}