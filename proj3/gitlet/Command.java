package gitlet;


/** The command interface.
 * @author Victor Shi
 */

public interface Command {
    /** To run command taking in GITLET and ARGS and throwing GITLETEXCEPTION.
     * @param gitlet argument.
     * @param args argument.
     * @throws GitletException error.
     */
    void execute(Gitlet gitlet, String[] args) throws GitletException;

}
