const nodeUrl = "http://localhost:8080/getNodes"
const edgeUrl = "http://localhost:8080/getEdges"


const canvas = document.getElementById("canvas")
const drawingDiv = document.getElementById("drawing")
const inputDiv = document.getElementById("input")
const addButton= document.getElementById("add")
const nameEntry = document.getElementById("name")
const fromEntry = document.getElementById("from")
const toEntry = document.getElementById("to")
const weightEntry = document.getElementById("weight")
const connectButton = document.getElementById("connect")
const sendButton = document.getElementById("send")
const infoField = document.getElementById("info")


const r = 20
const spacing = 7*r
const fontSize = 12
const arrowSize = 10
const rloop = 17

const nodeColor = "#ff8080"
const fontColor = "#000000"
const bgColor = "#f5f1f1"

canvas.width = window.innerWidth*10
canvas.height = window.innerHeight+100

const y = canvas.height/2


const ctx = canvas.getContext("2d")

//{name:string, x:number}
const nodes = []
//{from:string, to: string, w:number}
const edges = []



ctx.font = `${fontSize}px sans-serif`;


ctx.beginPath()

let c = 100

// ctx.fillStyle = "#000000"
// ctx.moveTo(100, 100)
// ctx.quadraticCurveTo(150,c, 200,100)
// ctx.stroke()

//p = (1-t)^2 * P[from] + 2(1-t)t * p[control] + t^2 * p[to] 
//t = 0.5 to get vertex
// let vy = Math.pow(0.5,2)*100+2*0.5*0.5*c+0.5*0.5*100
// let vx = 150
// ctx.fillText("p",vx,vy,fontSize)

function getYVertex(yfrom, yto, control){
    return Math.pow(0.5,2)*yfrom+2*0.5*0.5*control+0.5*0.5*yto
}


// ctx.closePath()

function drawArrowhead(locx, locy, angle, sizex, sizey) {
    var hx = sizex / 2;
    var hy = sizey / 2;

    ctx.translate((locx ), (locy));
    ctx.rotate(angle);
    ctx.translate(-hx,-hy);

    ctx.beginPath();
    ctx.moveTo(0,0);
    ctx.lineTo(0,1*sizey);    
    ctx.lineTo(1*sizex,1*hy);
    ctx.closePath();
    ctx.fill();

    ctx.translate(hx,hy);
    ctx.rotate(-angle);
    ctx.translate(-locx,-locy);
}        

// returns radians
function findAngle(sx, sy, ex, ey) {
    // make sx and sy at the zero point
    return Math.atan2((ey - sy), (ex - sx));
}


//drawArrowhead(200,100, findAngle(150,10,200,100),10,10)


function drawNode(x, name){
    ctx.beginPath()
    ctx.arc(x,y,r,0,2*Math.PI)
    ctx.fillStyle = nodeColor
    ctx.fill()
    ctx.closePath()

    ctx.fillStyle = fontColor
    ctx.fillText(name, x-(fontSize/4)*(name.length),y+fontSize/3)

    nodes.push({name:name, x:x})

    
}

function drawCurve(xfrom, xto, control, weight){

    let delta = -10
    if(xto < xfrom){
        delta = -delta
    }
    let yOffset = 0
    let xOffset = 0

    if(xto > xfrom && control !== 0){
        yOffset = -r
    }else if(xto < xfrom){
        yOffset = r
    }
    if(control === 0){
        xOffset = r
    }

    ctx.beginPath()

    let xi = xfrom+xOffset
    let newY = y+yOffset

    let xf = xto-xOffset 

    ctx.moveTo(xi, newY)
    ctx.fillStyle = "#000000"
    ctx.quadraticCurveTo((xi+xf)/2, newY+control, xf, newY)
    ctx.stroke()
    ctx.closePath()

    drawArrowhead(
        xf, 
        newY, 
        findAngle((xi+xf)/2, newY+control, xf, newY), 
        arrowSize, 
        arrowSize
    )

    ctx.fillText(weight, 
        (xi+xf)/2,
        getYVertex(newY,newY,newY+control)+delta,
        fontSize
    )

}

// drawCurve(200,300,250,2)

function getControl(xfrom, xto, occ=0){
    
    let deltaX = xfrom-xto

    deltaX = deltaX/spacing 

    if(deltaX < 0){
        deltaX++
        deltaX-=occ
    }else{
        deltaX+=occ
    }
       
    return deltaX*70
}

function drawArrow(xfrom, xto, weight, occ=0){
    //forward
    drawCurve(xfrom, xto, getControl(xfrom, xto, occ), weight)
}

let xPos = 30
addButton.addEventListener("click", 
()=>{
    let name = nameEntry.value;
    // name = name.filter((s)=>s !== " ")
    name = name.replaceAll(" ", "")
    if(name === ""){
        console.log("Please enter a name")
        return
    }else if(nodes.filter((ele)=>ele.name === name).length !== 0){
        console.log("Name already exists")
        return
    }else if(name.length > 3){
        console.log("Name is too long!")
        return
    }
    drawNode(xPos, nameEntry.value);
    xPos += spacing;
    


    //console.log(nodes)

})

function validateExistingNodeName(name){
    return (nodes.filter((ele)=>ele.name === name).length !== 0)
}

function validateNumber(num){
    return num !== "" && !isNaN(Number(num))
}

function getPosOfNode(name){
    return nodes.filter((ele)=>ele.name === name)[0].x
}

function edgeExists(fromNode, toNode){
    return edges.filter((ele)=>(ele.from === fromNode) && (ele.to === toNode)).length
}

function drawSelfLoop(x, weight){

    ctx.beginPath()
    ctx.arc(x,y,rloop,0,2*Math.PI)
    ctx.strokeStyle = fontColor
    ctx.stroke()
    ctx.closePath()


    drawArrowhead(x, y+rloop, 0, arrowSize, arrowSize)
    
    ctx.beginPath()
    // ctx.arc(x-1, y-rloop+7, fontSize/2,0,2*Math.PI)
    ctx.ellipse(x, y-rloop+7, fontSize, fontSize/2, 0, 0, Math.PI*2)
    ctx.fillStyle = bgColor
    ctx.fill()
    ctx.closePath()
    ctx.fillStyle = fontColor
    ctx.fillText(weight, x-5, y-rloop+10, fontSize)
}

function connect(fromNode, toNode, weight){
    const filterSpaces = (str)=>str.replaceAll(" ", "")
    fromNode = filterSpaces(fromNode)
    toNode = filterSpaces(toNode)
    weight = filterSpaces(weight)

    console.log("fromNode: "+fromNode)
    console.log("toNode: "+toNode)
    console.log("weight: "+weight)

    if(validateExistingNodeName(fromNode) &&
     validateExistingNodeName(toNode) &&
     validateNumber(weight)){
        let edge = {
            from: fromNode,
            to: toNode,
            weight: Number(weight),
        }

        let prevOcc = edgeExists(fromNode, toNode)
        
        if(fromNode === toNode){
            if(prevOcc != 0){
                // console.log(edges)
                let selfloop = edges.find((edge)=> (edge.from === fromNode) && (edge.to === toNode))
                selfloop.weight = Number(selfloop.weight) + Number(weight)
                console.log(edges)
                drawSelfLoop(getPosOfNode(fromNode)+2*rloop, selfloop.weight)
                return

            }else{
                drawSelfLoop(getPosOfNode(fromNode)+2*rloop, weight)

            }
        }else{
            
            drawArrow(
                getPosOfNode(fromNode),
                getPosOfNode(toNode),
                weight,
                prevOcc
                )
            }
            
        edges.push(edge)

        console.log("arrow drawn!")
    }
    //console.log(edges)
}

connectButton.addEventListener("click", ()=>connect(
    fromEntry.value,
    toEntry.value,
    weightEntry.value
))

/*
    0 --> request not initialized
    1 --> server connection established
    2 --> request received
    3 --> processing request
    4 --> processing finished, response is ready

    */

function handleArrayRequest(arr, url){
    let request = new XMLHttpRequest();
    request.open("POST", url,false)
    request.setRequestHeader("Content-Type", "application/json");
    request.send(JSON.stringify(arr))
    request.onreadystatechange = function(){
        if(this.readyState === 4 && this.status === 200){
            console.log(this.response)
        }
    }
}

const awaitResponse = async function(suffix){
    const response = await fetch("http://localhost:8080"+suffix)
    const data = await response.json()
    console.log(suffix);
    console.log(data)
    return data
}

const solveUrl = "/get";
const forwardPathUrl = "/get/forwardPath";
const loopsUrl = "/get/loop";
const combUrl = "/get/nonTouchingPairsComb";
const pathDeltasUrl = "/get/pathDelta";
const deltaUrl = "/get/delta";
const tfUrl = "/get/transferFunction";
const indexCombUrl = "/get/indexComb"


const sendRequest = async function(){
    handleArrayRequest(nodes.map((e)=>e.name), nodeUrl)
    handleArrayRequest(edges, edgeUrl)
    infoField.innerText = ""

    // const response = await fetch("http://localhost:8080/get")
    // const data = await response.json()
    // console.log(data)
    await awaitResponse(solveUrl);
    const forwardPaths= await awaitResponse(forwardPathUrl);
    const loops = await awaitResponse(loopsUrl);
    const comb = await awaitResponse(combUrl);
    const pathDeltas= await awaitResponse(pathDeltasUrl);
    const delta = await awaitResponse(deltaUrl);
    const tf = await awaitResponse(tfUrl);
    const indexComb = await awaitResponse(indexCombUrl);

    infoField.innerText += "Forward Paths:\n"
    forwardPaths.forEach(element => {
        
        infoField.innerText += `P${forwardPaths.indexOf(element)}: `
        element.forEach(n=>infoField.innerText += `(${n.from} -> ${n.to}), `)
        infoField.innerText += '\n'
    });

    infoField.innerText += "Loops:\n"
    loops.forEach(element=>{
        
        infoField.innerText += `L${loops.indexOf(element)}`
        element.forEach(n=>{
            infoField.innerText += `(${n.from}->${n.to}), `
        })
        infoField.innerText += '\n'
    })

    infoField.innerText += "Non-touching loops combinations:\n"
    Object.keys(indexComb).forEach((key, index)=>{
        infoField.innerText += key+" non-touching pairs:"+"\n"
        // list of list, contains all pairs
        indexComb[key].forEach(pair=>{
            infoField.innerText += pair.map((element)=>"L"+element)+"\n"
        })
    })

    infoField.innerText += `Δ = ${delta}\n`
    for(let i = 0; i < pathDeltas.length; i++){
        infoField.innerText += `Δ${i} = ${pathDeltas[i]}\n`
    }

    infoField.innerText += `TF = ${tf}\n`







    

}


sendButton.addEventListener("click", sendRequest)
function toggle()
{
    var elem=document.body;
    elem.classList.toggle("dark-mode");
}

