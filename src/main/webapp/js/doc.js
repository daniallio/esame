
const fUpload = document.getElementById("fUpload")
const file = document.getElementById("file")
const titolo = document.getElementById("titolo")



fUpload.addEventListener("submit", event => {

    var data = new FormData()
    data.append("file", file.files[0])
    data.append("titolo", titolo.value)
    data.append("id", localStorage.getItem("id"))
    event.preventDefault()

    fetch("http://localhost:8080/esame_cloud/rest/documenti/upload",
            {
                method: "POST",
                body: data
            }).then(response => {
                
       console.log(response.status) 


    })



})


