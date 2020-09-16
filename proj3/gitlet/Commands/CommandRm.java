package gitlet.Commands;

import gitlet.Commit;
import gitlet.Branches;
import gitlet.Utils;
import gitlet.Gitlet;
import gitlet.GitletException;
import gitlet.Command;
import gitlet.Remove;
import java.util.HashMap;
import java.util.List;

import java.io.File;

/** The command remove.
 *
 * @author Victor Shi
 */
public class CommandRm implements Command {
    @Override
    public void execute(Gitlet gitlet, String[] args) throws GitletException {
        String removeName = args[0];

        List<String> allStagedFiles
                = Utils.plainFilenamesIn(".gitlet/stagingArea");
        if (allStagedFiles.contains(removeName)) {
            File toRemove = new File(".gitlet/stagingArea/" + removeName);
            boolean x = toRemove.delete();
        }

        File branchFile = new File(".gitlet/branches");
        Branches branch = Utils.readObject(branchFile, Branches.class);
        String currCommitName = branch.getHeadCommit();
        File test = new File(".gitlet/commits/" + currCommitName);
        Commit currCommit = Utils.readObject(test, Commit.class);
        HashMap<String, String> currBlobs = currCommit.getBlobs();

        File removeFiles = new File(".gitlet/removeArea/remove");
        Remove remove = Utils.readObject(removeFiles, Remove.class);
        File deleteFile = new File("./" + removeName);

        if (currBlobs.containsKey(removeName)) {
            if (deleteFile.exists()) {
                deleteFile.delete();
            }
            remove.addRemove(removeName);
            Utils.writeObject(removeFiles, remove);
        }

        if (!allStagedFiles.contains(removeName)
                && !currBlobs.containsKey(removeName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

}
