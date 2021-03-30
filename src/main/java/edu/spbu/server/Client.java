package edu.spbu.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private final int port;
    private final String host;
    private final String URL;
    public  Client(String host, int port, String url)
    {
        this.port=port;
        this.host=host;
        this.URL=url;
    }
    public void GETrequest()
    {
        try
        {
            Socket socket = new Socket(this.host, this.port);
            PrintStream request = new PrintStream(socket.getOutputStream());
            Scanner response = new Scanner(socket.getInputStream());
            String getRequest = "GET /" + URL + " HTTP/1.1\r\nHost: " + this.host + "\r\nConnection: close\r\n\r\n\"";

            System.out.println(getRequest);
            request.println(getRequest);

            while (response.hasNextLine()) {
                System.out.println(response.nextLine());
            }
        } catch (UnknownHostException e) {
            System.out.println("Host are incorrect");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}