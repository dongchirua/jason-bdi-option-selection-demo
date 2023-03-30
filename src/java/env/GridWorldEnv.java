package env;

import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.Environment;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;

import java.awt.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GridWorldEnv extends Environment {

    public static final int GRID_SIZE = 5;
    public static final int FINISH_LINE = 16; // finsh line code in grid model

    private static final boolean SHOW_VIEW = true;

    private GridModel model;
    private GridView view;
    Location finishline = new Location(GRID_SIZE - 1, GRID_SIZE - 1);

    static Logger logger = Logger.getLogger(GridWorldEnv.class.getName());

    @Override
    public void init(String[] args) {
        model = new GridModel();

        if (args.length == 1 && args[0].equals("gui")) {
            view  = new GridView(model);
            model.setView(view);
        }
        clearPercepts(); // clear the percepts of the agents
        updatePercepts();
    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        logger.log(Level.INFO, "["+ag+"] doing: "+action);
        boolean result = false;
        if (action.getFunctor().equals("move")){
            String direction = action.getTerm(0).toString();
            result = move(direction);
        }
        return result;
    }

    public boolean move(String move) {
        try {
            if (SHOW_VIEW) {
                Thread.sleep(100);
            }
        } catch (Exception e) {}


        try {
            if (move.equals("right")) {
                model.right();
            } else if (move.equals("left")) {
                model.left();
            } else if (move.equals("up")) {
                model.up();
            } else if (move.equals("down")) {
                model.down();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        updatePercepts();
        return true;
    }

    void updateObsProperty(String name, Term... terms) {
        Literal p = ASSyntax.createLiteral(name, terms);
        addPercept(p);
    }
    void updateObsProperty(String name) {
        Literal p = ASSyntax.createLiteral(name);
        addPercept(p);
    }
    boolean hasObsProperty(String name) {
        Literal p = ASSyntax.createLiteral(name);
        return  containsPercept(p);
    }
    void removeObsProperty(String name) {
        Literal p = ASSyntax.createLiteral(name);
        removePercept(p);
    }

    void updatePercepts() {
        Location agentLocation = model.getAgPos(0);
        updateObsProperty("pos", ASSyntax.createNumber(agentLocation.x),
                ASSyntax.createNumber(agentLocation.y));

        if (model.hasObject(FINISH_LINE, agentLocation)) {
            if (!hasObsProperty("finishline"))
                updateObsProperty("finishline");
            model.reset();
            Location newAgentLocation = model.getAgPos(0);
            updateObsProperty("pos", ASSyntax.createNumber(newAgentLocation.x),
                    ASSyntax.createNumber(newAgentLocation.y));
        } else {
            try {
                removeObsProperty("finishline");
            } catch (IllegalArgumentException e) {}
        }
    }

    class GridModel extends GridWorldModel {
        Random rnd = new Random();

        private GridModel() {
            super(GRID_SIZE, GRID_SIZE, 2);
            add(FINISH_LINE, finishline);
            reset();
        }

        void right() throws Exception {
            Location r1 = getAgPos(0);
            if (r1.x < GRID_SIZE - 1)
                r1.x++;
            setAgPos(0, r1);
        }

        void left() throws Exception {
            Location r1 = getAgPos(0);
            if (r1.x > 0)
                r1.x--;
            setAgPos(0, r1);
        }

        void up() throws Exception {
            Location r1 = getAgPos(0);
            if (r1.y > 0)
                r1.y--;
            setAgPos(0, r1);
        }

        void down() throws Exception {
            Location r1 = getAgPos(0);
            if (r1.y < GRID_SIZE - 1)
                r1.y++;
            setAgPos(0, r1);
        }

        private void reset() {
            int x = 0;
            int y = 0;
            setAgPos(0, x, y);
        }

    }

    class GridView extends GridWorldView {

        private static final long serialVersionUID = 1L;

        public GridView(GridModel model) {
            super(model, "Grid World", 600);
            defaultFont = new Font("Arial", Font.BOLD, 18);
            setVisible(true);
            repaint();
        }

        @Override
        public void draw(Graphics g, int x, int y, int object) {
            switch (object) {
                case GridWorldEnv.FINISH_LINE:
                    drawFinishLine(g, x, y);
                    break;
            }
        }

        @Override
        public void drawAgent(Graphics g, int x, int y, Color c, int id) {
            String label = "A";
            c = Color.blue;
            super.drawAgent(g, x, y, c, -1);
            if (id == 0) {
                g.setColor(Color.black);
            }
            super.drawString(g, x, y, defaultFont, label);
            repaint();
        }

        public void drawFinishLine(Graphics g, int x, int y) {
            super.drawObstacle(g, x, y);
            g.setColor(Color.white);
            drawString(g, x, y, defaultFont, "F");
        }

    }
}
