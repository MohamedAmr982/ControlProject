package com.example.demo;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GraphService {
    private ArrayList<String> nodes;
    private ArrayList<Edge> edges;

    public ArrayList<String> getNodes() {
        return this.nodes;
    }

    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    public void setNodes(ArrayList<String> nodes){
        this.nodes = nodes;
    }

    public void setEdges(ArrayList<Edge> edges){
        this.edges = edges;
    }



}
