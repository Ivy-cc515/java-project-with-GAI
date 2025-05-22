package com.example.user.qrcode;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter // 自動生成getter和setter方法
public class QRCodeModel {
    private String id; // QRCodeID
    private String workspace;  // 工作空間
    private String qr_data; // QRCode數據
    private String primary_color;  // 顏色
    private boolean dynamic; // 是否為動態QRCode
    private boolean gps_tracking; // 是否啟用GPS追踪
    private boolean generate_png; // 是否生成PNG
    private String error_correction;  // 錯誤校正
    private String png; // PNG的URL
    private String status; // 狀態
    private String logo_url; // Logo的URL
    private String frame; // 框架

    // Constructor, 初始化QRCodeModel
    public QRCodeModel(String workspace, String qr_data, String primary_color, boolean dynamic, boolean gps_tracking, boolean generate_png, String logo_url, String frame) {
        this.workspace = workspace;
        this.qr_data = qr_data;
        this.primary_color = primary_color;
        this.dynamic = dynamic;
        this.gps_tracking = gps_tracking;
        this.generate_png = generate_png;
        this.frame = frame;
        this.logo_url = logo_url;
    }
}
