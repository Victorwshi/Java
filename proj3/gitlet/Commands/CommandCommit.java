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
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.ArrayList;

/** The command commit.
 * @author Victor Shi
 */

public class CommandCommit implements Command {
    @Override
        public void execute(Gitlet gitlet,
                            String[] args) throws GitletException {
        if (args.length == 0) {
            throw new GitletException("Please enter a commit message.");
        }
        if (args.length != 1) {
            throw new GitletException("Incorrect number of args.");
        }
        Date now = new Date();
        String message = args[0];

        List<String> files = Utils.plainFilenamesIn(".gitlet/stagingArea");

        File removeFile = new File(".gitlet/removeArea/remove");
        Remove remove = Utils.readObject(removeFile, Remove.class);

        if (files.isEmpty() && remove.getRemove().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        File branchFile = new File(".gitlet/branches");
        Branches branch = Utils.readObject(branchFile, Branches.class);
        String parentHash = branch.getHeadCommit();
        File parentComFile = new File(".gitlet/commits/" + parentHash);
        Commit parentCom = Utils.readObject(parentComFile, Commit.class);

        HashMap<String, String> currentBlobs = parentCom.getBlobs();

        for (String file: files) {
            File stageFile = new File(".gitlet/stagingArea/" + file);
            byte[] blobContentByte = Utils.readContents(stageFile);
            String blobContent
                    = new String(blobContentByte, StandardCharsets.UTF_8);
            if (!remove.getRemove().contains(file)) {
                currentBlobs.put(file, blobContent);
                stageFile.delete();
            }
        }
        ArrayList<String> getRemove = remove.getRemove();
        for (String temp : getRemove) {
            currentBlobs.remove(temp);
        }
        remove.clearRemove();
        Utils.writeObject(removeFile, remove);
        Commit newCommit = new Commit(now, message, parentHash, currentBlobs);
        String commitHash = newCommit.getCommitHash();

        File comFile = new File(".gitlet/commits/" + commitHash);
        Utils.writeObject(comFile, newCommit);

        branch.addFlagBranch(commitHash, branch.getHeadPointer());
        Utils.writeObject(branchFile, branch);

    }
}
