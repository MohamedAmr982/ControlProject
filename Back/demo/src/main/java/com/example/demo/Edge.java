package com.example.demo;

public class Edge {
    private final String from;
    private final String to;
    private final double weight;
    private final int id;
    private static int nextId = 0;


    public Edge(String fromNode, String toNode, double weight) {
        this.from = fromNode;
        this.to = toNode;
        this.weight = weight;
        this.id = nextId;
        nextId++;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }

    public String toString(){
        return "{from: "+this.from +", to: "+this.to +", weight: "+this.weight+"}\n";
    }


    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Edge){
            Edge e = (Edge) obj;
           return this.id == e.id;
        }
        return false;
    }
}
