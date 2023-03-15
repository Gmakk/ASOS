import java.util.Random;

public class CommandGenerator {
    CommandGenerator(String str){
        place=str;
    }
    String place;
    void putNewCommand(){
        int leftLimit = 97; // a
        int rightLimit = 122; // z
        int TotalLength = 100;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(TotalLength);
        for (int i = 0; i < TotalLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();

        System.out.println(generatedString);
    }
}
