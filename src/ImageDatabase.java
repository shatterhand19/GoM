import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bozhidar on 01.12.17.
 */
public class ImageDatabase {
    HashMap<String, String> image_text = new HashMap<>();
    HashMap<String, ArrayList<String>> image_keywords = new HashMap<>();

    public ImageDatabase(String dbPath) {
        //TODO: make reader
    }

    public ImageDatabase() {

    }

    public void addImage(String imageURL, String text, ArrayList<String> keywords) {
        image_text.put(imageURL, text);
        image_keywords.put(imageURL, keywords);
    }

    public String getText(String imageURL) {
        return image_text.getOrDefault(imageURL, null);
    }

    public ArrayList<String> getKeywords(String imageURL) {
        return image_keywords.getOrDefault(imageURL, null);
    }
}
