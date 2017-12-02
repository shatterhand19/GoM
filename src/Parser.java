import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by bozhidar on 30.11.17.
 */
public class Parser {
    public static String parseText(String json) {
        int start = json.indexOf("description: ") + "description ".length() + 2;
        int end = json.indexOf("bounding_poly") - 8;
        String text;
        if (start > 0 && end > 0 && start < json.length() && end < json.length()) {
            return json.substring(start, end).replace("\\n", " ").replace("\'", "'").replace("\\\"", "\"");
        }
        return "";
    }

    public static ArrayList<Keyword> parseKeywords(String json, KeyWordsExtractor extractor) {
        int start = json.indexOf("web_detection {");
        HashSet<String> keywords = new HashSet<>();
        Scanner scanner = new Scanner(json.substring(start + 16));
        boolean next = true;
        try {
            while (next) {
                String web_entity_start = scanner.nextLine();
                String entity_id = scanner.nextLine().trim();
                String score = scanner.nextLine().trim();
                String description = scanner.nextLine().trim();
                String web_entity_end = scanner.nextLine();

                if (Double.parseDouble(score.split(": ")[1]) > 1) {
                    keywords.add(description.split(": ")[1].replace("\"", "").toLowerCase());
                } else next = false;
            }
        } catch (Exception e) {
            System.out.println("Ooooops");
        }

        keywords.addAll(extractor.getKeywords(parseText(json)));
        ArrayList<Keyword> kw = new ArrayList<>();
        for (String word : keywords) {
            kw.add(new Keyword(word, 50));
        }


        return kw;
    }
}
