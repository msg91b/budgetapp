package com.teamfrugal.budgetapp.client;


import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;


public class Client {

    private Socket connection;
    private String result;

    public Client(){}

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

    public void sendImage(byte [] data, int type){
        if(connection == null)
            return;
        try {
            DataInputStream in = new DataInputStream(connection.getInputStream());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeInt(type);
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