package database;

public class TestConnection {
    public static void main(String[] args) {
        try {
            DBConnection.getConnection();
            System.out.println("Connected to MySQL successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}