package com.example.user.quiz;

import org.apache.pdfbox.pdmodel.PDDocument; // Apache PDFBox ：開源 java 函式庫，支持 PDF 文件的開發和轉換
import org.apache.pdfbox.text.PDFTextStripper; // 從 PDF 提取文本
import org.springframework.stereotype.Service;
import java.io.File; // 表示文件路徑
import java.io.IOException; //處理 IOException
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher; // 執行正則表達式匹配操作
import java.util.regex.Pattern; // 定義正則表達式

@Service
public class QuizService {

    // // 提取 PDF 中的問題和答案，返回一个 QuizModel 列表
    public List<QuizModel> extractQuestionsAnswers(String pdfPath) {
        List<QuizModel> questionsAnswers = new ArrayList<>();
        try {
            PDDocument document = PDDocument.load(new File(pdfPath)); // 加載 PDF 文檔
            PDFTextStripper pdfStripper = new PDFTextStripper(); // 創建 PDF 文本提取器
            String text = pdfStripper.getText(document); // 提取 PDF 文本內容

            // Pattern to match questions
            Pattern questionPattern = Pattern.compile("(\\d+)\\.\\s+(.+?)(?=(?:\\(A\\)|\\d+\\.\\s|\\Z))", Pattern.DOTALL);
            // Pattern to match options
            Pattern optionPattern = Pattern.compile("\\((A|B|C|D)\\)\\s+(.+?)(?=(?:\\(A\\)|\\(B\\)|\\(C\\)|\\(D\\)|Ans))", Pattern.DOTALL);
            // Pattern to match answers
            Pattern answerPattern = Pattern.compile("Ans：([A-D])");

            Matcher questionMatcher = questionPattern.matcher(text); // 創建問題匹配器

            while (questionMatcher.find()) {
                try {
                    String number = questionMatcher.group(1); // 獲取問題編號
                    String question = questionMatcher.group(2).trim(); // 獲取問題且去除空格

                    // Skip questions containing "請參閱附圖"
                    if (question.contains("請參閱附圖")) {
                        System.out.println("Skipping image question: " + question);
                        continue;
                    }
                    String optionA = "", optionB = "", optionC = "", optionD = "";
                    String correctAnswer = "";

                    // 查找當前問題的選項
                    int endIndex = questionMatcher.end(); // 獲取結束索引
                    String questionBlock = text.substring(endIndex);
                    Matcher optionMatcher = optionPattern.matcher(questionBlock); // 選項匹配器
                    while (optionMatcher.find() && optionMatcher.start() < questionBlock.indexOf("Ans")) { // 查找選項，直到遇到答案標記
                        String optionLabel = optionMatcher.group(1).toUpperCase(); // 選項（A、B、C、D）
                        String optionText = optionMatcher.group(2).trim(); // 獲取選項且去除空格
                        switch (optionLabel) {
                            case "A":
                                optionA = optionText;
                                break;
                            case "B":
                                optionB = optionText;
                                break;
                            case "C":
                                optionC = optionText;
                                break;
                            case "D":
                                optionD = optionText;
                                break;
                        }
                    }

                    // 查找當前問題的正確答案
                    Matcher answerMatcher = answerPattern.matcher(questionBlock); // 答案匹配器
                    if (answerMatcher.find()) { // 如果找到匹配的答案
                        correctAnswer = answerMatcher.group(1).toUpperCase();
                    }

                    // 印出問題和選項
                    System.out.println("Question: " + question);
                    System.out.println("Option A: " + optionA);
                    System.out.println("Option B: " + optionB);
                    System.out.println("Option C: " + optionC);
                    System.out.println("Option D: " + optionD);
                    System.out.println("Correct Answer: " + correctAnswer);

                    questionsAnswers.add(new QuizModel(number, question, optionA, optionB, optionC, optionD, correctAnswer)); // 添加至列表
                } catch (Exception e) {
                    System.err.println("Error parsing question: " + e.getMessage());
                }
            }

            document.close(); // 關閉 PDF 文檔
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questionsAnswers; // 回傳問題和答案
    }
}
