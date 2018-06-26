
const fUpload = document.getElementById("fUpload")
const file = document.getElementById("file")




fUpload.addEventListener("submit", event=>{
   console.log("ENTRO")
    var data = new FormData()
    data.append("file",file.files[0])
   // data.append("id", localStorage.getItem("id"))
    event.preventDefault()
    
    fetch("http://localhost:8080/esame_cloud/rest/documenti/upload",
    {
        method: 'POST',
        headers: {
                    'Content-Type': 'multipart/form-data'
                },
        body: data
    }).then(response =>{
        console.log(response.status)
    })
    
    
    
})


