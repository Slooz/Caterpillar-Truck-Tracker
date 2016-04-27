/*
 * Copyright 2016 Nathan Clark, Krzysztof Czelusniak, Michael Holwey, Dakota Leonard
 */

package edu.bradley.cattrucktracker;

class TruckState {
    private final BackEnd backEnd;

    private Type type = Type.UNKNOWN;

    private Boolean truckMoving;
    private Boolean truckBedVibrating;
    private Boolean truckBedRotating;
    private boolean truckLoaded = false;

    TruckState(BackEnd backEnd) {
        this.backEnd = backEnd;

        backEnd.sendTruckStateAndTime(type);
    }

    void setTruckMovingStateAndUpdate(Boolean truckMoving) {
        this.truckMoving = truckMoving;

        update();
    }

    void setTruckBedVibrating(Boolean truckBedVibrating) {
        this.truckBedVibrating = truckBedVibrating;
    }

    void setTruckBedRotating(boolean truckBedRotating) {
        this.truckBedRotating = truckBedRotating;
    }

    void update() {
        Type oldType = type;

        if (truckMoving == null || truckBedVibrating == null) {
            type = Type.UNKNOWN;
        } else {
            if (truckMoving) {
                type = Type.MOVING;
            } else if (type == Type.MOVING || type == Type.UNKNOWN) {
                type = Type.STOPPED;
            }

            if (type == Type.STOPPED && truckBedVibrating) {
                if (truckLoaded) {
                    type = Type.STATIC_DUMP;
                    truckLoaded = false;
                } else {
                    type = Type.LOADING;
                    truckLoaded = true;
                }
            }
        }

        if (oldType != type) {
            backEnd.sendTruckStateAndTime(type);
        }
    }

    enum Type {
        UNKNOWN, MOVING, STOPPED, LOADING, MOVING_DUMP, STATIC_DUMP
    }
}
