import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by bozhidar on 03.10.17.
 *
 * This is a runnable for the server. It executes in a separate thread and does all actions the server is supposed to.
 */
public class ServerRunnable implements Runnable {
    private Socket client;
    private ServerSocket server;
    private ImageDatabase image_db;
    private KeywordDatabase keyw_db;

    ServerRunnable(Socket client, ServerSocket server, ImageDatabase image_db, KeywordDatabase keyw_db) {
        this.client = client;
        this.server = server;
        this.image_db = image_db;
        this.keyw_db = keyw_db;
    }

    @Override
    public void run() {
        try {
            //Create input and output streams from client
            BufferedReader inFromClient = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(
                    client.getOutputStream());

            String requestMessageLine;
            String fileName;

            HashMap<String, String> headerValue = new HashMap<>();
            //Get all headers
            while (true) {
                //Read header
                requestMessageLine = inFromClient.readLine();
                //If end of headers
                if (requestMessageLine == null || requestMessageLine.equals("")) break;

                //Tokenize line
                StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);

                //Put all headers and their data into the database
                if (tokenizedLine.hasMoreElements()) {
                    String headerType = tokenizedLine.nextToken();
                    String values = requestMessageLine.substring(headerType.length() + 1);
                    headerValue.put(headerType, values);
                }
            }
            //Look if there was a GET header
            if (headerValue.containsKey("GET")) {
                StringTokenizer tokens = new StringTokenizer(headerValue.get("GET"));
                fileName = tokens.nextToken();

                if (fileName.startsWith("/search/")) {
                    System.out.println(fileName);
                } else {
                    OutputHandler.writeOutput(fileName, outToClient);
                    client.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
