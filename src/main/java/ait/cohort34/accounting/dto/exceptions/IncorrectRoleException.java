package ait.cohort34.accounting.dto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(value = HttpStatus.BAD_REQUEST)   // same as:
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Incorrect role")
public class IncorrectRoleException extends RuntimeException {
}
