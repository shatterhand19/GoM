import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
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
    private KeyWordsExtractor extractor;

    ServerRunnable(Socket client, ServerSocket server, ImageDatabase image_db, KeywordDatabase keyw_db, KeyWordsExtractor extractor) {
        this.client = client;
        this.server = server;
        this.image_db = image_db;
        this.keyw_db = keyw_db;
        this.extractor = extractor;
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
            if (headerValue.containsKey("POST")) {
                StringTokenizer tokens = new StringTokenizer(headerValue.get("POST"));
                String[] request = tokens.nextToken().replace("%20", " ").split(":");
                System.out.println(Arrays.toString(request) + "\n");
                boolean toUpdateKeywords = image_db.voteKeyword(request[0], request[1], request[2]);
                if (toUpdateKeywords) {
                    keyw_db.addImage(request[1], request[0]);
                    keyw_db.writeToFile(null);
                }
                outToClient.writeBytes("HTTP/1.1 200 OK\r\n");
                outToClient.writeBytes("Content-Length:" + 0 + "\r\n");

                //Write the content type by getting it from the database or a default one
                outToClient.writeBytes("Content-Type:application/json; charset=UTF-8\r\n");

                outToClient.writeBytes("\r\n");
                outToClient.flush();
            }
            //Look if there was a GET header
            if (headerValue.containsKey("GET")) {
                StringTokenizer tokens = new StringTokenizer(headerValue.get("GET"));
                fileName = tokens.nextToken().replace("%20", " ");
                System.out.println(fileName);
                if (fileName.startsWith("/search/")) {
                    String response = SearchAlgorithm.search(fileName.substring(8), image_db, keyw_db, extractor);
                    System.out.println("Json response: " + response);
                    OutputHandler.sendJson(response, outToClient);
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
