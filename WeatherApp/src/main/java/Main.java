import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Main {
    private static final Color NAVY = new Color(18, 32, 58);
    private static final Color BLUE = new Color(45, 104, 230);
    private static final Color BACKGROUND = new Color(239, 244, 252);
    private static final Color MUTED = new Color(103, 116, 139);
    private static final Color RED = new Color(180, 35, 45);

    private final JFrame frame = new JFrame("Weather App");
    private final JTextField cityField = new JTextField("Manila", 22);
    private final JButton searchButton = new JButton("Get Weather");
    private final JLabel statusLabel = new JLabel("Enter a city to check its current weather.");

    private final JLabel cityLabel = new JLabel("Search for a city");
    private final JLabel conditionLabel = new JLabel("Current conditions will appear here.");
    private final JLabel iconLabel = new JLabel("☀");
    private final JLabel temperatureLabel = new JLabel("--°C");
    private final JLabel feelsLikeLabel = new JLabel("--");
    private final JLabel humidityLabel = new JLabel("--");
    private final JLabel windLabel = new JLabel("--");
    private final JLabel pressureLabel = new JLabel("--");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().show());
    }

    private void show() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(760, 610);
        frame.setMinimumSize(new Dimension(640, 560));
        frame.setLocationRelativeTo(null);
        frame.setContentPane(buildInterface());

        searchButton.addActionListener(event -> searchWeather());
        cityField.addActionListener(event -> searchWeather());

        frame.setVisible(true);
        cityField.requestFocusInWindow();
        statusLabel.setText("Setup tip: add your OpenWeatherMap API key to config.properties.");
    }

    private JPanel buildInterface() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(24, 28, 22, 28));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Weather App");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(NAVY);

        JLabel subtitle = new JLabel("Current conditions for cities around the world");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);
        subtitle.setBorder(new EmptyBorder(5, 1, 0, 0));

        header.add(title);
        header.add(subtitle);
        root.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(buildSearchPanel(), BorderLayout.NORTH);
        center.add(buildWeatherPanel(), BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(MUTED);
        root.add(statusLabel, BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 227, 238)),
                new EmptyBorder(12, 14, 12, 14)));

        cityField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        cityField.setToolTipText("Example: Manila, Tokyo, London");
        cityField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 218, 231)),
                new EmptyBorder(9, 10, 9, 10)));

        searchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(BLUE);
        searchButton.setFocusPainted(false);
        searchButton.setBorder(new EmptyBorder(10, 18, 10, 18));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        panel.add(cityField, BorderLayout.CENTER);
        panel.add(searchButton, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildWeatherPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 227, 238)),
                new EmptyBorder(22, 24, 22, 24)));

        JPanel mainWeather = new JPanel(new BorderLayout(18, 0));
        mainWeather.setOpaque(false);

        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 58));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(100, 90));
        mainWeather.add(iconLabel, BorderLayout.WEST);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        cityLabel.setFont(new Font("SansSerif", Font.BOLD, 21));
        cityLabel.setForeground(NAVY);
        conditionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        conditionLabel.setForeground(MUTED);
        conditionLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        temperatureLabel.setFont(new Font("SansSerif", Font.BOLD, 42));
        temperatureLabel.setForeground(BLUE);
        temperatureLabel.setBorder(new EmptyBorder(9, 0, 0, 0));

        details.add(cityLabel);
        details.add(conditionLabel);
        details.add(temperatureLabel);
        mainWeather.add(details, BorderLayout.CENTER);
        panel.add(mainWeather, BorderLayout.CENTER);

        JPanel metrics = new JPanel(new GridLayout(2, 2, 12, 12));
        metrics.setOpaque(false);
        metrics.setBorder(new EmptyBorder(16, 0, 0, 0));
        metrics.add(makeMetric("FEELS LIKE", feelsLikeLabel));
        metrics.add(makeMetric("HUMIDITY", humidityLabel));
        metrics.add(makeMetric("WIND SPEED", windLabel));
        metrics.add(makeMetric("PRESSURE", pressureLabel));
        panel.add(metrics, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel makeMetric(String title, JLabel valueLabel) {
        JPanel tile = new JPanel();
        tile.setBackground(new Color(246, 248, 252));
        tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
        tile.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        titleLabel.setForeground(MUTED);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        valueLabel.setForeground(NAVY);
        valueLabel.setBorder(new EmptyBorder(7, 0, 0, 0));

        tile.add(titleLabel);
        tile.add(valueLabel);
        return tile;
    }

    private void searchWeather() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            showStatus("Please enter a city name.", true);
            cityField.requestFocusInWindow();
            return;
        }

        String apiKey;
        try {
            apiKey = readApiKey();
        } catch (IOException exception) {
            showStatus("Could not read config.properties. Make sure it is in the project folder.", true);
            return;
        }

        if (apiKey.isBlank() || apiKey.equalsIgnoreCase("YOUR_OPENWEATHERMAP_API_KEY")) {
            showStatus("Add your API key to config.properties before searching.", true);
            return;
        }

        searchButton.setEnabled(false);
        searchButton.setText("Loading...");
        showStatus("Fetching weather for " + city + "...", false);

        SwingWorker<WeatherData, Void> worker = new SwingWorker<>() {
            @Override
            protected WeatherData doInBackground() throws Exception {
                return fetchWeather(city, apiKey);
            }

            @Override
            protected void done() {
                try {
                    WeatherData data = get();
                    displayWeather(data);
                    showStatus("Weather data loaded successfully.", false);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showStatus("The request was interrupted. Please try again.", true);
                } catch (ExecutionException exception) {
                    Throwable cause = exception.getCause();
                    String message = cause == null ? "An unexpected error occurred." : cause.getMessage();
                    showStatus(message == null ? "An unexpected error occurred." : message, true);
                } finally {
                    searchButton.setEnabled(true);
                    searchButton.setText("Get Weather");
                }
            }
        };
        worker.execute();
    }

    private String readApiKey() throws IOException {
        Properties properties = new Properties();
        Path configPath = Path.of("config.properties");
        if (!Files.exists(configPath)) {
            throw new IOException("config.properties not found");
        }
        try (InputStream input = Files.newInputStream(configPath)) {
            properties.load(input);
        }
        return properties.getProperty("api.key", "").trim();
    }

    private WeatherData fetchWeather(String city, String apiKey) throws Exception {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
        String url = "https://api.openweathermap.org/data/2.5/weather?q="
                + encodedCity + "&appid=" + encodedKey + "&units=metric";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException(getApiErrorMessage(response.statusCode(), response.body()));
        }

        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject main = root.getAsJsonObject("main");
        JsonArray weatherArray = root.getAsJsonArray("weather");
        JsonObject weather = weatherArray.get(0).getAsJsonObject();
        JsonObject wind = root.has("wind") ? root.getAsJsonObject("wind") : new JsonObject();
        JsonObject sys = root.has("sys") ? root.getAsJsonObject("sys") : new JsonObject();

        WeatherData data = new WeatherData();
        data.city = getString(root, "name", city);
        data.country = getString(sys, "country", "");
        data.condition = getString(weather, "main", "Weather");
        data.description = capitalize(getString(weather, "description", "Current conditions"));
        data.temperature = getDouble(main, "temp");
        data.feelsLike = getDouble(main, "feels_like");
        data.humidity = getInt(main, "humidity");
        data.pressure = getInt(main, "pressure");
        data.windSpeed = getDouble(wind, "speed");
        return data;
    }

    private String getApiErrorMessage(int statusCode, String body) {
        String apiMessage = "";
        try {
            JsonObject errorJson = JsonParser.parseString(body).getAsJsonObject();
            if (errorJson.has("message")) {
                apiMessage = errorJson.get("message").getAsString();
            }
        } catch (RuntimeException ignored) {
            // Use the friendly status message below if the response is not valid JSON.
        }

        if (statusCode == 401) {
            return "API key not accepted. Check config.properties and make sure your key is active.";
        }
        if (statusCode == 404) {
            return "City not found. Check the spelling or try adding the country, such as Manila, PH.";
        }
        if (statusCode == 429) {
            return "Too many requests. Please wait a little and try again.";
        }
        if (statusCode >= 500) {
            return "The weather service is temporarily unavailable. Please try again later.";
        }
        if (!apiMessage.isBlank()) {
            return "Weather request failed: " + apiMessage;
        }
        return "Weather request failed (HTTP " + statusCode + "). Please try again.";
    }

    private String getString(JsonObject object, String key, String fallback) {
        JsonElement element = object.get(key);
        return element == null || element.isJsonNull() ? fallback : element.getAsString();
    }

    private double getDouble(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element == null || element.isJsonNull() ? 0.0 : element.getAsDouble();
    }

    private int getInt(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element == null || element.isJsonNull() ? 0 : element.getAsInt();
    }

    private String capitalize(String text) {
        if (text == null || text.isBlank()) {
            return "Current conditions";
        }
        return text.substring(0, 1).toUpperCase(Locale.ROOT) + text.substring(1);
    }

    private String weatherSymbol(String condition) {
        if (condition == null) return "🌡";
        return switch (condition.toLowerCase(Locale.ROOT)) {
            case "clear" -> "☀";
            case "clouds" -> "☁";
            case "rain", "drizzle" -> "🌧";
            case "thunderstorm" -> "⛈";
            case "snow" -> "❄";
            case "mist", "fog", "haze", "smoke", "dust", "sand", "ash", "squall", "tornado" -> "🌫";
            default -> "🌡";
        };
    }

    private void displayWeather(WeatherData data) {
        String location = data.city;
        if (!data.country.isBlank()) {
            location += ", " + data.country;
        }
        cityLabel.setText(location);
        conditionLabel.setText(data.description);
        iconLabel.setText(weatherSymbol(data.condition));
        temperatureLabel.setText(String.format(Locale.US, "%.1f°C", data.temperature));
        feelsLikeLabel.setText(String.format(Locale.US, "%.1f°C", data.feelsLike));
        humidityLabel.setText(data.humidity + "%");
        windLabel.setText(String.format(Locale.US, "%.1f m/s", data.windSpeed));
        pressureLabel.setText(data.pressure + " hPa");
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? RED : MUTED);
    }

    private static class WeatherData {
        String city;
        String country;
        String condition;
        String description;
        double temperature;
        double feelsLike;
        int humidity;
        int pressure;
        double windSpeed;
    }
}
