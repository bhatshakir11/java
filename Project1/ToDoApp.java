package Project1;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;

public class ToDoApp extends JFrame {
    private Connection conn;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtDesc, txtSearch;
    private JComboBox<String> comboPriority;
    private JSpinner spinnerDueDate;

    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(45, 45, 45);
    private static final Color PANEL_COLOR = new Color(60, 60, 60);
    private static final Color BUTTON_COLOR = new Color(0, 150, 136);
    private static final Color FONT_COLOR = Color.WHITE;
    private static final Color TABLE_HEADER_COLOR = new Color(50, 50, 50);
    private static final Color TABLE_ROW_COLOR = new Color(70, 70, 70);

    public ToDoApp() {
        setTitle("To-Do List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Fix: Connect to database BEFORE initializing UI
        connectDatabase();
        initUI();
        loadTasks("");
    }

    private void initUI() {
        // Top panel for adding tasks
        JPanel panelTop = new JPanel();
        panelTop.setLayout(new BoxLayout(panelTop, BoxLayout.Y_AXIS));
        panelTop.setBackground(PANEL_COLOR);
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Row 1: Description field
        JPanel panelDesc = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelDesc.setBackground(PANEL_COLOR);
        JLabel lblDesc = createStyledLabel("Description:");
        panelDesc.add(lblDesc);
        txtDesc = createStyledTextField(25);
        panelDesc.add(txtDesc);
        panelTop.add(panelDesc);

        // Row 2: Priority, Due Date, Add button
        JPanel panelDetails = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelDetails.setBackground(PANEL_COLOR);
        JLabel lblPriority = createStyledLabel("Priority:");
        panelDetails.add(lblPriority);

        comboPriority = new JComboBox<>(new String[] {"High", "Medium", "Low"});
        styleComboBox(comboPriority);
        panelDetails.add(comboPriority);

        JLabel lblDueDate = createStyledLabel("Due Date:");
        panelDetails.add(lblDueDate);

        spinnerDueDate = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerDueDate, "yyyy-MM-dd");
        spinnerDueDate.setEditor(dateEditor);
        spinnerDueDate.setValue(new Date());
        styleSpinner(spinnerDueDate);
        panelDetails.add(spinnerDueDate);

        JButton btnAdd = createStyledButton("Add Task", "add.png");
        panelDetails.add(btnAdd);
        panelTop.add(panelDetails);

        add(panelTop, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"ID", "Description", "Priority", "Due Date", "Completed"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(TABLE_ROW_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel: search and action buttons
        JPanel panelBottom = new JPanel();
        panelBottom.setLayout(new BoxLayout(panelBottom, BoxLayout.Y_AXIS));
        panelBottom.setBackground(PANEL_COLOR);
        panelBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSearch.setBackground(PANEL_COLOR);
        panelSearch.add(createStyledLabel("Search:"));
        txtSearch = createStyledTextField(20);
        panelSearch.add(txtSearch);
        JButton btnSearch = createStyledButton("Search", "search.png");
        panelSearch.add(btnSearch);
        JButton btnShowAll = createStyledButton("Show All", "refresh.png");
        panelSearch.add(btnShowAll);
        panelBottom.add(panelSearch);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelButtons.setBackground(PANEL_COLOR);
        JButton btnComplete = createStyledButton("Mark Completed", "check.png");
        JButton btnDelete = createStyledButton("Delete Task", "delete.png");
        JButton btnClear = createStyledButton("Clear All", "clear.png");
        panelButtons.add(btnComplete);
        panelButtons.add(btnDelete);
        panelButtons.add(btnClear);
        panelBottom.add(panelButtons);

        add(panelBottom, BorderLayout.SOUTH);

        // Wire up button actions
        btnAdd.addActionListener(e -> addTask());
        btnComplete.addActionListener(e -> markTaskCompleted());
        btnDelete.addActionListener(e -> deleteTask());
        btnClear.addActionListener(e -> clearTasks());
        btnSearch.addActionListener(e -> searchTasks());
        btnShowAll.addActionListener(e -> loadTasks(""));

        txtSearch.addActionListener(e -> searchTasks());
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(FONT_COLOR);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBackground(PANEL_COLOR.brighter());
        textField.setForeground(FONT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BUTTON_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(FONT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        try {
            // Try both with and without leading slash for resource path
            java.net.URL iconUrl = getClass().getResource("/Project1/icons/" + iconPath);
            if (iconUrl == null) {
                iconUrl = getClass().getResource("icons/" + iconPath);
            }
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image scaled = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaled));
                button.setHorizontalTextPosition(SwingConstants.LEFT);
                button.setIconTextGap(10);
            } else {
                System.out.println("Icon not found: " + iconPath);
            }
        } catch (Exception e) {
            System.out.println("Icon not found: " + iconPath);
        }

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(BUTTON_COLOR.brighter());
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });

        return button;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(PANEL_COLOR.brighter());
                setForeground(FONT_COLOR);
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                return this;
            }
        });
        combo.setBackground(PANEL_COLOR.brighter());
        combo.setForeground(FONT_COLOR);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void styleSpinner(JSpinner spinner) {
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        editor.getTextField().setBackground(PANEL_COLOR.brighter());
        editor.getTextField().setForeground(FONT_COLOR);
        editor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editor.getTextField().setBorder(BorderFactory.createLineBorder(BUTTON_COLOR, 1));
    }

    private void styleTable() {
        table.setBackground(TABLE_ROW_COLOR);
        table.setForeground(FONT_COLOR);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(100, 100, 100));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(FONT_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Column renderers
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(row % 2 == 0 ? TABLE_ROW_COLOR : TABLE_ROW_COLOR.darker());
                c.setForeground(FONT_COLOR);

                // Priority column styling
                if (column == 2) {
                    String priority = (String) value;
                    Color bgColor = switch (priority) {
                        case "High" -> new Color(255, 102, 102);
                        case "Medium" -> new Color(255, 178, 102);
                        case "Low" -> new Color(102, 255, 102);
                        default -> TABLE_ROW_COLOR;
                    };
                    c.setBackground(bgColor);
                    c.setForeground(Color.BLACK);
                }

                // Completed column styling
                if (column == 4) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    if ("Yes".equals(value)) {
                        setIcon(UIManager.getIcon("OptionPane.informationIcon"));
                        setText("");
                    } else {
                        setIcon(UIManager.getIcon("OptionPane.errorIcon"));
                        setText("");
                    }
                } else {
                    setIcon(null);
                }

                return c;
            }
        });

        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
    }

    // Database connection and CRUD methods
    private void connectDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:todo.db");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT, priority TEXT, due_date TEXT, completed INTEGER)");
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
        }
    }

    private void loadTasks(String search) {
        try {
            tableModel.setRowCount(0);
            String sql = "SELECT * FROM tasks";
            if (search != null && !search.trim().isEmpty()) {
                sql += " WHERE description LIKE ?";
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            if (search != null && !search.trim().isEmpty()) {
                ps.setString(1, "%" + search + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                    rs.getInt("id"),
                    rs.getString("description"),
                    rs.getString("priority"),
                    rs.getString("due_date"),
                    rs.getInt("completed") == 1 ? "Yes" : "No"
                });
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load tasks: " + e.getMessage());
        }
    }

    private void addTask() {
        String desc = txtDesc.getText().trim();
        String priority = (String) comboPriority.getSelectedItem();
        Date dueDate = (Date) spinnerDueDate.getValue();
        String dueDateStr = new SimpleDateFormat("yyyy-MM-dd").format(dueDate);
        if (desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Description cannot be empty.");
            return;
        }
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO tasks (description, priority, due_date, completed) VALUES (?, ?, ?, 0)");
            ps.setString(1, desc);
            ps.setString(2, priority);
            ps.setString(3, dueDateStr);
            ps.executeUpdate();
            ps.close();
            txtDesc.setText("");
            loadTasks("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to add task: " + e.getMessage());
        }
    }

    private void markTaskCompleted() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to mark as completed.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE tasks SET completed = 1 WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            loadTasks("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to mark task as completed: " + e.getMessage());
        }
    }

    private void deleteTask() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM tasks WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            loadTasks("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to delete task: " + e.getMessage());
        }
    }

    private void clearTasks() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all tasks?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM tasks");
            stmt.close();
            loadTasks("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to clear tasks: " + e.getMessage());
        }
    }

    private void searchTasks() {
        String search = txtSearch.getText().trim();
        loadTasks(search);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", BACKGROUND_COLOR);
            UIManager.put("nimbusBlueGrey", PANEL_COLOR);
            UIManager.put("control", PANEL_COLOR);
        } catch (Exception e) { }

        SwingUtilities.invokeLater(() -> {
            ToDoApp app = new ToDoApp();
            app.setVisible(true);
        });
    }
}