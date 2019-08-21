package com.googlecode.junittoolbox;


public abstract class RunnableAssert {

    private final String description;

    protected RunnableAssert(String description) {
        this.description = description;
    }

    public abstract void run() throws Exception;

    @Override
    public String toString() {
        return "RunnableAssert(" + description + ")";
    }
}
