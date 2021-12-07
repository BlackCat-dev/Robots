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

    public static WindowState deserialize(File file){
        WindowState windowState = new WindowState();
        try {
            InputStream is = new FileInputStream(file);
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
    }

    public static void restore(JInternalFrame nameWindowOne, JInternalFrame nameWindowTwo, MainApplicationFrame nameFrame){
        File fileNameOne = new File(homeDir, nameWindowOne.getName() + ".bin");
        File fileNameTwo = new File(homeDir, nameWindowTwo.getName() + ".bin");
        File frameFile = new File(homeDir, nameFrame.getName() + ".bin");
        if (fileNameOne.exists() && fileNameTwo.exists() && frameFile.exists()) {
            if (!ConfirmWindow.confirmRestore(MainApplicationFrame.desktopPane)) {
                windowInfo(fileNameOne, nameWindowOne);
                windowInfo(fileNameTwo, nameWindowTwo);
                frameStateMain(frameFile, nameFrame);
            } else {
                standardWindow(nameFrame);
            }
        }
    }

    private static void windowInfo(File fileName, JInternalFrame widowName) {
        WindowState info = deserialize(fileName);
        restoreWindow(widowName, info);
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
            windowState.setName(window.getName());
            windowState.setWidth(window.getWidth());
            windowState.setHeight(window.getHeight());
            windowState.setPositionX(window.getX());
            windowState.setPositionY(window.getY());
            windowState.setMax(window.isMaximum());
            windowState.setMin(window.isIcon());
            Saver.serialize(windowState, window.getName() + ".bin");
        }
    }

    public static void frameStateMain(File fileName, MainApplicationFrame frame) {
        WindowState frameInfo = deserialize(fileName);
        restoreContainer(frame, frameInfo);
        frame.setVisible(true);
    }

    public static void saveWindowsMain(MainApplicationFrame frame) {
        WindowState windowState = new WindowState();
        windowState.setName(frame.getName());
        windowState.setWidth(frame.getWidth());
        windowState.setHeight(frame.getHeight());
        windowState.setPositionX(frame.getX());
        windowState.setPositionY(frame.getY());
        serialize(windowState, frame.getName() + ".bin");
    }

    public static void standardWindow(MainApplicationFrame frame){
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    }

}
