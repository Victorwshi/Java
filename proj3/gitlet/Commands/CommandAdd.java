package gitlet.Commands;
import gitlet.Commit;
import gitlet.Branches;
import gitlet.Remove;
import gitlet.Utils;
import gitlet.Gitlet;
import gitlet.GitletException;
import gitlet.Blob;
import gitlet.Command;

import java.io.File;
import java.util.HashMap;

/** The command add.
 *
 * @author Victor Shi
 */
public class CommandAdd implements Command {
    @Override
    public void execute(Gitlet gitlet, String[] args) throws GitletException {
        if (args.length != 1) {
            throw new GitletException("Incorrect operands.");
        }
        File addFile = new File("./" + args[0]);
        if (!addFile.exists()) {
            throw new GitletException("File does not exist.");
        }
        Blob inFile = new Blob(Utils.readContents(addFile));
        String shaFile = inFile.getBlobHash();


        File branchFile = new File(".gitlet/branches");
        Branches branch = Utils.readObject(branchFile, Branches.class);
        String commitName = branch.getHeadCommit();
        File commitFile = new File(".gitlet/commits/" + commitName);
        Commit curCommit = Utils.readObject(commitFile, Commit.class);
        HashMap<String, String> contents = curCommit.getBlobs();

        File removeFile = new File(".gitlet/removeArea/remove");
        Remove remove = Utils.readObject(removeFile, Remove.class);
        if (remove.getRemove().contains(args[0])) {
            remove.deleteRemove(args[0]);
            Utils.writeObject(removeFile, remove);
        }

        if (contents.size() != 0) {
            String sb = contents.get(args[0]);
            if (sb != null) {
                Blob blobContent = new Blob(sb.getBytes());
                String blobHash = blobContent.getBlobHash();
                if (contents.containsKey(args[0])
                        && blobHash.equals(shaFile)) {
                    return;
                }
            }
        }

        File store = new File(".gitlet/stagingArea/" + args[0]);
        Utils.writeContents(store, (Object) inFile.getBlob());



    }
}
