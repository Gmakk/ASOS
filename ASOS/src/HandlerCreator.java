import java.util.ArrayList;

public class HandlerCreator {
    private static final int MAX_NUMBER = 10;
    private static int currentNumber;
    //массив тредов-обработчиков?
    ArrayList<Thread> Threads;
    Thread createThread(){
        //Threads.add()
        //возвращать не это а созданный поток
        return Thread.currentThread();
    }
}
