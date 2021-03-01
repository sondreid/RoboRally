package inf112.skeleton.app.objects.Actors;

import inf112.skeleton.app.map.ProgramSheet;
import inf112.skeleton.app.enums.Direction;
import inf112.skeleton.app.objects.IObject;

public interface IActor extends IObject {

    /**
     * @return Directions actor is looking.
     */
    Direction getLookDirection();

    /**
     * @return actor's unique program sheet.
     */
    ProgramSheet getProgramSheet();

    /**
     * Change direction actor is looking.
     * @param direction
     */
    void setLookDirection(Direction direction);

    /**
     *
     * @param rotation: rotation direction. With or against clock
     */
    void rotate(Direction rotation);
}