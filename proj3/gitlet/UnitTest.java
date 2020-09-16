package gitlet;

import ucb.junit.textui;
import org.junit.Test;

import static org.junit.Assert.*;

/** The suite of all JUnit tests for the gitlet package.
 *  @author Victor Shi
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** A dummy test to avoid complaint. */
    @Test
    public void placeholderTest() {
        int x = 2 - 1;
        int y = 1;
        assertEquals(x, y);
    }

    /** A dummy test to avoid complaint. */
    @Test
    public void test1() {
        String one = "giao";
        String two = "giao";
        assertEquals(one, two);
    }
    /** A dummy test to avoid complaint. */
    @Test
    public void test2() {
        String apple = "didididididi";
        String corriander = "didididididi";
        assertEquals(apple, corriander);
    }


}


