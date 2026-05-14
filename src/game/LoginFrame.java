package game;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JTextField     emailField;

    public LoginFrame() {
        setTitle("Snake Hunter - Đăng nhập");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        showLoginPanel();
        setVisible(true);
    }

    private void showLoginPanel() {
        getContentPane().removeAll();

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        panel.add(new JLabel("Tên đăng nhập:"));
        panel.add(usernameField);
        panel.add(new JLabel("Mật khẩu:"));
        panel.add(passwordField);

        JButton loginBtn  = new JButton("Đăng nhập");
        JButton switchBtn = new JButton("Chưa có tài khoản?");

        loginBtn.addActionListener(e  -> handleLogin());
        switchBtn.addActionListener(e -> showRegisterPanel());

        panel.add(loginBtn);
        panel.add(switchBtn);

        add(panel);
        revalidate();
        repaint();
    }

    private void showRegisterPanel() {
        getContentPane().removeAll();

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        panel.add(new JLabel("Tên đăng nhập:"));
        panel.add(usernameField);
        panel.add(new JLabel("Mật khẩu:"));
        panel.add(passwordField);

        JButton registerBtn = new JButton("Đăng ký");
        JButton switchBtn   = new JButton("Đã có tài khoản?");

        registerBtn.addActionListener(e -> handleRegister());
        switchBtn.addActionListener(e   -> showLoginPanel());

        panel.add(registerBtn);
        panel.add(switchBtn);

        add(panel);
        revalidate();
        repaint();
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (AccountManager.login(username, password)) {
            JOptionPane.showMessageDialog(this, "Chào mừng " + username + "!");
            dispose();
            new GameFrame(username); // mở game
        } else {
            JOptionPane.showMessageDialog(this, "❌ Sai tên đăng nhập hoặc mật khẩu!");
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (AccountManager.register(username, password)) {
            JOptionPane.showMessageDialog(this, "✅ Đăng ký thành công! Hãy đăng nhập.");
            showLoginPanel();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Tên đăng nhập đã tồn tại!");
        }
    }
}