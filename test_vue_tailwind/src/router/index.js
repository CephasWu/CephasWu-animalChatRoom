
import loginChatRoom from '@/components/views/loginChatRoom.vue';
import chatRoom from '@/components/views/chatRoom.vue';
import {createRouter, createWebHistory} from "vue-router";


const routes = [
    {
        path: '/',
        redirect: '/loginChatRoom'
    },
    {
        path: '/loginChatRoom',
        name: 'loginChatRoom',
        component: loginChatRoom,
    },
    {
        path: '/chatRoom',
        name: 'chatRoom',
        component: chatRoom,
    },
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes,
});

export default router;