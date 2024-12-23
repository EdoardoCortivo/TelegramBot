import java.sql.*;
import java.time.LocalDateTime;

public class salvaDB {

    private Connection conn;

    public salvaDB(String address, String port, String username, String password) {
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
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Salva (" +
                "Id_Utente BIGINT NOT NULL, " +
                "nome_artista VARCHAR(255) NOT NULL, " +
                "nome_album VARCHAR(255) NOT NULL, " +
                "formato VARCHAR(20) NOT NULL, " +
                "venditore VARCHAR(20) NOT NULL, " +
                "PRIMARY KEY (Id_Utente, nome_artista, nome_album, formato, venditore), " +
                "FOREIGN KEY (Id_Utente) REFERENCES Utenti (id), " +
                "FOREIGN KEY (nome_artista, nome_album, formato, venditore) REFERENCES Albums (nome_artista, nome_album, formato, venditore));";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("Tabella 'Salva' creata o già esistente.");
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

    public String selectScraper(String where, String where2 ,String is, String is2) {
        String result = "";
        try {
            if (!conn.isValid(5)) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query = "SELECT venditore FROM Albums WHERE " + where + " = ?" + " AND " + where2 + " = ? ";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, is);
            statement.setString(2, is2);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    result += rs.getString(i) + "";
                    //if the record is too short this if add a new tabulation
                    if (rs.getString(i).length() < 8) result += "";
                }
                result += "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return result;
    }

    public String selectFormato(String where, String where2 ,String is, String is2) {
        String result = "";
        try {
            if (!conn.isValid(5)) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query = "SELECT formato FROM Albums WHERE " + where + " = ?" + " AND " + where2 + " = ? ";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, is);
            statement.setString(2, is2);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    result += rs.getString(i) + "";
                    //if the record is too short this if add a new tabulation
                    if (rs.getString(i).length() < 8) result += "";
                }
                result += "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return result;
    }


    public String select(String from, String where, long is) {
        String result = "";
        try {
            if (!conn.isValid(5)) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query = "SELECT nome_artista, nome_album, formato, venditore FROM " + from + " WHERE " + where + " = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setLong(1, is);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    result += rs.getString(i) + "____";
                    //if the record is too short this if add a new tabulation
                    if (rs.getString(i).length() < 8) result += "";
                }
                result += "69104";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return result;
    }

    public String selectUtente(String from, String where, String where2, String is, String is2) {
        String result = "";
        try {
            if (!conn.isValid(5)) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query = "SELECT Id_Utente FROM " + from + " WHERE " + where + " = ?" + " AND " + where2 + " = ? ";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, is);
            statement.setString(2, is2);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    result += rs.getString(i) + "____";
                    //if the record is too short this if add a new tabulation
                    if (rs.getString(i).length() < 8) result += "";
                }
                result += "69104";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return result;
    }

    public String selectAll(String from) {
        String result = "";
        try {
            if (!conn.isValid(5)) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query = "SELECT nome_artista, nome_album FROM " + from;

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    result += rs.getString(i) + "\n";
                    //if the record is too short this if add a new tabulation
                    if (rs.getString(i).length() < 8) result += "\n";
                }
                result += "69104";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return result;
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------\\
    public boolean insertSave(long chat_id, Album album) {
        try {
            if (!conn.isValid(5)) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        String query = "INSERT INTO Salva (Id_Utente, nome_artista, nome_album, venditore, formato) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setLong(1, chat_id);
            statement.setString(2, album.getAutore());
            statement.setString(3, album.getTitolo());
            statement.setString(4, album.getVenditore());
            statement.setString(5, album.getFormato());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean deleteUtente(long chat_id) {
        try {
            if (!conn.isValid(5)) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        String query = "DELETE FROM Utenti WHERE id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setLong(1, chat_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}