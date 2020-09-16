package gitlet.Commands;
import gitlet.Commit;
import gitlet.Branches;
import gitlet.Utils;
import gitlet.Gitlet;
import gitlet.GitletException;
import gitlet.Command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.util.Formatter;

/** The command log.
 *
 * @author Victor Shi
 */
public class CommandLog implements Command {
    @Override
    public void execute(Gitlet gitlet, String[] args) throws GitletException {
        File branchFile = new File(".gitlet/branches");
        Branches branch = Utils.readObject(branchFile, Branches.class);
        String currCommitName = branch.getHeadCommit();
        File test = new File(".gitlet/commits/" + currCommitName);
        Commit testCommit = Utils.readObject(test, Commit.class);

        while (currCommitName != null) {

            File currCommitFile = new File(".gitlet/commits/" + currCommitName);
            Commit currCommit = Utils.readObject(currCommitFile, Commit.class);
            String parentCommitName = currCommit.getParent();

            Formatter output = new Formatter();
            output.format("===%n");
            Date now = currCommit.getDate();
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
            output.format("commit %s%n", currCommitName);
            output.format("Date: %s%n%s%n", dateFormat.format(now),
                    currCommit.getMessage());
            System.out.println(output);
            currCommitName = parentCommitName;
        }
    }
}
