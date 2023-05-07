# ControlProject
  
Computer & Systems Engineering Department 
CSE-241-Control Systems Basics Contributors:  
 	Name  	ID  
1 	George Selim Abdallah Khalil 	20010436 
2 	Omar Tarek Abd El Wahhab Mohamed Ali 	20010998  
3 	Karim Fathy Abd Al Aziz Mohamed Mostafa 	20011116 
4  	Mohamed Amr Abdelfattah Mahmoud 	20011675 
5 	Mahmoud Ali Ahmed Ali Ghallab 	20011811 
 	 
1)	Problem Statement: 
  
Part1: 
Given:  
Signal flow graph representation of the system. Assume that total number of nodes and numeric  branches gains are given.  
Required:  
1-	Graphical interface.  
2-	Draw the signal flow graph showing nodes, branches, gains, …  
3-	Listing all forward paths, individual loops, all combination of nontouching loops.  
4-	The values of delta, delta1, …, deltam where m is number of forward paths. 
5-	Overall system transfer function. 
Part2: 
Given:  
Characteristic equation of the system. Assume that all the coefficients of s0 to sn are given. Input example: “s^5+s^4+10s^3+72s^2+152s+240” Required: 
1-	Using Routh criteria, state if the system is stable or not.  
2-	If the system is not stable, list the number and values of poles in the RHS of the s-plane. 
 
2)	Main Features of the program and additional options if they exist. 
  
•	Main page o There is a main page in the startup of the project that makes the user choose “Signal Flow Graph” part or “Routh Stability” part, and this will navigate to the chosen page. 
•	Signal Flow Graph Features: 
o	In the Signal Flow graph, the user can enter the nodes, connect two existing nodes with an edge, and specify a weight to that edge. 
o	User input is validated, the code validates if the user doesn’t enter anything or enter a space, etc. 
o	Also, he may enter more than one edge connecting the same two nodes. o If more than one cycle is added, their weights are added together. (Additional feature). o Dark Mode also is added to the website as an additional feature for the user. (Additional feature). 
• Routh Stability Criterion: 
o	User can enter the equation as a string, he doesn’t be needed to be aware of choosing the order and then the coefficients, He can enter the equation in this form:” ±As^n±Bs^n-1±...±Fs±C” for example:” s^3-5s^2+s+15”. 
o	Dark Mode also is added to the website as an additional feature for the user. (Additional feature). 
o	The Code identify if the system is stable, Unstable or critically stable 
(Additional feature.) 
o Test cases for some systems are added to the repo (Additional feature).
o	Routh table is printed to the user
  
3)	Data Structure. 
  
1)	Arrays 
2)	Array List 
3)	Map 4) HashMap 5) Created: 
a.	Pair: (Boolean, Integer) 
b.	Edge: (String, String, Double) 
 
4)	Main modules. 
  
1)	Signal Flow Graph: 
a.	getPaths: returns all forward paths of the system. 
b.	generateLoops: returns all the loops of the system. 
c.	GetNonTouchingLoops: returns all the combinations of Non touching loops. 
d.	getGain: returns the gain value of a path. 
e.	getPathDelta: returns the delta of a given forward path.  
f.	buildGraph: builds the graph with the provided edges and nodes. 
g.	Drawgraph: responsible for drawing nodes and edges and handling any drawing issue. 
h.	InitDeltas: responsible for calculate the Δ’s. 2) Routh Criteria: 
a.	Extract Coefficients: Convert the String of the equation to a Double array of the coefficients. 
b.	IsStable: returns whether the system is stable, not stable or critically stable. 
3) Controller:  
a. This class relates the frontend and backend part. 
 
5)	Algorithms used. 
  
1)	Dfs traversal to get the loops, forward paths. 
Loop through the nodes, at visiting an edge that is already visited, a  	cycle is detected and registered. 
2)	Get the loops by “bit mask”. Each path (including loops) is represented using the binary representation of an integer, where each bit corresponds to a node, and is set to 1 if the path visits the corresponding node, and to 0 otherwise. 
3)	Get Non touching loops using HashMap. Using bitmasks of loops, we use bitwise and (&) between two loops, if the result is bigger than 0, then these two loops have at least one node in common, then they are added to a HashMap under the corresponding key (Each key of the HashMap is set to N, and the value is a list of the N-Non touching loops.). 
4)	Get the delta of a path using its bit mask. Using the HashMap of the Non touching loops, and the bitmasks of the paths, we check whether the path has contact with combination of non-touching loops, and the delta is set accordingly. 
5)	Get the Routh’s matrix entries by matrix’s determinants. 
  
6)	Sample runs. 
  
 
  
 
  
 
 
  
 
 
  
  
 
 
 
  
  
 
 
 
 
  
 
 
  
 
 
  
 
 
 
  
 
 
  
 
 

  
 
7)	Simple user guide. 
  
First: How to run: 
1)	First, you should download the front-end part and back-end part to your PC from this link “https://github.com/MohamedAmr982/ControlProject.git” and through “code” icon you will click on Download ZIP. 
  
2)	Extract the zip file. 
3)	Second, then you will open the “front” folder in any editor you prefer (Eg. VS Code). 
4)	Click right click in “index.html” file and choose “open with live Server” or open the chrome directly 
  
Or 
  
5)	Open the “back” folder using any Editor or ide (Intelliji for example). 
6)	Then, you will run the back-end part through 
“/Back/demo/src/main/java/com/example/demo/ SignalFlowBackApplication” 7) Click run. 
  
8)	That's it. Now let’s go to how to use the application. 
a.	Start with this page.
  
b.	Click in “SignalFlow” to start using “Signal flow graph application” OR “Routh Stability” to start using “Routh Stability Checker”. 
c.	In case choosing “SignalFlow” the following page will appear. 
  
•	In this, enter node name such as “a” in the input field “Enter name here”, click Add node, the output will be: 
  
•	To add an Edge you should enter the following information: 
o Edge out from node ...? (from) field o Edge destination (To) node …? (to) field o Weight of this edge. (Edge weight) field. 
Example: 
  
• To see the results click on “Send to Back”. Results will be shown below the graph you may need to you move your cursor slightly down. 
  
9) In case of choosing “Routh Stability”. 
•	This Page will appear. 
  
•	Write the system equation that you want to check its stability in the form “±As^n±Bs^n-1±...±Ws±Z” as n represents the system order and (A..Z) are the coefficients of the terms. 
•	For Example: 
  
•	Click on “Click” button to see the result “System is stable/unstable/critically stable” and “Number of RHS poles = ??” 
 
8)	Video Link. 
  
https://drive.google.com/file/d/1nPDF5iGkk1AXt3PM1jet6fEvr6m8M2GE/view?u sp=sharing 
 
9)	GitHub repository 
  
https://github.com/MohamedAmr982/ControlProject.git 
