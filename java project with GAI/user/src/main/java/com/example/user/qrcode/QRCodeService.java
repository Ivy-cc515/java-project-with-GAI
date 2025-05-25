package com.example.user.qrcode;

// 使用了Spring MVC的Controller和Service架構來處理HTTP請求和業務邏輯。
// Hovercode API: 外部API，用於創建和刪除QR碼
import com.google.gson.Gson; // Gson: 用於將Java對象轉換為JSON字符串
import org.springframework.http.HttpEntity; 
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // RestTemplate: 用於發送HTTP請求和處理HTTP響應
import java.util.Objects;


@Service // 註解為Spring的服務類
public class QRCodeService {

    private final RestTemplate restTemplate;
    private final Gson gson;

    // Constructor, 初始化QRCodeService
    public QRCodeService(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    // 生成並刪除QRCode，回傳QRCode的URL，之後呼叫deleteQrCodeWithDelay 10秒後刪除。
    // 通過POST請求調用外部API（hovercode.com）創建QRCode，然後在schedule中延遲10秒才會刪除該QRCode
    public String generateAndDeleteQrCode() throws Exception {
        // 創建QRCodeModel實例
        QRCodeModel generate = new QRCodeModel("6aa64eb1-b80d-4f72-a1d6-7b8971fc508e", "https://0e6c-120-113-180-141.ngrok-free.app/register", "#1DA1F2", true, true, true, "https://img.lovepik.com/free-png/20210919/lovepik-cute-pig-image-png-image_400807463_wh1200.png", "circle-viewfinder");
        // 將實例轉換為JSON字符串
        String jsonRequest = gson.toJson(generate);

        // 設置HTTP請求頭
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Token fc460508430e8779ef4194a9cc70f73ceb4cbd1a");
        headers.set("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity<>(jsonRequest, headers);

        // 發送POST請求以創建QRCode
        String createUrl = "https://hovercode.com/api/v2/hovercode/create/";
        ResponseEntity<String> postResponse = restTemplate.exchange(createUrl, HttpMethod.POST, request, String.class);
        generate = gson.fromJson(Objects.requireNonNull(postResponse.getBody()), QRCodeModel.class);

        // 獲取生成的QRCode的URL
        String qrCodeUrl = generate.getPng();
        final String qrCodeId = generate.getId();  // 將QRCodeID 設為final

        // Schedule deletion of QR code with delay in a separate thread
        new Thread(() -> {
            try {
                deleteQrCodeWithDelay(qrCodeId, 10000); // 延遲10秒後刪除
            } catch (Exception e) {
                System.out.println("Failed to delete QR Code: " + e.getMessage());
            }
        }).start();

        return qrCodeUrl;
    }

    // 延遲刪除QR碼的方法
    private void deleteQrCodeWithDelay(String qrCodeId, int delayMillis) throws InterruptedException {
        Thread.sleep(delayMillis); // 休眠(毫秒)

        // 構建刪除URL和HTTP請求頭
        String deleteUrl = "https://hovercode.com/api/v2/hovercode/" + qrCodeId + "/delete/";
        HttpHeaders deleteHeaders = new HttpHeaders();
        deleteHeaders.set("Authorization", "Token fc460508430e8779ef4194a9cc70f73ceb4cbd1a");
        HttpEntity<Void> deleteRequest = new HttpEntity<>(deleteHeaders);
        ResponseEntity<String> deleteResponse = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, deleteRequest, String.class);

        // 根據HTTP響應狀態碼檢查刪除是否成功
        if (deleteResponse.getStatusCode().value() == 204) {
            System.out.println("QR Code deleted successfully.");
        } else {
            System.out.println("Failed to delete QR Code.");
        }
    }
}


