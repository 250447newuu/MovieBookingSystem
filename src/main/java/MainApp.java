//@raximjonov_22
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {
    private Stage stage;
    private int customerId;
    private String customerName;
    private int selectedShowId;
    private double selectedShowPrice;
    private List<Integer> selectedSeatIds = new ArrayList<>();
    private List<String> selectedSeatNumbers = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Movie Ticket Booking System");
        showLoginScreen();
    }

//@raximjonov_22
    private void showLoginScreen() {
        Label title = new Label("Movie Ticket Booking System");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Create Account");

        Label message = new Label();

        loginButton.setOnAction(e -> login(emailField.getText(), passwordField.getText(), message));

        registerButton.setOnAction(e -> showRegisterScreen());

        VBox root = new VBox(10);
        root.getChildren().addAll(title, emailField, passwordField, loginButton, registerButton, message);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 500, 350);
        stage.setScene(scene);
        stage.show();
    }

//@raximjonov_22
    private void login(String email, String password, Label message) {
        email = email.trim();
        password = password.trim();

        if (email.isEmpty() || password.isEmpty()) {
            message.setText("Enter email and password");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, name FROM customers WHERE email = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                customerId = resultSet.getInt("id");
                customerName = resultSet.getString("name");
                showHomeScreen();
            } else {
                message.setText("Wrong email or password");
            }
        } catch (Exception ex) {
            message.setText(ex.getMessage());
        }
    }

//@raximjonov_22
    private void showRegisterScreen() {
        Label title = new Label("Create Account");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button createButton = new Button("Create");
        Button backButton = new Button("Back");

        Label message = new Label();

        createButton.setOnAction(e -> createAccount(
                nameField.getText(),
                emailField.getText(),
                phoneField.getText(),
                passwordField.getText(),
                message
        ));

        backButton.setOnAction(e -> showLoginScreen());

        VBox root = new VBox(10);
        root.getChildren().addAll(title, nameField, emailField, phoneField, passwordField, createButton, backButton, message);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 500, 400);
        stage.setScene(scene);
    }

//@raximjonov_22
    private void createAccount(String name, String email, String phone, String password, Label message) {
        name = name.trim();
        email = email.trim();
        phone = phone.trim();
        password = password.trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            message.setText("Name, email, and password are required");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO customers (name, email, phone, password) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setString(4, password);

            statement.executeUpdate();
            message.setText("Account created. Go back and login.");
        } catch (Exception ex) {
            message.setText(ex.getMessage());
        }
    }

//@raximjonov_22
    private void showHomeScreen() {
        Label title = new Label("Welcome, " + customerName);

        TextField searchField = new TextField();
        searchField.setPromptText("Search movie");

        Button searchButton = new Button("Search");
        Button bookingsButton = new Button("My Bookings");
        Button logoutButton = new Button("Logout");

        VBox moviesBox = new VBox(10);
        moviesBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(moviesBox);
        scrollPane.setFitToWidth(true);

        searchButton.setOnAction(e -> loadMovies(moviesBox, searchField.getText()));
        bookingsButton.setOnAction(e -> showBookingsScreen());
        logoutButton.setOnAction(e -> showLoginScreen());

        HBox buttons = new HBox(10);
        buttons.getChildren().addAll(searchButton, bookingsButton, logoutButton);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(15);
        root.getChildren().addAll(title, searchField, buttons, scrollPane);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 750, 550);
        stage.setScene(scene);

        loadMovies(moviesBox, "");
    }

//@raximjonov_22
    private void loadMovies(VBox moviesBox, String search) {
        moviesBox.getChildren().clear();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM movies WHERE title LIKE ? OR language LIKE ? OR genre LIKE ? ORDER BY title";
            PreparedStatement statement = connection.prepareStatement(sql);

            String searchText = "%" + search.trim() + "%";
            statement.setString(1, searchText);
            statement.setString(2, searchText);
            statement.setString(3, searchText);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int movieId = resultSet.getInt("id");
                String movieTitle = resultSet.getString("title");
                String language = resultSet.getString("language");
                String genre = resultSet.getString("genre");
                int duration = resultSet.getInt("duration_minutes");
                String description = resultSet.getString("description");

                Label movieLabel = new Label(
                        movieTitle +
                                "\nLanguage: " + language +
                                "\nGenre: " + genre +
                                "\nDuration: " + duration + " minutes" +
                                "\n" + description
                );

                Button showButton = new Button("View Shows");
                showButton.setOnAction(e -> showShowsScreen(movieId, movieTitle));

                VBox movieBox = new VBox(5);
                movieBox.getChildren().addAll(movieLabel, showButton);
                movieBox.setPadding(new Insets(10));

                moviesBox.getChildren().add(movieBox);
            }
        } catch (Exception ex) {
            moviesBox.getChildren().add(new Label(ex.getMessage()));
        }
    }

//@raximjonov_22
    private void showShowsScreen(int movieId, String movieTitle) {
        Label title = new Label("Shows for " + movieTitle);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showHomeScreen());

        VBox showsBox = new VBox(10);
        showsBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(showsBox);
        scrollPane.setFitToWidth(true);

        VBox root = new VBox(15);
        root.getChildren().addAll(title, backButton, scrollPane);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 750, 550);
        stage.setScene(scene);

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql =
                    "SELECT sh.id, sh.show_time, sh.price, c.name AS cinema_name, h.name AS hall_name, ci.name AS city_name " +
                            "FROM shows sh " +
                            "JOIN cinema_halls h ON sh.hall_id = h.id " +
                            "JOIN cinemas c ON h.cinema_id = c.id " +
                            "JOIN cities ci ON c.city_id = ci.id " +
                            "WHERE sh.movie_id = ? " +
                            "ORDER BY sh.show_time";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, movieId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int showId = resultSet.getInt("id");
                String time = resultSet.getString("show_time");
                double price = resultSet.getDouble("price");
                String cinema = resultSet.getString("cinema_name");
                String hall = resultSet.getString("hall_name");
                String city = resultSet.getString("city_name");

                Label showLabel = new Label(
                        "City: " + city +
                                "\nCinema: " + cinema +
                                "\nHall: " + hall +
                                "\nTime: " + time +
                                "\nPrice: $" + price
                );

                Button seatButton = new Button("Select Seats");
                seatButton.setOnAction(e -> showSeatScreen(showId, price, movieTitle));

                VBox showBox = new VBox(5);
                showBox.getChildren().addAll(showLabel, seatButton);
                showBox.setPadding(new Insets(10));

                showsBox.getChildren().add(showBox);
            }
        } catch (Exception ex) {
            showsBox.getChildren().add(new Label(ex.getMessage()));
        }
    }

//@raximjonov_22
    private void showSeatScreen(int showId, double price, String movieTitle) {
        selectedShowId = showId;
        selectedShowPrice = price;

        selectedSeatIds.clear();
        selectedSeatNumbers.clear();

        Label title = new Label("Select Seats for " + movieTitle);

        GridPane seatGrid = new GridPane();
        seatGrid.setHgap(10);
        seatGrid.setVgap(10);
        seatGrid.setAlignment(Pos.CENTER);

        Label selectedLabel = new Label("Selected seats: none");

        TextField couponField = new TextField();
        couponField.setPromptText("Coupon code");

        ComboBox<String> paymentBox = new ComboBox<>();
        paymentBox.getItems().add("Credit Card");
        paymentBox.getItems().add("Cash");
        paymentBox.setValue("Credit Card");

        Button bookButton = new Button("Book Selected Seats");
        Button backButton = new Button("Back");

        Label message = new Label();

        bookButton.setOnAction(e -> bookSeats(couponField.getText(), paymentBox.getValue(), message));
        backButton.setOnAction(e -> showHomeScreen());

        VBox root = new VBox(15);
        root.getChildren().addAll(title, seatGrid, selectedLabel, couponField, paymentBox, bookButton, backButton, message);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 750, 550);
        stage.setScene(scene);

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, seat_number, status FROM show_seats WHERE show_id = ? ORDER BY seat_number";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, showId);

            ResultSet resultSet = statement.executeQuery();

            int index = 0;

            while (resultSet.next()) {
                int seatId = resultSet.getInt("id");
                String seatNumber = resultSet.getString("seat_number");
                String status = resultSet.getString("status");

                Button seatButton = new Button(seatNumber);
                seatButton.setPrefWidth(110);

                if (status.equals("BOOKED")) {
                    seatButton.setText(seatNumber + " booked");
                    seatButton.setDisable(true);
                } else {
                    seatButton.setOnAction(e -> {
                        if (selectedSeatIds.contains(seatId)) {
                            selectedSeatIds.remove(Integer.valueOf(seatId));
                            selectedSeatNumbers.remove(seatNumber);
                            seatButton.setText(seatNumber);
                        } else {
                            selectedSeatIds.add(seatId);
                            selectedSeatNumbers.add(seatNumber);
                            seatButton.setText(seatNumber + " selected");
                        }

                        updateSelectedLabel(selectedLabel);
                    });
                }

                seatGrid.add(seatButton, index % 5, index / 5);
                index++;
            }
        } catch (Exception ex) {
            message.setText(ex.getMessage());
        }
    }

//@raximjonov_22
    private void updateSelectedLabel(Label selectedLabel) {
        if (selectedSeatNumbers.isEmpty()) {
            selectedLabel.setText("Selected seats: none");
        } else {
            selectedLabel.setText("Selected seats: " + String.join(", ", selectedSeatNumbers));
        }
    }

//@nigmatullayev_azizbek
    private void bookSeats(String couponCode, String paymentMethod, Label message) {
        couponCode = couponCode.trim().toUpperCase();

        if (selectedSeatIds.isEmpty()) {
            message.setText("Select at least one seat");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                double total = selectedSeatIds.size() * selectedShowPrice;
                int discount = 0;

                if (!couponCode.isEmpty()) {
                    String couponSql = "SELECT discount_percent FROM coupons WHERE code = ?";
                    PreparedStatement couponStatement = connection.prepareStatement(couponSql);
                    couponStatement.setString(1, couponCode);

                    ResultSet couponResult = couponStatement.executeQuery();

                    if (couponResult.next()) {
                        discount = couponResult.getInt("discount_percent");
                    } else {
                        throw new SQLException("Invalid coupon code");
                    }
                }

                total = total - (total * discount / 100);

                for (int seatId : selectedSeatIds) {
                    String checkSql = "SELECT status FROM show_seats WHERE id = ? FOR UPDATE";
                    PreparedStatement checkStatement = connection.prepareStatement(checkSql);
                    checkStatement.setInt(1, seatId);

                    ResultSet checkResult = checkStatement.executeQuery();

                    if (checkResult.next()) {
                        String status = checkResult.getString("status");

                        if (!status.equals("AVAILABLE")) {
                            throw new SQLException("One seat is already booked");
                        }
                    }
                }

                String bookingSql = "INSERT INTO bookings (customer_id, show_id, booking_time, total_amount, payment_method, coupon_code, status) VALUES (?, ?, NOW(), ?, ?, ?, ?)";
                PreparedStatement bookingStatement = connection.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS);
                bookingStatement.setInt(1, customerId);
                bookingStatement.setInt(2, selectedShowId);
                bookingStatement.setDouble(3, total);
                bookingStatement.setString(4, paymentMethod);

                if (couponCode.isEmpty()) {
                    bookingStatement.setString(5, null);
                } else {
                    bookingStatement.setString(5, couponCode);
                }

                bookingStatement.setString(6, "CONFIRMED");
                bookingStatement.executeUpdate();

                ResultSet keys = bookingStatement.getGeneratedKeys();
                keys.next();

                int bookingId = keys.getInt(1);

                for (int seatId : selectedSeatIds) {
                    String bookingSeatSql = "INSERT INTO booking_seats (booking_id, show_seat_id) VALUES (?, ?)";
                    PreparedStatement bookingSeatStatement = connection.prepareStatement(bookingSeatSql);
                    bookingSeatStatement.setInt(1, bookingId);
                    bookingSeatStatement.setInt(2, seatId);
                    bookingSeatStatement.executeUpdate();

                    String updateSeatSql = "UPDATE show_seats SET status = 'BOOKED' WHERE id = ?";
                    PreparedStatement updateSeatStatement = connection.prepareStatement(updateSeatSql);
                    updateSeatStatement.setInt(1, seatId);
                    updateSeatStatement.executeUpdate();
                }

                connection.commit();

                showAlert("Booking created. Total: $" + total);
                showBookingsScreen();
            } catch (Exception ex) {
                connection.rollback();
                message.setText(ex.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception ex) {
            message.setText(ex.getMessage());
        }
    }

//https://t.me/rustamboy_ev
    private void showBookingsScreen() {
        Label title = new Label("My Bookings");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showHomeScreen());

        VBox bookingsBox = new VBox(10);
        bookingsBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(bookingsBox);
        scrollPane.setFitToWidth(true);

        VBox root = new VBox(15);
        root.getChildren().addAll(title, backButton, scrollPane);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 800, 550);
        stage.setScene(scene);

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql =
                    "SELECT b.id, b.booking_time, b.total_amount, b.payment_method, b.coupon_code, b.status, m.title, sh.show_time " +
                            "FROM bookings b " +
                            "JOIN shows sh ON b.show_id = sh.id " +
                            "JOIN movies m ON sh.movie_id = m.id " +
                            "WHERE b.customer_id = ? " +
                            "ORDER BY b.booking_time DESC";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, customerId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int bookingId = resultSet.getInt("id");
                String movieTitle = resultSet.getString("title");
                String showTime = resultSet.getString("show_time");
                String bookingTime = resultSet.getString("booking_time");
                double total = resultSet.getDouble("total_amount");
                String payment = resultSet.getString("payment_method");
                String coupon = resultSet.getString("coupon_code");
                String status = resultSet.getString("status");

                if (coupon == null || coupon.isEmpty()) {
                    coupon = "none";
                }

                String seats = getSeatsForBooking(connection, bookingId);

                Label bookingLabel = new Label(
                        "Booking ID: " + bookingId +
                                "\nMovie: " + movieTitle +
                                "\nShow Time: " + showTime +
                                "\nBooking Time: " + bookingTime +
                                "\nSeats: " + seats +
                                "\nPayment: " + payment +
                                "\nCoupon: " + coupon +
                                "\nTotal: $" + total +
                                "\nStatus: " + status
                );

                Button cancelButton = new Button("Cancel Booking");
                cancelButton.setDisable(!status.equals("CONFIRMED"));
                cancelButton.setOnAction(e -> cancelBooking(bookingId));

                VBox bookingBox = new VBox(5);
                bookingBox.getChildren().addAll(bookingLabel, cancelButton);
                bookingBox.setPadding(new Insets(10));

                bookingsBox.getChildren().add(bookingBox);
            }
        } catch (Exception ex) {
            bookingsBox.getChildren().add(new Label(ex.getMessage()));
        }
    }

//@raximjonov_22
    private String getSeatsForBooking(Connection connection, int bookingId) throws SQLException {
        List<String> seats = new ArrayList<>();
        String sql =
                "SELECT ss.seat_number " +
                        "FROM booking_seats bs " +
                        "JOIN show_seats ss ON bs.show_seat_id = ss.id " +
                        "WHERE bs.booking_id = ? " +
                        "ORDER BY ss.seat_number";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, bookingId);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            seats.add(resultSet.getString("seat_number"));
        }

        if (seats.isEmpty()) {
            return "none";
        }

        return String.join(", ", seats);
    }

//https://t.me/rustamboy_ev
    private void cancelBooking(int bookingId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                List<Integer> seatIds = new ArrayList<>();

                String seatSql = "SELECT show_seat_id FROM booking_seats WHERE booking_id = ?";
                PreparedStatement seatStatement = connection.prepareStatement(seatSql);
                seatStatement.setInt(1, bookingId);

                ResultSet seatResult = seatStatement.executeQuery();

                while (seatResult.next()) {
                    seatIds.add(seatResult.getInt("show_seat_id"));
                }

                String bookingSql = "UPDATE bookings SET status = 'CANCELLED' WHERE id = ? AND customer_id = ? AND status = 'CONFIRMED'";
                PreparedStatement bookingStatement = connection.prepareStatement(bookingSql);
                bookingStatement.setInt(1, bookingId);
                bookingStatement.setInt(2, customerId);

                int rows = bookingStatement.executeUpdate();

                if (rows == 0) {
                    throw new SQLException("Booking cannot be cancelled");
                }

                for (int seatId : seatIds) {
                    String updateSeatSql = "UPDATE show_seats SET status = 'AVAILABLE' WHERE id = ?";
                    PreparedStatement updateSeatStatement = connection.prepareStatement(updateSeatSql);
                    updateSeatStatement.setInt(1, seatId);
                    updateSeatStatement.executeUpdate();
                }

                String deleteSeatsSql = "DELETE FROM booking_seats WHERE booking_id = ?";
                PreparedStatement deleteSeatsStatement = connection.prepareStatement(deleteSeatsSql);
                deleteSeatsStatement.setInt(1, bookingId);
                deleteSeatsStatement.executeUpdate();

                connection.commit();

                showAlert("Booking cancelled");
                showBookingsScreen();
            } catch (Exception ex) {
                connection.rollback();
                showAlert(ex.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception ex) {
            showAlert(ex.getMessage());
        }
    }

//@raximjonov_22
    private void showAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}