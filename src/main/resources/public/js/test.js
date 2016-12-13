function loginCheck() {
    var x, y;

    // Get the value of the input field with id
    x = document.getElementById("email").value;
    y = document.getElementById("password").value;

    if (x.length===0 || y.length===0) {
        alert("email and password cannot be empty");
    } else {
        alert("success!");
    }
}    


function registerCheck() {
    var a, b, c, d;

    // Get the value of the input field with id="numb"
    a = document.getElementById("email").value;
    b = document.getElementById("psw").value;
    c = document.getElementById("first").value;
    d = document.getElementById("last").value;
  
    if (a.length===0 || b.length===0 || c.length===0 || d.length===0) {
        alert("inputs cannot be empty!");
    } else {
        alert("success!");
    }
} 
