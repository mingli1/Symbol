package com.symbol.game.map;

import com.badlogic.gdx.math.Rectangle;

public class MapObject {

    private Rectangle bounds;
    private MapObjectType type;
    private int damage;

    public MapObject(Rectangle bounds, MapObjectType type, int damage) {
        this.bounds = bounds;
        this.type = type;
        this.damage = damage;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public MapObjectType getType() {
        return type;
    }

    public void setType(MapObjectType type) {
        this.type = type;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

}