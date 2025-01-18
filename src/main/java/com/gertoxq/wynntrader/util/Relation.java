package com.gertoxq.wynntrader.util;

public enum Relation {
    GREATER(">"),
    GREATER_OR_EQUALS(">="),
    SMALLER("<"),
    SMALLER_OR_EQUALS("<="),
    EQUALS("=");

    public final String sign;

    Relation(String string) {
        this.sign = string;
    }

    public static Relation from(String string) {
        return switch (string) {
            case "=" -> EQUALS;
            case ">" -> GREATER;
            case ">=" -> GREATER_OR_EQUALS;
            case "<" -> SMALLER;
            case "<=" -> SMALLER_OR_EQUALS;
            case null, default -> throw new IllegalArgumentException("Couldn't cast string to relation (=, >, ...)");
        };
    }

    public boolean compare(double value, double than) {
        return switch (this) {
            case EQUALS -> value == than;
            case GREATER -> value > than;
            case GREATER_OR_EQUALS -> value >= than;
            case SMALLER -> value < than;
            case SMALLER_OR_EQUALS -> value <= than;
        };
    }
}
