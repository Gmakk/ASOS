import java.nio.file.Path;
import java.nio.file.Paths;

public class FileURI {

    public Path getFileURIFromResources(String fileName) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        return Paths.get(classLoader.getResource(fileName).getPath());
    }

}
