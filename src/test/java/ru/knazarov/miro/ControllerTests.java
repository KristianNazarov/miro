package ru.knazarov.miro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.knazarov.miro.controller.APIController;
import ru.knazarov.miro.model.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ControllerTests {

    @Autowired
    APIController apiController;

    @Test
    void createOneWidgetAndGetList()
    {
        Widget widget = Widget.getInstance(1,1,1,1,1);
        Widget createdWidget = extractWidget(apiController.add(widget));
        assertEquals(widget.getZ(), createdWidget.getZ());

        apiController.delete(createdWidget.getId());
    }

    @Test
    void createTwoWidgetsAndTestList()
    {
        Widget widget1 = Widget.getInstance(1,1,1,1,1);
        Widget widget2 = Widget.getInstance(2,2,1,1,3);

        apiController.add(widget1);
        apiController.add(widget2);

        List<Widget> widgetList = extractList(apiController.all());
        assertEquals(2, widgetList.size());

        apiController.delete(widgetList.get(0).getId());
        apiController.delete(widgetList.get(1).getId());
    }

    @Test
    void createAndDeleteWidget()
    {
        Widget widget = Widget.getInstance(1,1,1,1,1);
        Widget createdWidget = extractWidget(apiController.add(widget));

        ResponseEntity<?> deleteResponse = apiController.delete(createdWidget.getId());
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        List<Widget> widgetList = extractList(apiController.all());
        assertEquals(0, widgetList.size());
    }

    @Test
    void createAndUpdateWidget()
    {
        Widget widget = Widget.getInstance(1,1,1,1,1);
        Widget createdWidget = extractWidget(apiController.add(widget));

        Widget widgetWithUpdate = Widget.getInstance(1,1,20,20,1);
        ResponseEntity<?> responseEntity = apiController.update(widgetWithUpdate, createdWidget.getId());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        Widget updatedWidget = extractWidget(responseEntity);
        assertEquals(20, updatedWidget.getWidth());

        apiController.delete(updatedWidget.getId());
    }

    @Test
    void checkPagination()
    {
        List<String> widgetIds = new ArrayList<>();

        for(int i=0 ; i < 9; i++)
            widgetIds.add(extractWidget(apiController.add(Widget.getInstance(1,1,1,1,1))).getId());

        List<Widget> widgetList = extractList(apiController.page(2, 3));
        assertEquals(3, widgetList.size());
        assertEquals(4, widgetList.get(0).getZ());


        for(String id: widgetIds)
            apiController.delete(id);

        System.out.println("ok");
    }


//----------------------------------------------------------------------------------------------------------------------
    private Widget extractWidget(ResponseEntity<?> responseEntity)
    {
        Object body = responseEntity.getBody();
        assertEquals(EntityModel.class, body.getClass());
        EntityModel<?> entityModel = (EntityModel<?>) body;
        assertEquals(Widget.class, entityModel.getContent().getClass());
        Widget widget = (Widget) entityModel.getContent();

        return widget;
    }

    private List<Widget> extractList(CollectionModel<EntityModel<Widget>> widgetsCollection)
    {
        List<Widget> widgetList = widgetsCollection.getContent().stream().map(widgetEntityModel -> widgetEntityModel.getContent()).collect(Collectors.toList());

        return widgetList;
    }

}
