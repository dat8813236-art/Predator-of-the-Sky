package ui;

import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("Snake Hunter - Login");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        panel.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        panel.add(txtUsername);

        panel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        JButton btnLogin = new JButton("Login");
        panel.add(btnLogin);

        JButton btnRegister = new JButton("Register");
        panel.add(btnRegister);

        add(panel);

        // Login action
        btnLogin.addActionListener(this::login);

        // Register action
        btnRegister.addActionListener(this::register);
    }

    private void login(ActionEvent e) {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login thành công!");
                dispose();
                new game.GameFrame();
                // Sau này mở game ở đây
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void register(ActionEvent e) {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO users(username, password) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Đăng ký thành công!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Username đã tồn tại!");
        }
    }
}