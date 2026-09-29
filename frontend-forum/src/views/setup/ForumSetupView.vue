<template>
  <v-main class="setup-page">
    <v-container class="content">
      <div class="page-header">
        <div>
          <h1>論壇初始化範本</h1>
          <v-alert type="red-darken-2" variant="tonal" class="mt-6">
            請注意!!套用後會將以上分類與討論版寫入資料庫，
            論壇已有分類時將無法套用。
          </v-alert>
        </div>
      </div>

      <v-progress-linear
        v-if="loading"
        indeterminate
        color="primary"
        class="mb-6"
      />

      <v-alert v-if="loadError" type="error" variant="tonal" class="mb-6">
        {{ loadError }}
      </v-alert>

      <v-row v-if="!loading">
        <v-col
          v-for="template in templates"
          :key="template.code"
          cols="12"
          sm="6"
          md="3"
        >
          <v-card
            class="template-card"
            :class="{
              selected: selectedCode === template.code,
            }"
            variant="outlined"
            @click="selectTemplate(template.code)"
          >
            <v-card-title>
              {{ template.name }}
            </v-card-title>

            <v-card-subtitle class="pt-1 pb-4">
              描述你的論壇用途，AI 將自動規劃分類與討論版
            </v-card-subtitle>

            <v-card-text class="ai-dialog-content">
              <p class="mb-4">
                {{ template.description }}
              </p>

              <v-chip color="primary" variant="tonal" size="small">
                {{ template.categories.length }} 個分類
              </v-chip>
            </v-card-text>

            <v-card-actions>
              <v-radio
                :model-value="selectedCode"
                :value="template.code"
                label="選擇此範本"
                @click.stop="selectTemplate(template.code)"
              />
            </v-card-actions>
          </v-card>
        </v-col>
        <!-- AI 範本 -->
        <v-col cols="12" sm="6" md="3">
          <v-card
            class="template-card ai-template-card"
            :class="{ selected: selectedCode === 'AI_GENERATED' }"
            variant="outlined"
            @click="openAiDialog"
          >
            <v-card-title class="d-flex align-center">
              <v-icon icon="mdi-robot-outline" color="primary" class="mr-2" />
              AI 自訂範本
            </v-card-title>

            <v-card-subtitle>AI_GENERATED</v-card-subtitle>

            <v-card-text>
              <p class="mb-4">輸入論壇用途，由 AI 建議分類與討論版</p>

              <v-chip
                color="primary"
                variant="tonal"
                size="small"
                prepend-icon="mdi-auto-fix"
              >
                {{
                  aiTemplate
                    ? `${aiTemplate.categories.length} 個分類`
                    : "AI 智慧產生"
                }}
              </v-chip>
            </v-card-text>

            <v-card-actions>
              <v-btn
                variant="text"
                color="primary"
                prepend-icon="mdi-auto-fix"
                @click.stop="openAiDialog"
              >
                {{ aiTemplate ? "重新產生" : "開始產生" }}
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>

        <!-- 自訂論壇 -->
        <v-col cols="12" sm="6" md="3">
          <v-card
            class="template-card"
            :class="{ selected: selectedCode === 'CUSTOM' }"
            variant="outlined"
            @click="selectCustomTemplate"
          >
            <v-card-title class="d-flex align-center">
              <v-icon icon="mdi-tune-variant" color="secondary" class="mr-2" />
              自訂論壇
            </v-card-title>

            <v-card-subtitle>CUSTOM</v-card-subtitle>

            <v-card-text>
              <p class="mb-4">自行建立需要的分類與討論版</p>

              <v-chip
                color="secondary"
                variant="tonal"
                size="small"
                prepend-icon="mdi-cog-outline"
              >
                自由設定
              </v-chip>
            </v-card-text>

            <v-card-actions>
              <v-radio
                :model-value="selectedCode"
                value="CUSTOM"
                label="選擇自訂"
                @click.stop="selectCustomTemplate"
              />
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>

      <section
        v-if="selectedTemplate || selectedCode === 'CUSTOM'"
        class="preview-section"
      >
        <!-- 固定範本的預覽內容 -->
        <template v-if="selectedTemplate">
          <h2 class="mb-4">{{ selectedTemplate.name }}預覽</h2>

          <v-expansion-panels variant="accordion">
            <v-expansion-panel
              v-for="category in selectedTemplate.categories"
              :key="category.name"
            >
              <v-expansion-panel-title>
                <div class="d-flex align-center" @click.stop>
                  <v-checkbox
                    :model-value="isCategorySelected(category)"
                    :indeterminate="isCategoryIndeterminate(category)"
                    hide-details
                    density="compact"
                    @update:model-value="toggleCategory(category, $event)"
                  />

                  <strong>{{ category.name }}</strong>

                  <span class="ml-2 text-medium-emphasis">
                    已選
                    {{ selectedBoards[category.name]?.length || 0 }}
                    / {{ category.boards.length }}
                    個討論版
                  </span>
                </div>
              </v-expansion-panel-title>

              <v-expansion-panel-text>
                <v-checkbox
                  v-for="board in category.boards"
                  :key="board.name"
                  v-model="selectedBoards[category.name]"
                  :value="board.name"
                  hide-details
                >
                  <template #label>
                    <div>
                      <div>{{ board.name }}</div>

                      <div class="text-medium-emphasis text-caption">
                        {{ board.description }}
                      </div>
                    </div>
                  </template>
                </v-checkbox>
              </v-expansion-panel-text>
            </v-expansion-panel>
          </v-expansion-panels>
          <div class="forum-preview mt-6">
            <div class="preview-header">
              <div>
                <div class="text-overline text-primary">FORUM PREVIEW</div>
                <h2>{{ selectedTemplate.name }}</h2>
                <p class="text-medium-emphasis mb-0">
                  以下為套用範本後的論壇分類與看板外觀
                </p>
              </div>

              <v-chip color="primary" variant="tonal">
                {{ previewCategories.length }} 個分類
              </v-chip>
            </div>

            <v-alert
              v-if="previewCategories.length === 0"
              type="info"
              variant="tonal"
            >
              請至少選擇一個討論版以顯示預覽
            </v-alert>

            <div v-else class="preview-content">
              <ForumCategorySection
                v-for="category in previewCategories"
                :key="category.id"
                :category="category"
                :get-category-icon="getCategoryIcon"
                :get-category-style="getCategoryStyle"
              />
            </div>

            <div class="preview-notice">
              <v-icon icon="mdi-eye-outline" size="18" />
              此區域僅供預覽，尚未建立任何分類或看板。
            </div>
          </div>
        </template>

        <!-- 下方操作按鈕 -->
        <div class="actions">
          <!-- 固定範本時顯示 -->
          <v-btn
            v-if="selectedTemplate"
            color="primary"
            size="large"
            :loading="applying"
            :disabled="applying"
            @click="confirmDialog = true"
          >
            套用此範本
          </v-btn>
          <!-- 自訂論壇時顯示 -->
          <v-btn
            v-else-if="selectedCode === 'CUSTOM'"
            color="primary"
            size="large"
            prepend-icon="mdi-cog"
            @click="goToForumAdmin"
          >
            前往自訂類別
          </v-btn>
        </div>
      </section>
      <v-dialog v-model="aiDialog" max-width="680" persistent>
        <v-card rounded="lg">
          <v-card-title class="d-flex align-center">
            <v-icon icon="mdi-robot-outline" color="primary" class="mr-2" />
            AI 論壇範本產生器
          </v-card-title>

          <v-card-text>
            <v-textarea
              v-model="aiForm.description"
              label="請描述論壇用途"
              placeholder="例如：建立一個大學生程式設計與專題交流論壇"
              variant="outlined"
              rows="4"
              counter="300"
              maxlength="300"
              :disabled="aiGenerating"
            />

            <v-row>
              <v-col cols="12" sm="6">
                <v-select
                  v-model="aiForm.categoryCount"
                  label="分類數量"
                  :items="[1, 2, 3, 4, 5]"
                  variant="outlined"
                  :disabled="aiGenerating"
                />
              </v-col>

              <v-col cols="12" sm="6">
                <v-select
                  v-model="aiForm.boardsPerCategory"
                  label="每個分類的討論版數量"
                  :items="[1, 2, 3, 4, 5, 6]"
                  variant="outlined"
                  :disabled="aiGenerating"
                />
              </v-col>
            </v-row>

            <v-alert v-if="aiError" type="error" variant="tonal" class="mt-2">
              {{ aiError }}
            </v-alert>
          </v-card-text>

          <v-card-actions class="px-6 pb-5">
            <v-spacer />

            <v-btn
              variant="text"
              :disabled="aiGenerating"
              @click="aiDialog = false"
            >
              取消
            </v-btn>

            <v-btn
              color="primary"
              prepend-icon="mdi-auto-fix"
              :loading="aiGenerating"
              @click="generateAiTemplate"
            >
              AI 產生範本
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
      <v-dialog v-model="confirmDialog" max-width="520" persistent>
        <v-card rounded="lg">
          <v-card-title class="d-flex align-center">
            <v-icon
              color="red-darken-2"
              icon="mdi-alert-outline"
              class="mr-2"
            />
            再次確認套用範本
          </v-card-title>

          <v-card-text>
            <p>
              確定要選擇
              <strong class="text-primary">{{ selectedTemplate?.name }}</strong>
              嗎？
            </p>

            <p class="mt-3 text-medium-emphasis">
              系統將建立 {{ previewCategories.length }} 個分類，以及
              {{
                previewCategories.reduce(
                  (total, category) => total + category.boards.length,
                  0,
                )
              }}
              個討論版。
            </p>

            <v-alert
              color="red-darken-2"
              variant="tonal"
              icon="mdi-alert-outline"
              class="mt-4"
            >
              套用後不能再次使用初始化範本，請確認選擇內容是否正確。
            </v-alert>
          </v-card-text>

          <v-card-actions class="px-6 pb-5">
            <v-spacer />

            <v-btn
              variant="text"
              :disabled="applying"
              @click="confirmDialog = false"
            >
              返回修改
            </v-btn>

            <v-btn
              color="primary"
              :loading="applying"
              @click="applySelectedTemplate"
            >
              確認套用
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-container>
  </v-main>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";

import ForumCategorySection from "@/components/forum/ForumCategorySection.vue";
import { forumTemplateService } from "@/services/forumTemplateService";
import { useNotification } from "@/composables/useNotification";

const router = useRouter();
const { showMessage } = useNotification();

const templates = ref([]);
const selectedCode = ref("");
const loading = ref(false);
const applying = ref(false);
const loadError = ref("");
const confirmDialog = ref(false);

//AI 功能
const aiDialog = ref(false);
const aiGenerating = ref(false);
const aiError = ref("");
const aiTemplate = ref(null);

const aiForm = ref({
  description: "",
  categoryCount: 3,
  boardsPerCategory: 3,
});
const selectedBoards = ref({});

function selectAllBoards(template) {
  const result = {};

  template?.categories?.forEach((category) => {
    result[category.name] = category.boards.map((board) => board.name);
  });

  selectedBoards.value = result;
}

const selectedTemplate = computed(() => {
  if (selectedCode.value === "AI_GENERATED") {
    return aiTemplate.value;
  }
  return templates.value.find(
    (template) => template.code === selectedCode.value,
  );
});

const previewCategories = computed(() => {
  if (!selectedTemplate.value) return [];

  return selectedTemplate.value.categories
    .map((category, categoryIndex) => ({
      ...category,
      id: `preview-category-${categoryIndex}`,
      boards: category.boards
        .filter((board) =>
          selectedBoards.value[category.name]?.includes(board.name),
        )
        .map((board, boardIndex) => ({
          ...board,
          id: `preview-board-${categoryIndex}-${boardIndex}`,
          latestPost: null,
        })),
    }))
    .filter((category) => category.boards.length > 0);
});

function getErrorMessage(error, fallback) {
  const data = error.response?.data;

  if (typeof data === "string") {
    return data;
  }

  return data?.message || fallback;
}

async function loadTemplates() {
  loading.value = true;
  loadError.value = "";

  try {
    const response = await forumTemplateService.getTemplates();

    templates.value = response.data;

    if (templates.value.length > 0) {
      selectTemplate(templates.value[0].code);
    }
  } catch (error) {
    loadError.value = getErrorMessage(error, "載入論壇範本失敗");
  } finally {
    loading.value = false;
  }
}

async function applySelectedTemplate() {
  const selections = buildSelections();

  if (selections.length === 0) {
    showMessage("請至少選擇一個分類和討論版", "warning");
    return;
  }

  applying.value = true;

  try {
    let response;

    if (selectedCode.value === "AI_GENERATED") {
      response = await forumTemplateService.applyAiTemplate({
        ...aiTemplate.value,
        categories: previewCategories.value.map((category, categoryIndex) => ({
          name: category.name,
          rank: category.rank ?? categoryIndex + 1,
          boards: category.boards.map((board, boardIndex) => ({
            name: board.name,
            description: board.description,
            rank: board.rank ?? boardIndex + 1,
          })),
        })),
      });
    } else {
      response = await forumTemplateService.applyTemplate(
        selectedCode.value,
        selections,
      );
    }

    confirmDialog.value = false;

    showMessage(response.data?.message || "論壇範本套用成功");

    router.push("/admin/categories");
  } catch (error) {
    confirmDialog.value = false;

    showMessage(getErrorMessage(error, "套用論壇範本失敗"), "error");
  } finally {
    applying.value = false;
  }
}
function selectTemplate(code) {
  selectedCode.value = code;

  const template = templates.value.find((item) => item.code === code);

  selectAllBoards(template);
}

function isCategorySelected(category) {
  return selectedBoards.value[category.name]?.length === category.boards.length;
}
function isCategoryIndeterminate(category) {
  const count = selectedBoards.value[category.name]?.length || 0;

  return count > 0 && count < category.boards.length;
}
function toggleCategory(category, checked) {
  selectedBoards.value = {
    ...selectedBoards.value,
    [category.name]: checked ? category.boards.map((board) => board.name) : [],
  };
}
function buildSelections() {
  return selectedTemplate.value.categories
    .map((category) => ({
      categoryName: category.name,
      boardNames: selectedBoards.value[category.name] || [],
    }))
    .filter((category) => category.boardNames.length > 0);
}

function openAiDialog() {
  aiError.value = "";
  aiDialog.value = true;
}

async function generateAiTemplate() {
  const description = aiForm.value.description.trim();

  if (!description) {
    aiError.value = "請輸入論壇用途";
    return;
  }

  aiGenerating.value = true;
  aiError.value = "";

  try {
    const response = await forumTemplateService.generateAiTemplate({
      description,
      categoryCount: aiForm.value.categoryCount,
      boardsPerCategory: aiForm.value.boardsPerCategory,
    });

    aiTemplate.value = response.data;
    selectedCode.value = "AI_GENERATED";

    selectAllBoards(aiTemplate.value);

    aiDialog.value = false;

    showMessage("AI 論壇範本產生成功");
  } catch (error) {
    aiError.value = getErrorMessage(error, "AI 範本產生失敗，請稍後再試");
  } finally {
    aiGenerating.value = false;
  }
}
function selectCustomTemplate() {
  selectedCode.value = "CUSTOM";
  selectedBoards.value = {};
}
function goToForumAdmin() {
  router.push("/admin/categories");
}
const getCategoryIcon = (name = "") => {
  if (name.includes("遊戲")) return "🎮";
  if (name.includes("影音")) return "🎬";
  if (name.includes("科技")) return "💻";
  if (name.includes("美食")) return "🍕";
  if (name.includes("休閒")) return "🏖️";
  if (name.includes("公告") || name.includes("資訊")) return "📢";
  if (name.includes("工作")) return "💼";
  if (name.includes("學習")) return "📚";
  if (name.includes("校園")) return "🏫";
  if (name.includes("生活")) return "☕";
  return "📌";
};
const getCategoryStyle = (name = "") => {
  let accent = "#2563eb";
  let soft = "#eff6ff";

  if (name.includes("美食")) {
    accent = "#b45309";
    soft = "#fff7ed";
  } else if (name.includes("動漫")) {
    accent = "#7c3aed";
    soft = "#f5f3ff";
  } else if (name.includes("科技")) {
    accent = "#0e7490";
    soft = "#ecfeff";
  } else if (name.includes("休閒")) {
    accent = "#047857";
    soft = "#ecfdf5";
  }

  return {
    "--category-accent": accent,
    "--category-soft": soft,
  };
};
onMounted(loadTemplates);
</script>

<style scoped>
.setup-page {
  min-height: 100vh;
  background: #f8fafc;
}
.content {
  padding: 32px;
}

.page-header {
  margin-bottom: 24px;
}

.template-card {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 220px;
  overflow: hidden;
  cursor: pointer;
  border-radius: 14px;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    transform 0.2s ease;
}

.template-card :deep(.v-card-text) {
  flex: 1;
}

.template-card:hover {
  border-color: #93c5fd;
  box-shadow: 0 8px 22px rgb(15 23 42 / 10%);
  transform: translateY(-3px);
}

.template-card.selected {
  border: 2px solid #2563eb;
  box-shadow: 0 8px 24px rgb(37 99 235 / 18%);
}
.ai-template-card {
  background: linear-gradient(
    145deg,
    rgb(239 246 255 / 75%),
    rgb(255 255 255 / 100%)
  );
}

.preview-section {
  max-width: 900px;
  margin-top: 40px;
}

.actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}
.forum-preview {
  padding: 24px;
  border: 1px solid #dbe3ee;
  border-radius: 16px;
  background: #fff;
}

.preview-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.preview-header h2 {
  margin: 0;
}
.forum-preview {
  padding: 28px;
  border: 1px solid #dbe3ee;
  border-radius: 16px;
  background: #f5f7fa;
}

.preview-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid #dfe5ec;
}

.preview-header h2 {
  margin: 0 0 6px;
  color: #253349;
}

.preview-content {
  pointer-events: none;
}

.preview-notice {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding-top: 16px;
  border-top: 1px solid #dfe5ec;
  color: #6b7789;
  font-size: 13px;
}
@media (max-width: 600px) {
  .forum-preview {
    padding: 16px;
  }

  .preview-header {
    align-items: flex-start;
    flex-direction: column;
  }
}
.ai-dialog-content {
  padding-top: 12px;
}

.ai-dialog-content :deep(.v-field) {
  border-radius: 12px;
}

.ai-dialog-content :deep(.v-textarea textarea) {
  line-height: 1.65;
}
</style>
