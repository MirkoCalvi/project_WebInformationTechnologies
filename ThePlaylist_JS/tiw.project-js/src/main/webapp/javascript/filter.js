
window.addEventListener("load" , () => {
    if(sessionStorage.getItem("userName") == null){
        window.location.href = "sign-in.html";
    }else{
        render.showHomePage();
    }
} , false);