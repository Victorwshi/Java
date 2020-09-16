package gitlet.Commands;
import gitlet.Commit;
import gitlet.Utils;
import gitlet.Gitlet;
import gitlet.GitletException;
import gitlet.Command;
import java.util.List;


import java.io.File;

/** The command find.
 *
 * @author Victor Shi
 */
public class CommandFind implements Command {
    @Override
    public void execute(Gitlet gitlet, String[] args) throws GitletException {
        String commitMessage = args[0];
        File allCommitFile = new File(".gitlet/commits");
        List<String> allCommits = Utils.plainFilenamesIn(allCommitFile);
        int checker = 0;
        for (String commitName: allCommits) {
            File commitFile = new File(".gitlet/commits/" + commitName);
            Commit tempCommit = Utils.readObject(commitFile, Commit.class);
            if (tempCommit.getMessage().equals(commitMessage)) {
                System.out.println(commitName);
                checker = 1;
            }
        }
        if (checker == 0) {
            System.out.println("Found no commit with that message.");
        }

    }

}
