package eecs1510.Game;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Created by nathan on 2/17/15
 */
public class OptionsParser {

    public interface SwitchOption{
        public void apply();
    }

    private HashMap<String, Consumer<String>> options;
    private HashMap<String, SwitchOption> switchOptions;

    public OptionsParser(){
        options = new HashMap<>();
        switchOptions = new HashMap<>();
    }

    public OptionsParser add(String name, Consumer<String> func){
        options.put(name, func);
        return this;
    }

    public OptionsParser addSwitch(String name, SwitchOption callback){
        switchOptions.put(name, callback);
        return this;
    }

    public void parse(String...args){
        for(int i=0; i<args.length; i++){
            String cmd = stripIndicator(args[i]);
            if(options.containsKey(cmd) && i < args.length){
                String value = args[++i];
                options.get(cmd).accept(value);
            }else if(switchOptions.containsKey(cmd)){
                switchOptions.get(cmd).apply();
            }
        }
    }

    public String stripIndicator(String cmd){
        if(cmd.startsWith("-") || cmd.startsWith("/")){
            return stripIndicator(cmd.substring(1));
        }
        return cmd;
    }

}
