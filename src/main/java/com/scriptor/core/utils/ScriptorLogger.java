package com.scriptor.core.utils;

import java.io.*;
import java.util.Scanner;

public class ScriptorLogger {
    private File file;
    public static int INFO = 1;
    public static int WARNING = 1 << 1;
    public static int ERROR = 1 << 2;
    public static int FATAL = 1 << 3;

    public ScriptorLogger() {
        try {
            file = new File("scriptor.log");

            if (!file.exists()) {
                file.createNewFile();
            }

        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public void insert(String text) {
        String data = read();

        data += text.endsWith("\n") ? text : (text + "\n");

        write(data);
    }

    public void clearAll() {
        write("");
    }

    private String read() {
        String result = "";

        try {
            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {
                result += (reader.nextLine() + "\n");
            }

            reader.close();
        } catch (FileNotFoundException error) {
            error.printStackTrace();
        }

        return result;
    }

    private void write(String text) {
        try {
            FileWriter writer = new FileWriter("scriptor.log");
            writer.write(text);
            writer.close();

        } catch (IOException error) {
            error.printStackTrace();
        }
    }
}
