import java.util.Iterator;
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
    }

    private class UpperCaseHandler extends Thread {
        //Поле, овтечающее за то, какой символ он изменяет
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
            //получем доступ к разделяемому ресурсу
            boolean isLocked = lock.tryLock();
            try {
                //если занят, то ждем освобождения
                if (!isLocked) {
                    while (lock.isLocked())
                        condition.await();
                }

                //меняем регистр буквы
                String capitalized = command.getCommand().substring(0, letterNumber - 2) +   //проверить substring когла конец меньше начала
                        command.getCommand().substring(letterNumber - 1, 1).toUpperCase() +
                            command.getCommand().substring(letterNumber);
                                command.updateCommand(capitalized);

                //проверяем все ли отработали, если да то updateCommand
                if(IsHandled())
                    updateCommand(CommandGenerator.generateNewCommand(maxNumber));    //какой длины генерить строку

                //сообщаем остальным процессам, для которых вызван condition.await(), что ожидание завершено
                condition.signalAll();
            }catch (InterruptedException e){}
            finally{
                lock.unlock();
            }

            //кладем текущий обработчик в конец очереди
            handlers.add(handlers.poll());
            //запускаем новый поток-обработчик
            startProcess();
            //прерываем выполнявшийс до этого поток
            Thread.currentThread().interrupt();
        }
    }

    //установка новой строки для изменения
    public void updateCommand(String sharedString){


        //что делать с блокировкой, если не runnable?
        // (вроде нормально просто блокировать для потоков объект)



        //получем доступ к разделяемому ресурсу
        boolean isLocked = lock.tryLock();
        try {
            //если занят, то ждем освобождения
            if (!isLocked) {
                while (lock.isLocked())
                    condition.await();
            }

            //меняем команду
            this.command.updateCommand(sharedString);
            maxNumber = sharedString.length();

            Iterator<UpperCaseHandler> iterator = handlers.iterator();
            //удалить лишние обработчики которые меняют несуществующие символы
            while (iterator.hasNext()) {


                //проверить не пролистывается ли несколько раз через next


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
        //проверка на то что обработчик меняет существующйи символ
        if(maxNumber< letterNumber){
            System.out.println("Error! The processed letter does not exist");
            return;
        }
        UpperCaseHandler newHandler = new UpperCaseHandler(letterNumber);
        handlers.add(newHandler);


        //запускать его и делать join?



    }

    //проверка отработали ли все обработчики
    public boolean IsHandled(){

        for (UpperCaseHandler handler : handlers){
            if(!Character.isUpperCase(command.getCommand().charAt(handler.getLetterNumber())))
                return false;
        }
        return true;
    }

    //run первого в очереди(остальыне вызовутся по цепочке)
    public void startProcess(){
        if (handlers.isEmpty()){
            System.out.println("Error! There is no handlers");
            return;
        }
        handlers.peek().start();
    }

//    public void setNextHandler(){
//        if(checkIsHandled() == true){
//            System.out.println("Последний обработчик завершил работу");
//            //вызвать CommandGenerator??? и сделать что-то с обработчиками?
//        }else{
//            ////
//        }
//    }
}
