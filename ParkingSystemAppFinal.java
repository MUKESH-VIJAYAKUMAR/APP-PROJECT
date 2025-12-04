import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class ParkingSystemAppFinal extends JFrame {

    CardLayout card;
    JPanel mainPanel, startPanel, signupPanel, loginPanel, parkingPanel;
    JTextField signupUser, loginUser;
    JPasswordField signupPass, loginPass;
    JButton signupBtn, loginBtn;
    JLabel loginMsg;
    JButton[] slots;
    JLabel infoLabel, slotCounterLabel;

    Map<String, String> usersMap = new HashMap<>();
    boolean[] occupied = new boolean[8];
    String[] tokenNumbers = new String[8];
    int nextToken = 101;
    String currentToken;

    // Theme colors
    Color primaryBlue = new Color(33, 150, 243);
    Color lightBlue = new Color(100, 181, 246);
    Color background = new Color(245, 247, 250);
    Color panelWhite = Color.WHITE;
    Color slotEmpty = new Color(76, 175, 80);
    Color slotTaken = new Color(244, 67, 54);
    Color textColor = new Color(33, 33, 33);

    public ParkingSystemAppFinal() {
        setTitle("Smart Parking Space Finder");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        loadUsersFromFile();

        card = new CardLayout();
        mainPanel = new JPanel(card);
        mainPanel.setBackground(background);

        // ------------------ START PANEL ------------------
        startPanel = new GradientPanel(primaryBlue, lightBlue);
        startPanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("🚗 SMART PARKING SPACE FINDER", JLabel.CENTER);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 38));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(150, 10, 30, 10));

        JPanel btns = new JPanel(new GridLayout(2, 1, 20, 20));
        btns.setOpaque(false);
        btns.setBorder(new EmptyBorder(0, 550, 0, 550));

        JButton gotoSignup = createStyledButton("Create New Account");
        JButton gotoLogin = createStyledButton("Login to Existing Account");
        btns.add(gotoSignup);
        btns.add(gotoLogin);

        startPanel.add(title, BorderLayout.NORTH);
        startPanel.add(btns, BorderLayout.CENTER);
        mainPanel.add(startPanel, "Start");

        // ------------------ SIGNUP PANEL ------------------
        signupPanel = createFormPanel("📝 Create Your Account");
        signupUser = new JTextField();
        signupPass = new JPasswordField();
        signupBtn = createStyledButton("Sign Up");
        JButton backSignup = createStyledButton("Back");

        JPanel signupFields = createFormFields("Username:", signupUser, "Password:", signupPass, signupBtn, backSignup);
        signupPanel.add(signupFields, BorderLayout.CENTER);
        mainPanel.add(signupPanel, "Signup");

        // ------------------ LOGIN PANEL ------------------
        loginPanel = createFormPanel("🔐 Login to Continue");
        loginUser = new JTextField();
        loginPass = new JPasswordField();
        loginBtn = createStyledButton("Login");
        JButton backLogin = createStyledButton("Back");
        loginMsg = new JLabel("", JLabel.CENTER);
        loginMsg.setForeground(Color.RED);
        JPanel loginFields = createFormFields("User ID:", loginUser, "Password:", loginPass, loginBtn, backLogin);
        loginPanel.add(loginFields, BorderLayout.CENTER);
        loginPanel.add(loginMsg, BorderLayout.SOUTH);
        mainPanel.add(loginPanel, "Login");

        // ------------------ PARKING PANEL ------------------
        parkingPanel = new JPanel(new BorderLayout());
        parkingPanel.setBackground(background);

        GradientPanel header = new GradientPanel(primaryBlue, lightBlue);
        header.setLayout(new GridLayout(2, 1));
        infoLabel = new JLabel("Welcome!", JLabel.CENTER);
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        infoLabel.setForeground(Color.WHITE);
        slotCounterLabel = new JLabel("Available: 8 | Occupied: 0", JLabel.CENTER);
        slotCounterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        slotCounterLabel.setForeground(Color.WHITE);
        header.add(infoLabel);
        header.add(slotCounterLabel);

        JPanel grid = new JPanel(new GridLayout(2, 4, 30, 30));
        grid.setBackground(background);
        grid.setBorder(new EmptyBorder(60, 100, 60, 100));

        slots = new JButton[8];
        for (int i = 0; i < 8; i++) {
            slots[i] = createSlotButton(i);
            grid.add(slots[i]);
        }

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        bottom.setBackground(panelWhite);
        JButton logout = createStyledButton("Logout");
        JButton switchAcc = createStyledButton("Switch Account");
        JButton backParking = createStyledButton("Back");
        bottom.add(backParking);
        bottom.add(logout);
        bottom.add(switchAcc);

        parkingPanel.add(header, BorderLayout.NORTH);
        parkingPanel.add(grid, BorderLayout.CENTER);
        parkingPanel.add(bottom, BorderLayout.SOUTH);
        mainPanel.add(parkingPanel, "Parking");

        add(mainPanel);
        card.show(mainPanel, "Start");

        // ------------------ ACTIONS ------------------
        gotoSignup.addActionListener(e -> card.show(mainPanel, "Signup"));
        gotoLogin.addActionListener(e -> card.show(mainPanel, "Login"));
        backSignup.addActionListener(e -> card.show(mainPanel, "Start"));
        backLogin.addActionListener(e -> card.show(mainPanel, "Start"));
        signupBtn.addActionListener(e -> handleSignup());
        loginBtn.addActionListener(e -> handleLogin());
        logout.addActionListener(e -> card.show(mainPanel, "Login"));
        switchAcc.addActionListener(e -> card.show(mainPanel, "Login"));
        backParking.addActionListener(e -> card.show(mainPanel, "Login"));
    }

    // ------------------ REUSABLE COMPONENTS ------------------
    JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(primaryBlue);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 25, 12, 25));
        btn.setOpaque(true);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(lightBlue); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(primaryBlue); }
        });
        return btn;
    }

    JPanel createFormPanel(String titleText) {
        JPanel panel = new GradientPanel(Color.WHITE, background);
        panel.setLayout(new BorderLayout());
        JLabel title = new JLabel(titleText, JLabel.CENTER);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        title.setForeground(textColor);
        title.setBorder(new EmptyBorder(50, 10, 30, 10));
        panel.add(title, BorderLayout.NORTH);
        return panel;
    }

    JPanel createFormFields(String label1, JTextField field1, String label2, JPasswordField field2, JButton btn1, JButton btn2) {
        JPanel p = new JPanel(new GridLayout(3, 2, 20, 20));
        p.setBackground(panelWhite);
        p.setBorder(new CompoundBorder(new EmptyBorder(50, 450, 50, 450), new LineBorder(new Color(220, 220, 220), 1, true)));

        JLabel l1 = new JLabel(label1);
        JLabel l2 = new JLabel(label2);
        l1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        field1.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field2.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        p.add(l1); p.add(field1);
        p.add(l2); p.add(field2);
        p.add(btn1); p.add(btn2);

        return p;
    }

    JButton createSlotButton(int index) {
        JButton slot = new JButton("Slot " + (index + 1) + " [Empty]");
        slot.setBackground(slotEmpty);
        slot.setForeground(Color.WHITE);
        slot.setFont(new Font("Segoe UI", Font.BOLD, 18));
        slot.setFocusPainted(false);
        slot.setBorder(new EmptyBorder(20, 10, 20, 10));
        slot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        slot.addActionListener(e -> handleSlotClick(index));
        return slot;
    }

    // ------------------ LOGIC ------------------
    void loadUsersFromFile() {
        File f = new File("users.txt");
        try {
            if (!f.exists()) f.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) usersMap.put(parts[0], parts[1]);
            }
            br.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    void saveUserToFile(String user, String pass) {
        try (FileWriter fw = new FileWriter("users.txt", true)) {
            fw.write(user + "," + pass + "\n");
        } catch (Exception e) { e.printStackTrace(); }
    }

    void handleSignup() {
        String u = signupUser.getText().trim();
        String p = new String(signupPass.getPassword()).trim();
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }
        if (usersMap.containsKey(u)) {
            JOptionPane.showMessageDialog(this, "User already exists!");
            return;
        }
        usersMap.put(u, p);
        saveUserToFile(u, p);
        JOptionPane.showMessageDialog(this, "Account Created Successfully!");
        signupUser.setText(""); signupPass.setText("");
        card.show(mainPanel, "Login");
    }

    void handleLogin() {
        String u = loginUser.getText().trim();
        String p = new String(loginPass.getPassword()).trim();
        if (usersMap.containsKey(u) && usersMap.get(u).equals(p)) {
            currentToken = String.valueOf(nextToken++);
            infoLabel.setText("Welcome, " + u + " | Token: " + currentToken);
            refreshSlots();
            card.show(mainPanel, "Parking");
            loginMsg.setText("");
        } else {
            loginMsg.setText("Invalid Username or Password!");
        }
    }

    void handleSlotClick(int index) {
        if (occupied[index]) {
            if (tokenNumbers[index].equals(currentToken)) {
                int exit = JOptionPane.showConfirmDialog(this, "Exit from this slot?");
                if (exit == JOptionPane.YES_OPTION) {
                    occupied[index] = false;
                    tokenNumbers[index] = null;
                    refreshSlots();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Slot already taken by Token " + tokenNumbers[index]);
            }
        } else {
            int confirm = JOptionPane.showConfirmDialog(this, "Book this slot and proceed to payment?");
            if (confirm == JOptionPane.YES_OPTION) {
                occupied[index] = true;
                tokenNumbers[index] = currentToken;
                refreshSlots();
                JOptionPane.showMessageDialog(this, "Payment Successful!");
            }
        }
    }

    void refreshSlots() {
        int available = 0;
        for (int i = 0; i < slots.length; i++) {
            if (occupied[i]) {
                slots[i].setBackground(slotTaken);
                slots[i].setText("Slot " + (i + 1) + " [Token " + tokenNumbers[i] + "]");
            } else {
                slots[i].setBackground(slotEmpty);
                slots[i].setText("Slot " + (i + 1) + " [Empty]");
                available++;
            }
        }
        slotCounterLabel.setText("Available: " + available + " | Occupied: " + (8 - available));
    }

    // ------------------ CUSTOM GRADIENT PANEL ------------------
    static class GradientPanel extends JPanel {
        private final Color c1, c2;
        GradientPanel(Color c1, Color c2) { this.c1 = c1; this.c2 = c2; }
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            int w = getWidth(), h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ParkingSystemAppFinal().setVisible(true));
    }

}
