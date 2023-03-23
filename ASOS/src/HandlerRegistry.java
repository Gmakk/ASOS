import java.util.ArrayList;
public class HandlerRegistry {
    //меализовать монитор здесь?
    //очереди к каждой переменной мониора, если обработчиков больше чем букв в команде
    private static final int MAX_NUMBER = 10;
    private static int currentNumber;
    //массив тредов-обработчиков?
    ArrayList<Thread> Threads;
    Thread createThread(){
        //Threads.add()
        //возвращать не это а созданный обработчик UpperCaseHandler
        return Thread.currentThread();
    }
}
