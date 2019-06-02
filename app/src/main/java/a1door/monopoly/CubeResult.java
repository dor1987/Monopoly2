package a1door.monopoly;

import a1door.monopoly.Entities.ElementEntity;

public class CubeResult {
    private ElementEntity destCity;
    private int dice1;
    private int dice2;

    public CubeResult() {}

    public ElementEntity getDestCity() {
        return destCity;
    }

    public void setDestCity(ElementEntity destCity) {
        this.destCity = destCity;
    }

    public int getDice1() {
        return dice1;
    }

    public void setDice1(int dice1) {
        this.dice1 = dice1;
    }

    public int getDice2() {
        return dice2;
    }

    public void setDice2(int dice2) {
        this.dice2 = dice2;
    }
}
