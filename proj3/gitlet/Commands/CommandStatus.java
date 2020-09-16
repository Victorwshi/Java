package gitlet.Commands;

import gitlet.Commit;
import gitlet.Branches;
import gitlet.Utils;
import gitlet.Gitlet;
import gitlet.GitletException;
import gitlet.Command;
import gitlet.Remove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.io.File;
import java.util.Set;

/** The command status.
 *
 * @author Victor Shi
 */

public class CommandStatus implements Command {
    @Override
    public void execute(Gitlet gitlet, String[] args)
            throws GitletException {
        if (args.length != 0) {
            throw new GitletException("Invalid operands.");
        }
        System.out.println("=== Branches ===");
        File branchFile = new File(".gitlet/branches");
        Branches branches = Utils.readObject(branchFile, Branches.class);
        Set<String> allNames = branches.getAllNames();
        String currCommitName = branches.getHeadCommit();
        File currCommitFile
                = new File(".gitlet/commits/" + currCommitName);
        Commit currCommit = Utils.readObject(currCommitFile, Commit.class);
        ArrayList<String> printOut = new ArrayList<String>();
        for (String name: allNames) {
            if (!name.equals(branches.getHeadPointer())) {
                printOut.add(name);
            } else {
                String sum = "*" + name;
                printOut.add(sum);
            }
        }
        Collections.sort(printOut);
        for (String x : printOut) {
            System.out.println(x);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");

        List<String> allStagedFiles
                = Utils.plainFilenamesIn(".gitlet/stagingArea");
        Collections.sort(allStagedFiles);
        for (String names: allStagedFiles) {
            System.out.println(names);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        File removeFile = new File(".gitlet/removeArea/remove");
        Remove remove = Utils.readObject(removeFile, Remove.class);
        ArrayList<String> removeList = remove.getRemove();
        for (String removeName: removeList) {
            System.out.println(removeName);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        File workDir = new File(".");

        System.out.println();
        System.out.println("=== Untracked Files ===");

    }

}
