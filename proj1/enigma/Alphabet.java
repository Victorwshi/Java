package enigma;

import java.util.ArrayList;
import static enigma.EnigmaException.*;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Victor Shi
 */
class Alphabet {

    /** The arraylist of all characters in alphabet. */
    private ArrayList<Character> _chars = new ArrayList<Character>();

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        for (int i = 0; i < chars.length(); i++) {
            _chars.add(chars.charAt(i));
        }
    }
    /** Return the arraylist of all characters in alphabet. */
    ArrayList<Character> getChar() {
        return _chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.size();
    }

    /** Returns true if preprocess(CH) is in this alphabet. */
    boolean contains(char ch) {
        return _chars.contains(ch);
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return _chars.get(index);
    }

    /** Returns the index of character preprocess(CH), which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (!_chars.contains(ch)) {
            throw error("Can't recognize the character");
        } else {
            return _chars.indexOf(ch);
        }
    }
    /** Change the alphabet in the ring setting C. */
    void modAlpha(Character c) {
        if (!_chars.contains(c)) {
            throw error("Ring setting is not in alphabet");
        } else {
            ArrayList<Character> copyChars = new ArrayList<Character>();
            for (int x = toInt(c); x < _chars.size(); x++) {
                copyChars.add(_chars.get(x));
            }
            for (int y = 0; y < toInt(c); y++) {
                copyChars.add(_chars.get(y));
            }
            _chars = copyChars;
        }
    }
}
