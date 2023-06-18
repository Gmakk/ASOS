import java.util.Iterator;
import java.util.LinkedList;
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

    public class UpperCaseHandler extends Thread {
        //Поле, отвечающее за то, какой символ он изменяет
        private int index;
        UpperCaseHandler(int index){
            this.index = index;
        }
        public void setIndex(int index) {
            if(index > 0)
                this.index = index;
            else
                System.out.println("Index should be positive");
        }
        public int getIndex() {
            return index;
        }

        @Override
        public void run() {
            System.out.println("Process "+ index + " started");

            try {
                //если занят, то ждем освобождения
                if (lock.isLocked()) {
                    System.out.println("Process "+ index + " is waiting");

                    while (lock.isLocked()) {
                        //if(lock.isLocked())
                       try{ condition.await();}
                       catch (IllegalMonitorStateException e){
                           System.out.println("Is locked: " + lock.isLocked());
                       }
                    }
                    System.out.println("Process "+ index + " has finished waiting");
                }
                //получаем доступ к разделяемому ресурсу
                if(lock.tryLock())
                    System.out.println("Command is captured by " + index + " process");



                int next= findNextUnhandled();
                if(next != -1) {
                    //меняем нужную строку
                    String capitalized;
                    capitalized = command.getCommand().substring(0, next - 1) +
                            command.getCommand().substring(next - 1, next).toUpperCase() +
                            command.getCommand().substring(next);
                    command.updateCommand(capitalized);
                    System.out.println("Process " + index + " changed its letter");

                }
                if(isHandled()) {
                    System.out.println("Command is handled");
                    System.out.println(HandlerRegistry.this.toString());//вывод по окончанию
                    Main.latch.countDown();
                }else{
                    swapHandlers();//кладем текущий обработчик в конец очереди
                    startProcess();//запускаем новый поток-обработчик
                }

                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }

            }catch (InterruptedException e){}
            finally {
                //разблокируем общий ресурс
                try {
                    lock.unlock();
                }catch (IllegalMonitorStateException e){}

                System.out.println("Command unlocked by " + index + " process");
            }

            //прерываем выполнявшийся до этого поток
            Thread.currentThread().interrupt();
            if(Thread.currentThread().isInterrupted())
                System.out.println("Process "+ index + " ended");
        }
    }

    //установка новой строки для изменения
    public void updateCommand(String sharedString){
        //получаем доступ к разделяемому ресурсу

        try {
            //если занят, то ждем освобождения
            if (lock.isLocked()) {
                System.out.println("Waiting to change command");
                while (lock.isLocked())
                    condition.await();
            }
            lock.tryLock();
            //меняем команду
            this.command.updateCommand(sharedString);
            maxNumber = sharedString.length();


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
    public boolean isHandled(){
        if(command.getCommand().equals(command.getCommand().toUpperCase()))
            return true;
        else return false;
    }

    public int findNextUnhandled(){
        for(int i = 0;i<command.getLength();i++){
            if(Character.isLowerCase(command.getCommand().charAt(i)))
                return i+1;
        }
        return -1;
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
        handlers.add(new UpperCaseHandler(handlers.poll().index));
    }
}
