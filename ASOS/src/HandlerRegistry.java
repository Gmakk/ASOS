import java.util.ArrayList;
public class HandlerRegistry {

    private class UpperCaseHandler extends Thread {
        //Поле, овтечающее за то, какой символ он изменяет
        @Override
        public void run() {



            //установить блокировку



            //изменить один символ
            System.out.println(HandlerRegistry.this.sharedString.length());
        }
    }

    private static int maxNumber;
    //private static int currentNumber;
    private ArrayList<UpperCaseHandler> handlers;
    private volatile String sharedString;

    public void updateCommand(String sharedString){
        this.sharedString = sharedString;
        maxNumber = sharedString.length();


        //удалить лишние обработчики
    }
    void addHandler(){
        if(handlers.size()>=maxNumber){
            System.out.println("Error! the number of handlers will exceed the number of characters in the command");
            return;
        }
        UpperCaseHandler newHandler = new UpperCaseHandler();
        handlers.add(newHandler);
        //запускать его и делать join?
        return;
    }
}
