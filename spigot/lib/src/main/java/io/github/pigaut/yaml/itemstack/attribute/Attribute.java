package io.github.pigaut.yaml.itemstack.attribute;

public class Attribute {

    private final String attribute;
    private final String name;
    private final double amount;
    private final String slot;
    private final int operation;

    public Attribute(String attribute, String name, double amount, String slot, int operation) {
        this.attribute = attribute;
        this.name = name;
        this.amount = amount;
        this.slot = slot;
        this.operation = operation;
    }

    public String getAttributeType() {
        return attribute;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public String getSlot() {
        return slot;
    }

    public int getOperation() {
        return operation;
    }
}
