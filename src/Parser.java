import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by bozhidar on 30.11.17.
 */
public class Parser {
    public static String parseText(String json) {
        int start = json.indexOf("description: ") + "description ".length() + 2;
        int end = json.indexOf("bounding_poly") - 8;
        return json.substring(start, end).replace("\\n", " ").replace("\\'", "'");
    }

    public static String[] parseKeywords(String json, KeyWordsExtractor extractor) {
        int start = json.indexOf("web_detection {");
        ArrayList<String> keywords = new ArrayList<>();
        Scanner scanner = new Scanner(json.substring(start + 16));
        boolean next = true;
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

        keywords.addAll(extractor.getKeywords(parseText(json)));


        return keywords.toArray(new String[keywords.size()]);
    }
}
