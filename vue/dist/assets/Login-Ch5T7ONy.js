import{_ as B,q as U,E as d,c as z,a as l,j as i,b as s,w as t,s as L,aN as M,r as N,I,h as R,o as T,J as F,x as H,u as y,Z as J,ac as O,az as P,aO as j,W as A,t as D,aP as Q,y as W,aQ as Z,M as g,L as x,d as G}from"./index-D1uxLAHj.js";/* empty css                *//* empty css                  *//* empty css                 *//* empty css               *//* empty css               *//* empty css                *//* empty css              *//* empty css                     */import{r as K}from"./request-CPdzsuLA.js";/* empty css                *//* empty css                    */const X={class:"login-container"},Y={class:"content-box"},$={class:"login-box"},ee={class:"login-options"},ae={class:"left-options"},se={class:"right-options"},te={__name:"Login",setup(le){const a=N({form:{role:"USER",agreement:!1,verified:!1,verify:""},rules:{username:[{required:!0,message:"请输入账号",trigger:"blur"}],password:[{required:!0,message:"请输入密码",trigger:"blur"}],verify:[{validator:(r,e,o)=>{a.form.verified?o():o(new Error("请完成滑块验证"))},trigger:"change"}],agreement:[{validator:(r,e,o)=>{e?o():o(new Error("请阅读并同意用户协议和隐私政策"))},trigger:"change"}]}}),h=g(null),c=g(0),u=g(!1),b=Z(()=>({width:"320px",height:"40px",backgroundColor:u.value?"#42b983":"#f5f5f5",borderRadius:"8px",transition:"background-color 0.3s"})),w=r=>{r>=100&&(u.value=!0,a.form.verified=!0,a.form.verify="verified",d.success("验证通过"))},_=()=>{c.value=0,u.value=!1,a.form.verified=!1,a.form.verify=""},V=async()=>{try{if(await h.value.validate()){if(!a.form.agreement){d.warning("请先同意用户协议和隐私政策");return}if(!a.form.verified){d.warning("请完成滑块验证");return}const e=await K.post("/login",a.form);e.code==="200"?(d.success("登录成功"),localStorage.setItem("xm-user",JSON.stringify(e.data)),await G.push("/front/home")):(d.error(e.msg),_())}}catch(r){console.error("登录失败:",r),d.error("登录失败，请稍后重试"),_()}},k=()=>{x.alert(`
    <div class="agreement-content">
      <h3>用户协议</h3>
      <p>欢迎使用我们的旅游推荐平台！请仔细阅读以下条款：</p>
      
      <h4>1. 服务说明</h4>
      <p>本平台为用户提供旅游目的地推荐、行程规划、旅游攻略等服务。我们致力于为用户提供优质、真实的旅游信息。</p>
      
      <h4>2. 用户责任</h4>
      <ul>
        <li>用户需确保注册信息真实、准确</li>
        <li>遵守相关法律法规</li>
        <li>不得发布虚假、违法或侵权内容</li>
        <li>保护账号安全，对账号行为负责</li>
      </ul>
      
      <h4>3. 内容规范</h4>
      <p>用户发布的评论、攻略等内容需：</p>
      <ul>
        <li>真实可信，无虚假宣传</li>
        <li>遵守公序良俗</li>
        <li>尊重他人知识产权</li>
        <li>不含违法违规信息</li>
      </ul>
      
      <h4>4. 服务变更与终止</h4>
      <p>平台保留根据实际情况调整服务内容、范围、规则的权利。如遇重大变更，将提前通知用户。</p>
      
      <h4>5. 免责声明</h4>
      <p>对因不可抗力、第三方原因等导致的服���中断或用户损失，平台不承担责任。</p>
    </div>
    `,"用户协议",{confirmButtonText:"我知道了",dangerouslyUseHTMLString:!0,customClass:"agreement-dialog"})},E=()=>{x.alert(`
    <div class="privacy-content">
      <h3>隐私政策</h3>
      <p>我们重视您的隐私保护，请了解我们如何处理您的个人信息：</p>
      
      <h4>1. 信息收集</h4>
      <p>我们收集的信息包括：</p>
      <ul>
        <li>账号信息（用户名、密码等）</li>
        <li>个人资料（昵称、头像等）</li>
        <li>使用记录（浏览历史、搜索记录等）</li>
        <li>设备信息（设备型号、操作系统等）</li>
      </ul>
      
      <h4>2. 信息使用</h4>
      <p>我们使用收集的信息用于：</p>
      <ul>
        <li>提供和改进服务</li>
        <li>个性化推荐</li>
        <li>安全防护</li>
        <li>数据分析</li>
      </ul>
      
      <h4>3. 信息保护</h4>
      <p>我们采取的保护措施：</p>
      <ul>
        <li>数据加密存储</li>
        <li>访问权限控制</li>
        <li>安全审计</li>
        <li>应急响应机制</li>
      </ul>
      
      <h4>4. 信息共享</h4>
      <p>除以下情况外，我们不会共享您的个人信息：</p>
      <ul>
        <li>获得您的明确授权</li>
        <li>法律法规要求</li>
        <li>维护平台安全</li>
      </ul>
      
      <h4>5. 您的权利</h4>
      <p>您对个人信息享有：</p>
      <ul>
        <li>访问、更正权</li>
        <li>删除权</li>
        <li>撤回授权权</li>
        <li>注销账号权</li>
      </ul>
    </div>
    `,"隐私政策",{confirmButtonText:"我知道了",dangerouslyUseHTMLString:!0,customClass:"privacy-dialog"})},m=M();return U(()=>{m.query.registered==="true"&&m.query.username&&(a.form.username=m.query.username,d.success("注册成功，请登录"))}),(r,e)=>{const o=H,p=F,C=j,v=W,S=Q,q=I,f=R("router-link");return T(),z("div",X,[l("div",Y,[l("div",$,[e[14]||(e[14]=l("div",{class:"login-header"},[l("div",{class:"login-title"},[l("i",{class:"iconfont icon-travel"}),i(" 探索世界 ")]),l("div",{class:"login-subtitle"},"开启您的旅行之旅")],-1)),s(q,{ref_key:"formRef",ref:h,model:a.form,rules:a.rules,class:"login-form"},{default:t(()=>[s(p,{prop:"username"},{default:t(()=>[s(o,{"prefix-icon":y(J),size:"large",modelValue:a.form.username,"onUpdate:modelValue":e[0]||(e[0]=n=>a.form.username=n),placeholder:"请输入账号",class:"custom-input"},null,8,["prefix-icon","modelValue"])]),_:1}),s(p,{prop:"password"},{default:t(()=>[s(o,{"show-password":"","prefix-icon":y(O),size:"large",modelValue:a.form.password,"onUpdate:modelValue":e[1]||(e[1]=n=>a.form.password=n),placeholder:"请输入密码",class:"custom-input"},null,8,["prefix-icon","modelValue"])]),_:1}),s(p,{prop:"verify",class:"verify-item"},{default:t(()=>[l("div",{class:"slider-container",style:P(b.value)},[s(C,{modelValue:c.value,"onUpdate:modelValue":e[2]||(e[2]=n=>c.value=n),"show-tooltip":!1,min:0,max:100,onChange:w},{button:t(()=>[...e[4]||(e[4]=[l("div",{class:"slider-button"},[l("i",{class:"el-icon-right"})],-1)])]),_:1},8,["modelValue"]),l("div",{class:A(["slider-text",{verified:u.value}])},D(u.value?"验证通过":"向右滑动验证"),3)],4)]),_:1}),s(p,{prop:"agreement"},{default:t(()=>[s(S,{modelValue:a.form.agreement,"onUpdate:modelValue":e[3]||(e[3]=n=>a.form.agreement=n),class:"agreement-checkbox"},{default:t(()=>[e[7]||(e[7]=i(" 我已阅读并同意 ",-1)),s(v,{link:"",type:"primary",onClick:k},{default:t(()=>[...e[5]||(e[5]=[i("《用户协议》",-1)])]),_:1}),e[8]||(e[8]=i(" 和 ",-1)),s(v,{link:"",type:"primary",onClick:E},{default:t(()=>[...e[6]||(e[6]=[i("《隐私政策》",-1)])]),_:1})]),_:1},8,["modelValue"])]),_:1}),s(p,null,{default:t(()=>[s(v,{size:"large",type:"primary",class:"login-btn",onClick:V},{default:t(()=>[...e[9]||(e[9]=[i("立即登录",-1)])]),_:1})]),_:1})]),_:1},8,["model","rules"]),l("div",ee,[l("div",ae,[s(f,{to:"/front/home",class:"option-link"},{default:t(()=>[...e[10]||(e[10]=[i("游客访问",-1)])]),_:1}),s(f,{to:"/adminLogin",class:"option-link"},{default:t(()=>[...e[11]||(e[11]=[i("管理员登录",-1)])]),_:1})]),l("div",se,[e[13]||(e[13]=i(" 还没有账号？ ",-1)),s(f,{to:"/register",class:"register-link"},{default:t(()=>[...e[12]||(e[12]=[i("立即注册",-1)])]),_:1})])])]),e[15]||(e[15]=L('<div class="promo-box" data-v-34c503a4><div class="promo-content" data-v-34c503a4><h2 class="promo-title" data-v-34c503a4>发现精彩旅程</h2><div class="feature-list" data-v-34c503a4><div class="feature-item" data-v-34c503a4><i class="el-icon-location" data-v-34c503a4></i><div class="feature-text" data-v-34c503a4><h3 data-v-34c503a4>探索全球目的地</h3><p data-v-34c503a4>超过1000+精选旅游景点</p></div></div><div class="feature-item" data-v-34c503a4><i class="el-icon-star-on" data-v-34c503a4></i><div class="feature-text" data-v-34c503a4><h3 data-v-34c503a4>专业旅行建议</h3><p data-v-34c503a4>来自全球旅行达人的深度攻略</p></div></div><div class="feature-item" data-v-34c503a4><i class="el-icon-shopping-cart-full" data-v-34c503a4></i><div class="feature-text" data-v-34c503a4><h3 data-v-34c503a4>优质旅行产品</h3><p data-v-34c503a4>严选品质酒店与精品路线</p></div></div></div><div class="promo-footer" data-v-34c503a4><div class="stats" data-v-34c503a4><div class="stat-item" data-v-34c503a4><span class="stat-number" data-v-34c503a4>1000+</span><span class="stat-label" data-v-34c503a4>旅游目的地</span></div><div class="stat-item" data-v-34c503a4><span class="stat-number" data-v-34c503a4>50万+</span><span class="stat-label" data-v-34c503a4>用户信赖</span></div><div class="stat-item" data-v-34c503a4><span class="stat-number" data-v-34c503a4>99%</span><span class="stat-label" data-v-34c503a4>好评率</span></div></div></div></div></div>',1))])])}}},he=B(te,[["__scopeId","data-v-34c503a4"]]);export{he as default};
