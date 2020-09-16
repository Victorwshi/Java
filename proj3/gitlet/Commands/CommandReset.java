package gitlet.Commands;
import gitlet.Commit;
import gitlet.Branches;
import gitlet.Utils;
import gitlet.Gitlet;
import gitlet.GitletException;
import gitlet.Command;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/** The command remove branch.
 *
 * @author Victor Shi
 */
public class CommandReset implements Command {
    @Override
    public void execute(Gitlet gitlet, String[] args) throws GitletException {
        if (args.length != 1) {
            throw new GitletException("Incorrect operands");
        }
        String commitName = args[0];
        File allCommitsFiles = new File(".gitlet/commits");
        List<String> allCommits = Utils.plainFilenamesIn(allCommitsFiles);
        if (!allCommits.contains(commitName)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File branchFile = new File(".gitlet/branches");
        Branches branch = Utils.readObject(branchFile, Branches.class);
        File currCommitFile
                = new File(".gitlet/commits/" + branch.getHeadCommit());
        Commit currCommit
                = Utils.readObject(currCommitFile, Commit.class);
        HashMap<String, String> blobs = currCommit.getBlobs();
        File resetCommitFile
                = new File(".gitlet/commits/" + commitName);
        Commit resetCommit
                = Utils.readObject(resetCommitFile, Commit.class);
        HashMap<String, String> resetBlobs = resetCommit.getBlobs();


        File git = new File(".");
        List<String> allWorkingFiles = Utils.plainFilenamesIn(git);
        assert allWorkingFiles != null;
        for (String name: allWorkingFiles) {
            File file = new File("./" + name);
            byte[] fileContentByte = Utils.readContents(file);
            String fileContent
                    = new String(fileContentByte, StandardCharsets.UTF_8);
            if (!blobs.containsKey(name)
                    && !fileContent.equals(resetBlobs.get(name))
                    && resetBlobs.containsKey(name)) {
                System.out.println("There is an untracked "
                        + "file in the way; delete it or add it first.");
                System.exit(0);
            }
        }
        for (String name: resetBlobs.keySet()) {
            String[] arg = {commitName, "--", name};
            CommandCheckout checkout = new CommandCheckout();
            checkout.execute(gitlet, arg);
        }
        File stagingArea = new File(".gitlet/stagingArea");
        for (File file: Objects.requireNonNull(stagingArea.listFiles())) {
            file.delete();
        }
        String headPointer = branch.getHeadPointer();
        branch.removeBranch(headPointer);
        branch.addFlagBranch(commitName, headPointer);
        Utils.writeObject(branchFile, branch);
    }
}
