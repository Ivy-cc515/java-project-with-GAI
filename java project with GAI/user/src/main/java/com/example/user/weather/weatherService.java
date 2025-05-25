package com.example.user.weather;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class weatherService {

    // 從 API 獲取天氣數據
    public String fetchWeather() {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            // 含有天氣 API 的 url
            String uriString = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/F-D0047-057?"
                    + "Authorization=CWA-A2A89205-5E4E-4807-9CF4-9977030EAA69&"
                    + "limit=1&"
                    + "format=JSON&"
                    + "locationName=%E5%98%89%E7%BE%A9%E5%B8%82,%E8%A5%BF%E5%8D%80&"
                    + "elementName=WeatherDescription&"
                    + "sort=time";

            // 創建 HTTP GET 請求
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(uriString))
                    .header("Authorization", "CWA-A2A89205-5E4E-4807-9CF4-9977030EAA69")
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            // 發送請求並獲取響應
            HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            if (response.statusCode() == 200) {
                return response.body();  // Return the entire JSON response
                //返回 JSON
            } else {
                return "Failed to fetch weather data."; // 返回錯誤訊息
            }
        } catch (Exception e) { // 捕捉異常
            e.printStackTrace();
            return "Error occurred while fetching weather data.";
        }
    }
}
