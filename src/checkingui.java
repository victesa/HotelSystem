import javax.swing.*;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class checkingui extends JFrame {

    // Declare instance variables
    private JTable table;
    private String[] columnNames;
    private Connection connection;
    private JdbcRowSet jdbcRowSet;

    private  int columnCount;

    private JButton reloadButton;


    public checkingui() {
        super("Hotel Checkin");

        // Define JDBC driver and database URL
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/Hotel_system";
        String user = "root";  // Define database credentials
        String password = "";




        try  {
            // Load JDBC driver
            Class.forName(driver);

            // Create connection to database
            connection = DriverManager.getConnection(url, user, password);

            // Create a new RowSetFactory and JdbcRowSet
            RowSetFactory rowSetFactory = RowSetProvider.newFactory();
            jdbcRowSet = rowSetFactory.createJdbcRowSet();

            // Set properties of the JdbcRowSet to query the "checkin" table
            jdbcRowSet.setUrl(url);
            jdbcRowSet.setUsername(user);
            jdbcRowSet.setPassword(password);
            jdbcRowSet.setCommand("SELECT * FROM checkin");
            jdbcRowSet.execute();

            // Create a JTable to display the data
            ResultSetMetaData metaData = jdbcRowSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = metaData.getColumnName(i + 1);
            }
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            while (jdbcRowSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    rowData[i] = jdbcRowSet.getObject(i + 1);
                }
                model.addRow(rowData);
            }
            table = new JTable(model);

            // Add the JTable to a JScrollPane and add the JScrollPane to the frame
            JScrollPane scrollPane = new JScrollPane(table);
            getContentPane().add(scrollPane, BorderLayout.CENTER);
            setSize(800, 600);
            setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        // Add a button to insert a new record
        JButton insertButton = new JButton("Insert Record");
        insertButton.addActionListener(e -> {
            String FName = JOptionPane.showInputDialog("Enter First name:");
            String SName = JOptionPane.showInputDialog("Enter Second name:");
            String RegNo = JOptionPane.showInputDialog("Enter Registration number:");
            String Checkin = JOptionPane.showInputDialog("Enter CheckinDate:");
            String RoomNo = JOptionPane.showInputDialog("Enter Room Number:");
            String Numofpersons = JOptionPane.showInputDialog("Enter the number of people :");



            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO checkin( FName, SName, RegNo ,Checkin ,RoomNo , Numofpersons ) VALUES (?, ?, ?, ?, ? ,?)")) {
                statement.setString(1, FName);
                statement.setString(2, SName);
                statement.setString(3, RegNo);
                statement.setString(4,Checkin);
                statement.setString(5, RoomNo);
                statement.setString(6,Numofpersons);


                statement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Record inserted successfully.");
                // Reload the JdbcRowSet to reflect the changes
                jdbcRowSet.setCommand("SELECT * FROM checkin");
                jdbcRowSet.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        getContentPane().add(insertButton, BorderLayout.PAGE_START);


        // Add a button to delete a record
        JButton deleteButton = new JButton("Delete Record");
        deleteButton.addActionListener(e -> {
            String regNoToDelete = JOptionPane.showInputDialog("Enter Registration Number to Delete:");
            if (regNoToDelete != null && !regNoToDelete.isEmpty()) {
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM checkin WHERE reg_no = ?")) {
                    statement.setString(1, regNoToDelete);
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected == 1) {
                        JOptionPane.showMessageDialog(null, "Record deleted successfully.");
                        // Reload the JdbcRowSet to reflect the changes
                        jdbcRowSet.setCommand("SELECT * FROM checkin");
                        jdbcRowSet.execute();
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        model.setRowCount(0);
                        while (jdbcRowSet.next()) {
                            Object[] rowData = new Object[columnCount];
                            for (int i = 0; i < columnCount; i++) {
                                rowData[i] = jdbcRowSet.getObject(i + 1);
                            }
                            model.addRow(rowData);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Record not found.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        getContentPane().add(deleteButton, BorderLayout.PAGE_END);

        reloadButton = new JButton("Reload");
        reloadButton.addActionListener(e -> {
            try {
                // Reload the JdbcRowSet to reflect any changes
                jdbcRowSet.setCommand("SELECT * FROM checkin");
                jdbcRowSet.execute();

                // Clear the existing data from the JTable
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);

                // Add the updated data to the JTable
                ResultSetMetaData metaData = jdbcRowSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (jdbcRowSet.next()) {
                    Object[] rowData = new Object[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        rowData[i] = jdbcRowSet.getObject(i + 1);
                    }
                    model.addRow(rowData);
                }

                JOptionPane.showMessageDialog(null, "Data reloaded successfully.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        getContentPane().add(reloadButton, BorderLayout.LINE_START);



        setSize(800, 600);
        setVisible(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }



    public String[] getColumnNames() {
        return columnNames;
    }

    public static void main(String[] args) {
        new checkingui();
    }
}
