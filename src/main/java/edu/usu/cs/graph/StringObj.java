package edu.usu.cs.graph;

/**
 * A Data Object which consists of Strings.
 * Creation date: (2/18/2002 10:25:32 AM)
 * 
 * @author Randy Secrist
 */
public final class StringObj implements Data, java.io.Serializable {

    /**
     * Serial version uid
     */
    static final long serialVersionUID = -6446862734589412662L;

    /**
     * The display name of this node.
     */
    private String displayName;

    /**
     * This constructor should never be called directly.
     */
    public StringObj() {
        super();
        this.displayName = "NULL";
    }

    /**
     * Constructs a data object based on a displayname.
     */
    public StringObj(String displayName) {
        super();
        this.displayName = displayName;
    }

    /**
     * Specifies how one StringObj is equals to another SringObj
     * @return True if objects are equal (case insensitive), false otherwise.
     */
    public boolean equals(Data x) {
        if (x instanceof StringObj) {
            StringObj n = (StringObj) x;
            if (this.displayName.equals(n.displayName))
                return true;
            else
                return false;
        }
        else
            return false;
    }

    /**
     * Returns the display name for this object.
     * @return The display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Specifies how a new Data object can be created from a String.
     * @return A new Data object.
     */
    public Data getInstance(String val) {
        return new StringObj(val);
    }

    /**
     * The string representation of this object.
     *
     * This is used to reconstruct an instance of this object by calling the
     * String Constructor.
     * @return The string representation for this object.
     */
    public String toString() {
        return displayName;
    }
}
