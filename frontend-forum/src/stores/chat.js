import { defineStore } from "pinia";
import { computed, ref } from "vue";
export const useChatStore = defineStore('chat',()=>{
    const conversations = ref([
        {
            id: 1,
            type: 'DIRECT',
            lastMessage: '你好',
            createTime: '',
            updateTime:'',
            members:{
                id: 1,
                friend: {
                    userId: '00000000-0000-4000-8000-000000000002',
                    name: '老皮'
                },
                joinTime: '',
                lastReadTime: ''
            },
        }
    ]);
    const messages = ref([
        {
            1: [
                {
                    id: 1,
                    type: 'TEXT',
                    senderId: '00000000-0000-4000-8000-000000000001',
                    content: ' 哈囉',
                    createTime: '2026-09-13 18:00'
                },
                {
                    id: 2,
                    conversationId: '1',
                    type: 'TEXT',
                    senderId: '00000000-0000-4000-8000-000000000002',
                    content: '你好',
                    createTime: '2026-09-13 18:50'
                }
            ]
        }
  

    ]);
    const activeConversationId = ref(null);

    const getActiveConversation = computed(()=>{
        return conversations.value.find(
            conversation => conversation.id === activeConversationId .value
        )
    })

    const getActiveMessages = computed(()=>{
        return messages.value.find(
            message => message.value[activeConversationId.value]||[]
        )
    })

    // const sendMessage = (content) => {
    //     messages.value.push({
    //         id: Date.now(),
    //         type: 'TEXT',
    //         senderId: '00000000-0000-4000-8000-000000000001',
    //         content: content,
    //         createTime: new Date().toLocaleString()
    //     })
    // }

    return {
        conversations,
        messages,
        activeConversationId,
        getActiveConversation,
        getActiveMessages,
        sendMessage
    }
})