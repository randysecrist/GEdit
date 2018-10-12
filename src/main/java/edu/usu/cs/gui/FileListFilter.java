package edu.usu.cs.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FileListFilter extends FileFilter {
    public static final int DELIMIT = 1 << 0;
    public static final int SERIALIZE = 1 << 1;
    public static final int XML = 1 << 2;
    private int type;

    /**
     * A simple type which contsins both a String and a boolean.
     * @author Randy Secrist
     */
    class StringBool {
        private String description;
        private boolean accepted;
        StringBool(String description, boolean accepted) {
            this.description = description;
            this.accepted = accepted;
        }
        public boolean isAccepted() {
            return accepted;
        }
        public String getDescription() {
            return description;
        }
    }

    /**
     * Returns true if the given file is accepted by this filter.
     * Files are accepted if they matach extensions .txt, .grf, or .xml
     *
     * <p>Returns false otherwise.
     *
     * @param f a file to test an extension on.
     */
    public boolean accept(File f) {
        if (f == null)
            return false;
        else if (f.isDirectory())
            return true;
        String ex = getExtension(f);

        return switchType(ex).accepted;
    }

    /**
     * Handles all possible combinations of file type suggestions.
     * @param ex A possible file extension.
     * @return A StringBool.
     */
    private StringBool switchType(String ex) {
        if (ex == null)
            ex = "";
        switch(type) {
               case DELIMIT:
                   if (ex.equalsIgnoreCase("txt"))
                       return new StringBool("Import Files (*.txt)", true);
                   else
                      return new StringBool("Import Files (*.txt)", false);
               case SERIALIZE:
                   if (ex.equalsIgnoreCase("grf"))
                       return new StringBool("Graph Files (*.grf)", true);
                   else
                       return new StringBool("Graph Files (*.grf)", false);
               case 3:
                   if (ex.equalsIgnoreCase("grf") || ex.equalsIgnoreCase("txt"))
                       return new StringBool("Supported Files (*.grf,*.txt)", true);
                   else
                       return new StringBool("Supported Files (*.grf,*.txt)", false);
               case XML:
                   if (ex.equalsIgnoreCase("xml"))
                       return new StringBool("XML Files (*.xml)", true);
                   else
                       return new StringBool("XML Files (*.xml)", false);
               case 5:
                   if (ex.equalsIgnoreCase("xml") || ex.equalsIgnoreCase("txt"))
                       return new StringBool("Supported Files (*.xml,*.txt)", true);
                   else
                       return new StringBool("Supported Files (*.xml,*.txt)", false);
               case 6:
                   if (ex.equalsIgnoreCase("grf") || ex.equalsIgnoreCase("xml"))
                       return new StringBool("Supported Files (*.grf,*.xml)", true);
                   else
                       return new StringBool("Supported Files (*.grf,*.xml)", false);
               case 7:
                   if (ex.equalsIgnoreCase("grf") || ex.equalsIgnoreCase("xml") || ex.equalsIgnoreCase("txt"))
                       return new StringBool("Supported Files (*.grf,*.xml,*.txt)", true);
                   else
                       return new StringBool("Supported Files (*.grf,*.xml,*.txt)", false);
               default:
                   return new StringBool("Graph Files (*.grf)", false);
        }
    }

    /**
     * The description of this filter. This is what shows up in
     * the file dialog window.
     */
    public String getDescription() {
        return switchType(null).description;
    }

    /**
     * Return the extension portion of the file's name .
     *
     * @param f the file to parse the extension of
     * @see #getExtension
     * @see FileFilter#accept
     */
     private String getExtension(File f) {
        if(f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if(i > 0 && i < filename.length()-1)
                return filename.substring(i+1).toLowerCase();
        }
        return "";
    }

    /**
     * Constructs a new FileListFilter for use in a JFileChooser.
     */
    public FileListFilter(int type) {
        super();
        this.type = type;
    }
}