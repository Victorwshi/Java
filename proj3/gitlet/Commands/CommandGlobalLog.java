package gitlet.Commands;

import gitlet.Commit;
import gitlet.Utils;
import gitlet.Gitlet;
import gitlet.GitletException;
import gitlet.Command;
import java.util.List;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.Date;

/** The command global-log.
 *
 * @author Victor Shi
 */

public class CommandGlobalLog implements Command {
    @Override
    public void execute(Gitlet gitlet, String[] args) throws GitletException {
        File allCommits = new File(".gitlet/commits");
        List<String> commitList = Utils.plainFilenamesIn(allCommits);
        for (String commitName : commitList) {
            File commitFile = new File(".gitlet/commits/" + commitName);
            Commit commit = Utils.readObject(commitFile, Commit.class);
            Formatter outs = new Formatter();
            outs.format("===%n");
            Date time = commit.getDate();
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
            outs.format("commit %s%n", commitName, dateFormat.format(time),
                    commit.getMessage());
            outs.format("Date: %s%n%s%n",
                    dateFormat.format(time), commit.getMessage());
            System.out.println(outs);

        }
    }
}
