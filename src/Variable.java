import java.util.ArrayList;
import java.util.HashMap;

public class Variable {
    /**
     * The Variable Class will hold five variables:
     * 1) _name: The name of the variable.
     * 2) _parents: An ArrayList of type Variable representing the current variables parents.
     * 3) _children: An ArrayList of type Variable representing the current variables children.
     * 4) _outcomes: An ArrayList of type String representing the current variables outcome probabilities.
     * 5) _cpt: The current variables CPT.
     * 6)
     */
    private String _name;
    private ArrayList<Variable> _parents;
    private ArrayList<Variable> _children;
    private ArrayList<String> _outcomes;
    public CPT _cpt;
    public int _numOfNeighbors;
    public int _numOfFactors;
    public int _max_rows;

    /**
     * Constructor for the Variable.
     *
     * @param name - Name of the event.
     */
    public Variable(String name) {
        _name = name;
        _parents = new ArrayList<>();
        _children = new ArrayList<>();
        _outcomes = new ArrayList<>();
        _cpt = new CPT();
        _numOfNeighbors = 0;
        _numOfFactors = 0;
        _max_rows = 0;
    }

    /**
     * Copy Constructor for the Variable:
     *
     * @param other - The variable to be copied from.
     */
    public Variable(Variable other) {
        _name = other._name;

        _parents = new ArrayList<>();
        _parents.addAll(other._parents);

        _children = new ArrayList<>();
        _children.addAll(other._children);

        _outcomes = new ArrayList<>();
        _outcomes.addAll(other._outcomes);

        _cpt = new CPT(other._cpt);

        _numOfNeighbors = 0;
        _numOfFactors = 0;
        _max_rows = 0;
    }

    /**
     * Getters:
     */
    public String getName() {
        return _name;
    }

    public ArrayList<Variable> getParents() {
        return _parents;
    }

    public ArrayList<Variable> getChildren() {
        return _children;
    }

    public ArrayList<String> getOutcomes() {
        return _outcomes;
    }

    public CPT getCPT() {
        return _cpt;
    }

    public int get_numOfNeighbors() {
        return _numOfNeighbors;
    }

    public int get_numOfFactors() {
        return _numOfFactors;
    }

    public int get_max_rows() {
        return _max_rows;
    }

    /**
     * This function adds a variable to the current variables parent list.
     *
     * @param p - parent variable to be added.
     */
    public void addParent(Variable p) {
        this._parents.add(p);
    }

    /**
     * This function adds a variable to the current variables child list.
     *
     * @param c - child variable to be added.
     */
    public void addChild(Variable c) {
        this._children.add(c);
    }


    /**
     * This function adds an outcome to the current variables outcome list.
     *
     * @param out - outcome to be added.
     */
    public void addOutcomes(String out) {
        this._outcomes.add(out);
    }

    /**
     * @param values - the values of the probability from the xml file.
     *               Each index of the String array represents a row of the CPT table.
     */
    public void initCPT(String[] values) {
        //initializing the cpt row size:
        _cpt._row_size = values.length;
        //looping over the values and adding them to the CPT:
        for (int i = 0; i < values.length; i++) {
            //Creating a CPT row which is a HashMap
            HashMap<String, String> rows = new HashMap<>();
            rows.put(_name, _outcomes.get(i % _outcomes.size()));
            //Updating the outcomes according to the parent variables:
            int j = _parents.size() - 1;
            int outcome_size = _outcomes.size();
            while (j >= 0) {
                Variable temp = _parents.get(j);
                rows.put(temp.getName(), temp.getOutcomes().get((i / outcome_size % temp.getOutcomes().size())));
                j--;
                outcome_size *= temp.getOutcomes().size();
            }
            //adding the probability to the row:
            rows.put("Prob", values[i]);
            //adding the row to the final CPT:
            _cpt._cpt_table.add(rows);
        }
    }

    /**
     * Recursion Method to check if a current variable is a descendant of a given variable.
     *
     * @param other - potential ancestor variable.
     * @return - True or false.
     */
    public boolean isAncestor(Variable other) {
        if (other.getChildren().contains(this))
            return true;
        for (int i = 0; i < other.getChildren().size(); i++) {
            if (isAncestor(other.getChildren().get(i)))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder var = new StringBuilder("\nVariable Name: " + this._name);
        var.append("\nParents: [");
        for (Variable p : this._parents)
            var.append(p.getName()).append(", ");
        if (this._parents.size() != 0)
            var = new StringBuilder(var.substring(0, var.length() - 2));
        var.append("]\nChildren: [");
        for (Variable c : this._children)
            var.append(c.getName()).append(", ");
        if (this._children.size() != 0)
            var = new StringBuilder(var.substring(0, var.length() - 2));
        var.append("]\nOutcomes: ").append(this._outcomes);
        var.append("\n\tCPT: ").append(this._cpt.toString());
        return var.toString();
    }

//    /*****/
//    @Override
//    public int compareTo(Variable other) {
//        if (this._numOfNeighbors > other._numOfNeighbors) return 1;
//        else if (this._numOfNeighbors < other._numOfNeighbors) return -1;
//        return 1;
//    }
}
