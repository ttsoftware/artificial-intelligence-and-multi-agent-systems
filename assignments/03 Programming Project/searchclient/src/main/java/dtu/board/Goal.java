package dtu.board;

public class Goal extends BoardObject {
    private int weight;

    public Goal(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
