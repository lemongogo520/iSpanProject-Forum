<<template>
    <aside class="friend-sidebar" :class="{ open: isOpen }">
        <div class="sidebar-header">
            <h3>好友</h3>
            <button class="close-button" @click="emit('close')">></button>
        </div>

        <FriendList></FriendList>
    </aside>
</template>
    <script setup>

    import { useFriendStore } from '@/stores/friend';
    import FriendList from './FriendList.vue';
    import { storeToRefs } from 'pinia';
    import { onMounted, ref } from 'vue';
    import { VBtn } from 'vuetify/components';

    defineProps({
        isOpen: {
            type: Boolean,
            default: false
        }
    })

    const emit = defineEmits(['close'])

    const friendStore = useFriendStore();

    const { friendList } = storeToRefs(friendStore);

    const selectedFriend = ref(null);

    const selectFriend = (friend) => {
        selectedFriend.value = friend;
        currentConversation.value = null;
        getConversation(friend.userId);
    }
    onMounted(() => {
        friendStore.refreshFriendData();
    })

</script>
    <style>

    .sidebar-header {
        height: 55px;

        display: flex;
        align-items: center;

        padding: 0 20px;


        font-size: 20px;
        font-weight: 900;

    }

    .sidebar-list {
        height: calc(100% - 55px);

        padding: 10px;
        margin-left: 20px;

        overflow-y: auto;
    }

    .close-button {
        position: absolute;

        right: 0;

        background: none;
        /* color: white; */

        font-size: 15px;
        font-weight: 700;

        border: none;

        cursor: pointer;

        transition: 0.1s;
    }

</style>