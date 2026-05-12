package org.example.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import org.example.db.DatabaseManager;
import org.example.model.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinanceFlowNoPattern extends JFrame {


    private static final Color BG_COLOR = new Color(15, 15, 18);
    private static final Color SIDEBAR_COLOR = new Color(22, 22, 26);
    private static final Color CARD_COLOR = new Color(28, 28, 33);
    private static final Color ACCENT_COLOR = new Color(59, 130, 246);
    private static final Color TEXT_SECONDARY = new Color(160, 160, 165);

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField amountField, descField, searchField;
    private JComboBox<Category> categoryBox;
    private JComboBox<User> userBox;
    private JComboBox<String> typeBox;
    private JLabel balanceLabel, incomeLabel, expenseLabel;
    private JPanel mainContent, goalsContainer;
    private CardLayout cardLayout;

    public FinanceFlowNoPattern() {
        setupTheme();
        setTitle("FinanceFlow (БЕЗ ПАТТЕРНА)");
        setSize(1250, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadDropdownData();
        refreshData();
    }

    private void setupTheme() {
        try { UIManager.setLookAndFeel(new FlatDarkLaf()); } catch (Exception e) { e.printStackTrace(); }
    }

    private void initUI() {
        setLayout(new BorderLayout());


        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));
        sidebar.add(createMenuButton("🏠  Обзор", "DASHBOARD", true));
        sidebar.add(createMenuButton("📊  Аналитика", "CHARTS", false));
        sidebar.add(createMenuButton("🎯  Мои цели", "GOALS", false));
        sidebar.add(createMenuButton("👥  Команда", "USERS", false));
        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.add(createDashboard(), "DASHBOARD");
        mainContent.add(createAnalyticsPage(), "CHARTS");
        mainContent.add(createGoalsPage(), "GOALS");
        mainContent.add(createUserPage(), "USERS");
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createDashboard() {
        JPanel panel = new JPanel(new BorderLayout(0, 30));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));


        JPanel header = new JPanel(new GridLayout(1, 3, 30, 0));
        header.setOpaque(false);
        balanceLabel = createStatCard("ОБЩИЙ БАЛАНС", header, CARD_COLOR);
        incomeLabel = createStatCard("ДОХОДЫ", header, new Color(25, 40, 30));
        expenseLabel = createStatCard("РАСХОДЫ", header, new Color(40, 25, 25));
        panel.add(header, BorderLayout.NORTH);


        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_COLOR);
        form.setBorder(new EmptyBorder(25, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(8, 12, 8, 12); gbc.weightx = 1.0;

        userBox = new JComboBox<>(); amountField = new JTextField();
        descField = new JTextField(); categoryBox = new JComboBox<>();
        typeBox = new JComboBox<>(new String[]{"Доход", "Расход"});
        JButton addBtn = new JButton("Добавить запись");
        addBtn.addActionListener(e -> addTransaction()); // Логика SQL внутри!

        gbc.gridy = 0; gbc.gridx = 0; form.add(new JLabel("Кто"), gbc);
        gbc.gridx = 1; form.add(new JLabel("Сумма"), gbc);
        gbc.gridx = 2; form.add(new JLabel("Категория"), gbc);
        gbc.gridx = 3; form.add(new JLabel("Тип"), gbc);
        gbc.gridy = 1; gbc.gridx = 0; form.add(userBox, gbc);
        gbc.gridx = 1; form.add(amountField, gbc);
        gbc.gridx = 2; form.add(categoryBox, gbc);
        gbc.gridx = 3; form.add(typeBox, gbc);
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 3; form.add(new JLabel("Описание"), gbc);
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 3; form.add(descField, gbc);
        gbc.gridx = 3; gbc.gridwidth = 1; form.add(addBtn, gbc);


        tableModel = new DefaultTableModel(new String[]{"ID", "Владелец", "Дата", "Тип", "Категория", "Описание", "Сумма"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(50);
        table.setDefaultRenderer(Object.class, new CustomCellRenderer());

        searchField = new JTextField();
        searchField.addCaretListener(e -> refreshData());

        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);
        center.add(form, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        return panel;
    }



    private void addTransaction() {
        // Прямое соединение и SQL-запрос в обработчике кнопки
        String sql = "INSERT INTO transactions (amount, type, date, category_id, user_id, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, Double.parseDouble(amountField.getText()));
            pstmt.setString(2, typeBox.getSelectedIndex() == 0 ? "INCOME" : "EXPENSE");
            pstmt.setString(3, LocalDate.now().toString());
            pstmt.setInt(4, ((Category) categoryBox.getSelectedItem()).getId());
            pstmt.setInt(5, ((User) userBox.getSelectedItem()).getId());
            pstmt.setString(6, descField.getText());

            pstmt.executeUpdate();
            refreshData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка сохранения в БД напрямую!");
        }
    }

    private void refreshData() {
        tableModel.setRowCount(0);
        double bal = 0, inc = 0, exp = 0;

        // Сложный запрос с JOIN прямо в методе обновления интерфейса
        String sql = "SELECT t.*, u.name as user_name, c.name as cat_name " +
                "FROM transactions t " +
                "JOIN users u ON t.user_id = u.id " +
                "JOIN categories c ON t.category_id = c.id";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                double a = rs.getDouble("amount");
                String type = rs.getString("type");
                String desc = rs.getString("description");
                if (searchField != null && !desc.toLowerCase().contains(searchField.getText().toLowerCase())) continue;

                if (type.equals("INCOME")) { inc += a; bal += a; }
                else { exp += a; bal -= a; }

                tableModel.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("user_name"), rs.getString("date"),
                        type.equals("INCOME") ? "ДОХОД" : "РАСХОД", rs.getString("cat_name"), desc, a + " ₽"
                });
            }
            balanceLabel.setText(bal + " ₽");
            incomeLabel.setText("+ " + inc);
            expenseLabel.setText("- " + exp);


            updateAnalyticsWithSQL();
            updateGoalsWithSQL(bal);

        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void updateAnalyticsWithSQL() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        String sql = "SELECT c.name, SUM(t.amount) as total FROM transactions t " +
                "JOIN categories c ON t.category_id = c.id WHERE t.type='EXPENSE' GROUP BY c.name";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) dataset.setValue(rs.getString("name"), rs.getDouble("total"));

            JFreeChart chart = ChartFactory.createPieChart(null, dataset, false, true, false);
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ₽"));
            chart.setBackgroundPaint(BG_COLOR);
            plot.setBackgroundPaint(BG_COLOR);

            JPanel p = (JPanel) mainContent.getComponent(1);
            p.removeAll();
            p.add(new ChartPanel(chart), BorderLayout.CENTER);
            p.revalidate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void updateGoalsWithSQL(double currentBalance) {
        if (goalsContainer == null) return;
        goalsContainer.removeAll();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM goals")) {
            while (rs.next()) {
                String name = rs.getString("name");
                double target = rs.getDouble("target_amount");
                double progress = Math.min(100, (currentBalance / target) * 100);
                JProgressBar bar = new JProgressBar(0, 100);
                bar.setValue((int) progress);
                bar.setString(name + ": " + (int)progress + "%");
                bar.setStringPainted(true);
                goalsContainer.add(bar);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadDropdownData() {
        categoryBox.removeAllItems(); userBox.removeAllItems();
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT * FROM categories");
            while (rs1.next()) categoryBox.addItem(new Category(rs1.getInt("id"), rs1.getString("name")));
            ResultSet rs2 = conn.createStatement().executeQuery("SELECT * FROM users");
            while (rs2.next()) userBox.addItem(new User(rs2.getInt("id"), rs2.getString("name")));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private JLabel createStatCard(String t, JPanel p, Color c) {
        JPanel card = new JPanel(new BorderLayout()); card.setBackground(c); card.setBorder(new EmptyBorder(20,20,20,20));
        JLabel title = new JLabel(t); title.setForeground(TEXT_SECONDARY);
        JLabel val = new JLabel("0.0 ₽"); val.setFont(new Font("Sans", Font.BOLD, 22)); val.setForeground(Color.WHITE);
        card.add(title, BorderLayout.NORTH); card.add(val, BorderLayout.SOUTH);
        p.add(card); return val;
    }

    private JButton createMenuButton(String text, String card, boolean act) {
        JButton b = new JButton(text); b.setPreferredSize(new Dimension(230, 50));
        b.addActionListener(e -> cardLayout.show(mainContent, card));
        return b;
    }

    private JPanel createAnalyticsPage() { return new JPanel(new BorderLayout()); }
    private JPanel createGoalsPage() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(BG_COLOR);
        goalsContainer = new JPanel(); goalsContainer.setLayout(new BoxLayout(goalsContainer, BoxLayout.Y_AXIS));
        p.add(new JLabel("Цели (Загружаются напрямую из SQL)"), BorderLayout.NORTH);
        p.add(goalsContainer, BorderLayout.CENTER);
        return p;
    }
    private JPanel createUserPage() { return new JPanel(); }

    static class CustomCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean is, boolean h, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t, v, is, h, r, c);
            String type = (String) t.getModel().getValueAt(r, 3);
            comp.setForeground(type.equals("ДОХОД") ? new Color(100, 220, 140) : new Color(255, 110, 110));
            comp.setBackground(BG_COLOR);
            return comp;
        }
    }
}