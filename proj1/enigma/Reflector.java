package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a reflector in the enigma.
 *  @author Victor Shi
 */
class Reflector extends FixedRotor {

    /** The permutation of the reflector. */
    private Permutation _perm;

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM. */
    Reflector(String name, Permutation perm) {
        super(name, perm);
        _perm = perm;
    }

    @Override
    /** Return whether the rotor can reflect. */
    boolean reflecting() {
        return true;
    }
    /** Reflector cannot set positions. */
    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector has only one position");
        }
    }

}
