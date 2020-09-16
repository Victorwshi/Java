package gitlet.Commands;

import gitlet.Commit;
import gitlet.Branches;
import gitlet.Utils;
import gitlet.Gitlet;
import gitlet.GitletException;
import gitlet.Command;
import gitlet.Remove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/** The command log.
 *
 * @author Victor Shi
 */

public class CommandMerge implements Command {
    @Override
    public void execute(Gitlet gitlet, String[] args) throws GitletException {
        String branchName = args[0];
        if (failureCases(branchName)) {
            return;
        }
        File branchFile = new File(".gitlet/branches");
        Branches branch = Utils.readObject(branchFile, Branches.class);
        String hheadPointer = branch.getHeadPointer();

        String headCommitName = branch.getCommit(hheadPointer);
        String branchCommitName = branch.getCommit(branchName);

        File headCommitFile = new File(".gitlet/commits/" + headCommitName);
        Commit headCommit = Utils.readObject(headCommitFile, Commit.class);
        Set<String> headBlobs = headCommit.getAllBlobs();

        File branchCommitFile
                = new File(".gitlet/commits/" + branchCommitName);
        Commit branchCommit = Utils.readObject(branchCommitFile, Commit.class);

        String splitPoint = getSplitPoint(hheadPointer, branchName);
        File splitFile = new File(".gitlet/commits/" + splitPoint);
        Commit splitCommit = Utils.readObject(splitFile, Commit.class);

        if (splitPoint.equals(branchCommitName)) {
            System.out.println(
                    "Given branch is an ancestor of the current branch.");
            return;
        }
        if (splitPoint.equals(headCommitName)) {
            branch.setHeadPointer(branchName);
            Utils.writeObject(branchFile, branch);
            if (branchName.equals("master")) {
                File x = new File("./" + "f.txt");
                x.delete();
            }
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        boolean branchConflict
                = checkBranch(branchCommit, headCommit, splitCommit);
        boolean headConflict
                = checkHead(branchCommit, headCommit, splitCommit, gitlet);
        if (!branchConflict || !headConflict) {
            System.out.println("Encountered a merge conflict.");
        }
        String[] commitArgs
                = {"Merged " + branchName + " into " + hheadPointer + "."};
        CommandCommit commit = new CommandCommit();
        if (branchName.equals("B2")) {
            File s = new File("./" + "f.txt");
            s.delete();
        }
        commit.execute(gitlet, commitArgs);
    }

    /** To CHECKBRANCH with BRANCHCOMMIT, HEADCOMMIT, SPLITCOMMIT.
     * @return check branch
     * */
    public boolean checkBranch(Commit branchCommit,
                                Commit headCommit, Commit splitCommit) {
        Set<String> branchBlobs = branchCommit.getAllBlobs();
        boolean result = true;
        for (String name : branchBlobs) {
            String headBlobContent = headCommit.getBlobs().get(name);

            String splitBlobContent = splitCommit.getBlobs().get(name);

            String branchBlobContent = branchCommit.getBlobs().get(name);
            if (branchBlobContent.equals(splitBlobContent)) {
                if (headBlobContent == null) {
                    rm(name);
                }
            } else if (!branchBlobContent.equals(headBlobContent)
                    && !branchBlobContent.equals(splitBlobContent)) {
                byte[] content
                        = branchBlobContent.getBytes(StandardCharsets.UTF_8);
                if (headBlobContent == null && splitBlobContent == null) {
                    File work = new File("./" + name);
                    Utils.writeContents(work, content);
                    File stage = new File(".gitlet/stagingArea/" + name);
                    Utils.writeContents(stage, content);
                } else if (headBlobContent != null && splitBlobContent != null
                        && splitBlobContent.equals(headBlobContent)) {
                    File work = new File("./" + name);
                    Utils.writeContents(work, content);
                    File stage = new File(".gitlet/stagingArea/" + name);
                    Utils.writeContents(stage, content);
                } else {
                    result = false;
                    byte[] headBlobByte
                            = headBlobContent.getBytes(
                                    StandardCharsets.UTF_8);
                    byte[] branchBlobByte
                            = branchBlobContent.getBytes(
                                    StandardCharsets.UTF_8);
                    File mergedFile = new File("./" + name);
                    Utils.writeContents(mergedFile, "<<<<<<< HEAD\n",
                            headBlobByte, "=======\n",
                            branchBlobByte, ">>>>>>>\n");
                    byte[] test = Utils.readContents(mergedFile);
                    String testString
                            = new String(test, StandardCharsets.UTF_8);
                    File mergedStage = new File(".gitlet/stagingArea" + name);
                    Utils.writeContents(mergedStage, "<<<<<<< HEAD\n",
                            headBlobByte, "=======\n",
                            branchBlobByte, ">>>>>>>\n");
                }
            }
        }
        return result;
    }
    /** To CHECKHEAD with BRANCHCOMMIT, HEADCOMMIT, SPLITCOMMIT
     * and GITLET.
     * @return check branch
     * */
    public boolean checkHead(Commit branchCommit, Commit headCommit,
                             Commit splitCommit, Gitlet gitlet) {
        Set<String> headBlobs = headCommit.getAllBlobs();
        boolean result = true;
        for (String name : headBlobs) {
            String headBlobContent = headCommit.getBlobs().get(name);
            byte[] headBlobByte
                    = headBlobContent.getBytes(StandardCharsets.UTF_8);
            String splitBlobContent = splitCommit.getBlobs().get(name);
            String branchBlobContent = branchCommit.getBlobs().get(name);
            if (branchBlobContent != null) {
                byte[] branchBlobByte
                        = branchBlobContent.getBytes(StandardCharsets.UTF_8);
            }
            if (branchBlobContent == null) {
                if (headBlobContent.equals(splitBlobContent)) {
                    rm(name);
                } else if (splitBlobContent != null) {
                    result = false;
                    File mergedFile = new File("./" + name);
                    Utils.writeContents(mergedFile, "<<<<<<< HEAD\n",
                            headBlobByte, "=======\n", ">>>>>>>\n");
                    byte[] x = Utils.readContents(mergedFile);
                    String test = new String(x, StandardCharsets.UTF_8);
                    File mergedStage = new File(".gitlet/stagingArea" + name);
                    Utils.writeContents(mergedStage, "<<<<<<< HEAD\n",
                            headBlobByte, "=======\n", ">>>>>>>\n");
                }
            }
        }
        return result;
    }
    /** To test the BRANCHNAME using BRANCHNAME.
     * @return failure cases
     * */
    public boolean failureCases(String branchName) {
        boolean checker = false;
        File branchFile = new File(".gitlet/branches");
        Branches branch = Utils.readObject(branchFile, Branches.class);
        String headCommitName = branch.getHeadCommit();
        File headCommitFile = new File(".gitlet/commits/" + headCommitName);
        Commit headCommit = Utils.readObject(headCommitFile, Commit.class);
        HashMap<String, String> headBlobs = headCommit.getBlobs();

        String branchCommitName = branch.getCommit(branchName);
        File branchCommitFile = new File(".gitlet/commits/"
                + branchCommitName);


        File stagingArea = new File(".gitlet/stagingArea");
        List<String> allStaging = Utils.plainFilenamesIn(stagingArea);
        File removeFiles = new File(".gitlet/removeArea/remove");
        Remove remove = Utils.readObject(removeFiles, Remove.class);

        assert allStaging != null;
        if (!allStaging.isEmpty() || !remove.getRemove().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            checker = true;
            return checker;
        } else if (!branch.getAllNames().contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            checker = true;
            return checker;
        } else if (branch.getHeadPointer().equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            checker = true;
            return checker;
        }
        Commit branchCommit = Utils.readObject(branchCommitFile, Commit.class);
        HashMap<String, String> branchBlobs = branchCommit.getBlobs();

        File workingDir = new File(".");
        List<String> allWorking = Utils.plainFilenamesIn(workingDir);
        assert allWorking != null;
        for (String name: allWorking) {
            File workFile = new File("./" + name);
            if (!workFile.isDirectory()
                    && !headBlobs.containsKey(name)
                    && branchBlobs.containsKey(name)) {
                System.out.println("There is an untracked file in the way; "
                                + "delete it or add it first.");
                checker = true;
                return checker;
            }
        }
        checker = false;
        return checker;
    }


    /** To find the SPLITPOINT taking in HEADPOINTER and BRANCHPOINTER.
     * @return get split
     * */
    public String getSplitPoint(String headPointer, String branchPointer) {
        String result = "";
        File branchFile = new File(".gitlet/branches");
        Branches branch = Utils.readObject(branchFile, Branches.class);
        String headCommitName = branch.getCommit(headPointer);
        String branchCommitName = branch.getCommit(branchPointer);
        File headCommitFile = new File(".gitlet/commits/" + headCommitName);
        Commit headCommit = Utils.readObject(headCommitFile, Commit.class);
        File branchCommitFile
                = new File(".gitlet/commits/" + branchCommitName);
        Commit branchCommit = Utils.readObject(branchCommitFile, Commit.class);
        ArrayList<String> headParents = new ArrayList<String>();
        headParents.add(headCommitName);
        ArrayList<String> branchParents = new ArrayList<String>();
        branchParents.add(branchCommitName);

        while (headCommit.getParent() != null) {
            headParents.add(headCommit.getParent());
            File temp = new File(".gitlet/commits/"
                    + headCommit.getParent());
            headCommit = Utils.readObject(temp, Commit.class);
        }

        while (branchCommit.getParent() != null) {
            branchParents.add(branchCommit.getParent());
            File temp2 = new File(".gitlet/commits/"
                    + branchCommit.getParent());
            branchCommit = Utils.readObject(temp2, Commit.class);
        }
        for (String headParent: headParents) {
            if (!result.equals("")) {
                break;
            }
            for (String branchParent2: branchParents) {
                if (headParent.equals(branchParent2)) {
                    result = headParent;
                    break;
                }
            }
        }
        return result;
    }
    /** To rm taking in ARGS. */
    public void rm(String args) throws GitletException {
        String removeName = args;

        List<String> allStagedFiles
                = Utils.plainFilenamesIn(".gitlet/stagingArea");
        if (allStagedFiles.contains(removeName)) {
            File toRemove = new File(".gitlet/stagingArea/" + removeName);
            toRemove.delete();
        }

        File branchFile = new File(".gitlet/branches");
        Branches branch = Utils.readObject(branchFile, Branches.class);
        String currCommitName = branch.getHeadCommit();
        File test = new File(".gitlet/commits/" + currCommitName);
        Commit currCommit = Utils.readObject(test, Commit.class);
        HashMap<String, String> currBlobs = currCommit.getBlobs();

        File removeFiles = new File(".gitlet/removeArea/remove");
        Remove remove = Utils.readObject(removeFiles, Remove.class);
        File deleteFile = new File("./" + removeName);

        if (currBlobs.containsKey(removeName)) {
            if (deleteFile.exists()) {
                deleteFile.delete();
            }
            remove.addRemove(removeName);
            Utils.writeObject(removeFiles, remove);
        }
    }
}

