package ru.knazarov.miro.exception;

public class WidgetNotFoundException extends RuntimeException {

    public WidgetNotFoundException(String id)
    {
        super("widget with id = \"" + id + "\" doesn't exist");
    }
}
