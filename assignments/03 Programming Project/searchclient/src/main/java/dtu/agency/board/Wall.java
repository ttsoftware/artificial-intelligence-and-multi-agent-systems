package dtu.agency.board;

public class Wall extends BoardObject {

    public Wall(String label) {
        super(label);
    }

    @Override
    public BoardCell getType() {
        return BoardCell.WALL;
    }

    public Wall(Wall obj) {
        super(obj.getLabel());
    }
}
