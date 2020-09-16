package gitlet;

import gitlet.Commands.CommandCommit;
import gitlet.Commands.CommandRm;
import gitlet.Commands.CommandAdd;
import gitlet.Commands.CommandMerge;
import gitlet.Commands.CommandBranch;
import gitlet.Commands.CommandFind;
import gitlet.Commands.CommandGlobalLog;
import gitlet.Commands.CommandLog;
import gitlet.Commands.CommandReset;
import gitlet.Commands.CommandRmBranch;
import gitlet.Commands.CommandStatus;
import gitlet.Commands.CommandCheckout;

import java.io.File;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Victor Shi
 */
public class Main {
    /** A Gitlet object. */
    private static Gitlet gitlet = new Gitlet();

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length <= 0 || args == null) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String command = args[0];
        try {
            Command commandFound = getCommand(args);
            File gitletDir = new File(".gitlet");
            if (!gitletDir.exists() || !gitletDir.isDirectory()) {
                if (commandFound == null) {
                    gitlet.init();
                    return;
                } else {
                    throw new GitletException("Not in an"
                            + " initialized gitlet directory.");
                }
            }
            if (commandFound != null) {
                String[] commands = new String[args.length - 1];
                System.arraycopy(args, 1,
                        commands, 0, args.length - 1);
                commandFound.execute(gitlet, commands);
            } else {
                gitlet.init();
            }
        } catch (GitletException exception) {
            System.out.println(exception.getMessage());
            System.exit(0);
        }
    }
    /** To get COMMAND RESULT taking in ARGS.
     * @return a command
     * */
    private static Command getCommand(String... args) {
        String command = args[0];
        Command result = null;
        if (command.equals("add")) {
            result = new CommandAdd();
            return result;
        } else if (command.equals("commit")) {
            result = new CommandCommit();
            return result;
        } else if (command.equals("checkout")) {
            result = new CommandCheckout();
            return result;
        } else if (command.equals("log")) {
            result = new CommandLog();
            return result;
        } else if (command.equals("init")) {
            return result;
        } else if (command.equals("remove")) {
            result = new CommandRm();
            return result;
        } else if (command.equals("global-log")) {
            result = new CommandGlobalLog();
            return result;
        } else if (command.equals("rm")) {
            result = new CommandRm();
            return result;
        } else if (command.equals("find")) {
            result = new CommandFind();
            return result;
        } else if (command.equals("branch")) {
            result = new CommandBranch();
            return result;
        } else if (command.equals("rm-branch")) {
            result = new CommandRmBranch();
            return result;
        } else if (command.equals("status")) {
            result = new CommandStatus();
            return result;
        } else if (command.equals("reset")) {
            result = new CommandReset();
            return result;
        } else if (command.equals("merge")) {
            result = new CommandMerge();
            return result;
        } else {
            throw new GitletException("No command with that name exists.");
        }
    }

}
