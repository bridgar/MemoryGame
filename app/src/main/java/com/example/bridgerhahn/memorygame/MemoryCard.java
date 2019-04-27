package com.example.bridgerhahn.memorygame;

class MemoryCard {

    private int identity;
    private boolean isFaceUp = false;

    MemoryCard(int identity) {
        this.identity = identity;
    }

    int getIdentity() {
        return identity;
    }

    boolean isFaceUp() {
        return isFaceUp;
    }

    void flipCard() {
        isFaceUp = !isFaceUp;
    }


}
