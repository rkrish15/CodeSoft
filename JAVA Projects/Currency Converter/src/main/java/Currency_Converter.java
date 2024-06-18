import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import org.json.JSONObject;

public class Currency_Converter {

    private static final String API_URL = "https://v6.exchangerate-api.com/v6/9314aa4ff7166c3b5982268f/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the base currency (e.g., USD): ");
        String baseCurrency = scanner.nextLine().toUpperCase();

        System.out.println("Enter the target currency (e.g., INR): ");
        String targetCurrency = scanner.nextLine().toUpperCase();

        System.out.println("Enter the amount to convert: ");
        double amount = scanner.nextDouble();

        double convertedAmount = convertCurrency(baseCurrency, targetCurrency, amount);

        System.out.printf("Converted Amount: %.2f %s (from %s)%n", convertedAmount, targetCurrency, baseCurrency);
    }

    public static double convertCurrency(String baseCurrency, String targetCurrency, double amount) {
        double exchangeRate = fetchExchangeRate(baseCurrency, targetCurrency);
        return amount * exchangeRate;
    }

    private static double fetchExchangeRate(String baseCurrency, String targetCurrency) {
        try {
            String url = API_URL + baseCurrency;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            // Print the response body for debugging purposes
            System.out.println("API Response: " + responseBody);

            // Parse the JSON response
            JSONObject json = new JSONObject(responseBody);

            // Check if the request was successful
            if (!json.getString("result").equals("success")) {
                System.out.println("Error: Request was not successful.");
                return 0.0;
            }

            // Check for the "conversion_rates" key
            if (!json.has("conversion_rates")) {
                System.out.println("Error: 'conversion_rates' key not found in the API response.");
                return 0.0;
            }

            JSONObject rates = json.getJSONObject("conversion_rates");
            return rates.getDouble(targetCurrency);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
