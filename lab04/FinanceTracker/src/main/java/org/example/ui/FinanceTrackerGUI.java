package org.example.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import org.example.mapper.*;
import org.example.model.*;
import org.jfree.chart.*;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FinanceTrackerGUI extends JFrame {

    
    private static final Color BG_COLOR = new Color(15, 15, 18);
    private static final Color SIDEBAR_COLOR = new Color(22, 22, 26);
    private static final Color CARD_COLOR = new Color(28, 28, 33);
    private static final Color ACCENT_COLOR = new Color(59, 130, 246);
    private static final Color TEXT_SECONDARY = new Color(160, 160, 165);
    private static final Color SUCCESS_COLOR = new Color(74, 222, 128);
    private static final Color DANGER_COLOR = new Color(248, 113, 113);

    private final TransactionMapper transactionMapper = new TransactionMapper();
    private final CategoryMapper categoryMapper = new CategoryMapper();
    private final UserMapper userMapper = new UserMapper();
    private final GoalMapper goalMapper = new GoalMapper();

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField amountField, descField, searchField;
    private JComboBox<Category> categoryBox;
    private JComboBox<User> userBox;
    private JComboBox<String> typeBox;
    private JLabel balanceLabel, incomeLabel, expenseLabel;
    private JPanel mainContent, sidebar, goalsContainer;
    private CardLayout cardLayout;

    public FinanceTrackerGUI() {
        setupTheme();
        setTitle("FinanceFlow Pro — Учёт финансов");
        setSize(1250, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadDropdownData();
        refreshData();
    }

    private void setupTheme() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIManager.put("Button.arc", 16);
            UIManager.put("Component.arc", 16);
            UIManager.put("TextComponent.arc", 16);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void initUI() {
        setLayout(new BorderLayout());

        
        sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(45, 45, 50)));

        JLabel logo = new JLabel("FinanceFlow");
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setForeground(ACCENT_COLOR);
        logo.setBorder(new EmptyBorder(30, 0, 40, 0));
        sidebar.add(logo);

        sidebar.add(createMenuButton("🏠  Обзор", "DASHBOARD", true));
        sidebar.add(createMenuButton("📊  Аналитика", "CHARTS", false));
        sidebar.add(createMenuButton("🎯  Мои цели", "GOALS", false));
        sidebar.add(createMenuButton("👥  Команда", "USERS", false));

        add(sidebar, BorderLayout.WEST);

        
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(BG_COLOR);

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
        balanceLabel = createStatCard("ОБЩИЙ БАЛАНС", "💰", header, CARD_COLOR);
        incomeLabel = createStatCard("ДОХОДЫ", "📈", header, new Color(25, 40, 30));
        expenseLabel = createStatCard("РАСХОДЫ", "📉", header, new Color(40, 25, 25));
        panel.add(header, BorderLayout.NORTH);

        
        JPanel center = new JPanel(new BorderLayout(0, 30));
        center.setOpaque(false);

        // Форма добавления 
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_COLOR);
        form.setBorder(new EmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.weightx = 1.0;

        userBox = new JComboBox<>();
        amountField = new JTextField();
        descField = new JTextField();
        categoryBox = new JComboBox<>();
        typeBox = new JComboBox<>(new String[]{"Доход", "Расход"});

        JButton addBtn = new JButton("Добавить запись");
        addBtn.setBackground(ACCENT_COLOR);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        addBtn.setPreferredSize(new Dimension(160, 42));
        addBtn.addActionListener(e -> addTransaction());

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Кто"), gbc);
        gbc.gridx = 1; form.add(new JLabel("Сумма (₽)"), gbc);
        gbc.gridx = 2; form.add(new JLabel("Категория"), gbc);
        gbc.gridx = 3; form.add(new JLabel("Тип"), gbc);

        gbc.gridy = 1;
        gbc.gridx = 0; form.add(userBox, gbc);
        gbc.gridx = 1; form.add(amountField, gbc);
        gbc.gridx = 2; form.add(categoryBox, gbc);
        gbc.gridx = 3; form.add(typeBox, gbc);

        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 3;
        JLabel descL = new JLabel("Описание / Комментарий"); descL.setBorder(new EmptyBorder(10,0,0,0));
        form.add(descL, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 3;
        form.add(descField, gbc);

        gbc.gridx = 3; gbc.gridwidth = 1;
        form.add(addBtn, gbc);

        center.add(form, BorderLayout.NORTH);

        // Поиск и Таблица
        JPanel tablePanel = new JPanel(new BorderLayout(0, 15));
        tablePanel.setOpaque(false);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(0, 45));
        searchField.putClientProperty("JTextField.placeholderText", "  🔍  Быстрый поиск по описанию...");
        searchField.addCaretListener(e -> refreshData());
        tablePanel.add(searchField, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Владелец", "Дата", "Тип", "Категория", "Описание", "Сумма"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(50);
        table.setShowVerticalLines(false);
        table.setDefaultRenderer(Object.class, new CustomCellRenderer());
        table.getTableHeader().setBackground(BG_COLOR);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_COLOR);
        tablePanel.add(scroll, BorderLayout.CENTER);

        center.add(tablePanel, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        JButton delBtn = new JButton("🗑 Удалить выбранную запись");
        delBtn.setForeground(new Color(255, 100, 100));
        delBtn.addActionListener(e -> deleteTransaction());
        panel.add(delBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel createStatCard(String title, String icon, JPanel parent, Color bg) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(bg);
        card.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel iconL = new JLabel(icon);
        iconL.setFont(new Font("SansSerif", Font.PLAIN, 32));

        JLabel titleL = new JLabel(title);
        titleL.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleL.setForeground(TEXT_SECONDARY);

        JLabel valL = new JLabel("0.00 ₽");
        valL.setFont(new Font("SansSerif", Font.BOLD, 28));
        valL.setForeground(Color.WHITE);

        JPanel textP = new JPanel(new GridLayout(2, 1, 0, 5));
        textP.setOpaque(false);
        textP.add(titleL);
        textP.add(valL);

        card.add(iconL, BorderLayout.WEST);
        card.add(textP, BorderLayout.CENTER);
        parent.add(card);
        return valL;
    }

    private JButton createMenuButton(String text, String cardName, boolean active) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(230, 55));
        btn.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 16));
        btn.setForeground(active ? Color.WHITE : TEXT_SECONDARY);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 30, 0, 0));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);

        btn.addActionListener(e -> {
            cardLayout.show(mainContent, cardName);
            for (Component c : sidebar.getComponents()) {
                if (c instanceof JButton) {
                    c.setFont(new Font("SansSerif", Font.PLAIN, 16));
                    c.setForeground(TEXT_SECONDARY);
                }
            }
            btn.setFont(new Font("SansSerif", Font.BOLD, 16));
            btn.setForeground(Color.WHITE);
        });
        return btn;
    }

    private void addTransaction() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String type = typeBox.getSelectedIndex() == 0 ? "INCOME" : "EXPENSE";
            String desc = descField.getText();
            Category cat = (Category) categoryBox.getSelectedItem();
            User user = (User) userBox.getSelectedItem();
            transactionMapper.insert(new Transaction(amount, type, LocalDate.now(), cat, user, desc));
            amountField.setText(""); descField.setText("");
            refreshData();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Введите корректную сумму!"); }
    }

    private void deleteTransaction() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            transactionMapper.delete((int) tableModel.getValueAt(row, 0));
            refreshData();
        }
    }

    private void refreshData() {
        tableModel.setRowCount(0);
        double bal = 0, inc = 0, exp = 0;
        String filter = searchField != null ? searchField.getText().toLowerCase() : "";
        List<Transaction> list = transactionMapper.findAll();
        for (Transaction t : list) {
            if (!t.getDescription().toLowerCase().contains(filter)) continue;
            double a = t.getAmount();
            String typeDisplay = t.getType().equals("INCOME") ? "ДОХОД" : "РАСХОД";
            if (t.getType().equals("INCOME")) { inc += a; bal += a; }
            else { exp += a; bal -= a; }
            tableModel.addRow(new Object[]{t.getId(), t.getUser().getName(), t.getDate(), typeDisplay, t.getCategory().getName(), t.getDescription(), a + " ₽"});
        }
        balanceLabel.setText(String.format("%.2f ₽", bal));
        incomeLabel.setText(String.format("+ %.2f ₽", inc));
        expenseLabel.setText(String.format("- %.2f ₽", exp));
        updateAnalytics(list);
        updateGoalsUI(bal);
    }

    private void updateAnalytics(List<Transaction> list) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        list.stream().filter(t -> t.getType().equals("EXPENSE"))
                .collect(Collectors.groupingBy(t -> t.getCategory().getName(), Collectors.summingDouble(Transaction::getAmount)))
                .forEach(dataset::setValue);
        JFreeChart chart = ChartFactory.createPieChart(null, dataset, false, true, false);
        chart.setBackgroundPaint(BG_COLOR);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(BG_COLOR);
        plot.setOutlineVisible(false);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ₽ ({2})"));
        plot.setLabelBackgroundPaint(CARD_COLOR);
        plot.setLabelPaint(Color.WHITE);
        JPanel p = (JPanel) mainContent.getComponent(1);
        p.removeAll();
        p.add(new ChartPanel(chart), BorderLayout.CENTER);
        p.revalidate();
    }

    private JPanel createAnalyticsPage() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_COLOR);
        return p;
    }

    private JPanel createGoalsPage() {
        JPanel p = new JPanel(new BorderLayout(0, 30));
        p.setBackground(BG_COLOR);
        p.setBorder(new EmptyBorder(40,40,40,40));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        form.setOpaque(false);
        JTextField nameF = new JTextField(15);
        JTextField amountF = new JTextField(10);
        JButton btn = new JButton("Создать цель");
        btn.addActionListener(e -> {
            goalMapper.insert(new Goal(nameF.getText(), Double.parseDouble(amountF.getText())));
            nameF.setText(""); amountF.setText("");
            refreshData();
        });
        form.add(new JLabel("Название:")); form.add(nameF);
        form.add(new JLabel("Сумма (₽):")); form.add(amountF);
        form.add(btn);

        goalsContainer = new JPanel();
        goalsContainer.setLayout(new BoxLayout(goalsContainer, BoxLayout.Y_AXIS));
        goalsContainer.setOpaque(false);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(goalsContainer), BorderLayout.CENTER);
        return p;
    }

    private void updateGoalsUI(double currentBalance) {
        if (goalsContainer == null) return;
        goalsContainer.removeAll();
        for (Goal g : goalMapper.findAll()) {
            JPanel card = new JPanel(new BorderLayout(20, 0));
            card.setBackground(CARD_COLOR);
            card.setBorder(new EmptyBorder(25,25,25,25));
            card.setMaximumSize(new Dimension(1200, 110));

            double progress = Math.max(0, Math.min(100, (currentBalance / g.getTargetAmount()) * 100));
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue((int) progress);
            bar.setStringPainted(true);
            bar.setString(g.getName() + ": " + (int)progress + "% (" + currentBalance + " / " + g.getTargetAmount() + " ₽)");

            card.add(bar, BorderLayout.CENTER);
            JButton del = new JButton("🗑");
            del.addActionListener(e -> { goalMapper.delete(g.getId()); refreshData(); });
            card.add(del, BorderLayout.EAST);

            goalsContainer.add(card);
            goalsContainer.add(Box.createVerticalStrut(15));
        }
        goalsContainer.revalidate();
    }

    private JPanel createUserPage() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 100));
        p.setBackground(BG_COLOR);
        JTextField f = new JTextField(20);
        JButton b = new JButton("Зарегистрировать участника");
        b.addActionListener(e -> {
            userMapper.insert(new User(f.getText()));
            f.setText("");
            loadDropdownData();
            refreshData();
        });
        p.add(new JLabel("Имя нового пользователя:")); p.add(f); p.add(b);
        return p;
    }

    private void loadDropdownData() {
        categoryBox.removeAllItems(); userBox.removeAllItems();
        categoryMapper.findAll().forEach(categoryBox::addItem);
        userMapper.findAll().forEach(userBox::addItem);
    }

    static class CustomCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean is, boolean hf, int r, int c) {
            Component comp = super.getTableCellRendererComponent(table, value, is, hf, r, c);
            setBorder(new EmptyBorder(0, 20, 0, 20));
            String type = (String) table.getModel().getValueAt(r, 3);
            if (!is) {
                comp.setBackground(BG_COLOR);
                comp.setForeground(type.equals("ДОХОД") ? SUCCESS_COLOR : DANGER_COLOR);
            }
            return comp;
        }
    }
}
