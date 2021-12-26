package gui;

import javax.swing.*;
import java.awt.*;

public class ConfirmWindow {
    public static boolean confirmExit(Component component){
         int result = JOptionPane.showOptionDialog(component, "Закрыть?", "Выход",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, new Object[]{"Да", "Нет"}, "Да");
        return result == 1;
    }

    public static boolean confirmRestore(Component component) {
        int result = JOptionPane.showOptionDialog(component, "Восстановить?",
                "Есть сохранённое состояние окошек приложения", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{"Да", "Нет"}, "Да");
        return result == 1;
    }
}