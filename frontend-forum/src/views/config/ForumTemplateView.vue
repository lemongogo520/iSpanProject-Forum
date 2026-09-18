<template>
  <div class="layout">
    <Sidebar />

    <main class="content">
      <div class="page-header">
        <div>
          <h1>論壇初始化範本</h1>
          <p class="text-medium-emphasis">
            選擇適合的使用情境，系統會自動建立分類與討論版。
          </p>
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

            <v-card-subtitle>
              {{ template.code }}
            </v-card-subtitle>

            <v-card-text>
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
        <v-col cols="12" sm="6" md="3">
          <v-card
            class="template-card"
            :class="{ selected: selectedCode === 'CUSTOM' }"
            variant="outlined"
            @click="selectCustomTemplate"
          >
            <v-card-title> 自訂論壇 </v-card-title>

            <v-card-subtitle> CUSTOM </v-card-subtitle>

            <v-card-text>
              <p class="mb-4">自行建立需要的分類與討論版</p>

              <v-chip color="secondary" variant="tonal" size="small">
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

      <section v-if="selectedTemplate || selectedCode === 'CUSTOM'" class="preview-section">
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

          <v-alert type="warning" variant="tonal" class="mt-6">
            套用後會將以上分類與討論版寫入資料庫， 論壇已有分類時將無法套用。
          </v-alert>
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

      <v-dialog v-model="confirmDialog" max-width="500">
        <v-card>
          <v-card-title> 確認套用範本 </v-card-title>

          <v-card-text>
            確定要套用
            <strong>{{ selectedTemplate?.name }}</strong>
            嗎？系統將建立對應的分類與討論版。
          </v-card-text>

          <v-card-actions>
            <v-spacer />

            <v-btn variant="text" @click="confirmDialog = false"> 取消 </v-btn>

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
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";

import Sidebar from "@/components/config/ConfigSidebar.vue";
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

const selectedBoards = ref({});

const selectedTemplate = computed(() => {
  return templates.value.find(
    (template) => template.code === selectedCode.value,
  );
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
    const response = await forumTemplateService.applyTemplate(
      selectedCode.value,
      selections,
    );

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

  const result = {};

  template?.categories.forEach((category) => {
    result[category.name] = category.boards.map((board) => board.name);
  });

  selectedBoards.value = result;
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

function selectCustomTemplate() {
  selectedCode.value = "CUSTOM";
  selectedBoards.value = {};
}
function goToForumAdmin() {
  router.push("/admin/categories");
}
onMounted(loadTemplates);
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}

.content {
  flex: 1;
  padding: 32px;
  background: #f8fafc;
}

.page-header {
  margin-bottom: 24px;
}

.template-card {
  height: 100%;
  cursor: pointer;
  transition:
    border-color 0.2s,
    box-shadow 0.2s,
    transform 0.2s;
}

.template-card:hover {
  transform: translateY(-2px);
}

.template-card.selected {
  border: 2px solid #2563eb;
  box-shadow: 0 6px 18px rgb(37 99 235 / 15%);
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
</style>
