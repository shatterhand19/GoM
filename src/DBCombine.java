import java.util.ArrayList;
import java.util.StringTokenizer;

public class DBCombine {
    public static void main(String[] args) {
        String db1 = args[0], db2 = args[1], db3 = args[2], db4 = args[3];
        combineImageDB(db1, db2);
        combineKeywordDB(db3, db4);
    }

    public static void combineImageDB(String db1, String db2) {
        ImageDatabase img_db1 = new ImageDatabase(db1), img_db2 = new ImageDatabase(db2);
        for (String image : img_db2.getImages()) {
            img_db1.addImage(image, img_db2.getText(image), img_db2.getKeywordsObj(image));
        }
        img_db1.writeToFile(db1);
    }

    public static void combineKeywordDB(String db1, String db2) {
        KeywordDatabase kw_db1 = new KeywordDatabase(db1), kw_db2 = new KeywordDatabase(db2);
        for (String keyword : kw_db2.word_image.keySet()) {
            ArrayList<String> images = kw_db2.getImages(keyword);
            for (String image : images) {
                kw_db1.addImage(keyword, image);
            }
        }
        kw_db1.writeToFile(db1);
    }
}
