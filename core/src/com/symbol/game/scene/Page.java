package com.symbol.game.scene;

import com.badlogic.gdx.scenes.scene2d.Actor;

public interface Page {

    void reset();

    void notifySeen();

    boolean hasSeen();

    Actor getActor();

}
