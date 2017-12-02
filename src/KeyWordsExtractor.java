import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Created by bozhidar on 01.12.17.
 */
public class KeyWordsExtractor {
    ArrayList<String> words = new ArrayList<>();

    public KeyWordsExtractor(String dicPath) throws FileNotFoundException {
        File dic = new File(dicPath);
        Scanner scanner = new Scanner(dic);
        while (scanner.hasNextLine()) {
            words.add(scanner.nextLine().toLowerCase());
        }
    }

    public ArrayList<String> getKeywords(String sentence) {
        String[] sentenceWords = sentence.split("\\W+");
        ArrayList<String> foundKeywords = new ArrayList<>();
        for (int i = 0; i < sentenceWords.length; i++) {
            String word = sentenceWords[i].toLowerCase();
            if (Collections.binarySearch(words, word) > 0) {
                foundKeywords.add(word);
            }
        }
        Collections.sort(foundKeywords);
        return foundKeywords;
    }
}
