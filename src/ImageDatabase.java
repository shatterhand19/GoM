import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by bozhidar on 01.12.17.
 */
public class ImageDatabase {
    HashMap<String, String> image_text = new HashMap<>();
    HashMap<String, ArrayList<String>> image_keywords = new HashMap<>();

    public ImageDatabase(String dbPath) {
        try (Scanner scanner = new Scanner(new File(dbPath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split("-----");
                String image = split[0];
                String text = split[1];
                String[] keywords = split[2].replace("[", "").replace("]", "").split(", ");
                image_text.put(image, text);
                image_keywords.put(image, new ArrayList<>(Arrays.asList(keywords)));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File for the Image database was not found!");
        }
        System.out.println(image_text);
        System.out.println(image_keywords);
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

    public void writeToFile(String dbPath) {
        ArrayList<String> images = new ArrayList<>(image_text.keySet());
        Collections.sort(images);
        try (FileWriter writer = new FileWriter(dbPath)) {
            for (String key : images) {
                writer.write(key + "-----" + image_text.get(key) + "-----" + image_keywords.get(key).toString() + "\n");
            }
        } catch (Exception e) {
            System.out.println("Couldn't write to database");
        }
    }
}
