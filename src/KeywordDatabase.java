import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by bozhidar on 01.12.17.
 */
public class KeywordDatabase {
    HashMap<String, ArrayList<String>> word_image = new HashMap<>();

    public KeywordDatabase() {

    }

    public KeywordDatabase(String dbPath) {
        try (Scanner scanner = new Scanner(new File(dbPath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split("-----");
                String keyword = split[0];
                String[] images = split[1].replace("[", "").replace("]", "").split(", ");
                word_image.put(keyword, new ArrayList<>(Arrays.asList(images)));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File for the Image database was not found!");
        }
        System.out.println(word_image);
    }

    public void addImage(String keyword, String image) {
        word_image.putIfAbsent(keyword, new ArrayList<>());
        word_image.get(keyword).add(image);
    }

    public ArrayList<String> getImages(String keyword) {
        return word_image.getOrDefault(keyword, null);
    }

    public void writeToFile(String dbPath) {
        ArrayList<String> keywords = new ArrayList<>(word_image.keySet());
        Collections.sort(keywords);
        try (FileWriter writer = new FileWriter(dbPath)) {
            for (String key : keywords) {
                writer.write(key + "-----" + word_image.get(key).toString() + "\n");
            }
        } catch (Exception e) {
            System.out.println("Couldn't write to database");
        }
    }
}
