package engine;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Not found")
class QuizNotFoundException extends RuntimeException {
}

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Somthing wrong with auth")
class AuthException extends RuntimeException {
}

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Someone else's card")
class SomeoneElsesCardException extends RuntimeException {
}
