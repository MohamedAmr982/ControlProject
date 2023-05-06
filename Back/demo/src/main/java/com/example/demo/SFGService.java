package com.example.demo;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SFGService {

    private ArrayList<String> nodes;
    private ArrayList<Edge> edges;

    private HashMap<String, List<Edge>> graph;

    private ArrayList<List<Edge>> paths;

    private List<List<Edge>> loops;

    private HashMap<Integer, List<List<Integer>>> loopPairIndexTable;

    private int[] loopsMasks;

    private int[] pathMasks;


    private HashMap<Integer, Double> pathGains = new HashMap<>();
    private HashMap<Integer, Double> loopGains = new HashMap<>();

    private List<Double> pathsDeltas = new ArrayList<>();
    private double delta;

    public SFGService(){
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.paths = new ArrayList<>();
        this.loops = new ArrayList<>();
        this.loopPairIndexTable = new HashMap<>();
    }

    private void reset(){
        this.paths = new ArrayList<>();
        this.loops = new ArrayList<>();
        this.loopPairIndexTable = new HashMap<>();
        this.graph = new HashMap<>();
        this.pathGains = new HashMap<>();
        this.loopGains = new HashMap<>();
        this.pathsDeltas = new ArrayList<>();
    }

    public ArrayList<List<Edge>> getPaths(){
        return this.paths;
    }
    public List<List<Edge>> getLoops(){
        return this.loops;
    }

    public HashMap<Integer, List<List<Integer>>> getLoopPairTable(){
        return this.loopPairIndexTable;
    }

    public List<Double> getDeltas(){
        return this.pathsDeltas;
    }

    public double getDelta(){
        return this.delta;
    }

    public double getTF(){
        double tf = 0;

        for(int i = 0; i < pathGains.size(); i++){
            tf+= pathGains.get(i)*pathsDeltas.get(i);
        }

        tf/=delta;

        return tf;
    }

    public HashMap<Integer, List<List<Integer>>> getLoopPairIndexTable() {
        return loopPairIndexTable;
    }

    private HashMap<String, List<Edge>> buildGraph(){
        HashMap<String, List<Edge>> g = new HashMap<>();

        nodes.forEach((node)->{
            g.put(node,
                    edges.stream().filter((edge)->
                            edge.getFrom().equals(node)).toList()
            );
        });
        return g;
    }




    public void getPaths(String current, HashMap<String, Boolean> visited, List<Edge> path){
        visited.put(current, true);
        List<Edge> edgeList = graph.get(current);
        if(current.equals(nodes.get(nodes.size()-1))){
            paths.add(path);
            return;
        }
        for(Edge e: edgeList){
            if(!visited.get(e.getTo())){
                path.add(e);
                getPaths(e.getTo(), new HashMap<>(visited), new ArrayList<>(path));
                path.remove(path.size()-1);
            }
        }
    }

    public void generatePaths(){
        HashMap<String, Boolean> visited = new HashMap<>();
        for(String node: nodes){
            visited.put(node, false);
        }

        getPaths(nodes.get(0), visited, new ArrayList<>());
    }



    public void addLoop(List<Edge> path, Edge startEdge){
//        System.out.println(path);
        LinkedList<Edge> loop = new LinkedList<>();

        loop.addFirst(startEdge);

        if(startEdge.getTo().equals(startEdge.getFrom())){
            if(!loops.contains(loop)){
                loops.add(loop);
            }
            return;
        }
        String startNode = startEdge.getFrom();
        String endNode = startEdge.getTo();
        int i = path.size();
        do{
            i--;
            loop.addFirst(path.get(i));
        }while(i > 0 && !path.get(i).getFrom().equals(endNode));
//        System.out.println(loop);
//        System.out.println("------------------------------------");


        for(List<Edge> detectedLoop: loops){
            boolean equal = true;
            for(Edge edge: detectedLoop){
                if(!loop.contains(edge)) {
                    equal = false;
                    break;
                }
            }
            if(equal){
                //do not add loop as it was previously added
                return;
            }
        }

        loops.add(loop);
    }

    public void generateLoops(String current, HashMap<String, Boolean> cycled, List<Edge> path){
        cycled.put(current, true);
        List<Edge> edgeList = graph.get(current);
        for(Edge e: edgeList){
            if(!cycled.get(e.getTo())){
                path.add(e);
                generateLoops(e.getTo(), new HashMap<>(cycled), new ArrayList<>(path));
                path.remove(path.size()-1);
            }else{
                addLoop(path, e);
            }
        }

    }

    public void generateLoops(){
        HashMap<String, Boolean> cycled = new HashMap<>();
        for(String node: nodes){
            cycled.put(node, false);
        }
        generateLoops(nodes.get(0), cycled, new ArrayList<>());
    }

    public int loopBitmask(List<Edge> loop){
        int bitmask = 0;
        for(Edge e: loop){
            bitmask = bitmask | (1 << nodes.indexOf(e.getFrom()));
        }
        return bitmask;
    }

    public int pathBitmask(List<Edge> path){
        int bitmask = 0;
        for(Edge e: path){
            bitmask = bitmask | (1 << nodes.indexOf(e.getFrom()));
            bitmask = bitmask | (1 << nodes.indexOf(e.getTo()));
        }
        return bitmask;
    }

    public int[] getLoopsAsMasks(){
        int[] bitmasks = new int[loops.size()];

        int i = 0;
        for(List<Edge> loop: loops){
            bitmasks[i] = loopBitmask(loop);
            i++;
        }

        return bitmasks;
    }

    public void initPathMasks(){
        int[] bitmasks = new int[paths.size()];

        int i = 0;
        for(List<Edge> path: paths){
            bitmasks[i] = this.pathBitmask(path);
            i++;
        }

        pathMasks = bitmasks;
    }

    public HashMap<Integer, List<List<Integer>>> getNonTouchingLoopsAsIndices(){
        HashMap<Integer, List<List<Integer>>> table = new HashMap<>();
        int[] bitmasks = getLoopsAsMasks();

        loopsMasks = bitmasks;

        int combinationsMask = 0;
        //number of n-touching loops
        int touching = 2;

        for(touching = 2; touching<=loops.size(); touching++){
            boolean anyTouchingLoopsFound = false;
            table.put(touching, new LinkedList<>());
            for(combinationsMask = 3; combinationsMask < Math.pow(2,loops.size()); combinationsMask++){
                int numOnes = Integer.bitCount(combinationsMask);

                if(numOnes != touching){
                    continue;
                }

                //getting which loops are chosen in this combination
                List<Integer> indexList = new ArrayList<>();
                int k = 0;
                while(k < nodes.size()){
                    if((combinationsMask & (1<<k)) != 0){
                        indexList.add(k);
                    }
                    k++;
                }
                if(indexList.size() != touching){
                    break;
                }
                int result = 0;
                int[] counts = new int[nodes.size()];
                //for each loop
                for(int j = 0; j < indexList.size(); j++){
                    //for each bit
                    for(int q = 0; q < nodes.size(); q++){
                        if( (bitmasks[indexList.get(j)] & (1 << q)) != 0){
                            counts[q]++;
                        }
                    }
                }
                boolean loopsAreTouching = false;
                for(int c: counts){
                    if(c > 1){
                        // loops of this combination are touching
                        //need to continue to next combination
                        loopsAreTouching = true;
                        break;
                    }
                }
                if(loopsAreTouching) {continue;}

                table.get(touching).add(indexList);
                anyTouchingLoopsFound = true;
            }
            if(!anyTouchingLoopsFound){
                table.remove(touching);
                break;
            }
        }
        return table;

    }

    public HashMap<Integer, List<List<List<Edge>>>> getNonTouchingLoops(){
        HashMap<Integer, List<List<Integer>>> indexTable =  getNonTouchingLoopsAsIndices();
        HashMap<Integer, List<List<List<Edge>>>> loopTable = new HashMap<>();
        indexTable.forEach((key, list)->{
            //adding a new list to hold groups of nontouching loops
            loopTable.put(key, new LinkedList<>());
            list.forEach((listOfInts)->{
                //adding a new group of n-nontouching loops
                loopTable.get(key).add(new LinkedList<>());
                listOfInts.forEach((loopIndex)->{
                    loopTable.get(key).get(loopTable.get(key).size()-1).add(loops.get(loopIndex));
                });
            });
        });
//        System.out.println(indexTable);
        loopPairIndexTable = indexTable;
        return loopTable;

    }

    public double getGain(List<Edge> path){
        double gain = 1;
        for(Edge e: path){
            gain *= e.getWeight();
        }
        return gain;
    }

    public void initGain(){
        for(List<Edge> loop: loops){
            int index = loops.indexOf(loop);
            loopGains.put(index,getGain(loop));
        }
        for(List<Edge> path: paths){
            int index = paths.indexOf(path);
            pathGains.put(index,getGain(path));
        }
    }

    public double getPathDelta(int pathMask){

        ArrayList<Double> levelGains = new ArrayList<>();

        for(int level:loopPairIndexTable.keySet()){
            double gainSum = 0;
            boolean goToNextLevel = false;
            for(List<Integer> pair: loopPairIndexTable.get(level)){
                double gain = 1;
                boolean nonTouching = true;
                for(int loopIndex: pair){
                    if((pathMask & loopsMasks[loopIndex])==0){
                        gain *= loopGains.get(loopIndex);
                    }else{
                        nonTouching = false;
                        break;
                    }
                }
                if(nonTouching){
                    gainSum += gain;
                    goToNextLevel = true;
                }
            }
            if(goToNextLevel){
                levelGains.add(gainSum);
            }
        }

        double delta = 1;
        for(int i = 0; i < levelGains.size();i++){
            int sign = (int)Math.pow(-1, i);
            delta += sign * levelGains.get(i);
        }

        double sum = 0;
        for(int i = 0; i < loopsMasks.length; i++){
            if((loopsMasks[i] & pathMask) == 0){
                sum += loopGains.get(i);
            }
        }
        return delta-sum;
    }

    public void initDeltas(){
        initGain();
        this.initPathMasks();
        for(List<Edge> path: paths){
            //for each path, calc its delta(i)
            pathsDeltas.add(getPathDelta(pathMasks[paths.indexOf(path)]));
        }
        delta = getPathDelta(0);
    }

    public void testCase1(){
        for(int i = 1; i <= 8; i++){
            nodes.add("x"+i);
        }
        edges.add(new Edge("x1", "x2", 1));
        edges.add(new Edge("x2", "x3", 32));
        edges.add(new Edge("x3", "x4", 43));
        edges.add(new Edge("x4", "x5", 54));
        edges.add(new Edge("x5", "x6", 65));
        edges.add(new Edge("x6", "x7", 76));
        edges.add(new Edge("x7", "x8", 1));

        edges.add(new Edge("x2", "x4", 42));
        edges.add(new Edge("x2", "x7", 72));


        edges.add(new Edge("x7", "x6", 67));
        edges.add(new Edge("x6", "x5", 56));
        edges.add(new Edge("x5", "x4", 45));
        edges.add(new Edge("x4", "x3", 34));
        edges.add(new Edge("x3", "x2", 23));
        edges.add(new Edge("x7", "x5", 57));


        edges.add(new Edge("x7", "x7", 77));

        graph = buildGraph();
    }

    public void testCase2(){
        nodes.add("R");

        for(int i = 5; i > 0; i--){
            nodes.add("V"+i);
        }

        nodes.add("C");

        edges.add(new Edge("R", "V5", 1));
        edges.add(new Edge("V5", "V4", 2));
        edges.add(new Edge("V4", "V3", 3));
        edges.add(new Edge("V3", "V2", 4));
        edges.add(new Edge("V2", "V1", 5));
        edges.add(new Edge("V1", "C", 7));

        edges.add(new Edge("V2", "V1", 6));

        edges.add(new Edge("V1", "V3", -3));
        edges.add(new Edge("V2", "V3", -2));
        edges.add(new Edge("V4", "V5", -1));

        graph = buildGraph();

    }

    public void testCase3(){
        nodes.add("R");

        for(int i = 6; i > 0; i--){
            nodes.add("V"+i);
        }

        nodes.add("C");

        edges.add(new Edge("R", "V4", 1));
        edges.add(new Edge("V4", "V3", 2));
        edges.add(new Edge("V3", "V4", -1));
        edges.add(new Edge("V3", "V2", 3));
        edges.add(new Edge("V2", "V1", 4));
        edges.add(new Edge("V1", "V2", -2));

        edges.add(new Edge("V1", "C", 5));

        edges.add(new Edge("V6", "V4", 8));
        edges.add(new Edge("V5", "V6", 7));
        edges.add(new Edge("V6", "V5", -4));
        edges.add(new Edge("C", "V5", 6));

        graph = buildGraph();

    }


    public void setNodes(ArrayList<String> nodes){
        this.nodes = nodes;
    }

    public void setEdges(ArrayList<Edge> edges){
        this.edges = edges;
    }


    public void solveGraph(){
        this.reset();
        graph = buildGraph();
        System.out.println();
        generatePaths();

        System.out.println("Paths::= "+paths);

        System.out.println();

        generateLoops();

        System.out.println("Loops::= "+loops);
        System.out.println();

//       System.out.println( getNonTouchingLoops());
        HashMap<Integer, List<List<List<Edge>>>> table = getNonTouchingLoops();
        table.forEach((touchingLevel, levelList)->{
            System.out.println("Level = "+touchingLevel);
            levelList.forEach(System.out::println);
        });

        System.out.println();


//        System.out.println(loopPairIndexTable);
        initDeltas();

        System.out.println("Path gains::= "+pathGains);
        System.out.println();

        System.out.println("Loop gains::= "+loopGains);

        System.out.println();

        System.out.println(pathsDeltas);
        System.out.println(delta);
    }
//    public  void main(String[] args){
//
//        testCase3();
//
////        System.out.println(buildGraph());
//
//        System.out.println();
//        getPaths();
//
//        System.out.println("Paths::= "+paths);
//
//        System.out.println();
//
//        getLoops();
//
//        System.out.println("Loops::= "+loops);
//        System.out.println();
//
////       System.out.println( getNonTouchingLoops());
//       HashMap<Integer, List<List<List<Edge>>>> table = getNonTouchingLoops();
//       table.forEach((touchingLevel, levelList)->{
//           System.out.println("Level = "+touchingLevel);
//           levelList.forEach(System.out::println);
//       });
//
//        System.out.println();
//
//
////        System.out.println(loopPairIndexTable);
//        this.initDeltas();
//
//        System.out.println("Path gains::= "+pathGains);
//        System.out.println();
//
//        System.out.println("Loop gains::= "+loopGains);
//
//        System.out.println();
//
//        System.out.println(pathsDeltas);
//        System.out.println(delta);
//    }
}
