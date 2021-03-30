package server;

import edu.spbu.server.Client;
import edu.spbu.server.Server;
import edu.spbu.server.Disable;
import org.junit.Test;


public class ServerTest {
    @Test
    public void serverRun() throws Exception {
        class ServerParallelRunning implements Runnable {

            @Override
            public void run() {
                Server sr1 = new Server(80, "./src/main/");
                sr1.ServerStart();
            }
        }
        Disable.activate=false;

        Thread th = new Thread(new ServerParallelRunning());
        th.start();

        Client cl1 = new Client("localhost", 80, "m1.txt");
        cl1.GETrequest();
        Disable.activate=true;
        th.join();
    }
}