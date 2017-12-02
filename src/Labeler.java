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
    private static FileWriter image_db_writer;
    private static File keyw_db = new File("db/keyw_db");
    private static FileWriter keyw_db_writer;
    public static void main(String... args) throws Exception {
        KeyWordsExtractor extractor = new KeyWordsExtractor("dics/words.txt");
        image_db_writer = new FileWriter(image_db);
        keyw_db_writer = new FileWriter(keyw_db);
        dirs = args;
        // Instantiates a client
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

            for (String dir : dirs) {
                if (Files.isDirectory(Paths.get(dir))) {
                    System.out.println("Processing images in " + dir + "\n");
                    File directory = new File(dir);
                    File[] filenames = directory.listFiles();
                    for (File image : filenames) {
                        processImage(vision, extractor, image.getAbsolutePath());
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
        String[] keywords = Parser.parseKeywords(response.toString(), extractor);
        System.out.println("Text: " + text);
        System.out.println("Keywords: " + Arrays.toString(keywords));
        
        System.out.println("\n--------------------------------\n");
    }

}