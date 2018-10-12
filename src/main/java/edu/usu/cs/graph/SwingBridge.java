package edu.usu.cs.graph;

import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class SwingBridge implements DataVisitor {

    public String showInputDialog(Object s, Object parent) {
        try {
            Method m = this.getClass().getDeclaredMethod("visit", new Class[] {s.getClass(),Object.class});
            return (String) m.invoke(this, new Object[] {s, parent});
        }
        catch (Throwable e) {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            else
                throw new Error(e.getMessage(), e);
        }
    }

    private String visit(StringObj s, Object parent) {
        return JOptionPane.showInputDialog((JFrame)parent, "Enter Node Display Name", "Node Data Prompt", javax.swing.JOptionPane.OK_CANCEL_OPTION);
    }

    private String visit(MunicipalData md, Object parent) {
        // Messages
        Object[] message = new Object[4];
        message[0] = "City Name:";
        JTextField nameTxt = new JTextField();
        message[1] = nameTxt;
        message[2] = "City Population:";
        JTextField popTxt = new JTextField();
        message[3] = popTxt;
        // Options
        String[] options = {"Ok", "Cancel"};
        int result = JOptionPane.showOptionDialog((JFrame)parent, // the parent that
                                                          // the dialog blocks
                message, // the dialog message array
                "New Municipal Data:", // the title of the dialog window
                JOptionPane.DEFAULT_OPTION, // option type
                JOptionPane.QUESTION_MESSAGE, // message type
                null, // optional icon, use null to use the default icon
                options, // options string array, will be made into buttons
                options[0] // option that should be made into a default button
                );
        // Ensure we got a number for population
        int population;
        if (popTxt == null || popTxt.getText().length() <= 0 || popTxt.getText().equalsIgnoreCase(""))
            population = 0;
        else
            population = Integer.parseInt(popTxt.getText());
        switch (result) {
            case 0 :
                // yes
                return new String(nameTxt.getText() + "," + population);
            case 1 :
                // cancel
                break;
            default :
                break;
        }
        return null;
    }
}
