import java.util.ArrayList;
import java.util.Queue;

public class HandlerRegistry {

    private class UpperCaseHandler extends Thread {
        //Поле, овтечающее за то, какой символ он изменяет
        @Override
        public void run() {



            //установить блокировку если не установлена, ждать если устрановлена



            //изменить один символ
            System.out.println(HandlerRegistry.this.command.getCommand());

            //вызвать процесс смены доставания нового процесса из очрееди и засовывания текщего в конец
        }
    }

    private static int maxNumber;
    private Queue<UpperCaseHandler> handlers;
    private Command command;

    public void updateCommand(String sharedString){
        this.command.updateCommand(sharedString);
        maxNumber = sharedString.length();


        //удалить лишние обработчики
    }
    public void addHandler(){
        if(handlers.size()>=maxNumber){
            System.out.println("Error! the number of handlers will exceed the number of characters in the command");
            return;
        }
        UpperCaseHandler newHandler = new UpperCaseHandler();
        handlers.add(newHandler);
        //запускать его и делать join?
        return;
    }

    public boolean checkIsHandled(){
        //пройтись по очереди обработчиков и посмотреть подняты ли все регистры
        return true;
    }

    public void setNextHandler(){
        if(checkIsHandled() == true){
            System.out.println("Последний обработчик завершил работу");
            //вызвать CommandGenerator??? и сделать что-то с обработчиками?
        }else{
            ////
        }
    }
}
