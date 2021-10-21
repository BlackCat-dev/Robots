package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.File;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import javax.swing.*;

import log.Logger;


public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    String homeDir = System.getProperty("user.home");

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);


        LogWindow logWindow = createLogWindow();
        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);

        addWindow(gameWindow);
        addWindow(logWindow);

        File gameFile = new File(homeDir, gameWindow.getName() + ".bin");
        File logFile = new File(homeDir, logWindow.getName() + ".bin");
        if (gameFile.exists() && logFile.exists()) {
            boolean toRestore = ConfirmWindow.confirmRestore(this) == 0;
            if (toRestore) {
                WindowState gameInfo = Saver.deserialize(gameFile);
                Saver.restoreWindow(gameWindow, gameInfo);
                WindowState logInfo = Saver.deserialize(logFile);
                Saver.restoreWindow(logWindow, logInfo);
            }
        }
        setJMenuBar(generateMenuBar());
        close();

    }

    private void saveWindows(JDesktopPane desktopPane){
        for (JInternalFrame window: desktopPane.getAllFrames()) {
            WindowState windowState = new WindowState(window.getName(),
                    window.getWidth(), window.getHeight(), window.getX(),
                    window.getY(), window.isMaximum(), window.isIcon());
            Saver.serialize(windowState, window.getName() + ".bin");
        }
    }

    protected void close(){
        MainApplicationFrame frame = this;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                if (ConfirmWindow.confirmExit(frame) == 0){
                    saveWindows(desktopPane);
                    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
                }
            }
        });
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        String frameName = frame.getName();
        desktopPane.add(frame);
        frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                int exitCode = ConfirmWindow.confirmExit(frame);
                if (exitCode == JOptionPane.YES_OPTION){
                    WindowState windowState = new WindowState(frameName, frame.getWidth(),
                            frame.getHeight(), frame.getX(), frame.getY(),
                            frame.isMaximum(), frame.isIcon());
                    Saver.serialize(windowState, frameName + ".bin");
                    frame.dispose();
                }
            }
        });
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(CreateLookAndFeelMenu());
        menuBar.add(CreateTestMenu());
        menuBar.add(CreateExitMenu());
        return menuBar;
    }

    private static JMenu createSubMenu(String name, int key, String subMenuNames) {
        JMenu menu = new JMenu(name);
        menu.setMnemonic(key);
        menu.getAccessibleContext().setAccessibleDescription(subMenuNames);
        return menu;
    }

    private void createMenuItem(String name, int key, JMenu menu, ActionListener action)
    {
        JMenuItem item = new JMenuItem(name, key);
        item.addActionListener(action);
        menu.add(item);
    }

    private JMenu CreateLookAndFeelMenu()
    {
        JMenu lookAndFeelMenu = createSubMenu("Режим отображения", KeyEvent.VK_V,
                "Управление режимом отображения приложения");
        createMenuItem("Системная схема", KeyEvent.VK_S, lookAndFeelMenu, (event) ->
        { setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate(); });
        createMenuItem("Универсальная схема", KeyEvent.VK_S, lookAndFeelMenu, (event) ->
        { setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate(); });
        return lookAndFeelMenu;
    }

    private JMenu CreateTestMenu()
    {
        JMenu testMenu = createSubMenu("Тесты", KeyEvent.VK_T, "Тестовые команды");
        createMenuItem("Сообщение в лог", KeyEvent.VK_S, testMenu, (event) -> Logger.debug("Новая строка"));
        return testMenu;
    }

    private JMenu CreateExitMenu()
    {
        JMenu exitMenu = createSubMenu("Выход", KeyEvent.VK_E, "Закрытие приложения");
        createMenuItem("Закрыть приложение", KeyEvent.VK_E, exitMenu, (event) -> {Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                new WindowEvent(this, WindowEvent.WINDOW_CLOSING));});
        return exitMenu;
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
