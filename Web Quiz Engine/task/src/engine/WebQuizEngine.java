package engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class WebQuizEngine {

    public static void main(String[] args) {
        SpringApplication.run(WebQuizEngine.class, args);
    }


    @Bean
    public List<QuizCard> getSavedQuizCards(QuizCardRepository repository) {
        List<QuizCard> temp = new ArrayList<>();

        temp.addAll(repository.findAll());

        System.out.println("********************************************************************************************");
        System.out.printf("Total cards readed: %d%n", temp.size());
        System.out.println("********************************************************************************************");

        return temp;
    }

}