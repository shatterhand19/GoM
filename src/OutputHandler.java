import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by bozhidar on 01.10.17.
 * <p>
 * This class handles writing the file to the output stream
 */
final class OutputHandler {
    //Database with extensions and responses of the server to such a data type
    private static HashMap<String, String> extensions = new HashMap<>();

    static {
        extensions.put(".html", "Content-Type:text/html; charset=UTF-8\r\n");
        extensions.put(".png", "Content-Type:image/png; charset=UTF-8\r\n");
        extensions.put(".jpg", "Content-Type:image/jpg; charset=UTF-8\r\n");
        extensions.put(".jpeg", "Content-Type:image/jpeg; charset=UTF-8\r\n");
        extensions.put(".css", "Content-Type:text/css; charset=UTF-8\r\n");
        extensions.put(".txt", "Content-Type:text/plain; charset=UTF-8\r\n");
        extensions.put(".svg", "Content-Type:image/svg+xml; charset=UTF-8\r\n");
        extensions.put(".ico", "Content-Type:image/*; charset=UTF-8\r\n");
        extensions.put(".js", "Content-Type:application/javascript; charset=UTF-8\r\n");
    }

    /**
     * Writes the output to the client.
     *
     * @param fileName is the requested file
     * @param out
     * @throws IOException
     */
    static void writeOutput(String fileName, DataOutputStream out) throws IOException {
        if (fileName.equals("/")) fileName = "index.html";
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }

        //If the file type is supported
        File file;
        if (fileName.startsWith("Memes_front")) {
            file = new File(System.getProperty("user.dir"), fileName);
        } else {
            file = new File(System.getProperty("user.dir") + "/Memes_front", fileName);
        }
        System.out.println(file.getPath());
        int numOfBytes = (int) file.length();
        try {
            FileInputStream inFile = new FileInputStream(file);
            byte[] fileInBytes = new byte[numOfBytes];
            int r = inFile.read(fileInBytes);
            //Check if all bytes were written; if yes, send them; if not, transmit a error page
            if (r == numOfBytes) {
                //Header
                out.writeBytes("HTTP/1.1 200 OK\r\n");

                //Write the content type by getting it from the database or a default one
                out.writeBytes(extensions.getOrDefault(getExtension(fileName), "Content-Type:text/plain; charset=UTF-8\r\n"));



                out.writeBytes("\r\n");
                //Write file
                out.write(fileInBytes, 0, numOfBytes);
                out.flush();
            } else {
                //If not all bytes are read from the files
                System.out.println("Problem with reading the  file");
            }
        } catch (FileNotFoundException e) {
            //If the file is not found
            System.out.println("File not found: " + fileName);
        }
    }

    public static void sendJson(String json, DataOutputStream out) throws IOException {
        out.writeBytes("HTTP/1.1 200 OK\r\n");

        //Write length of file
        out.writeBytes("Content-Length:" + json.getBytes().length + "\r\n");

        //Write the content type by getting it from the database or a default one
        out.writeBytes("Content-Type:application/json; charset=UTF-8\r\n");

        out.writeBytes("\r\n");
        //Write file
        out.write(json.getBytes(), 0, json.getBytes().length);
        out.flush();
    }

    /**
     * Gets the extension of a file.
     *
     * @param fileName is the name of the file in question
     * @return a String that contains the extension of the file
     */
    private static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
