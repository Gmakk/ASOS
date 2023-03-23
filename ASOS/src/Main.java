import java.io.IOException;
import java.net.URI;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.EnumSet;

public class Main {

    public static void main(String[] args) {
        MappedByteBuffer mappedByteBuffer;
        FileURI test = new FileURI();
        CharBuffer charBuffer = null;
        FileChannel fileChannel;

        //Разбираемся с путем к файлу "подкачки"
        Path pathToFile = new Path() {
            @Override
            public FileSystem getFileSystem() {
                return null;
            }

            @Override
            public boolean isAbsolute() {
                return false;
            }

            @Override
            public Path getRoot() {
                return null;
            }

            @Override
            public Path getFileName() {
                return null;
            }

            @Override
            public Path getParent() {
                return null;
            }

            @Override
            public int getNameCount() {
                return 0;
            }

            @Override
            public Path getName(int index) {
                return null;
            }

            @Override
            public Path subpath(int beginIndex, int endIndex) {
                return null;
            }

            @Override
            public boolean startsWith(Path other) {
                return false;
            }

            @Override
            public boolean endsWith(Path other) {
                return false;
            }

            @Override
            public Path normalize() {
                return null;
            }

            @Override
            public Path resolve(Path other) {
                return null;
            }

            @Override
            public Path relativize(Path other) {
                return null;
            }

            @Override
            public URI toUri() {
                return null;
            }

            @Override
            public Path toAbsolutePath() {
                return null;
            }

            @Override
            public Path toRealPath(LinkOption... options) throws IOException {
                return null;
            }

            @Override
            public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
                return null;
            }

            @Override
            public int compareTo(Path other) {
                return 0;
            }
        };
        try {
            pathToFile = test.getFileURIFromResources("fileToRead.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }


        //ЧТЕНИЕ
        try {
            fileChannel = (FileChannel) Files.newByteChannel(pathToFile, EnumSet.of(StandardOpenOption.READ));
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());

            if (mappedByteBuffer != null) {
                charBuffer = Charset.forName("UTF-8").decode(mappedByteBuffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //информация из файла в charBuffer, по идее ее нужно менять обработчиками
        //mappedByteBuffer...



        //ЗАПИСЬ
        charBuffer.wrap("This will be written to the file");
        try {
            fileChannel = (FileChannel) Files.newByteChannel(pathToFile, EnumSet.of(
                    StandardOpenOption.READ,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING));
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, charBuffer.length());
            if (mappedByteBuffer != null) {
                mappedByteBuffer.put(Charset.forName("utf-8").encode(charBuffer));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}