import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

abstract class Item {
    protected String itemName;
    protected double price;

    public Item(String itemName, double price) {
        this.itemName = itemName;
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public double getPrice() {
        return price;
    }

    public abstract String getItemDetails();
}

class GroceryItem extends Item {
    protected String category;

    public GroceryItem(String itemName, double price, String category) {
        super(itemName, price);
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public String getItemDetails() {
        return itemName + " (" + category + "): $" + price;
    }
}

class LoginFrame extends JFrame implements ActionListener {
    private JLabel userLabel, passwordLabel;
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Grocery Store Login");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        userLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        userField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);

        add(panel);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String username = userField.getText();
        String password = new String(passwordField.getPassword());
        if (username.equals("admin") && password.equals("password")) {
            new ItemManagementFrame();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class ItemManagementFrame extends JFrame implements ActionListener {
    private JList<Item> itemList;
    private DefaultListModel<Item> listModel;
    private JTextField itemNameField, priceField, categoryField;
    private JButton addButton, viewButton, deleteButton, searchButton;

    public ItemManagementFrame() {
        setTitle("Grocery Store Item Management");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        itemList = new JList<Item>();
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listModel = new DefaultListModel<Item>();
        itemList.setModel(listModel);

        JScrollPane scrollPane = new JScrollPane(itemList);

        JPanel itemPanel = new JPanel(new GridLayout(3, 2));
        itemPanel.add(new JLabel("Item Name:"));
        itemNameField = new JTextField(20);
        itemPanel.add(itemNameField);
        itemPanel.add(new JLabel("Price:"));
        priceField = new JTextField(20);
        itemPanel.add(priceField);
        itemPanel.add(new JLabel("Category:"));
        categoryField = new JTextField(20);
        itemPanel.add(categoryField);

        addButton = new JButton("Add");
        addButton.addActionListener(this);
        viewButton = new JButton("View");
        viewButton.addActionListener(this);
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(itemPanel, BorderLayout.SOUTH);
        panel.add(buttonPanel, BorderLayout.NORTH);

        add(panel);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            try {
                String itemName = itemNameField.getText();
                double price = Double.parseDouble(priceField.getText());
                String category = categoryField.getText();
                GroceryItem item = new GroceryItem(itemName, price, category);
                addItemToDatabase(item);
                listModel.addElement(item);
                itemNameField.setText("");
                priceField.setText("");
                categoryField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding item to database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == viewButton) {
            Item selectedItem = itemList.getSelectedValue();
            if (selectedItem != null) {
                JOptionPane.showMessageDialog(this, selectedItem.getItemDetails(), "Item Details", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == deleteButton) {
            Item selectedItem = itemList.getSelectedValue();
            if (selectedItem != null) {
                int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    listModel.removeElement(selectedItem);
                    deleteItemFromDatabase(selectedItem);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == searchButton) {
            String searchQuery = JOptionPane.showInputDialog(this, "Enter search query:", "Search", JOptionPane.QUESTION_MESSAGE);
            if (searchQuery != null && !searchQuery.isEmpty()) {
                try {
                    listModel.clear();
                    ResultSet resultSet = searchItemsInDatabase(searchQuery);
                    while (resultSet.next()) {
                        String itemName = resultSet.getString("item_name");
                        double price = resultSet.getDouble("price");
                        String category = resultSet.getString("category");
                        GroceryItem item = new GroceryItem(itemName, price, category);
                        listModel.addElement(item);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error searching for items in database.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void addItemToDatabase(GroceryItem item) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocery_store", "root", "password");
            statement = connection.prepareStatement("INSERT INTO items (item_name, price, category) VALUES (?, ?, ?)");
            statement.setString(1, item.getItemName());
            statement.setDouble(2, item.getPrice());
            statement.setString(3, item.getCategory());
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private void deleteItemFromDatabase(Item item) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocery_store", "root", "password");
            statement = connection.prepareStatement("DELETE FROM items WHERE item_name = ?");
            statement.setString(1, item.getItemName());
            statement.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting item from database.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private ResultSet searchItemsInDatabase(String searchQuery) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocery_store", "root", "password");
            statement = connection.prepareStatement("SELECT * FROM items WHERE item_name LIKE ?");
            statement.setString(1, "%" + searchQuery + "%");
            resultSet = statement.executeQuery();
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return resultSet;
    }

    public static void main(String[] args) {
        LoginFrame loginFrame = new LoginFrame();
        ItemManagementFrame store = new ItemManagementFrame();
    }
}