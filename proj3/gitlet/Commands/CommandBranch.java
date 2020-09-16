package gitlet.Commands;

import gitlet.Branches;
import gitlet.Utils;
import gitlet.Gitlet;
import gitlet.GitletException;
import gitlet.Command;

import java.io.File;
import java.util.Set;

/** The command branch.
 *
 * @author Victor Shi
 */
public class CommandBranch implements Command {
    @Override
    public void execute(Gitlet gitlet, String[] args) throws GitletException {
        if (args.length != 1) {
            throw new GitletException("Incorrect operands.");
        }
        String branchName = args[0];
        File branchFile = new File(".gitlet/branches");
        Branches branch = Utils.readObject(branchFile, Branches.class);
        Set<String> names = branch.getAllNames();
        for (String name: names) {
            if (name.equals(branchName)) {
                System.out.println("A branch with that name already exists.");
                System.exit(0);
            }
        }
        String headCommit = branch.getHeadCommit();
        branch.addBranch(headCommit, branchName);
        Utils.writeObject(branchFile, branch);
    }
}
