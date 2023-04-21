package com.example.elo;

public class Elo {

    private float elo;
    private float refereeElo;

    //Constructor used by new players where the elo = 0
    public Elo(){
        this.elo = 0;
        this.refereeElo = 0;
    }

    public float getElo() {
        return this.elo;
    }

    public void setElo(float elo) {
        this.elo = elo;
    }


    public float getRefereeElo(){return this.refereeElo;}

    public void increaseRefereeElo(){this.refereeElo+=0.01;}

    public void setRefereeElo(float elo){this.refereeElo=elo;}
}
