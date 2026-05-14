package game;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AccountManager {

    private static final String FILE_PATH = "accounts.txt";
    private static Map<String, String> accounts = new HashMap<>();

    // Đọc tài khoản từ file khi khởi động
    static {
        loadAccounts();
    }

    // ── Đọc file ──
    private static void loadAccounts() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    accounts.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            // File chưa tồn tại → bình thường, chưa có tài khoản nào
        }
    }

    // ── Ghi file ──
    private static void saveAccounts() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Map.Entry<String, String> entry : accounts.entrySet()) {
                bw.write(entry.getKey() + ":" + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("❌ Lỗi lưu tài khoản: " + e.getMessage());
        }
    }

    // ── Đăng ký ──
    public static boolean register(String username, String password) {
        if (accounts.containsKey(username)) {
            return false; // tài khoản đã tồn tại
        }
        accounts.put(username, password);
        saveAccounts();
        return true;
    }

    // ── Đăng nhập ──
    public static boolean login(String username, String password) {
        return accounts.containsKey(username) &&
                accounts.get(username).equals(password);
    }
}