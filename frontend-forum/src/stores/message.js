import { defineStore } from "pinia";
import { ref } from "vue";

export const useMessageStore = defineStore('message',()=>{
    const messages = ref([
        {
            id: 1,
            type: 'TEXT',
            senderId: '00000000-0000-4000-8000-000000000001',
            content: ' 哈囉',
            createTime: '2026-09-13 18:00'
        },
        {
            id: 2,
            type: 'TEXT',
            senderId: '00000000-0000-4000-8000-000000000002',
            content: '你好',
            createTime: '2026-09-13 18:50'
        },
        {
            id: 3,
            type: 'TEXT',
            senderId: '00000000-0000-4000-8000-000000000001',
            content: ':)',
            createTime: '2026-09-13 18:52'
        }
    ]);

    const sendMessage = (content) => {
        messages.value.push({
            id: Date.now(),
            type: 'TEXT',
            senderId: '00000000-0000-4000-8000-000000000001',
            content: content,
            createTime: new Date().toLocaleString()
        })
    }

    return {
        messages,
        sendMessage,
    }
})