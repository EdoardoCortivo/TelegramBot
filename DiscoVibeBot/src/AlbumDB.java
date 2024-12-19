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
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome_artista VARCHAR(255) NOT NULL, " +
                "nome_album VARCHAR(255) NOT NULL, " +
                "prezzo_attuale DECIMAL(10, 2), " +
                "prezzo_minimo DECIMAL(10, 2), " +
                "data_minimo DATE, " +
                "prezzo_massimo DECIMAL(10, 2), " +
                "data_massimo DATE);";;

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

    public String selectALL(String from) {
        String result = "";
        try {
            if (!conn.isValid(5)) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query = "SELECT * FROM " + from;

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    result += rs.getString(i) + "\t";
                    //if the record is too short this if add a new tabulation
                    if (rs.getString(i).length() < 8) result += "\t";
                }
                result += "\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return result;
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
        String prezzo = select("prezzo_attuale","Albums","nome_artista", "nome_album", album.getAutore(), album.getTitolo());
        String prezzoMax = select("prezzo_massimo","Albums","nome_artista", "nome_album", album.getAutore(), album.getTitolo());
        String prezzoMin = select("prezzo_minimo","Albums","nome_artista", "nome_album", album.getAutore(), album.getTitolo());

        Integer p = Integer.valueOf(prezzo.replace(" €", ""));
        Integer pM = Integer.valueOf(prezzoMax.replace(" €", ""));
        Integer pm = Integer.valueOf(prezzoMin.replace(" €", ""));
        Integer pp = Integer.valueOf(album.getPrezzo().replace(" €", ""));
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
                prezzoMin = album.getPrezzo();
                String query = "UPDATE Albums SET prezzo_attuale = ? AND prezzo_minimo = ? AND data_minimo = ?) WHERE autore = ? AND titolo = ?";
                try {
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, album.getPrezzo());
                    statement.setString(2, prezzoMin);
                    statement.setDate(3, java.sql.Date.valueOf(D.toLocalDate()));
                    statement.setString(4, album.getAutore());
                    statement.setString(5, album.getTitolo());
                    statement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        else{
            if(pp>pM) {
                prezzoMax = album.getPrezzo();
                String query = "UPDATE Albums SET prezzo_attuale = ? AND prezzo_massimo = ? AND data_massimo = ?) WHERE autore = ? AND titolo = ?";
                try {
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, album.getPrezzo());
                    statement.setString(2, prezzoMax);
                    statement.setDate(3, java.sql.Date.valueOf(D.toLocalDate()));
                    statement.setString(4, album.getAutore());
                    statement.setString(5, album.getTitolo());
                    statement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }
}