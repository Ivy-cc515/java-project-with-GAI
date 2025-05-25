package com.example.user.quiz;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter // 自動生成getter和setter方法
public class QuizModel {

    private String number; // 問題編號
    private String question; // 問題文本
    private String optionA; // 選項 A
    private String optionB; // 選項 B
    private String optionC; // 選項 C
    private String optionD; // 選項 D
    private String correctAnswer; // 正確答案

    // Constructor, 初始化QuizModel
    public QuizModel(String number, String question, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
        this.number = number;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
    }
}
