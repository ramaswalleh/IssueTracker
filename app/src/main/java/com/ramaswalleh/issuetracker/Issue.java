package com.ramaswalleh.issuetracker;

class Issue {
    private int id, waterPointId;
    private String waterPointName;
    private String casePadlock, tankValve, baseStandBolt;
    private int rating;

    public Issue(int id, int waterPointId, String waterPointName, String casePadlock, String tankValve, String baseStandBolt, int rating) {
        this.id = id;
        this.waterPointId = waterPointId;
        this.waterPointName = waterPointName;
        this.casePadlock = casePadlock;
        this.tankValve = tankValve;
        this.baseStandBolt = baseStandBolt;
        this.rating = rating;
    }

    public int getId(){
        return id;
    }

    public int getWaterPointId(){
        return waterPointId;
    }

    public String getWaterPointName(){
        return waterPointName;
    }

    public String getCasePadlock(){
        return casePadlock;
    }

    public String getTankValve(){
        return tankValve;
    }

    public String getBaseStandBolt(){
        return baseStandBolt;
    }

    public int getRating(){
        return rating;
    }
}