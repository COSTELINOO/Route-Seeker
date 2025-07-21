package UI;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dtos.UserDTO;
import dtos.ErrorDTO;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class HttpJwtClient {

    public static String ip="0.0.0.0";
    public static int port=8082;
    public static String urlSTring="http://"+ip+":"+port;
    public static HttpURLConnection connection;
    public static URL url;
    private static String token = null;
    public static String loggedRequest;

    // Adaugă această metodă care actualizează loggedRequest când avem un token valid
    public static void updateLoggedRequestBearer() {
        if (token != null && !token.isEmpty()) {
            // Construiește string-ul pentru header-ul de autorizare în format "Bearer <token>"
            loggedRequest = "Bearer " + token;
            System.out.println("Token actualizat: Authorization header pregătit pentru request-uri.");
        } else {
            loggedRequest = null;
            System.out.println("Lipsă token de autentificare. Vă rugăm să vă autentificați mai întâi.");
        }
    }

    // Modificare pentru metoda sendAuthRequest pentru a actualiza loggedRequest după autentificare
    public static Boolean sendAuthRequest(UserDTO user, String endpoint) {
        try {
            URL url = new URL(HttpJwtClient.urlSTring + endpoint);
            HttpJwtClient.connection = (HttpURLConnection) url.openConnection();
            HttpJwtClient.connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection.setDoOutput(true);
            HttpJwtClient.connection.setRequestProperty("Content-Type", "application/json; utf-8");
            HttpJwtClient.connection.setRequestProperty("Accept", "application/json");
            Gson gson = new Gson();

            String jsonInputString = gson.toJson(user);
            try (OutputStream os = HttpJwtClient.connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int status = HttpJwtClient.connection.getResponseCode();

            BufferedReader in;
            if (status >= 200 && status < 300) {
                in = new BufferedReader(new InputStreamReader(HttpJwtClient.connection.getInputStream(), "utf-8"));
            } else {
                in = new BufferedReader(new InputStreamReader(HttpJwtClient.connection.getErrorStream(), "utf-8"));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine.trim());
            }
            in.close();

            String responseString = response.toString();
            System.out.println(responseString);

            if (status >= 200 && status < 300) {
                JSONObject jsonObject = new JSONObject(responseString);
                token = jsonObject.getString("token");
                updateLoggedRequestBearer();
                System.out.println("Token: "+loggedRequest);
                HttpJwtClient.connection.disconnect();
                return true;
            } else {
                // --- Adăugare tratament pentru ErrorDTO la autentificare/inregistrare! ---
                try {
                    ErrorDTO errorDTO = new Gson().fromJson(responseString, ErrorDTO.class);
                    if (errorDTO != null && errorDTO.getMessage() != null) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Error Message: " + errorDTO.getMessage() + "\n" + "Error code: " + errorDTO.getCode(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (Exception ignored) {}
            }
            HttpJwtClient.connection.disconnect();
        } catch (Exception ex) {
            System.out.println("Eroare la request: " + ex.getMessage());
        }
        return false;
    }
    public static <T> List<T> sendToServerForList(
            Object object,
            String endpoint,
            String method,
            boolean sendBody,
            boolean needsBody,
            Class<T> elementClass
    ) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlSTring + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection.setRequestProperty("Authorization", loggedRequest);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");

            if (sendBody) {
                connection.setDoOutput(true);
                String jsonInputString = new Gson().toJson(object);
                System.out.println(jsonInputString);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            int status = connection.getResponseCode();

            if (needsBody) {
                // Get appropriate stream, handling null case
                InputStream inputStream = null;
                if (status >= 200 && status < 300) {
                    inputStream = connection.getInputStream();
                } else {
                    inputStream = connection.getErrorStream();
                }

                // Check if we have a valid stream to read from
                if (inputStream == null) {
                    System.out.println("Error: No data stream available from server");
                    return new ArrayList<>();
                }

                try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine.trim());
                    }

                    String responseString = response.toString();
                    if (responseString.isEmpty()) {
                        System.out.println("Warning: Empty response from server");
                        return new ArrayList<>();
                    }

                    if (status >= 200 && status < 300) {
                        try {
                            // Handle case where response is empty or not a valid array
                            if (responseString.trim().startsWith("[")) {
                                Gson gson = new GsonBuilder().create();
                                JsonArray jsonArray = JsonParser.parseString(responseString).getAsJsonArray();
                                List<T> resultList = new ArrayList<>();

                                for (JsonElement element : jsonArray) {
                                    try {
                                        T item = gson.fromJson(element, elementClass);
                                        resultList.add(item);
                                    } catch (Exception e) {
                                        System.out.println("Error deserializing element: " + e.getMessage());
                                        // Continue processing other elements
                                    }
                                }
                                return resultList;
                            } else {
                                // POSIBIL ERRORDTO: tratează și acest caz
                                try {
                                    Gson gson = new Gson();
                                    ErrorDTO errorDTO = gson.fromJson(responseString, ErrorDTO.class);
                                    if (errorDTO != null && errorDTO.getMessage() != null) {
                                        JOptionPane.showMessageDialog(
                                                null,
                                                "Error Message: " + errorDTO.getMessage() + "\n" +
                                                        "Error code: " + errorDTO.getCode(),
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE
                                        );
                                    }
                                } catch (Exception ignored) {}

                                System.out.println("Warning: Response is not a JSON array: " + responseString);
                                return new ArrayList<>();
                            }
                        } catch (Exception e) {
                            System.out.println("Error parsing JSON response: " + e.getMessage());
                            return new ArrayList<>();
                        }
                    } else {
                        // POSIBIL ERRORDTO: tratează și acest caz
                        try {
                            Gson gson = new Gson();
                            ErrorDTO errorDTO = gson.fromJson(responseString, ErrorDTO.class);
                            if (errorDTO != null && errorDTO.getMessage() != null) {
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Error Message: " + errorDTO.getMessage() + "\n" +
                                                "Error code: " + errorDTO.getCode(),
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                        } catch (Exception ignored) {}

                        System.out.println("Server error: Status " + status + ", Response: " + responseString);
                        return new ArrayList<>();
                    }
                }
            }

            return new ArrayList<>(); // Return empty list when no body is needed
        } catch (Exception ex) {
            System.out.println("Error in HTTP request: " + ex.getMessage());
            ex.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static <T> T sendToServer(
            Object object,
            String endpoint,
            String method,
            boolean sendBody,
            boolean needsBody,
            Type responseType // <-- cheia!
    ) {
        try {
            URL url = new URL(urlSTring + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection.setRequestProperty("Authorization", loggedRequest);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");

            if (sendBody) {
                connection.setDoOutput(true);
                String jsonInputString = new Gson().toJson(object);
                System.out.println(jsonInputString);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            int status = connection.getResponseCode();

            if (needsBody) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(
                        status >= 200 && status < 300 ? connection.getInputStream() : connection.getErrorStream(), "utf-8"))) {

                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine.trim());
                    }

                    String responseString = response.toString();

                    if (status >= 200 && status < 300) {
                        return new Gson().fromJson(responseString, responseType);
                    } else {
                        // POSIBIL ERRORDTO: tratează și aici
                        try {
                            Gson gson = new Gson();
                            ErrorDTO errorDTO = gson.fromJson(responseString, ErrorDTO.class);
                            if (errorDTO != null && errorDTO.getMessage() != null) {
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Error Message: " + errorDTO.getMessage() + "\n" +
                                                "Error code: " + errorDTO.getCode(),
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                        } catch (Exception ignored) {}

                        System.out.println("Eroare server: " + response);
                        if (responseType == Boolean.class)
                            return (T) Boolean.FALSE;
                        else
                            return null;
                    }
                }
            }
            connection.disconnect();

            if (responseType == Boolean.class)
                return (T) Boolean.TRUE;
            else {
                return null;
            }
        } catch (Exception ex) {
            System.out.println("Eroare la request: " + ex.getMessage());

            if (responseType == Boolean.class)
                return (T) Boolean.FALSE;
            else
                return null;
        }
    }

    public static void setToken(String t) {
        token = t;
    }

    public static String getToken() {
        return token;
    }

    public static void clearToken() {
        token = null;
    }

    public static HttpResponse<String> sendRequest(String method, String endpoint, String bodyJson) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080" + endpoint))
                    .timeout(Duration.ofSeconds(10));

            // Adaugă header Authorization dacă avem token
            if (token != null) {
                builder.header("Authorization", "Bearer " + token);
            }

            // Adaugă body dacă e POST/PUT
            switch (method.toUpperCase()) {
                case "GET":
                    builder.GET();
                    break;
                case "POST":
                    builder.header("Content-Type", "application/json");
                    builder.POST(HttpRequest.BodyPublishers.ofString(bodyJson != null ? bodyJson : ""));
                    break;
                case "PUT":
                    builder.header("Content-Type", "application/json");
                    builder.PUT(HttpRequest.BodyPublishers.ofString(bodyJson != null ? bodyJson : ""));
                    break;
                case "DELETE":
                    builder.DELETE();
                    break;
                default:
                    throw new IllegalArgumentException("Metodă HTTP necunoscută: " + method);
            }

            HttpRequest request = builder.build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            System.out.println("Eroare la request: " + e.getMessage());
            return null;
        }
    }

    public static String getResponseBody(HttpResponse<String> response) {
        return response != null ? response.body() : "Eroare: response null";
    }

    public static int getStatusCode(HttpResponse<String> response) {
        return response != null ? response.statusCode() : -1;
    }
    public static Boolean sendFileRequest(String endpoint) {
        try {
            URL url = new URL(urlSTring + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection.setRequestProperty("Authorization", loggedRequest);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");

            int status = connection.getResponseCode();

            if (status >= 200 && status < 300) {
                connection.disconnect();
                return true;
            } else {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine.trim());
                    }

                    String responseString = response.toString();
                    try {
                        Gson gson = new Gson();
                        ErrorDTO errorDTO = gson.fromJson(responseString, ErrorDTO.class);
                        if (errorDTO != null && errorDTO.getMessage() != null) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Error Message: " + errorDTO.getMessage() + "\n" +
                                            "Error code: " + errorDTO.getCode(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } catch (Exception ignored) {
                        // Dacă nu e ErrorDTO, afișează mesajul brut
                        JOptionPane.showMessageDialog(
                                null,
                                "Eroare server: " + responseString,
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
                connection.disconnect();
                return false;
            }
        } catch (Exception ex) {
            System.out.println("Eroare la request: " + ex.getMessage());
            JOptionPane.showMessageDialog(
                    null,
                    "Eroare la comunicarea cu serverul: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

}