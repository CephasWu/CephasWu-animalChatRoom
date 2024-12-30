<template>
  <link href="../../output.css" rel="stylesheet">
  <div class="header">
    <img src="../../assets/pics/banner.jpg" alt="animals logo" class="w-screen h-40">
  </div>
  <div class="flex flex-col items-center w-auto h-screen bg-amber-700 ">
    <div class="">
      <h1 class="text-6xl font-cute m-8">阿貓阿狗聊天室</h1>
    </div>
    <div class="flex justify-between bg-amber-500 w-3/5 h-3/5 rounded-3xl">
      <menu class="content-around">
        <li v-for="(item, index) in animals" :key='item' @click="changeAnimals(index)"
            class="text-6xl font-cute p-8 ml-16 mb-8 border-4 border-black rounded-xl cursor-pointer"
            :class="{'selected':true,'selectedBorder': animalSelectedIndex === index }">
          {{ item.message }}
        </li>
      </menu>
      <div class="grid grid-rows-3 relative">
        <label class="text text-6xl font-cute mt-12">點選貓貓或狗狗</label>
        <img :src="animalPic.src" alt="dog1"
             class="w-56 h-52 rounded-lg border-4 border-black absolute inset-1/4">
        <input ref="nickName" type="text"
               class="rounded-lg h-16 w-full mt-12 absolute bottom-32 placeholder:text-xl text-xl"
               placeholder="請輸入您的暱稱">
        <button class="bg-blue-500 text-white rounded-lg h-16 w-full absolute bottom-10" @click="goChat()">進入聊天室
        </button>
      </div>
      <menu class="grid grid-rows-4 mr-20" v-if="changePage">
        <img v-for="(image, index) in dogPics" :key="index" :src="image.src" @click="changeBorderColor($event,index)"
             :alt="image.alt" :class="{'animalList':true,'animalListActive': selectedIndex === index }">
      </menu>
      <menu class="grid grid-rows-4 mr-20" v-else>
        <img v-for="(image, index) in catPics" :key="index" :src="image.src" @click="changeBorderColor($event,index)"
             :alt="image.alt" :class="{'animalList':true,'animalListActive': selectedIndex === index }">
      </menu>
    </div>
  </div>
</template>

<script setup>
import {useRouter} from 'vue-router';
import {ref} from 'vue';

const router = useRouter();
const animalPic = {src: require('@/assets/pics/dog_1.png')};
const animals = [
  {message: '狗狗', alt: 'dog'},
  {message: '貓貓', alt: 'cat'}
];
const changePage = ref(true);
const dogPics = [
  {src: require('@/assets/pics/dog_1.png'), alt: 'dog1'},
  {src: require('@/assets/pics/dog_2.png'), alt: 'dog2'},
  {src: require('@/assets/pics/dog_3.png'), alt: 'dog3'},
  {src: require('@/assets/pics/dog_4.png'), alt: 'dog4'}
];

const catPics = [
  {src: require('@/assets/pics/cat_1.png'), alt: 'cat1'},
  {src: require('@/assets/pics/cat_2.png'), alt: 'cat2'},
  {src: require('@/assets/pics/cat_3.png'), alt: 'cat3'},
  {src: require('@/assets/pics/cat_4.png'), alt: 'cat4'}
];
const selectedIndex = ref(0);
const animalSelectedIndex = ref(0);
const nickName = ref();

function changeAnimals(index) {
  animalSelectedIndex.value = index;
  changePage.value = animalSelectedIndex.value == 0 ? true : false;
  index == 0 ? animalPic.src = require('@/assets/pics/dog_1.png') : animalPic.src = require('@/assets/pics/cat_1.png');
  selectedIndex.value = 0;
}

function changeBorderColor(item, index) {
  selectedIndex.value = index;
  animalPic.src = item.target.src;
}

async function checkName() {
  const response = await fetch("http://localhost:8081/chatRoom", {
    method: 'GET',
    headers: {
      'userName': encodeURI(nickName.value.value),  // 將用戶名進行編碼
    },
  });
  // 如果请求失败，抛出错误
  if (!response.ok) {
    if (response.status === 409) {
      alert("暱稱已被使用，請重新輸入");
    } else {
      const message = `An error has occured: ${response.status}`
      alert(message)
    }
  } else {
    await router.push({name: "chatRoom", query: {name: nickName.value.value}}); // 如果 checkName 沒有拋出錯誤，跳轉到聊天室
  }
}

function goChat() {
  nickName.value.value.trim() == "" ? alert("請輸入您的暱稱") : checkName();  // 名稱不為空就檢查名稱是否重複
}

</script>
<style>
.animalList {
  margin: 5px;
  width: 150px;
  height: 130px;
  border-radius: 4px;
  cursor: pointer;
  border: 4px solid black;
}

.animalListActive {
  box-shadow: 0 0 30px rgba(241, 215, 198, 0.8), 0 0 30px rgba(241, 215, 198, 0.8);
  transform: scale(1.05);
  margin: 5px;
  width: 150px;
  height: 130px;
  border-radius: 10px;
  cursor: pointer;
  border: 4px solid red;
}

.selected {
  font-sizw: 48px;
  font-family: 'Cute';
  padding: 8px;
  margin-left: 16px;
  margin-bottom: 8px;
  border: 4px solid black;
  border-radius: 10px;
  cursor: pointer;
}

.selectedBorder {
  box-shadow: 0 0 30px rgba(241, 215, 198, 0.8), 0 0 30px rgba(241, 215, 198, 0.8);
  transform: scale(1.05);
  font-sizw: 48px;
  font-family: 'Cute';
  padding: 8px;
  margin-left: 16px;
  margin-bottom: 8px;
  border: 4px solid black;
  border-radius: 10px;
  cursor: pointer;
}
</style>

