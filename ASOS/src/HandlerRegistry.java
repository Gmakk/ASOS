import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class HandlerRegistry {
    private int maxNumber;
    private Queue<UpperCaseHandler> handlers;
    private Command command;

    ReentrantLock lock;  // блокировка
    Condition condition;  // условие блокировки

    HandlerRegistry(int maxNumber){
        this.maxNumber = maxNumber;
        lock = new ReentrantLock();
        condition = lock.newCondition();
        handlers = new LinkedList<>();
        command = new Command();
    }

    private class UpperCaseHandler extends Thread {
        //Поле, отвечающее за то, какой символ он изменяет
        private int letterNumber;
        UpperCaseHandler(int letterNumber){
            this.letterNumber = letterNumber;
        }
        public void setLetterNumber(int letterNumber) {
            if(letterNumber > 0)
                this.letterNumber = letterNumber;
            else
                System.out.println("Letter index should be positive");
        }
        public int getLetterNumber() {
            return letterNumber;
        }

        @Override
        public void run() {
            System.out.println("Process "+ letterNumber + " started");


            //boolean isLocked = lock.tryLock();
            //lock.lock();
            try {
//                if(letterNumber == 2)//для проверки
//                    System.out.println(2);
                //если занят, то ждем освобождения
                if (lock.isLocked()) {
                    System.out.println("Process "+ letterNumber + " is waiting");

                    while (lock.isLocked()) {
                        //if(lock.isLocked())
                       try{ condition.await();}
                       catch (IllegalMonitorStateException e){
                           System.out.println("Is locked: " + lock.isLocked());
                       }
                    }
                    System.out.println("Process "+ letterNumber + " has finished waiting");
                }
                //получаем доступ к разделяемому ресурсу
                if(lock.tryLock())
                    System.out.println("Command is captured by " + letterNumber + " process");


                //меняем нужную строку
                String capitalized;
                capitalized = command.getCommand().substring(0, letterNumber - 1) +
                        command.getCommand().substring(letterNumber - 1, letterNumber).toUpperCase() +
                        command.getCommand().substring(letterNumber);
                command.updateCommand(capitalized);
                System.out.println("Process "+ letterNumber + " changed its letter");


                if(IsHandled())
                    System.out.println(HandlerRegistry.this.toString());//вывод по окончанию
                else {
                    swapHandlers();//кладем текущий обработчик в конец очереди
                    startProcess();//запускаем новый поток-обработчик
                }


                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }

//                try {
//                    condition.awaitNanos(1000000000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
            }catch (InterruptedException e){}
            finally {
                //разблокируем общий ресурс
                try {
                    lock.unlock();
                }catch (IllegalMonitorStateException e){}

                //condition.signalAll();
                System.out.println("Command unlocked by " + letterNumber + " process");
            }


            //прерываем выполнявшийся до этого поток
            Thread.currentThread().interrupt();
            if(Thread.currentThread().isInterrupted())
                System.out.println("Process "+ letterNumber + " ended");
        }
    }

    //установка новой строки для изменения
    public void updateCommand(String sharedString){
        //получаем доступ к разделяемому ресурсу
        boolean isLocked = lock.tryLock();
        try {
            //если занят, то ждем освобождения
            if (!isLocked) {
                System.out.println("Waiting to change command");
                while (lock.isLocked())
                    condition.await();
            }

            //меняем команду
            this.command.updateCommand(sharedString);
            maxNumber = sharedString.length();

            Iterator<UpperCaseHandler> iterator = handlers.iterator();
            //удалить лишние обработчики которые меняют несуществующие символы
            while (iterator.hasNext()) {
                if(iterator.next().getLetterNumber() > maxNumber) {
                    iterator.next().interrupt();
                    iterator.remove();
                }
            }

            //сообщаем остальным процессам, для которых вызван condition.await(), что ожидание завершено
            condition.signalAll();
        }catch (InterruptedException e){}
        finally{
            lock.unlock();
        }
    }

    //добавление обработчика
    public void addHandler(int letterNumber){
        if(handlers.size()>=maxNumber){
            System.out.println("Error! The number of handlers will exceed the number of characters in the command");
            return;
        }
        //проверка на то что обработчик меняет существующий символ
        if(maxNumber< letterNumber){
            System.out.println("Error! The processed letter does not exist");
            return;
        }
        UpperCaseHandler newHandler = new UpperCaseHandler(letterNumber);
        handlers.add(newHandler);
    }

    //проверка отработали ли все обработчики
    public boolean IsHandled(){

        for (UpperCaseHandler handler : handlers){
            if(!Character.isUpperCase(command.getCommand().charAt(handler.getLetterNumber()-1)))
                return false;
        }
        return true;
    }

    //run первого в очереди(остальные вызовутся по цепочке)
    public void startProcess(){
        if(command.isNull() || command.getLength() == 0){
            System.out.println("Error! There is no command. \nCreating new one...");
            updateCommand(CommandGenerator.generateNewCommand(maxNumber));
            //return;
        }
        if (handlers.isEmpty()){
            System.out.println("Error! There is no handlers");
            return;
        }
        handlers.peek().start();
    }

    @Override
    public String toString() {
        return "\n\nHandlerRegistry{" +
                "\nMax number: " + maxNumber +
                "\nCommand: " + command.getCommand() +
                "\nIs blocked: " + lock.isLocked() +
                "}\n\n";
    }

    public void swapHandlers(){
        handlers.add(handlers.poll());
    }
}
