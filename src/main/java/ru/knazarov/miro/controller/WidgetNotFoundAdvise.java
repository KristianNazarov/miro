package ru.knazarov.miro.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.knazarov.miro.exception.WidgetNotFoundException;

@ControllerAdvice
public class WidgetNotFoundAdvise
{
    @ResponseBody
    @ExceptionHandler(WidgetNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String widgetNotFoundHandler(WidgetNotFoundException e)
    {
        return e.getMessage();
    }
}
