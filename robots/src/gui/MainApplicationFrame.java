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

import javax.swing.*;

import log.Logger;

public class MainApplicationFrame extends JFrame implements HaveStorableFrames
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

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        addWindowListener(initExitListener());

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
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

    protected WindowAdapter initExitListener() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Object[] options = {"Да", "Нет"};
                int result = JOptionPane.showOptionDialog(
                        desktopPane,
                        "Закрыть программу?",
                        "Закрыть программу?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );
                if (result == 0) {
                    try {
                        PositionStore store = new PositionStore(MainApplicationFrame.this, System.getProperty("user.home"));
                        store.storePositions();
                    } catch (IOException exc) {
                        JOptionPane.showMessageDialog(
                                desktopPane,
                                "Во время сохранения данных произошла ошибка."
                        );
                    }
                    setDefaultCloseOperation(EXIT_ON_CLOSE);
                }
            }
        };
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

    @Override
    public List<HasState> getDataForStore() {
        JInternalFrame[] allFrames = desktopPane.getAllFrames();
        List<HasState> toStore = new ArrayList<>();
        for (JInternalFrame frame: allFrames) {
            if (frame instanceof HasState) {
                toStore.add((HasState) frame);
            }
        }
        return toStore;
    }

    @Override
    public void restore(PositionStore store) {
        Map<String, WindowState> data = store.getStoredData();
        List<HasState> framesToRestore = getDataForStore();
        for (HasState frame: framesToRestore) {
            frame.setState(data);
        }
    }
}
