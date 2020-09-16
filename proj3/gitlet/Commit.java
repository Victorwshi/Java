package gitlet;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/** The Commit class for gitlet.
 * @author Victor Shi
 */

public class Commit extends GitObject {
    /** The date of this commit. */
    private Date date;
    /** The log message of this commit. */
    private String message;
    /** The parent link of this commit. */
    private String parent;
    /** The blob references of this commit. */
    private HashMap<String, String> blobs;
    /** The Hash name of this commit. */
    private String commitHash;
    /** The commit object with DDATE, MMESSAGE, PPARENT, BBLOBS.
    */
    public Commit(Date ddate, String mmessage,
                  String pparent, HashMap<String, String> bblobs) {
        if (mmessage.equals("")) {
            throw new GitletException("Please enter a commit message!");
        }
        this.date = ddate;
        this.message = mmessage;
        this.parent = pparent;
        this.blobs = bblobs;
        String hashCom = "";
        if (pparent != null) {
            hashCom = ddate + mmessage + pparent + blobs;
        } else {
            hashCom = ddate + mmessage + blobs;
        }
        this.commitHash = Utils.sha1(hashCom);
    }

    /** To get DATE.
     * @return get date
     * */
    public Date getDate() {
        return date;
    }
    /** To get MESSAGE.
     * @return get message
     * */
    public String getMessage() {
        return message;
    }
    /** To get PARENT.
     * @return get parent
     * */
    public String getParent() {
        return parent;
    }
    /** To get BLOBS.
     * @return get blobs
     * */
    public HashMap<String, String> getBlobs() {
        return blobs;
    }
    /** To get COMMITHASH.
     * @return get commithash
     * */
    public String getCommitHash() {
        return commitHash;
    }
    /** To get all BLOB.
     * @return get allblobs
     * */
    public Set<String> getAllBlobs() {
        Set<String> blob = blobs.keySet();
        return blob;
    }


}
