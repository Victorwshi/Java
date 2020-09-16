package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * A remove class for Gitlet.
 *
 * @author Victor Shi
 */

public class Remove implements Serializable {
    /** to REMOVE.*/
    private ArrayList<String> remove;
    /** to SERIALVERSIONUID.*/
    static final long serialVersionUID = -5753681634004458261L;
    /** A REMOVE object.*/
    public Remove() {
        remove = new ArrayList<>();
    }
    /** to get REMOVE.
     * @return an ArrayList
     * */
    public ArrayList<String> getRemove() {
        return remove;
    }
    /** to delete ARG.*/
    public void deleteRemove(String arg) {
        remove.remove(arg);
    }
    /** to add ARG.*/
    public void addRemove(String arg) {
        remove.add(arg);
    }
    /** to clear remove.*/
    public void clearRemove() {
        remove.clear();
    }
}
