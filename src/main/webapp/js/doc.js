
const fUpload = document.getElementById("fUpload")
const file = document.getElementById("file")
const titolo = document.getElementById("titolo")
const listaUser = document.getElementById("selUtente")
const listaDoc = document.getElementById("selFile")
//const condividi = document.getElementById("btnCondividi")


caricaDocumenti()
caricaUtDoc()
//funzione che carica la lista dei documenti per l'utente
function caricaDocumenti() {
    let container = document.getElementById("all");
    let container2 = document.getElementById("allDw");
    let container3 = document.getElementById("allEl");

    container.innerHTML = ""
    container2.innerHTML = ""
    container3.innerHTML = ""

    data = new URLSearchParams;
    data.append("id", localStorage.getItem("id"))

    fetch("http://localhost:8080/esame_cloud/rest/documenti",
            {
                method: "POST",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                },
                body: data
            }).then(resp => {
        return resp.json()
    }).then(jsonData => {
        console.log(jsonData[0])
        for (var i = 0; i < jsonData.length; i++) {
            //creo i link per il download e gli elementi della lista
            let el = document.createElement(`p`)
            let btnEl = document.createElement("a")
            let btnDown = document.createElement("a")
            btnEl.classList.add("badge-danger")
            btnEl.innerHTML = "X"
            btnDown.classList.add("badge-warning")
            btnDown.setAttribute("href", "http://localhost:8080/esame_cloud/rest/documenti/download/" + jsonData[i].path);
            btnEl.setAttribute("href", "#");
            btnDown.setAttribute("style", "height:49px")
            btnEl.setAttribute("style", "height:49px")
            btnEl.setAttribute(`onclick`, `elimina(${jsonData[i].idDocumento})`)
            btnDown.style.fontSize = "30px";
            btnDown.innerHTML = "&#8595;"
            el.classList.add("list-group-item");
            el.setAttribute("id", "pDoc");
            el.innerHTML = "<b> Descrizione File </b> - " + jsonData[i].titolo + " - <b> File - </b>" + jsonData[i].path

            container.appendChild(el)
            container2.appendChild(btnDown)
            container3.appendChild(btnEl)

        }


    })
}



//carica il documento
fUpload.addEventListener("submit", event => {

    var data = new FormData()
    data.append("file", file.files[0])
    data.append("titolo", titolo.value)
    data.append("id", localStorage.getItem("id"))

    event.preventDefault()

    fetch("http://localhost:8080/esame_cloud/rest/documenti/upload",
            {
                method: "POST",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                },
                body: data
            }).then(response => {
        console.log(response.status)
        caricaDocumenti()
    })

})


//condivisione documenti
function condividi(){
    console.log("entro")
    var data = new URLSearchParams()
    data.append("selUtente", listaUser.value)
    data.append("selFile", listaDoc.value)
    
    console.log("selFile", listaDoc.value)
     console.log("selUtente", listaUser.value)
     
    event.preventDefault()

    fetch("http://localhost:8080/esame_cloud/rest/documenti/condividi",
            {
                method: "POST",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                },
                body: data
            }).then(resp =>{
                console.log(resp)
            })
}


function elimina(id) {

    console.log(id)
    event.preventDefault();

    fetch("http://localhost:8080/esame_cloud/rest/documenti/elimina/" + id,
            {method: "DELETE",
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                }
            }).then(response => {
        if (response.status == 200) {
            console.log(response.status)
            caricaDocumenti()
        }
    })
}

function caricaUtDoc() {




    fetch("http://localhost:8080/esame_cloud/rest/utenti/" + localStorage.getItem('id'),
            {
                method: "GET"
            }).then(response => {
        if (response.status == 200) {
            return response.json()
        } else {
            console.log(response)
        }
    }).then(jsonData => {
        jsonData.forEach(json => {
            var option = document.createElement("option")
            option.value = json.id
            option.innerHTML = json.email
            listaUser.appendChild(option)
        });

    })

    fetch("http://localhost:8080/esame_cloud/rest/documenti/" + localStorage.getItem('id'),
            {
                method: "GET", headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                }
            }).then(response => {
        if (response.status == 200) {
            return response.json()
        } else {
            console.log(response)
        }
    }).then(jsonData => {
        jsonData.forEach(json => {
            var option = document.createElement("option")
            option.value = json.idDocumento
            option.innerHTML = json.path
            listaDoc.appendChild(option)
        });

    })


}







