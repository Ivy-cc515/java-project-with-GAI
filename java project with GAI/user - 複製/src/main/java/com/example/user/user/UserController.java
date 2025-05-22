package com.example.user.user;

import org.springframework.beans.factory.annotation.Autowired; // @Autowired：自動注入 Spring Bean
import org.springframework.stereotype.Controller; // @Controller：註解為 Spring MVC 控制器
import org.springframework.ui.Model; // 用於向視圖傳輸數據
import jakarta.validation.Valid; // @Valid：標記需要驗證的對象
import org.springframework.validation.BindingResult; // BindingResult：保存驗證結果
import org.springframework.web.bind.annotation.GetMapping; //映射 HTTP GET 請求
import org.springframework.web.bind.annotation.PostMapping; //映射 HTTP POST 請求
import org.springframework.web.bind.annotation.RequestParam; //獲取 HTTP 請求中的參數
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // RedirectAttributes：用於重定向時傳遞數據

import com.example.user.quiz.QuizModel;
import com.example.user.quiz.QuizService;
import com.example.user.weather.weatherService;
import java.util.Random; // 生成隨機數
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime; // 處理日期和時間
import java.util.Objects;
import java.util.Optional; // 處理可能為 null 的對象

@Controller // 註解為 Spring MVC 控制器
public class UserController {
    @Autowired // 自動注入 Spring Bean
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuizService pdfService;
    @Autowired
    private weatherService weatherService;

    private List<QuizModel> questions; // 儲存問題列表
    private List<Integer> answeredQuestions = new ArrayList<>(); // 儲存已回答過的問題
    private Random random = new Random(); // 生成隨機數
    

    @GetMapping("/register") // 映射 HTTP GET 請求到 /register 路徑
    // 負責處理顯示簽到頁面的請求，並返回視圖名"register"
    public String viewRegisterPage(Model model) { 
        String weatherInfo = weatherService.fetchWeather();
        model.addAttribute("weatherInfo", weatherInfo);
        model.addAttribute("name", "簽到");
        model.addAttribute("user", new UserModel());

        if (questions == null) { // 如果 questions 為空，則從pdf中提取問題和答案
            questions = pdfService.extractQuestionsAnswers("c:/Users/User/OneDrive/桌面/IJA.pdf");
        }

        // 確保 questions 不為空
        if (questions == null || questions.isEmpty()) {
            model.addAttribute("errorMessage", "無法讀取問題。");
            return "error";
        }

        QuizModel currentQuestion = getRandomQuestion(); // 產生隨機問題
        model.addAttribute("question", currentQuestion); //向視圖添加當前問題
        model.addAttribute("questionIdx", questions.indexOf(currentQuestion)); // 添加當前問題索引
        model.addAttribute("result", ""); //回答結果
        model.addAttribute("correctAnswer", ""); // 正確答案
        return "register";
    }

    @PostMapping("/register") // 映射 HTTP POST 請求到 /register 路徑
    // 處理簽到表單提交。檢查表單驗證是否有錯誤，有錯誤重定向回簽到頁面並顯示錯誤信息。若無錯誤，進行用戶簽到並根據結果設置成功或錯誤信息，然後重定向到簽到頁面
    public String registerProcess(@Valid UserModel user, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model, @RequestParam(required = false) String selectedAnswer) {
        if (bindingResult.hasErrors()) { // 驗證表單
            String message = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/register"; // 重定向
        }
        try {
            UserModel signedInUser = userService.signIn(user); // 用戶簽到
            redirectAttributes.addFlashAttribute("name", signedInUser.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "簽到成功");

            String result = "";
            String correctAnswer = "";

            if (selectedAnswer != null && !answeredQuestions.isEmpty()) { // 確保 answeredQuestions 不為空
                int lastQuestionIdx = answeredQuestions.get(answeredQuestions.size() - 1); //獲取已回答問題的最後一個索引
                QuizModel question = questions.get(lastQuestionIdx); // 獲取對應問題
                correctAnswer = question.getCorrectAnswer(); // 獲取正確答案
                if (correctAnswer.equalsIgnoreCase(selectedAnswer)) { //如果答對更新分數
                    signedInUser.setScore((signedInUser.getScore() != null ? signedInUser.getScore() : 0) + 2); // 更新當前用户的分數
                } else {
                    result = "錯誤，正確答案是 " + correctAnswer;
                }

                model.addAttribute("result", result);
                model.addAttribute("correctAnswer", correctAnswer);

                userRepository.save(signedInUser); // 保存用戶信息
            }

            return "redirect:/register";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "簽到出错: " + e.getMessage());
            return "redirect:/register";
        }

    }
    @GetMapping("/present") // 映射 HTTP GET 請求到 /present 路徑
    // 從 userService 獲取所有用戶並添加到模型中，返回視圖名為 present
    public String getUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "present";
    }

    @PostMapping("/resign") // 映射 HTTP POST 請求到 /resign 路徑
    // 處理簽到請求。根據 userid 查找用戶，若用戶存在則更新簽到時間和簽到次數，並保存更新後的用戶數據。
    public String resign(@RequestParam String userid, Model model) {
        Optional<UserModel> optionalUser = userRepository.findByUserid(userid);
        if (optionalUser.isPresent()) {
            UserModel user = optionalUser.get();
            user.setSignInTime(LocalDateTime.now()); // 更新簽到時間
            user.setSignedIn(true);
            if (user.getSignInCount() == null) {
                user.setSignInCount(1); // 如果簽到次數為null，設置為1
            } else {
                user.setSignInCount(user.getSignInCount() + 1); // 否則簽到次數加一
            }
            userRepository.save(user); //保存數據
        }

        // 重定向到顯示點名單的頁面
        return "redirect:/present";
    }

    private QuizModel getRandomQuestion() {
        int questionIdx;
        do {
            questionIdx = random.nextInt(questions.size()); // 隨機生成一個問題索引
        } while (answeredQuestions.contains(questionIdx)); // 確保問題未被回答過
        answeredQuestions.add(questionIdx); // 如果被回答過，問題加入 answeredQuestions
        return questions.get(questionIdx); // 回傳問題
    }
}
