import java.util.*;

public class SearchAlgorithm {
    public static String search(String text, ImageDatabase image_db, KeywordDatabase keyw_db, KeyWordsExtractor extractor) {
        ArrayList<String> keywords = extractor.getKeywords(text);
        System.out.println("Keywords from user: " + keywords);
        HashMap<String, Integer> images_score = new HashMap<>();
        for (String keyword : keywords) {
            ArrayList<String> images = keyw_db.getImages(keyword);
            if (images != null) {
                for (String image : images) {
                    if (image_db.getKeyword(image, keyword).rating > 0) {
                        if (images_score.containsKey(image)) {
                            images_score.put(image, images_score.get(image) + image_db.getKeyword(image, keyword).rating);
                        } else images_score.put(image, image_db.getKeyword(image, keyword).rating);
                    }
                }
            }
        }
        for (String image : image_db.getImages()) {
            String matched = LongestSubstringMatcher.getLongestSubstring(text.toLowerCase(), image_db.getText(image).toLowerCase());
            int score = matched.toCharArray().length;
            score += (matched.split("\\W+").length - 1) * 100;
            if (images_score.containsKey(image)) {
                images_score.put(image, images_score.get(image) + score);
            } else images_score.put(image, score);
        }
        return generateJson(images_score, image_db);
    }

    private static String generateJson(HashMap<String, Integer> image_score, ImageDatabase image_db) {
        ArrayList<String> images = new ArrayList(image_score.keySet());
        images.sort(Comparator.comparingInt(image_score::get).reversed());
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < (images.size() > 10 ? 10 : images.size()); i++) {
            StringBuilder object = new StringBuilder("{");
            object.append("\"path\": \"" + images.get(i) + "\",");
            object.append("\"keywords\": " + image_db.getKeywords(images.get(i)).toString().replace(", ", "\", \"").replace("[", "[\"").replace("]", "\"]"));
            object.append("},");
            json.append(object);
        }

        if (json.charAt(json.length() - 1) == ',') {
            json.deleteCharAt(json.length() - 1);
        }
        json.append("]");
        return json.toString();
    }
}
