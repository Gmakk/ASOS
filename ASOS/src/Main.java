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


import java.io.FileNotFoundException;
import java.io.PrintWriter;
public class  Main {
    public static void main(String[] args){
        try {
            PrintWriter writer = new PrintWriter("Time comparison.txt");

            //КОГДА ОБРАБОТЧИКОВ МЕНЬШЕ ЧЕМ ДЛИНА КОМАНДЫ

            long time1 = System.currentTimeMillis();//получаем начальное время
            HandlerRegistry reg1 = new HandlerRegistry(1000);//создаем регистр и указываем длину строки
            for (int i = 0; i < 100; i++)//создаем N обработчиков
                reg1.addHandler(i + 1);
            System.out.println(reg1);
            reg1.startProcess();
            System.out.println(reg1);
            writer.println("Time with lack of handlers:" + (System.currentTimeMillis() - time1));//сравниваем с конечным

            //КОГДА ОБРАБОТЧКОВ ХВАТАЕТ НА КОМАНДУ

            long time2 = System.currentTimeMillis();//получаем начальное время
            HandlerRegistry reg2 = new HandlerRegistry(1000);//создаем регистр и указываем длину строки
            for (int i = 0; i < 1000; i++)//создаем N обработчиков
                reg2.addHandler(i + 1);
            System.out.println(reg2);
            reg2.startProcess();
            System.out.println(reg2);
            writer.println("Regular time:" + (System.currentTimeMillis() - time2));//сравниваем с конечным

            writer.close();
        }catch (FileNotFoundException e){
            System.out.println("Incorrect file");
        }
    }
}