/**
 * A Java program to fetch and display weather data
 * for a specified location using the OpenWeatherMap API.
 * It sends an HTTP request, processes the JSON response, 
 * and displays the weather information, such as temperature and humidity, in a clear table format.
 * @author Akhil Peruri
 */
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * The WeatherFetcher class fetches weather data from the OpenWeatherMap API 
 * for a given city. It constructs a request URL using the city name and API key, 
 * sends an HTTP GET request, and processes the JSON response to extract weather details
 * like temperature, humidity, and wind speed. The results are displayed in a table format.
 */
public class WeatherFetcher {
    /** API key to authenticate requests to OpenWeatherMap. Created this 
     * by navigating to the website and signing up. */
    private static final String API_KEY = "a59003130ca0ce829a09d9f56238f68b";
    /** Base URL of the OpenWeatherMap API for weather data */
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    /** To test as url, this is the format, for example given Phoenix:
     * http://api.openweathermap.org/data/2.5/weather?q=Phoenix&appid=a59003130ca0ce829a09d9f56238f68b&units=metric
     * q=Phoenix: the city name parameter
     * appid=a59003130ca0ce829a09d9f56238f68b: my API key
     * units=metric: units to use for data
     */
    
    public static void main(String[] args) {
        /** Changes city as needed */
        /** Need to use + or %20 to indicate space */
        String city = "Holly+Springs";
        /** Gets weather data from the API */
        String jsonResponse = fetchWeatherData(city);
        /** If the response is not null, proceeds to parse and display the data */
        if (jsonResponse != null) {
            parseAndDisplayWeather(jsonResponse);
        }
    }

    /** Fetches weather data from OpenWeatherMap API */
    private static String fetchWeatherData(String city) {
        try {
            /** Uses metric as units so it doesn't default to Kelvin for temepratures */
            /** Built-in API request by city name - https://openweathermap.org/current#name */
            String urlString = String.format("%s?q=%s&appid=%s&units=metric", BASE_URL, city, API_KEY);
            /** Creates a url object from the urlString */
            URL url = new URL(urlString);
            /** Opens a HTTP connection to the URL */
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            /** Sets the request method to GET */
            conn.setRequestMethod("GET");

            // Checks if the response code is 200 (success)
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Unable to fetch data. HTTP code: " + conn.getResponseCode());
                /** Retusn null if the request didn't work (unsucessful) */
                return null;
            }

            /** Reads the response using Scanner */
            /** The input stream is connected to the HTTP URL, specificially to the
             * response from the API server */
            Scanner scanner = new Scanner(conn.getInputStream());
            String jsonResponse = "";
            /** Loops through each line in the response and adds it to jsonReponse */
            while (scanner.hasNextLine()) {
                jsonResponse += scanner.nextLine();  // String concatenation
            }
            /** Closes scanner after reading */
            scanner.close();
            /** Returns JSON response */
            return jsonResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Parse and display weather data
    private static void parseAndDisplayWeather(String jsonResponse) {
        /** Extracts city name from JSON response */
        String cityName = extractValue(jsonResponse, "\"name\":\"", "\"");
        /** Extracts temperature in Celsius */
        String temperature = extractValue(jsonResponse, "\"temp\":", ",");
        /** Extracts temperature in Celsius */
        String feelsLike = extractValue(jsonResponse, "\"feels_like\":", ",");
        /** Extracts humidity percentage */
        String humidity = extractValue(jsonResponse, "\"humidity\":", ",");
        /** Extracts wind speed is meters per second */
        String windSpeed = extractValue(jsonResponse, "\"speed\":", "}");

        /** Cleans up windSpeed value by removing extra characters (like ",\"deg\":0") */
        if (windSpeed.contains(",")) {
            /** Keeps only the speed value */
            windSpeed = windSpeed.split(",")[0];  // Take only the first part (the speed)
        }

        // Convert temperatures from Celsius to Fahrenheit
        double tempF = convertCelsiusToFahrenheit(Double.parseDouble(temperature));
        double feelsLikeF = convertCelsiusToFahrenheit(Double.parseDouble(feelsLike));
        double windSpeedMps = Double.parseDouble(windSpeed); // Wind speed remains in m/s

        /** Displays the weather data in a tabular format */
        System.out.println("+----------------------+--------------------+");
        System.out.printf("| %-20s | %-15s    |\n", "Description", "Value");
        System.out.println("+----------------------+--------------------+");
        System.out.printf("| %-20s | %-15s    |\n", "City", cityName);
        System.out.printf("| %-20s | %-15.2f °F |\n", "Temperature", tempF);
        System.out.printf("| %-20s | %-15.2f °F |\n", "Feels Like", feelsLikeF);
        System.out.printf("| %-20s | %-15s  %% |\n", "Humidity", humidity);
        System.out.printf("| %-20s | %-15.2f m/s|\n", "Wind Speed", windSpeedMps);
        System.out.println("+----------------------+--------------------+");
    }

    /** Helper method to convert Celsius to Fahrenheit */
    private static double convertCelsiusToFahrenheit(double celsius) {
        return (celsius * 9/5) + 32;
    }

    /** Helper method to extract values from JSON string */
    private static String extractValue(String json, String keyStart, String keyEnd) {
        /** Finds the start and end index of the desired value */
        int startIndex = json.indexOf(keyStart) + keyStart.length();
        int endIndex = json.indexOf(keyEnd, startIndex);
        /** Extracts the substring between startIndex and endIndex */
        return json.substring(startIndex, endIndex);
    }
}
