import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/banking_system";

    private static final String USERNAME =
            "root";

    private static final String PASSWORD =
            System.getenv("MYSQL_PASSWORD");

    public static Connection getConnection() {

        if (PASSWORD == null || PASSWORD.isBlank()) {

            System.out.println(
                    "MYSQL_PASSWORD environment variable is not set."
            );

            return null;
        }

        try {

            Connection connection =
                    DriverManager.getConnection(
                            URL,
                            USERNAME,
                            PASSWORD
                    );

            System.out.println(
                    "Database connected successfully."
            );

            return connection;

        } catch (SQLException e) {

            System.out.println(
                    "Database connection failed."
            );

            e.printStackTrace();

            return null;
        }
    }

    public static void main(String[] args) {

        Connection connection =
                getConnection();

        if (connection != null) {

            try {

                connection.close();

                System.out.println(
                        "Database connection closed."
                );

            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
    }
}