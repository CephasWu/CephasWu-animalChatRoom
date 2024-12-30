<template>
  <body class="flex justify-center">
  <div ref="scroll" class=" bg-amber-700 rounded-3xl w-1/2 h-screen relative p-6 flex flex-col overflow-y-auto">
    <div class="flex">
      <div class="bg-amber-200 pt-2 w-fit h-fit rounded-xl self-start">
        <div class="flex ">
          <img src="../../assets/pics/cat_icon_2.png" alt="cat" class="w-10 h-10 ml-2">
          <div class="mwt_border ">
            <div>
              <span class="arrow_l_int"></span>
              <span class="arrow_l_out "></span>
              <p>HI!!!大家好歡迎來到阿貓阿狗聊天室，大家要好好相處喔~~~</p>
            </div>
          </div>
        </div>
        <div>
        </div>
      </div>
      <div class="self-end ml-1 text-white text-xs">
        {{ localTime }}
      </div>
    </div>
    <div v-show="index!=0" v-for="(item,index) in systemText" :key="item" class="flex justify-center mt-5 text-white text-xm">
      <p>{{ item.post }}</p>
    </div>
    <div class="flex" v-show="index!=0" v-for="(item,index) in otherText" :key="item">
      <div class="bg-amber-200 pt-2 w-fit h-fit rounded-xl self-start mt-5">
        <div class="flex ">
          <img src="../../assets/pics/cat_icon_2.png" alt="cat" class="w-10 h-10 ml-2">
          <div class="mwt_border ">
            <div>
              <span class="arrow_l_int"></span>
              <span class="arrow_l_out "></span>
              <p>{{ item.post }}</p>
            </div>
          </div>
        </div>
      </div>
      <div class="self-end ml-1 text-white text-xs">
        {{ item.time }}
      </div>
    </div>
    <div v-show="index!=0" v-for="(item,index) in myText" :key="item" class="flex self-end ">
      <div class="text-white text-xs self-end mr-1">
        {{ item.time }}
      </div>
      <div class="w-fit h-fit mt-6 pt-2 bg-amber-300 rounded-xl">
        <div class="flex">
          <img src="../../assets/pics/cat_icon_1.png" alt="cat" class="w-10 h-10 ml-2">
          <div class="mwt_border">
            <div>
              <span class="arrow_l_int"></span>
              <span class="arrow_l_out "></span>
              {{ item.post }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="w-1/6 bg-amber-300 rounded-3xl justify-items-center text-xl font-cute">
    <div class="w-2/3 h-[860px] font-cute ring-cyan-600 ring-offset-2 ring-2 rounded-3xl mt-5">
      <div class="w-full h-10 ring-lime-500 ring-offset-2 ring-2 rounded-t-3xl flex justify-center items-center">
        <span>好友列表</span>
      </div>
      <div class="w-full mt-3 pb-1 border-black border-b-2 flex justify-center items-center">
        <span>上線人數：{{ friendCount }}</span>
      </div>
      <div
          class="w-full mt-3 pb-1 border-black border-b-2 flex justify-center items-center text-sky-600 text-glow animate-pulse">
        <span>{{ userId }}</span>
      </div>
      <div v-for="item in friends" :key="item"
           class="w-full text-xl font-cute mt-3 pb-1 border-black border-b-2 flex justify-center items-center">
        <span>{{ item.name }}</span>
      </div>
    </div>
  </div>
  </body>
  <footer class="absolute start-1/4 bottom-0 bg-amber-500 rounded-3xl w-1/3 h-20 flex items-center">
    <input ref="inputMessage" type="text"
           class="bg-white rounded-xl w-full h-4/5 border-cyan-300 border-2 ml-4 text-xl font-cute"
           placeholder="請輸入留言" v-model="inputValue">
    <img src="../../assets/pics/send.png" alt="send"
         class="w-20 h20 pointer-events-auto hover:bg-teal-200 hover:cursor-pointer hover:rounded-r-3xl"
         @click="sendMessage()">
  </footer>
</template>

<script setup>
import {ref, nextTick, onMounted} from "vue";
import {useRouter} from 'vue-router';

const router = useRouter();
const inputValue = ref();
const myText = ref([{post: '', time: ''}]);
const otherText = ref([{post: '', time: ''}]);
const systemText = ref([{post: '', time: ''}]);
const scroll = ref()
const inputMessage = ref()
const friends = ref();
const friendCount = ref(1);
const socket = ref();
const userId = ref();
const localTime = new Date().toLocaleTimeString();

function sendMessage() {
  // 傳送訊息到 WebSocket 伺服器
  if (socket.value && socket.value.readyState === WebSocket.OPEN) {
    if (inputMessage.value.value == "") {
      alert('請輸入留言');
    } else {
      socket.value.send(userId.value + "：" + inputMessage.value.value);
    }
  } else {
    alert("WebSocket 尚未連線。");
  }
}

function connectWebSocket() {
  //WbeSocket設定
  socket.value = new WebSocket("ws://localhost:8081/chatRoom/" + encodeURI(userId.value));//傳遞使用者名稱
  // WebSocket 連線建立成功後的處理
  socket.value.onopen = () => {
    console.log("WebSocket 連線已建立。");
  };
  // 接收到伺服器訊息時的處理
  socket.value.onmessage = (event) => {
    let user = event.data.split("：")[0];//取得發言者
    let message = event.data.split("：")[1];//取得發言內容
    let time = new Date().toLocaleTimeString();
    if (user == "System") {
      systemText.value.push({post: message, time: time});
      getFriendList();//取得聊天室列表
    } else if (user == userId.value) {//若發言者為自己，留言顯示在右邊
      myText.value.push({post: message, time: time});
    } else {
      otherText.value.push({post: message, time: time});//其他人的留言顯示在左邊
    }
    nextTick(() => {
      scroll.value.scrollTop = scroll.value.scrollHeight;
      inputMessage.value.value = '';
    })
  };

  // WebSocket 連線關閉時的處理
  socket.value.onclose = () => {
    alert("連線已關閉，請重新整理頁面。");
  };

  // WebSocket 發生錯誤時的處理
  socket.value.onerror = (error) => {
    console.error("WebSocket 錯誤:", error);
  };
}

function getFriendList() {
  //在線人數
  fetch('http://localhost:8081/getFriends', {
    method: 'GET',
    headers: {
      'userName': encodeURI(userId.value),//將名稱含有國字編碼後傳送
    },
  }).then(response => {
    friendCount.value += parseInt(response.headers.get("friends-count"));//取得在線人數
    return response.json();
  }).then(data => {
    friends.value = data;
  }).catch(error => {
    console.error('Error:', error);
  });
}

onMounted(() => {
  //loginChatRoom傳來的userName
  userId.value = router.currentRoute.value.query.name;

  connectWebSocket();//連接WebSocket
});


</script>
<style>

.mwt_border {
  width: fit-content;
  max-width: 200px;
  min-height: 20px;
  background: #fff;
  position: relative;
  word-wrap: break-word;
  margin: 0 10px 10px 16px;
  border-radius: 10px;
  padding: 16px;
}

/*箭頭左*/
.mwt_border .arrow_l_int {
  width: 10px;
  height: 10px;
  border-width: 15px;
  border-color: transparent white transparent transparent;
  position: absolute;
  top: 10px;
  left: -30px;
}

/*箭頭左-邊框*/
.mwt_border .arrow_l_out {
  border-width: 15px;
  border-color: transparent white transparent transparent;
  position: absolute;
  top: 10px;
  left: -29px;
}
</style>