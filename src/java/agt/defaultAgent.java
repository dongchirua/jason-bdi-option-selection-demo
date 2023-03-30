package agt;

import jason.asSemantics.Agent;
import jason.asSemantics.Option;

import java.util.*;

public class defaultAgent extends Agent {

    @Override
    public Option selectOption(List<Option> options) {
        if (options != null && !options.isEmpty()) {
            return options.remove(0);
        } else {
            return null;
        }
    }
}
