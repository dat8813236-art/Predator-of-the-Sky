package quiz;

public class Question {
    private String content;
    private String[] options;
    private int correctIndex;

    public Question(String content, String[] options, int correctIndex) {
        this.content = content;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    public String getContent() {
        return content;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }
}