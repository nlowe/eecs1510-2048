package eecs1510.Game;

import java.util.ArrayList;
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
public class OptionsParser
{

    private class Option
    {
        protected final String abbreviation;
        protected final String name;
        protected final String help;
        protected final Consumer<String> callback;

        protected Option(String abbreviation, String name, String help, Consumer<String> callback)
        {
            this.abbreviation = abbreviation;
            this.name = name;
            this.help = help;
            this.callback = callback;
        }
    }

    private class SwitchOption
    {
        protected final String name;
        protected final String help;
        protected final SwitchAction callback;

        protected SwitchOption(String name, String help, SwitchAction callback)
        {
            this.name = name;
            this.help = help;
            this.callback = callback;
        }
    }

    public interface SwitchAction
    {
        public void apply();
    }

    private final ArrayList<Option> options;
    private final ArrayList<SwitchOption> switchOptions;

    public OptionsParser()
    {
        options = new ArrayList<>();
        switchOptions = new ArrayList<>();

        addSwitch("help", "Print this help message and exit", () -> {
            System.out.println(getHelp());
            System.exit(0);
        });
    }

    public OptionsParser add(String name, String help, Consumer<String> callback)
    {
        return add(null, name, help, callback);
    }

    /**
     * Register a new option with the parser
     *
     * @param name The name of the option
     * @param callback The function to execute when the option is found. Takes one argument of type string and returns void
     * @return The option parser the option was added to. Useful for chaining together calls
     */
    public OptionsParser add(String abbreviation, String name, String help, Consumer<String> callback)
    {
        options.add(new Option(abbreviation, name, help, callback));
        return this;
    }

    /**
     * Register a new switch option with the parser
     *
     * @param name The name of the switch option
     * @param callback The function to execute when the switch option is found. Takes no arguments and returns void
     * @return The option parser the option was added to. Useful for chaining together calls
     */
    public OptionsParser addSwitch(String name, String help, SwitchAction callback)
    {
        switchOptions.add(new SwitchOption(name, help, callback));
        return this;
    }

    /**
     * Parses the given string array, invoking callbacks when registered options are encountered
     *
     * @param args The array of arguments to parse
     */
    public void parse(String... args)
    {
        for (int i = 0; i < args.length; i++)
        {
            String cmd = stripIndicator(args[i]);

            if(i < args.length-1)
            {
                String next = args[i + 1];

                options.stream().filter(
                        (o) -> (o.abbreviation != null && o.abbreviation.equals(cmd)) || o.name.equals(cmd)
                ).forEach(
                        (o) -> o.callback.accept(next)
                );
            }

            switchOptions.stream().filter(
                    (o) -> o.name.equals(cmd)
            ).forEach(
                    (o) -> o.callback.apply()
            );
        }
    }

    /**
     * Removes leading '-' or '/' characters from a given string recursively
     *
     * @param cmd the string to remove command indicators from
     * @return a string with all leading command indicators stripped from it
     */
    public String stripIndicator(String cmd)
    {
        if (cmd.startsWith("-") || cmd.startsWith("/"))
        {
            return stripIndicator(cmd.substring(1));
        }

        return cmd;
    }

    public String getHelp()
    {
        StringBuilder sb = new StringBuilder();

        for(Option opt : options)
        {
            sb.append("\t-").append(opt.abbreviation != null ? opt.abbreviation + ", --": "--").append(opt.name).append(" [VALUE]\t\t\t").append(opt.help);
            sb.append("\n");
        }

        for(SwitchOption opt : switchOptions)
        {
            sb.append("\t--").append(opt.name).append("\t\t\t").append(opt.help);
            sb.append("\n");
        }

        return sb.toString();
    }

}
