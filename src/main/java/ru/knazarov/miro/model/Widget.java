package ru.knazarov.miro.model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.uuid.Generators;

public class Widget {
    private final String id = Generators.timeBasedGenerator().generate().toString();
    private int x;
    private int y;
    private int z;
    private int width;
    private int height;
    private LocalDateTime modifiedDT;

    private Widget() {}

    public static Widget getInstance(int x, int y, int width, int height, int z)
    {
        Widget widget = new Widget();
        widget.x = x;
        widget.y = y;
        widget.width = width;
        widget.height = height;
        widget.z = z;
        widget.modifiedDT = LocalDateTime.now();

        return widget;
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }



    public int getY() {
        return y;
    }



    public int getZ() {
        return z;
    }

    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }



    public LocalDateTime getModifiedDT() {
        return modifiedDT;
    }


    public void updateWidget(Widget newWidget)
    {
        this.x = newWidget.x;
        this.y = newWidget.y;
        this.width = newWidget.width;
        this.height = newWidget.height;
        this.z = newWidget.z;
        this.modifiedDT = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Widget{" +
                "id='" + id + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", width=" + width +
                ", height=" + height +
                ", modifiedDT=" + modifiedDT +
                '}';
    }
}
