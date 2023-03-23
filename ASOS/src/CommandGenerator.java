import java.util.Random;

public class CommandGenerator {
    CommandGenerator(int length){
        this.length=length;
    }
    int length;
    void generateNewCommand(){
        int leftLimit = 97; // a
        int rightLimit = 122; // z
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();

        System.out.println(generatedString);
    }
}
