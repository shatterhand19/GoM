/**
 * Created by bozhidar on 30.11.17.
 */


import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Labeler {
    private static String[] dirs;
    private static File image_db = new File("db/image_db");
    private static File keyw_db = new File("db/keyw_db");
    private static ImageDatabase image_db_obj = new ImageDatabase();
    private static KeywordDatabase keyw_db_obj = new KeywordDatabase();
    public static void main(String... args) throws Exception {
        KeyWordsExtractor extractor = new KeyWordsExtractor("dics/words.txt");

        dirs = args;
        // Instantiates a client
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

            for (String dir : dirs) {
                if (Files.isDirectory(Paths.get(dir))) {
                    System.out.println("Processing images in " + dir + "\n");
                    File directory = new File(dir);
                    File[] filenames = directory.listFiles();
                    for (File image : filenames) {
                        processImage(vision, extractor, image.getPath());
                    }
                }
            }
        }
    }

    private static void processImage(ImageAnnotatorClient vision, KeyWordsExtractor extractor, String filePath) throws IOException {
        System.out.println("Processing image " + filePath + "\n");
        // Reads the image file into memory
        Path path = Paths.get(filePath);
        byte[] data = Files.readAllBytes(path);
        ByteString imgBytes = ByteString.copyFrom(data);

        // Builds the image annotation request
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature textDet = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
        Feature labDet = Feature.newBuilder().setType(Type.WEB_DETECTION).build();
        ArrayList<Feature> features = new ArrayList<>();
        features.add(textDet);
        features.add(labDet);
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addAllFeatures(features)
                .setImage(img)
                .build();
        requests.add(request);



        // Performs label detection on the image file
        BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
        //System.out.println(response);
        String text = Parser.parseText(response.toString());
        if (text.length() == 0) {
            Files.deleteIfExists(Paths.get(filePath));
        } else {
            ArrayList<Keyword> keywords = Parser.parseKeywords(response.toString(), extractor);
            image_db_obj.addImage(filePath, text, keywords);
            for (String word : image_db_obj.getKeywords(filePath)) {
                keyw_db_obj.addImage(word, filePath);
            }
            image_db_obj.writeToFile(image_db.getPath());
            keyw_db_obj.writeToFile(keyw_db.getPath());
            System.out.println("Text: " + text);
            System.out.println("Keywords: " + keywords.toString());

            System.out.println("\n--------------------------------\n");
        }
    }

}