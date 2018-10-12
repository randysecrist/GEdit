package edu.usu.cs.graph;

import java.util.StringTokenizer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Demonstrates how to store other data types in a graph ADT besides the
 * StringObj.
 * 
 * @author Randy Secrist
 */
public final class MunicipalData implements Data, java.io.Serializable {

    // Serial Version Id
    static final long serialVersionUID = 7640599580725058965L;
    private String cityName;
    private int cityPopulation;

    /**
     * This constructor should never be called directly. Creation date:
     * (3/5/2002 8:46:37 AM)
     */
    public MunicipalData() {
        super();
        this.cityName = "NULL";
        this.cityPopulation = 0;
    }

    /**
     * MunicipalData constructor comment.
     */
    public MunicipalData(String delimited) {
        super();
        // Tokenize delimited string looking for two parameters.
        StringTokenizer token = new StringTokenizer(delimited, ",");
        // If this container does not contain 2 tokens, return an empty set of
        // data.
        if (token.countTokens() != 2)
            return;
        // Please note that more robust code would better handle
        // NumberFormateExceptions - etc.
        this.cityName = token.nextToken();
        this.cityPopulation = new Integer(token.nextToken()).intValue();
    }

    /**
     * Specifies how one MunicipalData is equals to another MuniciplaData
     */
    public boolean equals(Data x) {
        if (x instanceof MunicipalData) {
            MunicipalData n = (MunicipalData) x;
            boolean cityEqual = this.cityName.equalsIgnoreCase(n.cityName);
            boolean popEqual = (this.cityPopulation == n.cityPopulation);
            if (cityEqual && popEqual)
                return true;
            else
                return false;
        }
        else {
            return false;
        }
    }

    /**
     * Returns the display name for this object.
     */
    public String getDisplayName() {
        return cityName;
    }

    /**
     * Specified how a new Data object can be created from a String.
     */
    public Data getInstance(String val) {
        return new MunicipalData(val);
    }

    /**
     * Displays a Swing JDialog Input for this Data Type.
     */
    public String showInputDialog(JFrame parent) {
        // Messages
        Object[] message = new Object[4];
        message[0] = "City Name:";
        JTextField nameTxt = new JTextField();
        nameTxt.setDoubleBuffered(true);
        message[1] = nameTxt;
        message[2] = "City Population:";
        JTextField popTxt = new JTextField();
        popTxt.setDoubleBuffered(true);
        message[3] = popTxt;
        // Options
        String[] options = {"Ok", "Cancel"};
        int result = JOptionPane.showOptionDialog(parent, // the parent that
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

    /**
     * The string representation of this object.
     *
     * This is used to reconstruct an instance of this object by calling the
     * String Constructor.
     */
    public String toString() {
        return cityName + "," + cityPopulation;
    }
}