package dtu.agency.board;

public class Box extends BoardObject {
    public Box(String label) {
        super(label);
    }

    @Override
    public BoardCell getType() {
        return BoardCell.BOX;
    }

    public Box(Box box) {
        super(box.getLabel());
    }
}
