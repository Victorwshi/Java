package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Victor Shi
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }
        _config = getInput(args[0]);
        _config2 = getInput(args[0]);
        _newAlpha = _config2.nextLine().trim();
        if (args.length > 1) {
            _input = getInput(args[1]);
            _input2 = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }
        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine newMachine = readConfig();
        int count = 0;
        String inpLine;
        Character first = _input2.nextLine().charAt(0);
        if (first != '*') {
            throw error("Input wrong format (no *)");
        }
        while (_input.hasNext()) {
            inpLine = _input.nextLine();
            Scanner scanInp = new Scanner(inpLine);
            if (inpLine.isEmpty()) {
                _output.println();
            } else if (inpLine.charAt(0) == '*') {
                setUp(newMachine, inpLine.substring(1).trim());
                count++;
            } else {
                String temp = "";
                while (scanInp.hasNext()) {
                    temp += scanInp.next();
                }
                if (!temp.equals("")) {
                    printMessageLine(newMachine.convert(temp));
                }
            }
        }
        if (_input.hasNextLine() && _input.nextLine().trim().isEmpty()) {
            _output.println();
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String newAlpha = _config.nextLine().trim();
            String rotorAndPawl = _config.nextLine().trim();
            _alphabet = new Alphabet(newAlpha);
            Character rotor = rotorAndPawl.charAt(0);
            int rotorNum = rotor.getNumericValue(rotor);
            Character pawl = rotorAndPawl.charAt(2);
            int pawlNum = rotor.getNumericValue(pawl);
            if (pawlNum >= rotorNum) {
                throw error("Wrong number of pawls");
            }
            ArrayList<Rotor> allRotors = new ArrayList<Rotor>();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            if (rotorNum > allRotors.size()) {
                throw error("Not enough available rotors!");
            }
            return new Machine(_alphabet, rotorNum, pawlNum, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            ArrayList<String> rotorInfo = new ArrayList<String>();
            rotorInfo.add(_config.next());
            rotorInfo.add(_config.next());
            String p = "";
            String temp = _config.next();
            while (temp.contains("(")) {
                p += temp;
                if (!temp.contains(")")) {
                    throw error("Illegal format");
                }
                if (_config.hasNext() && _config.hasNext("\\(.+\\)")) {
                    temp = _config.next();
                } else {
                    break;
                }
            }
            rotorInfo.add(p);
            String rotorType = rotorInfo.get(1);
            Alphabet rotorAlphabet = new Alphabet(_newAlpha);
            Permutation perm = new Permutation(rotorInfo.get(2), rotorAlphabet);
            if (rotorType.charAt(0) == 'M') {
                if (rotorType.length() < 1) {
                    throw error("Rotor missing notch!");
                }
                return new MovingRotor(rotorInfo.get(0), perm,
                        rotorType.substring(1));
            } else if (rotorType.equals("N")) {
                return new FixedRotor(rotorInfo.get(0), perm);
            } else if (rotorType.equals("R")) {
                return new Reflector(rotorInfo.get(0), perm);
            } else {
                throw error("Rotor has wrong format!");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        StringTokenizer tokens = new StringTokenizer(settings);
        Scanner scanInput = new Scanner(settings);
        String[] rotors = new String[M.numRotors()];
        String plugPerm = "";
        for (int i = 0; i < M.numRotors(); i++) {
            String temp = scanInput.next();
            rotors[i] = temp;
        }
        M.insertRotors(rotors);
        String rotorSetting = scanInput.next();
        if (rotorSetting.length() != M.numRotors() - 1) {
            throw error("Wrong length of setting");
        }
        if (scanInput.hasNext() && !scanInput.hasNext("\\(.+\\)")) {
            String ringSetting = scanInput.next();
            if (ringSetting.length() != M.numRotors() - 1) {
                throw error("Wrong length of ring setting");
            }
            for (int x = 1; x < M.getRotorInSlots().length; x++) {
                M.getRotorInSlots()[x].permutation().
                        alphabet().modAlpha(ringSetting.charAt(x - 1));
            }
        }
        M.setRotors(rotorSetting);
        if (scanInput.hasNext("\\(.+\\)")) {
            while (scanInput.hasNext()) {
                plugPerm += scanInput.next();
            }
        }
        if (plugPerm != null) {
            M.setPlugboard(new Permutation(plugPerm, _alphabet));
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int msgNum = msg.length() / 5;
        for (int i = 0; i < msgNum; i++) {
            if (!msg.substring((i * 5) + 5).equals("")) {
                _output.print(msg.substring(i * 5, (i * 5) + 5) + " ");
            } else {
                _output.print(msg.substring(i * 5, (i * 5) + 5));
            }
        }
        _output.println(msg.substring(msgNum * 5));
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;
    /** A copy of input messages. */
    private Scanner _input2;
    /** A copy of configuration. */
    private Scanner _config2;

    /** Source of machine configuration. */
    private Scanner _config;

    /** The alphabet of configuration. */
    private String _newAlpha;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
