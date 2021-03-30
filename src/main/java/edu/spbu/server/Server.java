package edu.spbu.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Server {
    private final int port;
    private final String link;
    public Server(int p, String d)
    {
        this.port=p;
        this.link=d;
    }
    public void ServerStart()
    {
        try
        {
            ServerSocket server = new ServerSocket(port);
            while (!Disable.activate){
                Socket client = server.accept();
                PrintStream ActiveStream = new PrintStream(client.getOutputStream());
                Scanner GetStream = new Scanner(client.getInputStream());
                if (GetStream.hasNextLine()) {
                    String URL = RequestedFile(GetStream);
                    File f = new File(link + URL);
                    if (f.exists()) {
                        int ConnectionStatus = 1;
                        String ConnectionSMessage = "Connection success";
                        this.sendResponse(ActiveStream, ConnectionStatus, ConnectionSMessage, "text/html", f.length());
                        Scanner FileScan = new Scanner(f);
                        while (FileScan.hasNextLine()) {
                            String s = FileScan.nextLine();
                            ActiveStream.println(s);
                        }
                    } else {
                        String nf = "Error, page not found";
                        int l = 8;
                        this.sendResponse(ActiveStream, 404, nf, "text/plain", l);
                        ActiveStream.println("Error, page not found");
                    }
                    client.close();
                    ActiveStream.flush();
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("Host are incorrect");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendResponse(PrintStream w, int statusCode, String statusText, String type, long size)
    {
        w.printf("HTTP/1.1 %s %s%n", statusCode, statusText);
        w.printf("Content-Type: %s%n", type);
        w.printf("Content-Length: %s%n%n", size);
    }
    private String RequestedFile(Scanner a)
    {
        String request = a.nextLine();
        return request.split(" ")[1];
    }
}