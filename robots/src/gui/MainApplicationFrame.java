package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import javax.swing.*;

import log.Logger;


public class MainApplicationFrame extends JFrame
{
    public static final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {

        MainApplicationFrame frame = this;

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

        Saver.restore(gameWindow, logWindow, frame);

        addWindowListener(initExitListener());
        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    protected WindowAdapter initExitListener() {
        MainApplicationFrame frame = this;
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!ConfirmWindow.confirmExit(frame)){
                    Saver.saveWindows(desktopPane);
                    Saver.saveWindowsMain(frame);
                    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
                }
            }
        };
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
                if (ConfirmWindow.confirmExit(frame)){
                    WindowState windowState = new WindowState();
                    windowState.setName(frame.getName());
                    windowState.setWidth(frame.getWidth());
                    windowState.setHeight(frame.getHeight());
                    windowState.setPositionX(frame.getX());
                    windowState.setPositionY(frame.getY());
                    windowState.setMax(frame.isMaximum());
                    windowState.setMin(frame.isIcon());
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
        createMenuItem("Закрыть приложение", KeyEvent.VK_E, exitMenu, (event) -> Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
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
