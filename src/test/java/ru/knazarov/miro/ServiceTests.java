package ru.knazarov.miro;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.knazarov.miro.exception.WidgetNotFoundException;
import ru.knazarov.miro.model.Widget;
import ru.knazarov.miro.service.WidgetOperationsService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@SpringBootTest
class ServiceTests {

	@Autowired
	WidgetOperationsService widgetOperationsService;


	@Test
	void createFirstWidgetWithoutZIndex()
	{

		Widget widget = widgetOperationsService.createWidget(1,1,10,15);
		assertEquals(0, widget.getZ());

		widgetOperationsService.cleanAll();
	}

	@Test
	void createTwoWidgetsWithoutZIndex()
	{

		Widget widget1 = widgetOperationsService.createWidget(1,1,10,15);
		Widget widget2 = widgetOperationsService.createWidget(2,1,20,15);
		assertEquals(0, widget1.getZ());
		assertEquals(1, widget2.getZ());

		widgetOperationsService.cleanAll();
	}

	@Test
	void createFirstWithZSecondWithoutZ()
	{

		Widget widget1 = widgetOperationsService.createWidget(1,1,10,15, 7);
		Widget widget2 = widgetOperationsService.createWidget(2,1,20,15);
		assertEquals(7, widget1.getZ());
		assertEquals(8, widget2.getZ());

		widgetOperationsService.cleanAll();
	}

	@Test
	void createFirstWidgetWithZIndex()
	{

		Widget widget = widgetOperationsService.createWidget(1,1,10,15, 3);
		assertEquals(3, widget.getZ());

		widgetOperationsService.cleanAll();
	}

	@Test
	void createTwoWidgetsWithShift()
	{
		Widget widget1 = widgetOperationsService.createWidget(1,1,10,15, 3);
		Widget widget2 = widgetOperationsService.createWidget(2,2,20,15, 3);

		assertEquals(4, widget1.getZ());
		assertEquals(3, widget2.getZ());

		widgetOperationsService.cleanAll();
	}

	@Test
	void createThreeWidgetsWithDoubleShift()
	{
		Widget widget1 = widgetOperationsService.createWidget(1,1,10,15, 3);
		Widget widget2 = widgetOperationsService.createWidget(2,2,20,15, 3);
		Widget widget3 = widgetOperationsService.createWidget(3,3,30,15, 3);

		assertEquals(4, widget2.getZ());
		assertEquals(5, widget1.getZ());
		assertEquals(3, widget3.getZ());

		widgetOperationsService.cleanAll();
	}

	@Test
	void createThreeWidgetsWithOneShift()
	{
		Widget widget1 = widgetOperationsService.createWidget(1,1,10,15, 3);
		Widget widget2 = widgetOperationsService.createWidget(2,2,20,15, 5);
		Widget widget3 = widgetOperationsService.createWidget(3,3,30,15, 3);

		assertEquals(5, widget2.getZ());
		assertEquals(4, widget1.getZ());
		assertEquals(3, widget3.getZ());

		widgetOperationsService.cleanAll();
	}

	@Test
	void updateWidget() throws WidgetNotFoundException
	{
		Widget widget = widgetOperationsService.createWidget(1,1,10,15, 3);
		Widget newWidget = Widget.getInstance(2,2,20,30,3);

		Widget updatedWidget = widgetOperationsService.updateWidget(widget.getId(), newWidget);

		assertEquals(30, updatedWidget.getHeight());

		widgetOperationsService.cleanAll();
	}

	@Test
	void updateWidgetWithShift() throws WidgetNotFoundException
	{
		Widget widget1 = widgetOperationsService.createWidget(1,1,10,15, 3);
		Widget widget2 = widgetOperationsService.createWidget(2,5,15,35, 4);
		Widget widget1Updated = Widget.getInstance(2,2, 10, 15, 4);

		widgetOperationsService.updateWidget(widget1.getId(), widget1Updated);

		assertEquals(4, widget1.getZ());
		assertEquals(5, widget2.getZ());

		widgetOperationsService.cleanAll();
	}

	@Test
	void updateWidgetWithShift2() throws WidgetNotFoundException
	{
		Widget widget1 = widgetOperationsService.createWidget(1,1,10,15, 3);
		Widget widget2 = widgetOperationsService.createWidget(2,5,15,35, 4);
		Widget widget3 = widgetOperationsService.createWidget(2,5,15,35, 5);
		Widget widget1Updated = Widget.getInstance(2,2, 10, 15, 4);

		widgetOperationsService.updateWidget(widget1.getId(), widget1Updated);

		assertEquals(4, widget1.getZ());
		assertEquals(5, widget2.getZ());
		assertEquals(6, widget3.getZ());

		widgetOperationsService.cleanAll();
	}


	@Test
	void testSortedListOfWidgets()
	{
		List<String> sortedIds = new ArrayList<>();
		Widget w1 = widgetOperationsService.createWidget(1,1,10,10, 1);//1
		Widget w2 = widgetOperationsService.createWidget(1,1,10,10, 2);//3
		Widget w3 = widgetOperationsService.createWidget(1,1,10,10, 5);//5
		Widget w4 = widgetOperationsService.createWidget(1,1,10,10, 8);//6
		Widget w5 = widgetOperationsService.createWidget(1,1,10,10, 2);//2
		Widget w6 = widgetOperationsService.createWidget(1,1,10,10, 4);//4

		sortedIds.add(w1.getId());
		sortedIds.add(w5.getId());
		sortedIds.add(w2.getId());
		sortedIds.add(w6.getId());
		sortedIds.add(w3.getId());
		sortedIds.add(w4.getId());

		List<String> listWidgetIds = widgetOperationsService.getWidgets().stream().map(w -> w.getId()).collect(Collectors.toList());

		System.out.println(sortedIds);
		System.out.println(listWidgetIds);

		assertEquals(sortedIds, listWidgetIds);

		widgetOperationsService.cleanAll();
	}

	@Test
	void testDeleteUnexistingWidget()
	{
		assertThrows(WidgetNotFoundException.class, () -> {
			String id = "12345";
			widgetOperationsService.deleteWidget(id);
		});
	}

	@Test
	void testDeleteWidget() throws WidgetNotFoundException
	{
		Widget widget1 = widgetOperationsService.createWidget(1,1,1,1,1);
		Widget widget2 = widgetOperationsService.createWidget(1,1,1,1,2);
		Widget widget3 = widgetOperationsService.createWidget(1,1,1,1,3);

		List<String> listToBe = new ArrayList<>();
		listToBe.add(widget1.getId());
		listToBe.add(widget3.getId());

		widgetOperationsService.deleteWidget(widget2.getId());

		List<String> listWidgetIds = widgetOperationsService.getWidgets().stream().map(w -> w.getId()).collect(Collectors.toList());
		assertEquals(listWidgetIds, listToBe);

		widgetOperationsService.cleanAll();
	}

	@Test
	void testUpdateDeletedWidget()
	{
		Widget widget1 = widgetOperationsService.createWidget(1,1,1,1,1);
		String id = widget1.getId();
		try {
			widgetOperationsService.deleteWidget(id);
		} catch (WidgetNotFoundException e) {}

		Widget widget1Updated = Widget.getInstance(2,2,1,1,1);

		assertThrows(WidgetNotFoundException.class, () -> {widgetOperationsService.updateWidget(id, widget1Updated);});

		widgetOperationsService.cleanAll();
	}

}

