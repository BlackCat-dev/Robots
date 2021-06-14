package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.PropertyVetoException;
import java.io.*;
import java.util.HashSet;

import log.Logger;

public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final HashSet<Component> windowRegistry = new HashSet<>();

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);

        customization();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    private void customization() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("position.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                int[] converted = new int[parts.length];

                switch (parts[0]) {
                    case "log":
                        for (int i = 1; i < 6; i++)
                            converted[i] = Integer.parseInt(parts[i]);
                        addWindow(createLogWindow(converted[1],converted[2],converted[3],converted[4],converted[5]));
                        break;
                    case "game":
                        for (int i = 1; i < 6; i++)
                            converted[i] = Integer.parseInt(parts[i]);
                        addWindow(createGameWindow(converted[1],converted[2],converted[3],converted[4],converted[5]));
                        break;
                    default:
                        break;

                }
            }
        } catch (Exception e) {
            createDefaultPair();
            Logger.debug("Ошибка: " + e.toString());
            Logger.debug("Созданы окна по-умолчанию.");
        }
    }

    protected LogWindow createLogWindow(int x, int y, int width, int height, int state)
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setName("log");
        logWindow.setLocation(x,y);
        logWindow.setSize(width, height);

        boolean windowState = intToBoolean(state);
        try {
            logWindow.setIcon(windowState);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }


        setMinimumSize(logWindow.getSize());
        windowRegistry.add(logWindow);
        Logger.debug("Протокол работает");
        return logWindow;
    }

    private LogWindow createLogWindow() {
        return createLogWindow(10,10 ,300, 800, 0);
    }

    private GameWindow createGameWindow(int x, int y, int width, int height, int state)
    {
        GameWindow gameWindow = new GameWindow();
        gameWindow.setName("game");
        gameWindow.setLocation(x, y);
        gameWindow.setSize(width, height);

        boolean windowState = intToBoolean(state);
        try {
            gameWindow.setIcon(windowState);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        windowRegistry.add(gameWindow);
        return gameWindow;
    }

    private GameWindow createGameWindow() {
        return createGameWindow(0, 0, 400, 400, 0);
    }

    private void createDefaultPair() {
        addWindow(createLogWindow());
        addWindow(createGameWindow());
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
        createMenuItem("Закрыть приложение", KeyEvent.VK_E, exitMenu, (event) -> confirmExit());
        return exitMenu;
    }

    private void confirmExit()
    {
        JFrame frame = new JFrame("Exit confirmation frame");
        String[] options = { "Да", "Нет" };
        int state;
        int n = JOptionPane.showOptionDialog(frame, "Вы действительно хотите закрыть приложение?",
                "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (n == JOptionPane.YES_OPTION){
            try {
                PrintWriter writer = new PrintWriter("position.txt");
                writer.close();
                BufferedWriter br = new BufferedWriter(new FileWriter("position.txt"));
                for (Component component : windowRegistry) {

                    if (component.getInputContext() != null)
                        state = 0;
                    else
                        state = 1;

                    br.append(component.getName()).append(" ")
                            .append(String.valueOf(component.getX())).append(" ")
                            .append(String.valueOf(component.getY())).append(" ")
                            .append(String.valueOf(component.getWidth())).append(" ")
                            .append(String.valueOf(component.getHeight())).append(" ")
                            .append(String.valueOf(state)).append(" ");
                    br.append(" \n");
                }
                br.flush();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.exit(0);
            }
        }
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

    private boolean intToBoolean(int input) {
        if((input==0)||(input==1)) {
            return input!=0;
        }else {
            throw new IllegalArgumentException("Входное значение может быть равно только 0 или 1 !");
        }
    }
}
