package gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.*;

public class RobotsProgram {
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    SwingUtilities.invokeLater(() -> {
      MainApplicationFrame frame = new MainApplicationFrame();
      frameState(frame);
      frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosing(WindowEvent e) {
          saveWindowsMain(frame);
          System.exit(0);
        }
      });

    });
  }

  protected static void frameState(MainApplicationFrame frame) {
    String homeDir = System.getProperty("user.home");
    File frameFile = new File(homeDir, frame.getName() + ".bin");
    if (frameFile.exists()) {
        WindowState frameInfo = Saver.deserialize(frameFile);
        Saver.restoreWindowMain(frame, frameInfo);
        frame.setVisible(true);
    } else {
      frame.pack();
      frame.setVisible(true);
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    }
  }

  static void saveWindowsMain(MainApplicationFrame frame) {
    WindowState windowState = new WindowState(frame.getName(),
            frame.getWidth(), frame.getHeight(), frame.getX(),
            frame.getY(), false, false);
    Saver.serialize(windowState, frame.getName() + ".bin");
  }

}
