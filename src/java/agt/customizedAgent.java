package agt;

import jason.asSemantics.Agent;
import jason.asSemantics.Option;

import java.util.List;
import java.util.Random;

public class customizedAgent extends Agent {

    Random random = new Random();

    @Override
    public Option selectOption(List<Option> options) {
        if (options != null && !options.isEmpty()) {
            int randomNumber = random.nextInt(options.size());
            return options.get(randomNumber);
        } else {
            return null;
        }
    }
}
