package application;

import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

public class Main extends Application {

    // ================= WEATHER DATABASE (Ethiopian Cities) =================
    private final Map<String, WeatherData> weatherDB = new HashMap<>();

    @Override
    public void start(Stage stage) {
// ======== Add Ethiopian cities ========
weatherDB.put("Addis Ababa", new WeatherData("23°C", "Partly Cloudy", "🌱 Water your plants today"));
weatherDB.put("Adama", new WeatherData("30°C", "Sunny", "☀️ Good for solar drying"));
weatherDB.put("Mekelle", new WeatherData("28°C", "Windy", "🍃 Great for wind energy"));
weatherDB.put("Bahir Dar", new WeatherData("27°C", "Cloudy", "☁️ Good day for sightseeing"));
weatherDB.put("Hawassa", new WeatherData("25°C", "Rainy", "🌧️ Collect rainwater"));
weatherDB.put("Gondar", new WeatherData("26°C", "Partly Cloudy", "🌱 Ideal day for garden work"));
weatherDB.put("Dire Dawa", new WeatherData("32°C", "Hot", "💧 Stay hydrated"));
weatherDB.put("Jimma", new WeatherData("24°C", "Humid", "💧 Good for coffee plantations"));
weatherDB.put("Harar", new WeatherData("29°C", "Sunny", "☀️ Great for outdoor activities"));
weatherDB.put("Shashamane", new WeatherData("25°C", "Partly Cloudy", "🌱 Water your garden"));
weatherDB.put("Korem", new WeatherData("22°C", "Cloudy", "☁️ Ideal day to plant vegetables"));

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // ================= TOP BAR =================
        Text cityTitle = new Text("Addis Ababa");
        cityTitle.getStyleClass().add("city-title");

        // Autocomplete ComboBox for first-letter search
        ComboBox<String> cityComboBox = new ComboBox<>();
        cityComboBox.setEditable(true);
        cityComboBox.setPromptText("Search city");

        ObservableList<String> cities = FXCollections.observableArrayList(weatherDB.keySet());
        cityComboBox.setItems(cities);

        // Filter suggestions by first letters typed
        cityComboBox.getEditor().addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            String typed = cityComboBox.getEditor().getText().toLowerCase();
            List<String> filtered = cities.stream()
                    .filter(city -> city.toLowerCase().startsWith(typed))
                    .collect(Collectors.toList());
            cityComboBox.setItems(FXCollections.observableArrayList(filtered));
            cityComboBox.show();
        });

        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().add("refresh-btn");
        searchBtn.disableProperty().bind(Bindings.isEmpty(cityComboBox.getEditor().textProperty()));

        HBox searchBox = new HBox(8, cityComboBox, searchBtn);
        searchBox.setAlignment(Pos.CENTER_RIGHT);

        BorderPane topBar = new BorderPane();
        topBar.setCenter(cityTitle);
        topBar.setRight(searchBox);
        topBar.setPadding(new Insets(10));
        topBar.getStyleClass().add("top-bar");

        root.setTop(topBar);

        // ================= CENTER =================
        Label temperature = new Label("23°C");
        temperature.getStyleClass().add("temperature");

        Label condition = new Label("Partly Cloudy");
        condition.getStyleClass().add("condition");

        Label tip = new Label("🌱 Water your plants today");
        tip.getStyleClass().add("tip");

        // Leaf shape
        Ellipse leaf = new Ellipse(40, 60);
        leaf.setFill(Color.web("#2E7D32"));
        leaf.setRotate(25);

        // Leaf animation
        RotateTransition rotate = new RotateTransition(Duration.seconds(4), leaf);
        rotate.setFromAngle(20);
        rotate.setToAngle(30);
        rotate.setAutoReverse(true);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.play();

        VBox centerBox = new VBox(15, temperature, condition, leaf, tip);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        root.setCenter(centerBox);

        // ================= BOTTOM FORECAST =================
        HBox forecastBox = new HBox(25,
                createForecast("Mon", "22°C"),
                createForecast("Tue", "24°C"),
                createForecast("Wed", "21°C")
        );
        forecastBox.setAlignment(Pos.CENTER);
        forecastBox.setPadding(new Insets(10));
        forecastBox.getStyleClass().add("forecast-box");

        root.setBottom(forecastBox);

        // ================= SEARCH BUTTON ACTION =================
        searchBtn.setOnAction(e -> {
            String city = cityComboBox.getEditor().getText().trim();
            if (!city.isEmpty()) {
                WeatherData w = weatherDB.entrySet()
                        .stream()
                        .filter(entry -> entry.getKey().equalsIgnoreCase(city))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElse(null);

                if (w != null) {
                    cityTitle.setText(city);
                    temperature.setText(w.temperature);
                    condition.setText(w.condition);
                    tip.setText(w.tip);
                } else {
                    cityTitle.setText(city);
                    temperature.setText("--°C");
                    condition.setText("Weather unknown");
                    tip.setText("❌ No weather data for this city");
                }
                cityComboBox.getEditor().clear();
            }
        });

        // ================= SCENE =================
        Scene scene = new Scene(root, 450, 600);

        // Load CSS safely
        if (getClass().getResource("style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        } else {
            System.out.println("CSS file not found! Make sure style.css is in the 'application' package.");
        }

        stage.setTitle("EcoLife Weather Widget - Ethiopia");
        stage.setScene(scene);
        stage.show();
    }

    // ================= FORECAST HELPER =================
    private VBox createForecast(String day, String temp) {
        Label d = new Label(day);
        d.getStyleClass().add("forecast-day");
        Label t = new Label(temp);
        t.getStyleClass().add("forecast-temp");
        VBox box = new VBox(5, d, t);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("forecast-box-item");
        return box;
    }

    // ================= WEATHER DATA CLASS =================
    static class WeatherData {
        String temperature;
        String condition;
        String tip;

        WeatherData(String temperature, String condition, String tip) {
            this.temperature = temperature;
            this.condition = condition;
            this.tip = tip;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
