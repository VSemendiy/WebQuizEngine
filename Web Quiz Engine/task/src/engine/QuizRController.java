package engine;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class QuizRController {

    @Autowired
    private QuizCardRepository quizCardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HistPointRepository histPointRepository;


    static boolean NotAuth = true;

    private static List<QuizCard> quiz = new ArrayList<>();

    public QuizRController() {
    }

    @PostMapping(path = "/api/quizzes/{id}/solve")
    public String solveQuizCard(@RequestBody(required = false) String answer, @PathVariable int id, @RequestHeader(name = "Authorization", required = false) String auth) {
        String userId = checkAuth(auth);

        List<Integer> answers = new ArrayList<>();
        String ansString = answer.replaceAll("(.*\\[)|(\\].*)|(.*:)|(}.*)", "").trim();
        if (ansString.length() > 0)
            answers = Arrays.stream(ansString.split(",")).map(Integer::parseInt).collect(Collectors.toList());

        QuizCard quizCard = quizCardRepository.findById(id);
        if (quizCard == null) throw new QuizNotFoundException();

        boolean res = quizCard.checkAnswer(answers);

        if (res) {
            HistPoint histPoint = new HistPoint();
            histPoint.setHistPointId((int) histPointRepository.count() + 1);
            histPoint.setUserId(userId);
            histPoint.setId(id);
            histPoint.setCompletedAt(LocalDateTime.now());
            histPointRepository.save(histPoint);

            return String.format("{\"success\":%b,\"feedback\":\"Congratulations, you're right!\"}", res);
        } else return String.format("{\"success\":%b,\"feedback\":\"Wrong answer! Please, try again.\"}", res);
    }

    @PostMapping(path = "/api/quizzes", consumes = "application/json")
    public String createQuizCard(@RequestBody QuizCard quizCard, @RequestHeader(name = "Authorization", required = false) String auth) {
        String author = checkAuth(auth);
        quizCard.setId(quiz.size() + 1);
        quizCard.setAuthor(author);
        quiz.add(quizCard);
        quizCardRepository.save(quizCard);
        String opt = Arrays.toString(Arrays.stream(quizCard.getOptions()).map(t -> "\"" + t + "\"").toArray());
        String answers = Arrays.toString(quizCard.getAnswer());
        return String.format("{\"id\": %d,\"title\": \"%s\",\"text\": \"%s\",\"answer\": %s,\"options\":%s}",
                quiz.size(), quizCard.getTitle(), quizCard.getText(), answers, opt);
    }

    @GetMapping(path = "/api/quizzes/{id}")
    public String getQuizCardById(@PathVariable int id, @RequestHeader(name = "Authorization", required = false) String auth) {
        checkAuth(auth);
        QuizCard quizCard = quizCardRepository.findById(id);
        if (quizCard == null) throw new QuizNotFoundException();
        return quizCard.toString().replaceFirst("\\{", "{\"id\":" + quiz.size() + ",");
    }

    @GetMapping(path = "/api/quizzes")
    public ResponseEntity<Page<IdTitleTextOptions>> getAllQuizCards(@RequestHeader(name = "Authorization", required = false) String auth,
                                  @RequestParam(defaultValue = "0") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size) {
        checkAuth(auth);
        List<String> resp = new ArrayList<>();
        var quiz = quizCardRepository.findAllBy(PageRequest.of(page, size));
        return new ResponseEntity<Page<IdTitleTextOptions>>(quiz, HttpStatus.OK);
    }

    @DeleteMapping(path = "/api/quizzes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteQuizCardById(@PathVariable int id, @RequestHeader(name = "Authorization", required = false) String auth) {
        String author = checkAuth(auth);
        QuizCard temp = quizCardRepository.findById(id);
        if (temp == null) throw new QuizNotFoundException();
        if (!author.equals(temp.getAuthor())) throw new SomeoneElsesCardException();
        quizCardRepository.deleteById(id);
    }

    @GetMapping(path = "/api/quizzes/completed")
    public ResponseEntity<Page<IdAndCompletedAt>> getUserHistory(@RequestHeader(name = "Authorization", required = false) String auth,
                                                                        @RequestParam(defaultValue = "0") Integer page,
                                                                        @RequestParam(defaultValue = "10") Integer size) {
        String userId = checkAuth(auth);
        List<String> resp = new ArrayList<>();

        Pageable pageble = PageRequest.of(page, size);

        var history = histPointRepository.findAllByUserIdOrderByCompletedAtDesc(userId, pageble);

        return new ResponseEntity<Page<IdAndCompletedAt>>(history, HttpStatus.OK);
    }


    @PostMapping(path = "/api/register")
    public void registerUser(@RequestBody User user) {
        User temp = userRepository.findById(user.getEmail()).orElse(null);
        if (temp == null) userRepository.save(user);
        else throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>());
    }

    public String checkAuth(String authHeader) {
        if (authHeader != null) {
            List<String> users = userRepository.findAll().stream().map(t -> t.getPassword()).collect(Collectors.toList());
            if (users != null) {
                authHeader = authHeader.replaceAll("Basic\\s*", "");
                if (users.contains(authHeader)) return userRepository.findByPassword(authHeader).getEmail();
            }
        }
        throw new AuthException();
    }

    @ExceptionHandler({javax.validation.ConstraintViolationException.class,
            NullPointerException.class,
            javax.persistence.RollbackException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public HashMap<String, String> handleIndexOutOfBoundsException(Exception e) {
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "Error");
        response.put("error", e.getClass().getSimpleName());
        return response;
    }


}

