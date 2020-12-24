package ru.knazarov.miro.service;

import org.springframework.stereotype.Component;
import ru.knazarov.miro.exception.WidgetNotFoundException;
import ru.knazarov.miro.model.Widget;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class WidgetOperationsService {

    private NavigableMap<Integer, Widget> widgets;
    private Map<String, Integer> zIndexes;


    public WidgetOperationsService(NavigableMap<Integer, Widget> widgets, Map<String, Integer> zIndexes)
    {
        this.widgets = widgets;
        this.zIndexes = zIndexes;
    }


    public void cleanAll()
    {
        widgets = new TreeMap<>();
        zIndexes = new HashMap<>();
    }

    public List<Widget> getWidgets()
    {
        return widgets.values().stream().collect(Collectors.toList());
    }

    public Widget getWidget(String id) throws WidgetNotFoundException
    {
        try {
            Integer zIndex = zIndexes.get(id);
            return widgets.get(zIndex);
        }
        catch (NullPointerException e)
        {
            throw new WidgetNotFoundException(id);
        }
    }

    public Widget createWidget(int x, int y, int width, int height, int z)
    {
        Widget widget = Widget.getInstance(x,y, width, height, z);
        putWidgetToStorage(widget);

        return widget;
    }

    public Widget createWidget(int x, int y, int width, int height)
    {
        return createWidget(x, y, width, height, widgets.size() == 0 ? 0 : widgets.lastKey() + 1);
    }

    public Widget createWidget(Widget widget)
    {
        return createWidget(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), widget.getZ());
    }

    public Widget updateWidget(String id, Widget newWidget) throws WidgetNotFoundException
    {
        try {
            Integer zIndex = zIndexes.get(id);//getting Z index of widget for update
            Widget widgetToUpdate = widgets.get(zIndex);//getting widget to update by z index
            boolean needShift = (widgetToUpdate.getZ() != newWidget.getZ()) && (widgets.get(newWidget.getZ()) != null);

            if (needShift)
                shiftWidget((widgets.get(newWidget.getZ())));

            widgetToUpdate.updateWidget(newWidget);

            if (needShift) {
                widgets.put(widgetToUpdate.getZ(), widgetToUpdate);
                widgets.remove(zIndex);
                zIndexes.put(widgetToUpdate.getId(), widgetToUpdate.getZ());
            }

            return widgetToUpdate;
        }
        catch (NullPointerException e)
        {
            throw new WidgetNotFoundException(id);
        }
    }

    public synchronized void deleteWidget(String id) throws WidgetNotFoundException
    {
        try {
            widgets.remove(zIndexes.get(id));
            zIndexes.remove(id);
        }
        catch (NullPointerException e )
        {
            throw new WidgetNotFoundException(id);
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    private synchronized void putWidgetToStorage(Widget widget)
    {
        if (widgets.get(widget.getZ()) != null)
            shiftWidget(widgets.get(widget.getZ()));

        widgets.put(widget.getZ(), widget);
        zIndexes.put(widget.getId(), widget.getZ());
    }

    private synchronized void shiftWidget(Widget widget)
    {
        int newZ = widget.getZ() + 1;
        Widget tempWidget = widgets.get(newZ);
        if(tempWidget != null)
        {
            shiftWidget(tempWidget);
        }
        widget.updateWidget(Widget.getInstance(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), newZ));
        widgets.put(newZ, widget);
        zIndexes.put(widget.getId(), newZ);
    }
}
