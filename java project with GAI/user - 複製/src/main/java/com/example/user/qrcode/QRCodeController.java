package com.example.user.qrcode;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller  // 註解為Spring MVC控制器
@RequestMapping("/qrcode") // 設定請求路徑的基礎路徑，所有方法均在此路徑下
public class QRCodeController {

    private final QRCodeService qrCodeService;

    //Constructor, 初始化QRCodeController
    public QRCodeController(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    // 處理GET請求，顯示QRCode頁面
    @GetMapping
    public String showQRCodePage() {
        return "qrcode";
    }
    // 處理POST請求，生成並顯示QRCode
    @PostMapping
    public String showQRCode(Model model) {
        try {
            // 調用服務生成和刪除QRCode，並獲取QRCode的URL
            String qrCodeUrl = qrCodeService.generateAndDeleteQrCode();
            model.addAttribute("qrCodeUrl", qrCodeUrl); // 將QRCode的URL添加到模型中
            return "qrcode"; // 返回視圖的名稱 "qrcode"
        } catch (Exception e) {
            e.printStackTrace();
            return "error"; // 錯誤處理
        }
    }
}
