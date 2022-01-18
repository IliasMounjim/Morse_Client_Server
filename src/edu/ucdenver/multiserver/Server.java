package edu.ucdenver.multiserver;


import edu.ucdenver.morse.Morse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
//import java.util.Scanner;


public class Server implements Runnable {
    private final int port;
    private final int backlog;
    private int connectionCounter;
    private ServerSocket serverSocket;
    private boolean keepRunningClient;

    private Socket clientConnection;
    private PrintWriter output;
    private BufferedReader input;

    private Morse morse;


    public Server(int port, int backlog) {
        this.port = port;
        this.backlog = backlog;
        this.connectionCounter = 0;
        this.morse = new Morse();
    }

    public Server() {
        this(10001, 10);
    }

    private Socket waitForClientConnection() throws IOException {
        System.out.println("Waiting for a connection.....");
        Socket clientConnection = serverSocket.accept();
        this.connectionCounter++;
        System.out.printf("Connection #%d accepted from %s %n", this.connectionCounter,
                clientConnection.getInetAddress().getHostName());
        return clientConnection;
    }

    private void getOutputStream(Socket clientConnection) throws IOException {
        this.output = new PrintWriter(clientConnection.getOutputStream(), true);
    }

    private void getInputStream(Socket clientConnection) throws IOException {
        this.input = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
    }

    private void sendMessage(String message) {
        this.output.println(message);//send to server
    }

    private void processClientRequest() throws IOException {

        /*
        Protocol:
            the communication will be text based.
            fields will be delimited by a vertical bar ( | )
            clients may send either two requests:
                "E|text" -> requesting the server to encode the clean text "text" into Morse Code
                "D|morse" -> requesting the server to decode the received text "morse" from Morse Code to clean text
            Server may respond:
                "0|text" -> to either E and D requests. Text will correspond with either the encoded or decoded string.
                "1|Not Implemented" -> if the server got a requests unrecognized by the server.
                    E.g. "C|" or "E Hello world"
                "2|Invalid Message Format" -> if the message format is not correct.
                    Eg. "E", the command is correct but the arguments received are not (in this case no text was sent).
                    Important Notes:
            Quotes were added just to delimit the message descriptions, and are not part of the actual message.
            Make sure to use the Class defined in Part #1
            To test your server, use the text-based client class implemented in our hands-on examples. We will be using a variation of that to test your server.
            Make sure your server terminates after the client disconnects. Your code test will be aborted otherwise.
         */

        String clientMessage = getClientRequest();
        String[] arguments = clientMessage.split("//|");
        String response = "";
        try {
            switch (arguments[0]) {
                case "E":
                    response = "0|" + this.morse.encode(arguments[1]);
                    sendMessage(response);
                    break;
                case "D":
                    response = "0|" + this.morse.decode(arguments[1]);
                    sendMessage(response);
                    break;
                case "T":
                    this.keepRunningClient = false;
                    closeClientConnection(this.clientConnection, this.input, this.output);
                    break;
                default:
                    response = "1|Not Implemented";
            }
        } catch (IllegalArgumentException iae) {
            response = "2|Invalid Message Format" + iae.getMessage();
        }
        if  (response=="")
            response = "2|Invalid Message Format";
        else
            sendMessage(response);

    }
    public String getClientRequest() throws IOException {
        String clientRequest = this.input.readLine().toUpperCase(Locale.ROOT);
        System.out.println("CLIENT >> " + clientRequest);
        return clientRequest;
    }

    private void closeClientConnection(Socket clientConnection, BufferedReader input, PrintWriter output) {
        //close all input, output and socket.
        try {
            input.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        try {
            output.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            clientConnection.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            this.serverSocket = new ServerSocket(this.port, this.backlog);

            while (true) {
                try {
                    //establishing connection
                    clientConnection = this.waitForClientConnection();

                    System.out.println("Getting Data Streams");
                  

                    //process requests and send messages
                    while (this.keepRunningClient) {
                        try {
                            getInputStream(clientConnection);
                            getOutputStream(clientConnection);
                            processClientRequest();

                        } catch (Exception e) {
                            System.out.println("\n++++++ Client terminated  ++++++++");
                            e.printStackTrace();
                            closeClientConnection(clientConnection, input, output);
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } finally {
                    closeClientConnection(clientConnection, input, output);
                }
            }
        } catch (IOException ioe) {
            System.out.println("\n++++++ CANNOT OPEN THE SERVER  ++++++++");
            ioe.printStackTrace();
        }

    }

    public static void main(String[] args) {
        edu.ucdenver.server.Server server = new edu.ucdenver.server.Server();
        server.run();
    }

}