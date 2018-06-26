
    const formReg = document.getElementById("regForm") 
    const email = document.getElementById("rEmail")
    const psw = document.getElementById("rPsw")
          
                  

formReg.addEventListener("submit", evt => {
       const data = new URLSearchParams;
       document.getElementById("esitoReg").removeAttribute("class")
      
      data.append("rEmail",email.value);
      data.append("rPsw",psw.value);
      evt.preventDefault();

      fetch("http://localhost:8080/esame_cloud/rest/utenti", {method: "POST", body: data})
              .then(res=>{
                if(res.status==200){
                   
                   let esito = document.getElementById("esitoReg")
                   esito.classList.add("bg-success");
                   esito.innerHTML = "Registrazione Avvenuta"
                   
                }
                if(res.status==403){
                   
                   let esito = document.getElementById("esitoReg")
                   esito.classList.add("alert-danger");
                   esito.innerHTML = "Email già presente - Registrazione non avvenuta"
                 }
        })
      
    
    
    
})