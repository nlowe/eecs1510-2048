package eecs1510.Game;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Created by nathan on 2/17/15
 *
 * A simple, lambda-based options parser
 *
 * Options may be simple switches or may provide an optional value. You must
 * cast the value from string to the desired data type.
 *
 * Options are not guaranteed to be called only once!
 */
public class OptionsParser {

    public interface SwitchOption {
        public void apply();
    }

    private HashMap<String, Consumer<String>> options;
    private HashMap<String, SwitchOption> switchOptions;

    public OptionsParser() {
        options = new HashMap<>();
        switchOptions = new HashMap<>();
    }

    /**
     * Register a new option with the parser
     *
     * @param name The name of the option
     * @param func The function to execute when the option is found. Takes one argument of type string and returns void
     * @return The option parser the option was added to. Useful for chaining together calls
     */
    public OptionsParser add(String name, Consumer<String> func) {
        options.put(name, func);
        return this;
    }

    /**
     * Register a new switch option with the parser
     *
     * @param name The name of the switch option
     * @param callback The function to execute when the switch option is found. Takes no arguments and returns void
     * @return The option parser the option was added to. Useful for chaining together calls
     */
    public OptionsParser addSwitch(String name, SwitchOption callback) {
        switchOptions.put(name, callback);
        return this;
    }

    /**
     * Parses the given string array, invoking callbacks when registered options are encountered
     *
     * @param args The array of arguments to parse
     */
    public void parse(String... args) {
        for (int i = 0; i < args.length; i++) {
            String cmd = stripIndicator(args[i]);

            if (options.containsKey(cmd) && i < args.length) {
                String value = args[++i];
                options.get(cmd).accept(value);
            } else if (switchOptions.containsKey(cmd)) {
                switchOptions.get(cmd).apply();
            }
        }
    }

    /**
     * Removes leading '-' or '/' characters from a given string recursively
     *
     * @param cmd the string to remove command indicators from
     * @return a string with all leading command indicators stripped from it
     */
    public String stripIndicator(String cmd) {
        if (cmd.startsWith("-") || cmd.startsWith("/")) {
            return stripIndicator(cmd.substring(1));
        }

        return cmd;
    }

}
