import java.sql.*;
import java.time.LocalDateTime;

public class UtenteDB {

    private Connection conn;

    public UtenteDB(String address, String port, String databaseName, String username, String password) {
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
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Utenti (" +
                "id BIGINT NOT NULL PRIMARY KEY);";
        ;

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("Tabella 'Utenti' creata o già esistente.");
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

    public String select(String what, String from, String where, String where2, String is, String is2) {
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
    public boolean insertUser(long chat_id) {
        try {
            if (!conn.isValid(5)) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        String query = "INSERT INTO User (chat_id) VALUES (?)";
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