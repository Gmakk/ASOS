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



public class  Main {
    public static void main(String[] args) {
        HandlerRegistry reg = new HandlerRegistry(5);
        for(int i=0;i<5;i++)
            reg.addHandler(i+1);
        System.out.println(reg);
//        for (int i=0;i<5;i++) {
            reg.startProcess();
//            reg.swapHandlers();
//        }
        System.out.println(reg);
    }
}