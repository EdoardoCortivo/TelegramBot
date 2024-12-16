import java.sql.*;
import java.time.LocalDateTime;

public class AlbumDB {

    /**
     * Represents a connection to a database.
     *
     * The conn variable is used to establish a connection to a database and execute SQL queries.
     *
     * Methods in the DB class utilize the conn variable to execute queries such as SELECT, INSERT, UPDATE, and DELETE.
     *
     * This variable should be initialized with a valid Connection object before using any of the database-related methods.
     */
    private Connection conn;


    /**
     * Represents a connection to a database.
     */
    public AlbumDB(String address, String port, String databaseName, String username, String password) {
        //stringa di connessione -> jdbc:mysql://127.0.0.1:3306/nomeDB
        String dbConnectionString = "jdbc:mysql://" + address + ":" + port + "/" + databaseName;
        try {
            conn = DriverManager.getConnection(dbConnectionString, username, password);
            if (conn != null)
                System.out.println("connessione avvenuta");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    /**
     * Executes a SELECT query on the database.
     *
     * @param what   the column(s) to select
     * @param from   the table(s) to select from
     * @param where  the condition to filter the result
     * @param is     the value to compare in the WHERE clause
     * @return a string representation of the selected data, formatted as tab-separated values
     * @throws SQLException if an SQL exception occurs while executing the query
     */
    public String select(String what, String from, String where, String is) {
        String result = "";
        try {
            if (!conn.isValid(5)) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        String query = "SELECT " + what + " FROM " + from + " WHERE " + where + " = ?" ;

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, is);
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

    /**
     * Executes a SELECT query on the database to retrieve all records from a specified table.
     *
     * @param from the name of the table to select from
     * @return a string representation of the selected data, formatted as tab-separated values
     * @throws SQLException if an SQL exception occurs while executing the query
     */
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
        String query = "INSERT INTO Albums (Nome_Artista, Nome_Album, formato, Prezzo_Attuale, immagine, venditore, Prezzo_Minimo, Prezzo_Massimo, Data_Minimo, Data_Massimo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
        //String prezzo = select("Prezzo_Attuale","Albums","Nome_Artista = " + album.getAutore() + "AND Nome_Album = " + album.getTitolo(),);
        try {
            if (!conn.isValid(5)) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        String query = "UPDATE Albums SET prezzo = ? WHERE autore = ? AND titolo = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, album.getPrezzo());
            statement.setString(2, album.getAutore());
            statement.setString(2, album.getTitolo());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}