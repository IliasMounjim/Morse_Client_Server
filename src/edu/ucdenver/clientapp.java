package edu.ucdenver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class clientapp {

    private final int serverPort;
    private final String serverIp;
    private boolean isConnected;
    private Socket serverConnection;
    private BufferedReader input;
    private PrintWriter output;

    private PrintWriter getOutputStream() throws IOException {
        return new PrintWriter(this.serverConnection.getOutputStream(), true);
    }

    private BufferedReader getInputStream() throws IOException {
        return new BufferedReader(new InputStreamReader(this.serverConnection.getInputStream()));
    }

    public clientapp(int serverPort, String serverIp) {
        this.serverPort = serverPort;
        this.serverIp = serverIp;
        this.isConnected = false;
    }

    public clientapp() {
        this.serverPort = 10000;
        this.serverIp = "127.0.0.1";
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public void connect() {
        displayMsg("Attempting connection to Server");
        try {
            this.serverConnection = new Socket("localhost", 10000);
            this.isConnected = true;
            this.output = this.getOutputStream();
            this.input = this.getInputStream();
            getSeverInitialResponse();



        } catch (IOException e) {
            this.input = null;
            this.output = null;
            this.serverConnection = null;
            this.isConnected = false;
        }
    }

    public void disconnect() {
        displayMsg("\n>> Terminating Client Connection to Server");
        try {
            this.input.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        try {
            this.output.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            serverConnection.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void getSeverInitialResponse() throws IOException {
        String srvResponse = this.input.readLine();
        displayMsg("Server >> " + srvResponse);
    }


    public String sendRequest(String request) throws IOException {
        this.output.println(request);
        displayMsg("CLIENT >> " + request);
        String srvResponse = this.input.readLine();
        displayMsg("Server >> " + srvResponse);
        return srvResponse;
    }

    public void displayMsg(String msg) {
        System.out.println(msg);
    }

    public static void main(String[] args) {

        clientapp client = new clientapp();
        client.connect();
    }


}


