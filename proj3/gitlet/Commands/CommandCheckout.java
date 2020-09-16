package gitlet.Commands;

import java.io.File;
import java.nio.charset.StandardCharsets;
import gitlet.Commit;
import gitlet.Branches;
import gitlet.Utils;
import gitlet.Gitlet;
import gitlet.GitletException;
import gitlet.Command;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** The command checkout.
 * @author Victor Shi
 */

public class CommandCheckout implements Command {
    @Override
    public void execute(Gitlet gitlet, String[] args) throws GitletException {
        if (args.length == 1 && !args[0].equals("--")) {
            checkoutBranch(args[0]);
        } else if (args.length == 2 && args[0].equals("--")) {
            checkout(args[1]);
        } else if (args.length == 3 && args[1].equals("--")) {
            checkout(args[0], args[2]);
        } else {
            throw new GitletException("Incorrect operands.");
        }
    }
    /** To checkout taking in a FILENAME.*/
    public void checkout(String fileName) {
        File branchFile = new File(".gitlet/branches");
        Branches branch = Utils.readObject(branchFile, Branches.class);
        String commitName = branch.getHeadCommit();

        File currentCommitFile = new File(".gitlet/commits/" + commitName);
        Commit currentCommit
                = Utils.readObject(currentCommitFile, Commit.class);
        HashMap<String, String> blobs = currentCommit.getBlobs();
        if (!blobs.containsKey(fileName)) {
            throw new GitletException("File does not exist in that commit.");
        }
        String targetFile = blobs.get(fileName);
        byte[] fileContent = targetFile.getBytes();
        File checkoutFile = new File("./" + fileName);
        Utils.writeContents(checkoutFile, fileContent);

    }
    /** To checkout taking in a COMMITID and FILENAME.*/
    public void checkout(String commitId, String fileName) {
        File commits = new File(".gitlet/commits");
        List<String> allCommitsNames = Utils.plainFilenamesIn(commits);
        String selectedCommitName = null;
        if (commitId.length() < Utils.getUidLength()) {
            int commitIdLength = commitId.length();
            for (String commitName: allCommitsNames) {
                if (commitName.substring(0, commitIdLength).equals(commitId)) {
                    selectedCommitName = commitName;
                    break;
                }
            }
            if (selectedCommitName == null) {
                throw new GitletException("No commit with that id exists.");
            }
        } else {
            selectedCommitName = commitId;
        }
        File commitfile = new File(".gitlet/commits/" + selectedCommitName);
        if (!commitfile.exists()) {
            throw new GitletException("No commit with that id exists.");
        }
        Commit checkoutCommit = Utils.readObject(commitfile, Commit.class);
        HashMap<String, String> blobs = checkoutCommit.getBlobs();
        if (!blobs.containsKey(fileName)) {
            throw new GitletException("File does not exist in that commit.");
        }
        String targetFile = blobs.get(fileName);
        byte[] fileContent = targetFile.getBytes();
        File checkoutFile = new File("./" + fileName);
        Utils.writeContents(checkoutFile, fileContent);
    }

    /** To checkout branch taking in a BRANCHNAME.*/
    public void checkoutBranch(String branchName) {
        File branchFile = new File(".gitlet/branches");
        Branches branch = Utils.readObject(branchFile, Branches.class);
        Set<String> allBranchNames = branch.getAllNames();
        if (!allBranchNames.contains(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        } else if (branchName.equals(branch.getHeadPointer())) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        String head = branch.getHeadCommit();
        String branchCommitName = branch.getCommit(branchName);

        File commitFileBranch = new File(".gitlet/commits/" + branchCommitName);
        Commit branchCommit = Utils.readObject(commitFileBranch, Commit.class);
        HashMap<String, String> branchBlobs = branchCommit.getBlobs();

        File commitFile = new File(".gitlet/commits/" + head);
        Commit headCommit = Utils.readObject(commitFile, Commit.class);
        HashMap<String, String> blobs = headCommit.getBlobs();

        List<String> allWorking = Utils.plainFilenamesIn(".");
        for (String name: allWorking) {
            File workFile = new File("./" + name);
            byte[] workFileByte = Utils.readContents(workFile);
            String content = new String(workFileByte, StandardCharsets.UTF_8);
            if (!blobs.containsKey(name)
                    && branchBlobs.containsKey(name)
                    && !branchBlobs.get(name).equals(blobs.get(name))) {
                System.out.println("There is an untracked"
                        + " file in the way; delete it or add it first.");
                System.exit(0);
            }
            if (blobs.containsKey(name)
                    && !branchBlobs.containsKey(name)) {
                workFile.delete();
            }
        }

        branch.setHeadPointer(branchName);
        Utils.writeObject(branchFile, branch);

        Set<String> allBranchFiles = branchBlobs.keySet();
        for (String name2: allBranchFiles) {
            String oneFile = branchBlobs.get(name2);
            byte[] content = oneFile.getBytes(StandardCharsets.UTF_8);
            String test = new String(content, StandardCharsets.UTF_8);

            File work = new File("./" + name2);
            Utils.writeContents(work, content);
        }

        File stagingArea = new File(".gitlet/stagingArea");
        for (File file: Objects.requireNonNull(stagingArea.listFiles())) {
            file.delete();
        }
    }
}
