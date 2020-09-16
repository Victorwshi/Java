package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Victor Shi
 */
class MovingRotor extends Rotor {


    /** The permutation of the rotor. */
    private Permutation _perm;
    /** The notch of the rotor. */
    private String _notches;

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _perm = perm;
        _notches = notches;
    }

    @Override
    /** Return whether the rotor can rotate. */
    boolean rotates() {
        return true;
    }
    @Override
    /** Return whether the rotor can reflect. */
    boolean reflecting() {
        return false;
    }
    /** Return whether the rotor is at notch. */
    boolean atNotch() {
        int temp = this.getSetting();
        Character setChar = (this.alphabet().toChar(temp));
        return _notches.contains(setChar.toString());
    }
    @Override
    /** Advance this rotor once. */
    void advance() {
        this.set(this.setting() + 1);
    }
}
