package gitlet.Commands;

import gitlet.Branches;
import gitlet.Utils;
import gitlet.Gitlet;
import gitlet.GitletException;
import gitlet.Command;


import java.io.File;
import java.util.Set;

/** The command remove branch.
 *
 * @author Victor Shi
 */
public class CommandRmBranch implements Command {
    @Override
    public void execute(Gitlet gitlet, String[] args) throws GitletException {
        if (args.length != 1) {
            throw new GitletException("Incorrect operands.");
        }
        String branchName = args[0];
        File branchFile = new File(".gitlet/branches");
        Branches branch = Utils.readObject(branchFile, Branches.class);
        Set<String> allNames = branch.getAllNames();
        String headName = branch.getHeadPointer();
        if (headName.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        if (!allNames.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        branch.removeBranch(branchName);
        Utils.writeObject(branchFile, branch);
    }
}
