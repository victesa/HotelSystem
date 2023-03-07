import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;

public class Hotelcheckin {

    // Define constants for database connection
    static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String URL = "jdbc:mysql://localhost:3306/Hotel_system";
    static final String USER = "root";
    static final String PASSWORD = "";

    public static void main (String[]args) {

        // Initialize necessary variables
        Connection connection = null;
        JdbcRowSet jdbcRowSet = null;

        try {
            // Load the driver class
            Class.forName(DRIVER);

            // Establish database connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Create RowSetFactory and JdbcRowSet objects
            RowSetFactory rowSetFactory = RowSetProvider.newFactory();
            jdbcRowSet = rowSetFactory.createJdbcRowSet();

            // Set connection details and execute query
            jdbcRowSet.setUrl(URL);
            jdbcRowSet.setUsername(USER);
            jdbcRowSet.setPassword("");
            jdbcRowSet.setCommand("SELECT * FROM checkin");
            jdbcRowSet.execute();

            // Insert a new row into the table
            jdbcRowSet.moveToInsertRow();
            jdbcRowSet.updateString("FName","Daniel");
            jdbcRowSet.updateString("SName","Karenzi" );
            jdbcRowSet.updateInt("RegNo",4537);
            jdbcRowSet.updateDate("Checkin",java.sql.Date.valueOf("2023-02-11") );
            jdbcRowSet.updateInt("Roomno",9);
            jdbcRowSet.updateInt("Numofpersons",5);
            jdbcRowSet.insertRow();

            // Print the column names
            System.out.printf("%-15s%-15s%-15s%-15s%-15s%-15s%n", "FName", "SName",
                    "RegNo", "Checkin", "Roomno", "Numofpersons");

            // Loop through the table and print the values
            while (jdbcRowSet.next()) {
                System.out.printf("%-15s%-15s%-15d%-15s%-15d%-15d%n",
                        jdbcRowSet.getString("FName"),
                        jdbcRowSet.getString("SName"),
                        jdbcRowSet.getInt("RegNo"),
                        jdbcRowSet.getDate("Checkin"),
                        jdbcRowSet.getInt("Roomno"),
                        jdbcRowSet.getInt("Numofpersons"));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    // Define constructor and method to get column names
    public Hotelcheckin(){
        // Initialize an array to store column names
        String[] columns = new String[6];
        columns[0]="FName";
        columns[1]="SName";
        columns[2]="RegNo";
        columns[3]="Checkin";
        columns[4]="RoomNo";
        columns[5]="Numofpersons";
    }

    // Return the array of column names
    public String[] columns(){
        return columns();
    }

}
