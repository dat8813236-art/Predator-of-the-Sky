package quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestionManager {

    private List<Question> questions = new ArrayList<>();
    private Random random = new Random();

    public QuestionManager() {
        questions.add(new Question(
                "Java dùng từ khóa nào để tạo class?",
                new String[]{"class", "define", "new", "object"},
                0
        ));

        questions.add(new Question(
                "Từ khóa kế thừa trong Java?",
                new String[]{"extends", "implements", "inherit", "super"},
                0
        ));
    }

    public Question getRandomQuestion() {
        if (questions.isEmpty()) {
            return null;
        }
        return questions.get(random.nextInt(questions.size()));
    }
}