package engine;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Entity
public class QuizCard {
    @Id
    private int id;
    @NotNull
    @NotBlank
    private String title;
    @NotNull
    @NotBlank
    private String text;
    @NotNull
    @Size(min = 2)
    private String[] options;
    private Integer[] answer = new Integer[0];
    @NotNull
    private String author;

    public QuizCard() {
    }

    @Override
    public String toString() {
        String opt = Arrays.toString(Arrays.stream(options).map(t -> "\"" + t + "\"").toArray());
        return String.format("{\"id\":%d,\"title\":\"%s\",\"text\":\"%s\",\"options\":%s}", id, title, text, opt);
    }

    public boolean checkAnswer(List<Integer> answers) {
        if (answer == null && answers.size() == 0) return true;
        if (answers.size() != answer.length) return false;
        Collections.sort(answers);
        for (int i = 0; i < answer.length; i++)
            if (!answers.get(i).equals(answer[i])) return false;
        return true;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String[] getOptions() {
        return options;
    }

    public Integer[] getAnswer() {
        return answer;
    }

    public String getAuthor() {
        return author;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public void setAnswer(Integer[] answer) {
        this.answer = answer;
        Arrays.sort(this.answer);
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}