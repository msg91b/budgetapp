package com.teamfrugal.budgetapp;

import android.util.Log;

import java.io.*;
import java.net.Socket;

/**
 * Created by Wanderlast on 3/6/2017.
 */

// socket connection class
// connects to a server running on sleipnir
// The class can send image data as a byte array to the server.
// If the server is able to scan the image it returns a the result
// else it returns an empty string.
public class Client {

    private Socket connection;
    private String result;

    Client(){}

    public void connect(String ip, int port){
        try {
            connection = new Socket(ip, port);
        } catch(Exception e){
            StringWriter s = new StringWriter();
            PrintWriter p = new PrintWriter(s);
            e.printStackTrace(p);
            Log.d("error:", s.toString());
        }
    }

    public void sendImage(byte [] data){
        if(connection == null)
            return;
        try {
            DataInputStream in = new DataInputStream(connection.getInputStream());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeInt( data.length);
            out.write(data, 0, data.length);
            result = in.readUTF();
            System.out.println("result: " + result);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getResult(){
        if(connection == null)
            return null;
        return result;
    }

    public void close(){
        try {
            if(connection == null)
                return;
            connection.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
