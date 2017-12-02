import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by bozhidar on 01.12.17.
 */
public class ImageDatabase {
    HashMap<String, String> image_text = new HashMap<>();
    HashMap<String, ArrayList<Keyword>> image_keywords = new HashMap<>();
    String path;


    public ImageDatabase(String dbPath) {
        path = dbPath;
        try (Scanner scanner = new Scanner(new File(dbPath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split("-----");
                String image = split[0];
                String text = split[1];
                String[] keywords = split[2].replace("[", "").replace("]", "").split(", ");
                ArrayList<Keyword> kw = new ArrayList<>();
                System.out.println(image);
                for (int i = 0; i < keywords.length; i++) {
                    if (keywords[i].split(":").length == 2) {
                        kw.add(new Keyword(keywords[i]));
                    }
                }
                image_text.put(image, text);
                image_keywords.put(image, kw);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File for the Image database was not found!");
        }
        System.out.println(image_text);
        System.out.println(image_keywords);
    }

    public ImageDatabase() {

    }

    public void addImage(String imageURL, String text, ArrayList<Keyword> keywords) {
        if (keywords.size() > 0) {
            image_text.put(imageURL, text);
            image_keywords.put(imageURL, keywords);
        }
    }

    public String getText(String imageURL) {
        return image_text.getOrDefault(imageURL, null);
    }

    public ArrayList<String> getImages() {
        return new ArrayList<>(image_text.keySet());
    }

    public ArrayList<String> getKeywords(String imageURL) {
        ArrayList<String> keywords = new ArrayList<>();
        if (image_keywords.containsKey(imageURL)) {
            for (Keyword kw : image_keywords.get(imageURL)) {
                keywords.add(kw.word);
            }
        }
        return keywords;
    }

    public Keyword getKeyword(String imageURL, String word) {
        ArrayList<Keyword> keywords = image_keywords.get(imageURL);
        for (Keyword k : keywords) {
            if (word.equals(k.word)) return k;
        }
        return null;
    }

    public ArrayList<Integer> getKeywordCounts(String imageURL) {
        ArrayList<Integer> ratings = null;
        if (image_keywords.containsKey(imageURL)) {
            ratings = new ArrayList<>();
            for (Keyword kw : image_keywords.get(imageURL)) {
                ratings.add(kw.rating);
            }
        }
        return ratings;
    }

    public boolean voteKeyword(String imageURL, String keyword, String vote) {
        if (imageURL.startsWith("/")) imageURL = imageURL.substring(1);
        if (image_keywords.containsKey(imageURL)) {
            if (getKeyword(imageURL, keyword) != null) {
                for (Keyword k : image_keywords.get(imageURL)) {
                    if (k.word.equals(keyword)) {
                        if (vote.equals("up")) k.rating += 1;
                        if (vote.equals("down")) k.rating -= 1;
                        if (k.rating <= 0) image_keywords.get(imageURL).remove(k);
                        this.writeToFile(path);
                        return false;
                    }
                }
            } else {
                image_keywords.get(imageURL).add(new Keyword(keyword, 1));
                this.writeToFile(path);
                return true;
            }
        }
        return false;
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
