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
        questions.add(new Question(
                "Trong Java, từ khóa nào dùng để tạo đối tượng",
                new String[]{"class", "import", "new", "void"},
                0
        ));
        questions.add(new Question(
                "Kiểu dữ liệu nào dùng để lưu số nguyên trong Java?",
                new String[]{"float", "boolean", "int", "char"},
                0
        ));
        questions.add(new Question(
                "Hàm nào là điểm bắt đầu của chương trình Java?",
                new String[]{"start()", "run()", "main()", "system()"},
                0
        ));
        questions.add(new Question(
                "Ký hiệu nào dùng để kết thúc một câu lệnh trong Java?",
                new String[]{":", ";", ".", ","},
                0
        ));
        questions.add(new Question(
                "Kiểu dữ liệu nào dùng để lưu giá trị đúng/sai?",
                new String[]{"int", "string", "boolean ", "double"},
                0
        ));
        questions.add(new Question(
                "Mảng trong Java bắt đầu từ chỉ số nào?",
                new String[]{"1", "-1", "0", "10"},
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