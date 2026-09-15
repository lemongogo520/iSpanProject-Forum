import httpClient from "@/services/httpClient";
import axios from "axios";
import { defineStore } from "pinia";
import { computed, ref } from "vue";
export const useChatStore = defineStore('chat',()=>{
    const conversations = ref([]);
    const currentConversation = ref(null);
    // 目前 Conversation 的訊息
    const messages = ref([]);
    const getConversation = async (targetUserId) => {
        const response = await httpClient({
            method: 'post',
            url: '/api/chat/direct',
            data: {
                targetUserId
            }
        });
        currentConversation.value = response.data;
        clearMessages();
    }
    
    const getcurrentConversation = (()=>{
        return conversations.value.find(
            conversation => conversation.id === currentConversation .value
        )
    })


    const getMessages = (async (conversationId)=>{
        const response = await httpClient.get(`/api/chat/message/${conversationId}`);
        return messages.value = response.data;
    })

    const sendMessage = async (conversationId,content) => {
        try {
            const response = await httpClient({
                method: 'post',
                url: '/api/chat/message',
                data: {
                    conversationId: conversationId,
                    content: content
                }
            });
            messages.value.push(response.data);
        } catch (error) {
            return console.log(error);
        }
    }

    const clearMessages = () =>{
        messages.value = [];
    }

        

    return {
        conversations,
        getConversation,
        currentConversation,
        getcurrentConversation,
        messages,
        getMessages,
        sendMessage,
        clearMessages
    }
})