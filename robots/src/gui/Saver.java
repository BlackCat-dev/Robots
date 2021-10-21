package gui;

import javax.swing.*;
import java.beans.PropertyVetoException;
import java.io.*;

public class Saver {
    public static void serialize(Object info, String name) {
        String homeDir = System.getProperty("user.home");
        File file = new File(homeDir, name);
        try (OutputStream os = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(os))) {
            oos.writeObject(info);
            oos.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static WindowState deserialize(File file){
        WindowState windowState = null;
        try {
            InputStream is = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));
            try {
                windowState = (WindowState) ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return windowState;
    }

    public static void restoreWindow(JInternalFrame frame, WindowState windowState){
        frame.setLocation(windowState.positionX, windowState.positionY);
        frame.setSize(windowState.width, windowState.height);
        try {
            frame.setMaximum(windowState.isMax);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        try {
            frame.setIcon(windowState.isMin);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public static void restoreWindowMain(MainApplicationFrame frame, WindowState windowState){
        frame.setLocation(windowState.positionX, windowState.positionY);
        frame.setSize(windowState.width, windowState.height);
    }

}
