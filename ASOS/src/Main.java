/*
Разработка конвейера обработки сообщений, состоящего из подключаемых обработчиков.

Разработать систему, позволяющую обрабатывать команду пользователя \
(в качестве команды должна выступать строка символов),
передавая управление ряду зарегистрированных обработчиков
(обработчики поднимают символ на определенной позиции строки в верхний регистр).
Процессы обработчик при запуске регистрируются в некоем глобальном регистре,
и получают доступ к разделяемой области памяти, которая содержит очередную заявку,
требующую обработки, при наступлении их очереди – они устанавливают блокировку на заявку,
совершают свою часть работы над ней, снимают блокировку и вызывают очередной обработчик.
Команды должны генерироваться автоматически процессом генератором, который так же имеет доступ в разделяемую память.
 */

//обработчики должны обработать все символы

//тест зависимости времени обработки 1000 символов от количества обработчиков
//1 10 100 500 1000 обработчиков

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static CountDownLatch latch = new CountDownLatch(1);//для ожидания завершения обработки и записи временно отметки
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter number of symbols in command: ");
        int symbolsNumber = in.nextInt();
        //int symbolsNumber = 1000;
        System.out.print("Enter number of handlers: ");
        int handlersNumber = in.nextInt();
        //int handlersNumber = 1000;
        try {
            PrintWriter writer = new PrintWriter("Time comparison.txt");

            writer.println("Command:" + symbolsNumber);
            writer.println("Number of handlers:" + handlersNumber);
            long totalTime = 0;
            long time = System.currentTimeMillis();//получаем начальное время
            HandlerRegistry reg = new HandlerRegistry(symbolsNumber);//создаем регистр и указываем длину строки
            for (int j = 0; j < handlersNumber; j++)//создаем N обработчиков
                reg.addHandler(j + 1);
            System.out.println(reg);
            reg.startProcess();
            System.out.println(reg);
            latch.await();//ожидаем сигнала от последнего обработчика
            writer.println("Time:" + (System.currentTimeMillis() - time));//сравниваем с конечным
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("Incorrect file");
        }
        catch (InterruptedException e) {
            System.out.println("Interrupted while waiting");
        }
    }
}