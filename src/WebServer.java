import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class WebServer {
    private static final int DEFAULT_PORT = 9437;

    private static int port = DEFAULT_PORT;

    private static ThreadPool pool;
    private static final int QUEUE_SIZE = 10000;
    private static final int THREADS = 50;


    public static void main(String[] args) throws FileNotFoundException {
        //Create pool
        pool = new ThreadPool(QUEUE_SIZE, THREADS);
        ImageDatabase image_db = new ImageDatabase("db/image_db");
        KeywordDatabase keyw_db = new KeywordDatabase("db/keyw_db");
        KeyWordsExtractor extractor = new KeyWordsExtractor("dics/words.txt");


        //Initialise with resources; As the resource is auto-closable it will close automatically in the end
        try (ServerSocket serverSocket = new ServerSocket(port, 100)) {
            while (true) {
                try {
                    //Get the new socket and add it to the pool
                    Socket client = serverSocket.accept();
                    pool.submitTask(new ServerRunnable(client, serverSocket, image_db, keyw_db, extractor));
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


