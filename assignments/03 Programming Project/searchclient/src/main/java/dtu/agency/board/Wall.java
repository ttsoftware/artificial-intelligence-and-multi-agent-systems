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

    @Override
    public boolean equals(Object object) {
        if (object instanceof Wall) {
            return super.equals(object);
        }
        return false;
    }
}
