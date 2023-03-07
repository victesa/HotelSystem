import javax.swing.*;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;


public class ClientdetailsGUI extends JFrame {
    private JTable table;
    private Connection connection;

    public ClientdetailsGUI() {
        super("Client Details");

        // Define JDBC driver and database URL
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/Hotel_system";
        String user = "root";  // Define database credentials
        String password = "";

        try {
            // Load JDBC driver
            Class.forName(driver);

            // Create connection to database
            connection = DriverManager.getConnection(url, user, password);

            // Create a new RowSetFactory and JdbcRowSet
            RowSetFactory rowSetFactory = RowSetProvider.newFactory();
            JdbcRowSet jdbcRowSet = rowSetFactory.createJdbcRowSet();

            // Set properties of the JdbcRowSet to query the "clientdetails" table
            jdbcRowSet.setUrl(url);
            jdbcRowSet.setUsername(user);
            jdbcRowSet.setPassword(password);
            jdbcRowSet.setCommand("SELECT * FROM Clientdetails");
            jdbcRowSet.execute();

            // Create a JTable to display the data
            ResultSetMetaData metaData = jdbcRowSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = metaData.getColumnName(i + 1);
            }
            Object[][] data = new Object[100][columnCount];
            int rowCount = 0;
            while (jdbcRowSet.next()) {
                for (int i = 0; i < columnCount; i++) {
                    data[rowCount][i] = jdbcRowSet.getObject(i + 1);
                }
                rowCount++;
            }
            table = new JTable(data, columnNames);

            // Add the JTable to a JScrollPane and add the JScrollPane to the frame
            JScrollPane scrollPane = new JScrollPane(table);
            getContentPane().add(scrollPane, BorderLayout.CENTER);


            // Add a button to insert a new record
            JButton insertButton = new JButton("Insert Record");
            insertButton.addActionListener(e -> {
                String ID = JOptionPane.showInputDialog("Enter ID number:");
                String FName = JOptionPane.showInputDialog("Enter first name:");
                String SName = JOptionPane.showInputDialog("Enter last name:");
                String RoomNo = JOptionPane.showInputDialog("Enter Room Number:");
                String RegNo = JOptionPane.showInputDialog("Enter Registration number:");
                String CheckinDate = JOptionPane.showInputDialog("Enter CheckinDate:");

                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Clientdetails ( ID, FName, SName, RoomNo ,RegNo ,CheckinDate ) VALUES (?, ?, ?, ?, ? ,?)")) {
                    statement.setString(1, ID);
                    statement.setString(2, FName);
                    statement.setString(3, SName);
                    statement.setString(4, RoomNo);
                    statement.setString(5, RegNo);
                    statement.setString(6,CheckinDate);

                    statement.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Record inserted successfully.");
                    // Reload the JdbcRowSet to reflect the changes
                    jdbcRowSet.setCommand("SELECT * FROM Clientdetails");
                    jdbcRowSet.execute();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
            getContentPane().add(insertButton, BorderLayout.PAGE_START);



            // Add a button to reload the data from the database
            JButton reloadButton = new JButton("Reload Data");
            int finalRowCount = rowCount;
            reloadButton.addActionListener(e -> {
                try {
                    // Refresh the data in the JdbcRowSet
                    jdbcRowSet.setCommand("SELECT * FROM Clientdetails");
                    jdbcRowSet.execute();

                    // Clear the JTable and reload the data
                    Object[][] newdata = new Object[finalRowCount][columnCount];
                    int rowIndex = 0;
                    jdbcRowSet.beforeFirst();
                    while (jdbcRowSet.next()) {
                        for (int i = 0; i < columnCount; i++) {
                            data[rowIndex][i] = jdbcRowSet.getObject(i + 1);
                        }
                        rowIndex++;
                    }

                    DefaultTableModel model = new DefaultTableModel();
                    table.setModel(new DefaultTableModel(data, columnNames));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
            getContentPane().add(reloadButton, BorderLayout.LINE_END);


            // Add a button to delete a record
            JButton deleteButton = new JButton("Delete Record");
            deleteButton.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int id = (int) table.getValueAt(selectedRow, 0);
                    try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Clientdetails WHERE id = ?")) {
                        statement.setInt(1, id);
                        statement.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Record deleted successfully.");
                        // Reload the JdbcRowSet to reflect the changes
                        jdbcRowSet.setCommand("SELECT * FROM Clientdetails");
                        jdbcRowSet.execute();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a record to delete.");
                }
            });
            getContentPane().add(deleteButton, BorderLayout.PAGE_END);

            setSize(800, 600);
            setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new ClientdetailsGUI();
    }
}