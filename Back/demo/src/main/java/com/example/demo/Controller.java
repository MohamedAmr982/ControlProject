package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
public class Controller {

    @Autowired
    public SFGService graphService;

    @Autowired
    public RSService routhService;


    @PostMapping(
            value = "/getNodes"
    )
    public void getNodes(@RequestBody ArrayList<String> nodes){
        System.out.println(nodes);
        graphService.setNodes(nodes);
    }


    @PostMapping(
            value = "/getEdges"
    )
    public void getEdges(@RequestBody ArrayList<Edge> edges){
        System.out.println(edges);
        graphService.setEdges(edges);
    }

    @GetMapping(value="/get")
    public List<String> solveGraph(){
        graphService.solveGraph();
        return List.of("Finished");
    }


    @GetMapping(value="/get/forwardPath")
    public List<List<Edge>> getForwardPaths(){
        return graphService.getPaths();
    }

    @GetMapping(value="/get/loop")
    public List<List<Edge>> getLoops(){
        return graphService.getLoops();
    }

    @GetMapping(value="/get/nonTouchingPairsComb")
    public HashMap<Integer, List<List<List<Edge>>>> getNonTouchingPairsComb(){
        return graphService.getNonTouchingLoops();
    }

    @GetMapping(value="/get/pathDelta")
    public List<Double> getPathDeltas(){
        return graphService.getDeltas();
    }

    @GetMapping(value="/get/delta")
    public double getDelta(){
        return graphService.getDelta();
    }

    @GetMapping(value="/get/transferFunction")
    public double getTransferFunction(){
        return graphService.getTF();
    }

    @GetMapping(value="/get/indexComb")
    public HashMap<Integer, List<List<Integer>>> getIndexComb(){
        return graphService.getLoopPairIndexTable();
    }

    @PostMapping(
            value="/getEqn"
    )
    public RSService.Pair getEqn(@RequestBody String eqn){
        System.out.println(eqn);
        return routhService.solve(eqn);
    }
    @PostMapping(
            value="/getTable"
    )
    public double[][] getTable(@RequestBody String eqn){
        System.out.println(eqn);
        return routhService.getTable(eqn);
    }
}