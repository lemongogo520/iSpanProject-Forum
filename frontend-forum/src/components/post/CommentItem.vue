<template>
  <article class="comment-card">
    <div class="comment-layout">
      <v-avatar size="40" color="light-blue-lighten-4" class="comment-avatar">
        <v-icon icon="mdi-account" color="light-blue-darken-2" size="27" />
      </v-avatar>

      <div class="comment-main">
        <div class="comment-author">{{ getUserName(comment) }}</div>

        <!-- 父留言編輯模式 -->
        <template v-if="editingCommentId === comment.id">
          <v-textarea
            :model-value="editingContent"
            label="編輯留言"
            variant="outlined"
            rows="3"
            auto-grow
            @update:model-value="emit('update:editing-content', $event)"
          />

          <div class="d-flex justify-end ga-2">
            <v-btn
              size="small"
              variant="text"
              :disabled="commentUpdating"
              @click="emit('cancel-edit')"
            >
              取消
            </v-btn>
            <v-btn
              size="small"
              color="primary"
              variant="flat"
              :loading="commentUpdating"
              @click="emit('update-comment', comment.id, null)"
            >
              儲存
            </v-btn>
          </div>
        </template>

        <!-- 父留言一般顯示模式 -->
        <template v-else>
          <div
            class="comment-content"
            :class="{ 'deleted-comment': comment.isDeleted }"
          >
            {{ comment.content }}
          </div>

          <div v-if="!comment.isDeleted" class="comment-actions">
            <span>{{ formatCommentDate(comment.createTime) }}</span>

            <!-- 只有留言作者可以編輯 -->
            <v-btn
              v-if="isCommentOwner(comment)"
              size="small"
              variant="text"
              @click.stop="emit('start-edit', comment)"
            >
              編輯
            </v-btn>

            <!-- 留言作者或該版版主可以刪除 -->
            <v-btn
              v-if="isCommentOwner(comment) || canModerate"
              size="small"
              variant="text"
              :disabled="deletingCommentId === comment.id"
              :loading="deletingCommentId === comment.id"
              @click.stop="emit('delete-comment', comment.id, null,comment.users?.id)"
            >
              刪除
            </v-btn>

            <v-btn
              v-if="comment.modifyTime"
              size="small"
              variant="text"
              prepend-icon="mdi-history"
              @click.stop="emit('show-comment-history', comment.id)"
            >
              編輯紀錄
            </v-btn>
            <v-btn
              size="small"
              variant="text"
              @click.stop="emit('start-reply', comment.id)"
            >
              回覆
            </v-btn>

            <v-btn
              class="reply-toggle"
              size="small"
              variant="text"
              @click.stop="emit('toggle-replies', comment.id)"
            >
              {{ expanded ? "收合回覆" : "查看回覆" }}
            </v-btn>
          </div>

          <!-- 回覆輸入框 -->
          <div
            v-if="!comment.isDeleted && replyingCommentId === comment.id"
            class="mt-4 ml-6"
          >
            <v-textarea
              :model-value="replyContent"
              label="回覆這則留言"
              placeholder="輸入回覆內容..."
              variant="outlined"
              rows="2"
              auto-grow
              @update:model-value="emit('update:reply-content', $event)"
            />

            <div class="d-flex justify-end ga-2">
              <v-btn
                size="small"
                variant="text"
                :disabled="replySubmitting"
                @click="emit('cancel-reply')"
              >
                取消
              </v-btn>
              <v-btn
                size="small"
                color="primary"
                variant="flat"
                :loading="replySubmitting"
                @click="emit('submit-reply', comment.id)"
              >
                送出回覆
              </v-btn>
            </div>
          </div>

          <!-- 子留言 -->
          <div v-if="expanded" class="mt-4 ml-6">
            <v-divider class="mb-3" />

            <div
              v-if="childComments.length === 0"
              class="text-caption text-grey"
            >
              目前沒有回覆
            </div>

            <div
              v-for="child in childComments"
              :key="child.id"
              class="child-comment"
            >
              <v-avatar size="32" color="grey-lighten-3">
                <v-icon icon="mdi-account" color="grey-darken-1" size="22" />
              </v-avatar>

              <div class="comment-main">
                <div class="comment-author">{{ getUserName(child) }}</div>

                <template v-if="editingCommentId === child.id">
                  <v-textarea
                    :model-value="editingContent"
                    variant="outlined"
                    rows="2"
                    auto-grow
                    hide-details
                    @update:model-value="emit('update:editing-content', $event)"
                  />
                  <div class="d-flex justify-end ga-2 mt-2">
                    <v-btn
                      size="small"
                      variant="text"
                      :disabled="commentUpdating"
                      @click="emit('cancel-edit')"
                    >
                      取消
                    </v-btn>
                    <v-btn
                      size="small"
                      color="primary"
                      variant="flat"
                      :loading="commentUpdating"
                      @click="emit('update-comment', child.id, comment.id)"
                    >
                      儲存
                    </v-btn>
                  </div>
                </template>

                <template v-else>
                  <div
                    class="comment-content child-content"
                    :class="{ 'deleted-comment': child.isDeleted }"
                  >
                    {{ child.content }}
                  </div>
                  <div class="comment-actions">
                    <span>{{ formatCommentDate(child.createTime) }}</span>
                    <!-- 只有子留言作者可以編輯 -->
                    <v-btn
                      v-if="!child.isDeleted && isCommentOwner(child)"
                      size="small"
                      variant="text"
                      @click.stop="emit('start-edit', child)"
                    >
                      編輯
                    </v-btn>

                    <!-- 子留言作者或該版版主可以刪除 -->
                    <v-btn
                      v-if="
                        !child.isDeleted &&
                        (isCommentOwner(child) || canModerate)
                      "
                      size="small"
                      variant="text"
                      :disabled="deletingCommentId === child.id"
                      :loading="deletingCommentId === child.id"
                      @click.stop="emit('delete-comment', child.id, comment.id,child.users?.id)"
                    >
                      刪除
                    </v-btn>
                  </div>
                </template>
                <v-btn
                  v-if="child.modifyTime && !child.isDeleted"
                  size="small"
                  variant="text"
                  prepend-icon="mdi-history"
                  @click.stop="emit('show-comment-history', child.id)"
                >
                  編輯紀錄
                </v-btn>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </article>
</template>

<script setup>
const props = defineProps({
  comment: { type: Object, required: true },
  childComments: { type: Array, default: () => [] },
  expanded: { type: Boolean, default: false },
  editingCommentId: { type: [Number, String], default: null },
  editingContent: { type: String, default: "" },
  replyingCommentId: { type: [Number, String], default: null },
  replyContent: { type: String, default: "" },
  currentUserId: { type: String, required: true },
  commentUpdating: { type: Boolean, default: false },
  replySubmitting: { type: Boolean, default: false },
  deletingCommentId: { type: [Number, String], default: null },
  canModerate: { type: Boolean, default: false },
});

const emit = defineEmits([
  "start-edit",
  "cancel-edit",
  "update-comment",
  "delete-comment",
  "start-reply",
  "cancel-reply",
  "submit-reply",
  "toggle-replies",
  "update:editing-content",
  "update:reply-content",
  "show-comment-history",
]);

const getUserName = (data) => {
  return data?.users?.account || "使用者";
};

const isCommentOwner = (data) => {
  const commentUserId = data?.users?.id;
  if (!commentUserId || !props.currentUserId) return false;
  return commentUserId.toLowerCase() === props.currentUserId.toLowerCase();
};

const formatCommentDate = (date) => {
  if (!date) return "";

  const value = new Date(date);
  const today = new Date();
  const time = value.toLocaleTimeString("zh-TW", {
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  });

  if (value.toDateString() === today.toDateString()) return `今天 ${time}`;

  return value.toLocaleString("zh-TW", {
    month: "numeric",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  });
};
</script>

<style scoped>
.comment-card {
  border-top: 1px solid #e5e5e5;
  padding: 20px 0 18px;
}
.comment-layout,
.child-comment {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}
.comment-avatar {
  flex: 0 0 auto;
}
.comment-main {
  min-width: 0;
  flex: 1;
}
.comment-author {
  margin-bottom: 5px;
  color: #202124;
  font-size: 15px;
  font-weight: 500;
}
.comment-content {
  color: #202124;
  font-size: 16px;
  line-height: 1.75;
  overflow-wrap: anywhere;
  white-space: pre-wrap;
}
.child-content {
  font-size: 15px;
}
.deleted-comment {
  color: #9e9e9e;
  font-style: italic;
}
.comment-actions {
  display: flex;
  align-items: center;
  gap: 2px;
  min-height: 32px;
  margin-top: 3px;
  color: #757575;
  font-size: 13px;
}
.comment-actions :deep(.v-btn) {
  min-width: auto;
  padding: 0 7px;
  color: #757575;
  font-size: 13px;
}
.reply-toggle {
  color: #757575;
  font-size: 13px;
}
.child-comment {
  padding: 12px 0;
}
.child-comment + .child-comment {
  border-top: 1px solid #eeeeee;
}
@media (max-width: 600px) {
  .comment-card {
    padding: 16px 0;
  }
  .comment-layout {
    gap: 10px;
  }
  .comment-content {
    font-size: 15px;
  }
}
</style>
