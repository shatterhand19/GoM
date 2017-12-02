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
        extensions.put(".png", "Content-Type:image/png; charset=UTF-8\r\n");
        extensions.put(".jpg", "Content-Type:image/jpg; charset=UTF-8\r\n");
        extensions.put(".jpeg", "Content-Type:image/jpeg; charset=UTF-8\r\n");
    }

    /**
     * Writes the output to the client.
     *
     * @param fileName is the requested file
     * @param out
     * @throws IOException
     */
    static void writeOutput(String fileName, DataOutputStream out) throws IOException {
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }

        //If the file type is supported
        File file = new File(System.getProperty("user.dir") + "/Memes_front", fileName);
        int numOfBytes = (int) file.length();
        try {
            FileInputStream inFile = new FileInputStream(fileName);
            byte[] fileInBytes = new byte[numOfBytes];
            int r = inFile.read(fileInBytes);
            //Check if all bytes were written; if yes, send them; if not, transmit a error page
            if (r == numOfBytes) {
                //Header
                out.writeBytes("HTTP/1.1 200 OK\r\n");

                //Write the content type by getting it from the database or a default one
                out.writeBytes(extensions.getOrDefault(getExtension(fileName), "Content-Type:text/plain; charset=UTF-8\r\n"));

                //Write length of file
                out.writeBytes("Content-Length:" + numOfBytes + "\r\n");

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
