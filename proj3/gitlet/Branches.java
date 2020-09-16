package gitlet;
import java.util.HashMap;
import java.util.Set;

/** Branches class for branches object.
 * @author Victor Shi
 */
public class Branches extends GitObject {
    /** _BRANCHES that represents the branch tree of gitlet.
     */
    private HashMap<String, String> _branches;
    /**The activated HEADPOINTER.*/
    private String headPointer;
    /**The POINTERB.*/
    private String pointerB;
    /** A BLOB object.*/
    public Branches() {
        _branches = new HashMap<String, String>();
        headPointer = null;
        pointerB = null;
    }
    /** To add the current branch taking in COMMIT and NAME.*/
    public void addFlagBranch(String commit, String name) {
        _branches.put(name, commit);
        headPointer = name;
    }
    /** To add a branch taking in COMMIT and NAME.*/
    public void addBranch(String commit, String name) {
        _branches.put(name, commit);
        pointerB = name;
    }
    /** To get GETCOMMIT taking in HEAD.
     * @return a commit
     * */
    public String getCommit(String head) {
        String getCommit = _branches.get(head);
        return getCommit;
    }
    /** To get the headPointer commit.
     * @return head commit
     * */
    public String getHeadCommit() {
        String ggetHeadCommit = _branches.get(headPointer);
        return ggetHeadCommit;
    }
    /** To get the PPOINTERB commit.
     * @return b commit
     * */
    public String getBCommit() {
        String ppointerB = _branches.get(pointerB);
        return _branches.get(ppointerB);
    }
    /** To get the HEADPOINTER.
     * @return a pointer
     * */
    public String getHeadPointer() {
        return headPointer;
    }

    /** To get the POINTERB.
     * @return pointer b
     * */
    public String getPointerB() {
        return pointerB;
    }
    /** To set the headPointer to NEWPOINTER.
     * */
    public void setHeadPointer(String newPointer) {
        headPointer = newPointer;
    }
    /** To get ALLNAMES in the branch.
     * @return all names
     * */
    public Set<String> getAllNames() {
        Set<String> allNames = _branches.keySet();
        return allNames;
    }
    /** To delete the branch with NAME in the branch.*/
    public void removeBranch(String name) {
        _branches.remove(name);
        pointerB = null;
    }
    /** To reset the branch taking in a COMMITNAME.*/
    public void resetBranch(String commitName) {
        headPointer = commitName;
    }
}
