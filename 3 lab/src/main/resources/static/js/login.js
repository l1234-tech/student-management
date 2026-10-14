let registering=false;
function mode(value){registering=value;document.getElementById("email-field").hidden=!value;document.getElementById("email").required=value;document.getElementById("email-hint").hidden=value;document.getElementById("register-hint").hidden=!value;document.getElementById("submit-auth").textContent=value?"Зарегистрироваться":"Войти";document.getElementById("password").autocomplete=value?"new-password":"current-password";showPageError("");}
document.getElementById("login-tab").onclick=()=>mode(false);document.getElementById("register-tab").onclick=()=>mode(true);
document.getElementById("auth-form").onsubmit=async e=>{
    e.preventDefault();const b=document.getElementById("submit-auth");b.disabled=true;showPageError("");
    try{const body={username:document.getElementById("username").value.trim(),password:document.getElementById("password").value};if(registering)body.email=document.getElementById("email").value.trim();
    const r=await fetch("/api/auth/"+(registering?"register":"login"),{method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify(body)});const data=await r.json();
    if(!r.ok){showPageError(data.error?.message+"\n"+Object.entries(data.error?.details||{}).map(([k,v])=>k+": "+v).join("\n"));return;}saveTokens(data);location.href="/";
    }catch(e){showPageError("Не удалось связаться с сервером");}finally{b.disabled=false;}
};
