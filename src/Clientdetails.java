import javax.sql.rowset.JdbcRowSet;  // Import JDBC classes
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;

public class Clientdetails{

    static final String DRIVER = "com.mysql.cj.jdbc.Driver";  // Define JDBC driver and database URL
    static final String URL = "jdbc:mysql://localhost:3306/Hotel_system";
    static final String USER = "root";  // Define database credentials
    static final String PASSWORD = "";

    public static void main (String[]args) {

        Connection connection = null;
        JdbcRowSet jdbcRowSet = null;

        try {
            Class.forName(DRIVER);  // Load JDBC driver

            connection = DriverManager.getConnection(URL, USER, PASSWORD);  // Create connection to database

            // Create a new RowSetFactory and JdbcRowSet
            RowSetFactory rowSetFactory = RowSetProvider.newFactory();
            jdbcRowSet = rowSetFactory.createJdbcRowSet();

            // Set properties of the JdbcRowSet to query the "clientdetails" table
            jdbcRowSet.setUrl(URL);
            jdbcRowSet.setUsername(USER);
            jdbcRowSet.setPassword("");
            jdbcRowSet.setCommand("SELECT * FROM Clientdetails");
            jdbcRowSet.execute();

            // Move to the insert row and update the columns with new values
            jdbcRowSet.moveToInsertRow();
            jdbcRowSet.updateInt("ID",48217392);
            jdbcRowSet.updateString("FName","Michael");
            jdbcRowSet.updateString("SName","Weymna" );
            jdbcRowSet.updateInt("RegNo",3865);
            jdbcRowSet.updateDate("CheckinDate",java.sql.Date.valueOf("2023-02-29") );
            jdbcRowSet.updateInt("Roomno",6);
            jdbcRowSet.insertRow();


            System.out.printf("%-15s%-15s%-15s%-15s%-15s%-15s%n","ID" ,  "FName", "SName",
                    "RegNo", "Checkin", "Roomno");

            while (jdbcRowSet.next()) {
                System.out.printf("%-15s%-15s%-15s%-15d%-15d%-15s%n",
                        jdbcRowSet.getInt("ID"),
                        jdbcRowSet.getString("FName"),
                        jdbcRowSet.getString("SName"),
                        jdbcRowSet.getInt("Roomno"),
                        jdbcRowSet.getInt("RegNo"),
                        jdbcRowSet.getDate("CheckinDate"));
            }



        }


        catch
        (Exception ex) {
            ex.printStackTrace();  // Print error message and stack trace
        }



    }

    // Define constructor and method to get column names
    public Clientdetails(){
        String[] columns = new String[6];
        columns[0]="ID";
        columns[1]="FName";
        columns[2]="SName";
        columns[3]="RoomNo";
        columns[4]="RegNo";
        columns[5]="CheckinDate";
    }

    // Return the array of column names
    public String[] columns(){
        return columns();
    }
}
