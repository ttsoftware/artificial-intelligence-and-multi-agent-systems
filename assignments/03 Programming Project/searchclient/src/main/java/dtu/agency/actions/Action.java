package dtu.agency.actions;

import java.io.Serializable;

public interface Action<T extends Enum<T>> extends Serializable {
    T getType();
}
