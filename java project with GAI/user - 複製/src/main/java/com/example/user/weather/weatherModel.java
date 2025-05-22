package com.example.user.weather;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class weatherModel {
    private String locationName; // 地點名稱
    private String locationsName; // 地點名稱
    private String sort; // 排序
    private String contentDescription; // 內容描述
    private String datasetDescription; // 數據集描述
    private String dataid; // 數據ID
    private String elementName; 
    private String TimeFrom; // 開始時間
    private String TimeTo; // 結束時間
    private String dataTime; // 數據時間
    private String value; // 值

    // Constructor, 初始化WeatherModel
    public weatherModel(String locationName, String locationsName, String sort, String TimeFrom, String TimeTo, String elementName)
    {
        this.locationName = locationName;
        this.locationsName = locationsName;
        this.sort = sort;
        this.TimeFrom = TimeFrom;
        this.TimeTo = TimeTo;
        this.elementName = elementName;
    }

}
