package ru.knazarov.miro;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.knazarov.miro.service.WidgetOperationsService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@SpringBootTest
public class ConcurrentTests {


    @Autowired
    WidgetOperationsService widgetOperationsService;

    boolean thread1Finished = false;
    boolean thread2Finished = false;

    @Test
    void concurrentTest()
    {

        List<Integer> correctIndexesList = new ArrayList<>();
        correctIndexesList.add(2);
        correctIndexesList.add(3);
        correctIndexesList.add(4);
        correctIndexesList.add(5);

        int i = 10;
        //try several times to repeat test for getting different threads combinations
        while(i > 0)
        {
            widgetOperationsService.cleanAll();
            callTwoThreads();
            List<Integer> realIndexesList = widgetOperationsService.getWidgets().stream().map(w -> w.getZ()).collect(Collectors.toList());
            assertEquals(correctIndexesList, realIndexesList);

            System.out.println(i + " ok");
            i--;
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    private void callTwoThreads()
    {
        Thread thread1 = new MyThread1("thread 1");
        Thread thread2 = new MyThread2("thread 2");

        thread1.start();
        thread2.start();

        if(!(thread1Finished&&thread2Finished))
        {
            try{
                Thread.sleep(300);
            } catch (InterruptedException e){}
        }
    }

    private class MyThread1 extends Thread {

        public MyThread1(String name)
        {
            super(name);
            thread1Finished = false;
        }

        public void run()
        {
            widgetOperationsService.createWidget(1,1,1,1,3);
            widgetOperationsService.createWidget(2,2,2,2,3);
            thread1Finished = true;
        }
    }

    private class MyThread2 extends Thread {

        public MyThread2(String name)
        {
            super(name);
            thread2Finished = false;
        }

        public void run()
        {
            widgetOperationsService.createWidget(1,1,1,1,2);
            widgetOperationsService.createWidget(2,2,2,2,2);
            thread2Finished = true;
        }
    }
}
