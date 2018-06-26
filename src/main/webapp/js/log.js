
const logForm = document.getElementById("logForm")
const lMail = document.getElementById("lEmail")
const lPsw = document.getElementById("lPsw")

logForm.addEventListener("submit", event => {
const esitoLog = document.getElementById("esitoLog")
esitoLog.classList.remove("alert-danger");
esitoLog.innerHTML = ""


    const login = {'email': lMail.value,
        'password': lPsw.value
    };

    

    event.preventDefault();

    fetch("http://localhost:8080/esame_cloud/rest/utenti/login",
            {method: "POST",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(login)
            }).then(resp => {
                    if(resp.status === 200){
                        return resp.json()
                        
                    }else{
                        esitoLog.classList.add("alert-danger")
                        esitoLog.innerHTML = "Email o Password errate"
                    }
        
            }).then(jsonData => {
                localStorage.setItem("token" , jsonData.token)
                localStorage.setItem("email" , jsonData.email)
                localStorage.setItem("id", jsonData.id)
                document.location.href = "home.html"
            })





})


