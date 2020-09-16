package gitlet;

import java.io.Serializable;
import java.util.Date;
import java.io.File;
import java.util.HashMap;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Victor Shi
 */
public class Gitlet extends GitObject implements Serializable {
    /** The STAGINGAREA.*/
    private File stagingArea;
    /** The REMOVEAREA.*/
    private File removeArea;
    /** The GITLETDIR.*/
    private File gitletDir;
    /** The COMMITDIR.*/
    private File commitDir;
    /** The WORKINGDIR.*/
    private File workingDir;
    /** The HEAD of the gitlet.*/
    private String head;
    /** The BRANCHHEAD of the gitlet.*/
    private String branchHead;
    /** The BRANCHES of the gitlet.*/
    private Branches branches;

    /** The GITLET.*/
    public Gitlet() {
        head = null;
        branchHead = null;
    }
    /** The INIT.*/
    public void init() {
        Date init = new Date(0);
        String message = "initial commit";

        gitletDir = new File(".gitlet");
        if (gitletDir.exists()) {
            throw new GitletException("A Gitlet version-control system "
                    + "already exists in the current directory.");
        }

        gitletDir.mkdir();
        stagingArea = new File(".gitlet/stagingArea");
        stagingArea.mkdir();
        commitDir = new File(".gitlet/commits");
        commitDir.mkdir();
        workingDir = new File(".");
        workingDir.mkdir();
        Remove remove = new Remove();
        File removeFile = new File(".gitlet/removeArea");
        removeFile.mkdir();
        File removeFileDeep = new File(".gitlet/removeArea/remove");
        Utils.writeObject(removeFileDeep, remove);


        branches = new Branches();
        File bbranches = new File(".gitlet/branches");
        HashMap<String, String> empty = new HashMap<>();
        Commit first = new Commit(init, message, null, empty);
        String commitHash = first.getCommitHash();
        branches.addFlagBranch(commitHash, "master");
        Utils.writeObject(bbranches, branches);

        File commitFile = new File(".gitlet/commits/" + commitHash);
        try {
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(commitFile));
            out.writeObject(first);
            out.close();
        } catch (IOException excp) {
            throw new GitletException();
        }


    }

}
