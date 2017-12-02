import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bozhidar on 01.12.17.
 */
public class KeywordDatabase {
    HashMap<String, ArrayList<String>> word_image = new HashMap<>();

    public KeywordDatabase() {

    }

    public KeywordDatabase(String dbPath) {
        //TODO: implement
    }

    public void addImage(String keyword, String image) {
        word_image.putIfAbsent(keyword, new ArrayList<>());
        word_image.get(keyword).add(image);
    }

    public ArrayList<String> getImages(String keyword) {
        return word_image.getOrDefault(keyword, null);
    }
}
