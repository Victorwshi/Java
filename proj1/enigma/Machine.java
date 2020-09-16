package enigma;
import java.util.Arrays;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Victor Shi
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */

    /** The total number of rotors. */
    private int _numRotor;
    /** The total number of pawls. */
    private int _pawl;
    /** The collection of all rotors. */
    private Collection<Rotor> _allRotor;
    /** The array of all rotors. */
    private Rotor[] _allRotors;
    /** The array of rotors in slots. */
    private Rotor[] rotorInSlots;
    /** The permutation of the plugboard. */
    private Permutation _plugBoard;
    /** An boolean array that tracks the move. */
    private boolean[] moveTracker;
    /** An enigma machine with input of ALPHA,
     * NUMROTORS, PAWLS, and ALLROTORS. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotor = numRotors;
        _pawl = pawls;
        _allRotor = allRotors;
        rotorInSlots = new Rotor[numRotors];
        moveTracker = new boolean[numRotors];
        _allRotors = allRotors.toArray(new Rotor[_allRotor.size() - 1]);
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotor;
    }
    /** Return rotors in slots. */
    Rotor[] getRotorInSlots() {
        return rotorInSlots;
    }
    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawl;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; i++) {
            for (int j = i + 1; j < rotors.length; j++) {
                if (rotors[i] == rotors[j]) {
                    throw error("Repeated rotor!");
                }
            }
        }
        int count = 0;
        for (int i = 0; i < rotors.length; i++) {
            for (int x = _allRotors.length - 1; x >= 0; x--) {
                if (_allRotors[x].name().equals(rotors[i])) {
                    rotorInSlots[i] = _allRotors[x];
                    if (_allRotors[x] instanceof MovingRotor) {
                        count += 1;
                    }
                }
            }
            if (rotorInSlots[i] == null) {
                throw error("Empty rotor slot!");
            }
        }
        for (int j = 0; j < rotors.length - numPawls(); j++) {
            if (j < (rotors.length - numPawls())
                    && (rotorInSlots[j] instanceof MovingRotor)) {
                throw error("Should be fixed rotor");
            }
        }
        if (!(rotorInSlots[0] instanceof Reflector)) {
            throw error("The first rotor should be a reflector");
        }
        if (count != numPawls()) {
            throw error("Wrong number of moving rotors inserted");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() > numRotors() - 1) {
            throw error("not enough rotors inserted");
        }
        for (int i = 0; i < setting.length(); i++) {
            if (!_alphabet.contains(setting.charAt(i))) {
                throw error("Setting not in alphabet");
            }
            rotorInSlots[i + 1].set(setting.charAt(i));

        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugBoard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        Boolean[] tracker = new Boolean[numRotors()];
        Arrays.fill(tracker, Boolean.TRUE);
        for (int i = numRotors() - numPawls(); i < numRotors() - 1; i++) {
            if (i == numRotors() - 2 && rotorInSlots[i + 1].atNotch()
                    && !rotorInSlots[i].atNotch()) {
                rotorInSlots[i].advance();
                moveTracker[i] = Boolean.TRUE;
                tracker[i] = Boolean.FALSE;
            } else if (rotorInSlots[i - 1].rotates()
                    && rotorInSlots[i].atNotch()) {
                if (tracker[i]) {
                    rotorInSlots[i].advance();
                    tracker[i] = Boolean.FALSE;
                }
                if (tracker[i - 1]) {
                    rotorInSlots[i - 1].advance();
                    tracker[i] = Boolean.FALSE;
                }
            }
        }
        if (tracker[numRotors() - 1]) {
            rotorInSlots[numRotors() - 1].advance();
        }
        c = _plugBoard.permute(c);
        for (int x = rotorInSlots.length - 1; x >= 0; x--) {
            c = rotorInSlots[x].convertForward(c);
        }
        for (int y = 1; y < rotorInSlots.length; y++) {
            c = rotorInSlots[y].convertBackward(c);
        }
        c = _plugBoard.invert(c);
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            result = result + _alphabet.toChar(
                    convert(_alphabet.toInt(msg.charAt(i))));
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
}
