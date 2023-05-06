
const stabilityres=document.getElementById("stability");
const No_RHS_Poles=document.getElementById("changes");
stabilityres.innerText="System is";
No_RHS_Poles.innerText="Number of right hand side poles = ";
stabilityres.innerText+=" ";


async function send()
{
    const eqn = document.getElementById("inp");
    console.log(eqn)
    console.log(eqn.value);
    if(eqn.value === ""){
        return
    }
    const response = await fetch("http://localhost:8080/getEqn",{
        method:"POST",
        mode:"cors",
        credentials:"same-origin",
        headers:{
            "Content-Type":"application/json"
        },
        redirect:"follow",
        referrerPolicy:"no-referrer",
        body:eqn.value
    }
    )
    const data = await response.json()
    console.log(data)
    
    No_RHS_Poles.innerText = `Number of RHS poles = ${data.changes}`
    if(!data.status && data.changes == 0){
        stabilityres.innerText = `System is critically stable`
    }else{
        stabilityres.innerText = `System is ${data.status? "stable": "unstable"}`
    }
    
}
function toggle()
{
    var elem=document.body;
    elem.classList.toggle("dark-mode");
}

const sendButton = document.getElementById("butt")
sendButton.addEventListener("click",send)