package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Victor Shi
 */
class Permutation {



    /** Cycles of this permutation.*/
    private String _cycle;

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, enigma.Alphabet alphabet) {
        _alphabet = alphabet;
        _cycle = cycles;
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycle = _cycle + " " + cycle;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        Character c = _alphabet.toChar(wrap(p));
        if (!_cycle.contains(c.toString())) {
            return wrap(p);
        }
        int index = _cycle.indexOf(c.toString());
        Character result;
        if (_cycle.charAt(index + 1) == ')') {
            result = _cycle.charAt(index);
            while (_cycle.charAt(index - 1) != '(') {
                index -= 1;
                result = _cycle.charAt(index);
            }
        } else {
            result = _cycle.charAt(index + 1);
        }
        return _alphabet.toInt(result);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int x = wrap(c);
        Character ch = _alphabet.toChar(x);
        if (!_cycle.contains(ch.toString())) {
            return x;
        }
        int index = _cycle.indexOf(ch.toString());
        Character result;
        if (_cycle.charAt(index - 1) == '(') {
            result = _cycle.charAt(index);
            while (_cycle.charAt(index + 1) != ')') {
                index += 1;
                result = _cycle.charAt(index);
            }
        } else {
            result = _cycle.charAt(index - 1);
        }
        return _alphabet.toInt(result);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _alphabet.toChar(permute(_alphabet.toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return _alphabet.toChar(invert(_alphabet.toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        _cycle = _cycle.substring(1, _cycle.length() - 1);
        return !_cycle.contains("(");
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

}
