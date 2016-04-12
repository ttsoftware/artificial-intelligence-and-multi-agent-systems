package dtu.agency.board;

public class Box extends BoardObject {
    public Box(String label) {
        super(label);
    }

    public Box(Box box) {
        super(box.getLabel());
    }
}
