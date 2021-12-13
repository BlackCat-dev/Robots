package gui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.*;

public class Saver {

    public static String homeDir = System.getProperty("user.home");

    public static void serialize(Object info, String name) {
        File file = new File(homeDir, name);
        try (OutputStream os = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(os))) {
            oos.writeObject(info);
            oos.flush();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    MainApplicationFrame.desktopPane,
                    "Во время сохранения данных произошла ошибка."
            );
        }
    }

    public static WindowState deserialize(Container nameFrame){
        File frameFile = new File(homeDir, nameFrame.getName() + ".bin");
        if (frameFile.exists()) {
            WindowState windowState = new WindowState();
            try {
                InputStream is = new FileInputStream(frameFile);
                ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));
                try {
                    windowState = (WindowState) ois.readObject();
                } catch (ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(
                            MainApplicationFrame.desktopPane,
                            "Во время восстановления данных произошла ошибка."
                    );
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        MainApplicationFrame.desktopPane,
                        "Во время восстановления данных произошла ошибка."
                );
            }
            return windowState;
        }else {
            return null;
        }
    }

    public static void windowInfo(JInternalFrame widowName) {
        WindowState info = deserialize(widowName);
        restoreWindow(widowName, info);
    }

    public static void frameStateMain(MainApplicationFrame frame) {
        WindowState frameInfo = deserialize(frame);
        assert frameInfo != null;
        restoreContainer(frame, frameInfo);
        frame.setVisible(true);
    }

    public static void restoreContainer(Container frame, WindowState windowState){
        frame.setLocation(windowState.getPositionX(), windowState.getPositionY());
        frame.setSize(windowState.getWidth(), windowState.getHeight());
    }

    public static void restoreWindow(JInternalFrame frame, WindowState windowState){
        restoreContainer(frame, windowState);
        try {
            frame.setMaximum(windowState.isMax());
        } catch (PropertyVetoException e) {
            JOptionPane.showMessageDialog(
                    MainApplicationFrame.desktopPane,
                    "Ошибка при установки указателя Maximized."
            );
        }
        try {
            frame.setIcon(windowState.isMin());
        } catch (PropertyVetoException e) {
            JOptionPane.showMessageDialog(
                    MainApplicationFrame.desktopPane,
                    "Ошибка при установки указателя Minimized."
            );
        }
    }

    public static void saveWindows(JDesktopPane desktopPane){
        for (JInternalFrame window: desktopPane.getAllFrames()) {
            WindowState windowState = new WindowState();
            savedParameters(windowState, window);
            windowState.setMax(window.isMaximum());
            windowState.setMin(window.isIcon());
            Saver.serialize(windowState, window.getName() + ".bin");
        }
    }

    public static void saveWindowsMain(MainApplicationFrame frame) {
        WindowState windowState = new WindowState();
        savedParameters(windowState, frame);
        serialize(windowState, frame.getName() + ".bin");
    }

    public static void savedParameters(WindowState windowState, Container frame){
        windowState.setName(frame.getName());
        windowState.setWidth(frame.getWidth());
        windowState.setHeight(frame.getHeight());
        windowState.setPositionX(frame.getX());
        windowState.setPositionY(frame.getY());
    }

}
