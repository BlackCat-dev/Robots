package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javax.swing.*;

import log.Logger;

public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);


        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MainApplicationFrame.confirmExit();
            }
        });

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
        desktopPane.add(frame);
        frame.setVisible(true);
    }

//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
//
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
//
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        return menuBar;
//    }

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

    private JMenuItem createMenuItem(String name, int key, JMenu menu)
    {
        JMenuItem item = new JMenuItem(name, key);
        menu.add(item);
        return item;
    }

    private JMenu CreateLookAndFeelMenu()
    {
        JMenu lookAndFeelMenu = createSubMenu("Режим отображения", KeyEvent.VK_V,
                "Управление режимом отображения приложения");
        JMenuItem systemLookAndFeel = createMenuItem("Системная схема", KeyEvent.VK_S, lookAndFeelMenu);
        addLookAndFeelListener(systemLookAndFeel, UIManager.getSystemLookAndFeelClassName());
        JMenuItem crossplatformLookAndFeel = createMenuItem("Универсальная схема", KeyEvent.VK_S, lookAndFeelMenu);
        addLookAndFeelListener(crossplatformLookAndFeel, UIManager.getCrossPlatformLookAndFeelClassName());
        return lookAndFeelMenu;
    }

    private JMenu CreateTestMenu()
    {
        JMenu testMenu = createSubMenu("Тесты", KeyEvent.VK_T, "Тестовые команды");
        JMenuItem addLogMessageItem = createMenuItem("Сообщение в лог", KeyEvent.VK_S, testMenu);
        addTestListener(addLogMessageItem);
        return testMenu;
    }

    private JMenu CreateExitMenu()
    {
        JMenu exitMenu = createSubMenu("Выход", KeyEvent.VK_E, "Закрытие приложения");
        JMenuItem subExitItem = createMenuItem("Закрыть приложение", KeyEvent.VK_E, exitMenu);
        addExitListener(subExitItem);
        return exitMenu;
    }

    private void addLookAndFeelListener(JMenuItem item, String name)
    {
        item.addActionListener((event) -> {
            setLookAndFeel(name);
            this.invalidate();
        });
    }

    private void addTestListener(JMenuItem item)
    {
        item.addActionListener((event) -> { Logger.debug("Новая строка"); });
    }

    private void addExitListener(JMenuItem item)
    {
        item.addActionListener((event) -> { confirmExit(); });
    }

    public static void confirmExit()
    {
        JFrame frame = new JFrame("Exit confirmation frame");
        String[] options = { "Да", "Нет" };
        int n = JOptionPane.showOptionDialog(frame, "Вы действительно хотите закрыть приложение?",
                "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
        if (n == 0)
            System.exit(0);
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
