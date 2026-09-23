<template>
  <!-- User Detail Dialog -->
  <v-dialog
    v-model="dialog"
    max-width="650"
    :persistent="!nameExistsInDatabase"
  >
    <v-card>
      <!-- 標題 -->
      <v-card-title class="text-h5 bg-blue-lighten-5">
        個人資訊
      </v-card-title>

      <v-divider />

      <v-card-text>
        <v-form ref="formRef">
          <!-- 帳號 -->
          <div class="field-row">
            <div class="field-label">帳號</div>

            <div class="field-content">
              <v-text-field v-model="form.account" disabled hide-details />
              <v-btn
                v-if="configStore.settings.allow_change_password === 'true'"
                variant="text"
                color="primary"
                @click="showChangePasswordDialog = true"
              >
                更改密碼
              </v-btn>
            </div>
          </div>

          <!-- 目前點數：僅顯示 -->
          <div class="field-row">
            <div class="field-label">目前點數</div>

            <div
              class="field-content"
              style="min-height: 56px"
              aria-live="polite"
            >
              <template v-if="pointsLoading">
                <v-progress-circular
                  indeterminate
                  size="18"
                  width="2"
                  color="primary"
                  class="mr-2"
                />
                <span class="text-body-2 text-medium-emphasis"> 讀取中… </span>
              </template>

              <span v-else-if="pointsError" class="text-body-2 text-error">
                {{ pointsError }}
              </span>

              <span
                v-else-if="pointsBalance !== null"
                class="text-h6 font-weight-bold text-primary"
              >
                {{ pointsDisplay }} 點
              </span>

              <span v-else class="text-medium-emphasis"> — </span>
            </div>
          </div>
          <!-- 點數功能入口 -->
<div class="field-row">
  <div class="field-label">點數功能</div>

  <div class="field-content points-actions">
    <v-btn
      type="button"
      color="primary"
      variant="tonal"
      :loading="pointsNavigating"
      :disabled="pointsNavigating"
      @click="goToPointsPage('DailyTasks')"
    >
      每日任務
    </v-btn>

    <v-btn
      type="button"
      variant="outlined"
      :disabled="!transactionsRouteReady || pointsNavigating"
      @click="goToPointsPage('CoinTransactions')"
    >
      點數交易紀錄
    </v-btn>

    <v-btn
      type="button"
      variant="text"
      :loading="pointsLoading"
      :disabled="pointsLoading || pointsNavigating"
      @click="loadPoints"
    >
      更新點數
    </v-btn>

    <div
      v-if="pointsNavigationError"
      class="text-body-2 text-error w-100"
      role="alert"
    >
      {{ pointsNavigationError }}
    </div>
  </div>
</div>


          <!-- 名稱 -->
          <div class="field-row">
            <div class="field-label">名稱</div>

            <div class="field-content">
              <v-text-field
                v-model="form.name"
                label="名稱"
                :rules="nameRules"
                @update:model-value="markDirty"
              />
            </div>
          </div>

          <!-- 暱稱 -->
          <div class="field-row" style="display: none;">
            <div class="field-label">暱稱</div>

            <div class="field-content">
              <v-text-field
                v-model="form.nickname"
                label="暱稱"
                :rules="nicknameRules"
                @update:model-value="markDirty"
              />

              <v-checkbox
                v-model="form.showNickname"
                label="站內以暱稱為顯示名稱"
                hide-details
                @update:model-value="markDirty"
              />
            </div>
          </div>

          <!-- 生日 -->
          <div class="field-row">
            <div class="field-label">生日</div>

            <div class="field-content">
              <v-text-field
                v-model="form.birthday"
                type="date"
                label="生日"
                :rules="birthdayRules"
                @update:model-value="markDirty"
              />

              <v-checkbox
                v-model="form.showBirthday"
                label="他人檢視是否顯示"
                hide-details
                @update:model-value="markDirty"
              />
            </div>
          </div>

          <!-- Email -->
          <div class="field-row">
            <div class="field-label">Email</div>

            <div class="field-content">
              <v-text-field
                v-model="form.email"
                label="Email"
                type="email"
                :rules="emailRules"
                @update:model-value="markDirty"
              />

              <v-checkbox
                v-model="form.showEmail"
                label="他人檢視是否顯示"
                hide-details
                @update:model-value="markDirty"
              />
            </div>
          </div>
          <!-- 照片 -->
          <div class="field-row">
            <div class="field-label">照片</div>

            <div class="field-content photo-area">
              <v-img
                v-if="form.photoUrl"
                :src="form.photoUrl"
                width="150"
                height="150"
                cover
                class="rounded"
              />

              <div v-else class="no-photo">尚無照片</div>
            </div>
          </div>
        </v-form>
      </v-card-text>

      <v-divider />

      <!-- 按鈕 -->
      <v-card-actions class="dialog-actions">
        <v-btn variant="outlined" @click="save"> 儲存 </v-btn>

        <v-btn variant="flat" color="primary" @click="saveAndClose">
          儲存並關閉
        </v-btn>

        <v-spacer />

        <v-btn variant="text" @click="requestClose"> 關閉 </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- 尚未儲存確認 Dialog -->
  <v-dialog v-model="confirmCloseDialog" max-width="450">
    <v-card>
      <v-card-title> 尚未儲存 </v-card-title>

      <v-card-text> 目前有尚未儲存的修改，確定要關閉嗎？ </v-card-text>

      <v-card-actions>
        <v-spacer />

        <v-btn variant="text" @click="confirmCloseDialog = false"> 取消 </v-btn>

        <v-btn color="error" variant="flat" @click="confirmClose">
          確定關閉
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
  <ChangePasswordDialog v-model="showChangePasswordDialog" />
</template>

<script setup>
import { ref, reactive, computed, watch , onMounted } from "vue";
import httpClient from "@/services/httpClient";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { userService } from "@/services/userService";
import { useConfigStore } from "@/stores/config"
import ChangePasswordDialog from '@/components/user/ChangePasswordDialog.vue'

const router = useRouter();
const pointsNavigating = ref(false);
const pointsNavigationError = ref("");
const showChangePasswordDialog = ref(false)
const configStore = useConfigStore();


const transactionsRouteReady = computed(() =>
  router.hasRoute("CoinTransactions"),
);

async function goToPointsPage(routeName) {
  if (pointsNavigating.value) return;

  pointsNavigationError.value = "";

  if (isDirty.value) {
    pointsNavigationError.value =
      "會員資料尚未儲存，請先按「儲存並關閉」，再開啟點數功能。";
    return;
  }

  if (!nameExistsInDatabase.value) {
    pointsNavigationError.value =
      "請先完成名稱並儲存會員資料，再使用點數功能。";
    return;
  }

  if (!router.hasRoute(routeName)) {
    pointsNavigationError.value = "此功能的頁面尚未設定。";
    return;
  }

  pointsNavigating.value = true;

  try {
    const failure = await router.push({ name: routeName });

    // 已在目的頁時，仍可關閉會員資料視窗。
    if (failure && router.currentRoute.value.name !== routeName) {
      pointsNavigationError.value = "未能切換頁面，請稍後再試。";
      return;
    }

    dialog.value = false;
  } catch (error) {
    console.error("點數頁面導向失敗：", error);
    pointsNavigationError.value = "無法開啟頁面，請稍後再試。";
  } finally {
    pointsNavigating.value = false;
  }
}
const userStore = useUserStore();
const nameExistsInDatabase = ref(false);

// 點數獨立於會員表單，不會隨儲存會員資料送出。
const pointsBalance = ref(null);
const pointsLoading = ref(false);
const pointsError = ref("");

const pointsDisplay = computed(() => {
  if (pointsBalance.value === null) {
    return "—";
  }

  return BigInt(pointsBalance.value).toLocaleString("zh-TW");
});


// 避免短時間重新開啟視窗時，舊請求覆蓋新結果。
let pointsRequestId = 0;

async function loadPoints() {
  const requestId = ++pointsRequestId;

  pointsLoading.value = true;
  pointsError.value = "";
  pointsBalance.value = null;

  try {
    const response = await httpClient.get("/api/coins/wallet");

    // 相容一般 Axios 回傳與已由攔截器取出 data 的情況。
    const data = response?.data ?? response;
    const balance = data?.balance;

    // 接受安全整數，或後端以字串回傳的完整整數。
    const validNumber =
      typeof balance === "number" && Number.isSafeInteger(balance);

    const validString = typeof balance === "string" && /^-?\d+$/.test(balance);

    if (!validNumber && !validString) {
      throw new Error("點數回傳格式不正確");
    }

    if (requestId !== pointsRequestId) {
      return;
    }

    pointsBalance.value = String(balance);
  } catch (error) {
    if (requestId !== pointsRequestId) {
      return;
    }

    const status = error.response?.status ?? error.status;

    pointsError.value =
      status === 401 ? "請先登入後查看點數" : "暫時無法取得點數";

    console.error("取得點數失敗：", error);
  } finally {
    if (requestId === pointsRequestId) {
      pointsLoading.value = false;
    }
  }
}

/*
|--------------------------------------------------------------------------
| Props / Emits
|--------------------------------------------------------------------------
*/
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  account: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(["update:modelValue", "saved"]);

watch(
  () => props.modelValue,
  (value) => {
    if (value) {
      openDialog();
    }
  },
);

/*
|--------------------------------------------------------------------------
| Dialog
|--------------------------------------------------------------------------
*/
const dialog = computed({
  get() {
    return props.modelValue;
  },

  set(value) {
    emit("update:modelValue", value);
  },
});

function closeDialog() {
  if (!nameExistsInDatabase.value) {
    return;
  }

  dialog.value = false;
}

const confirmCloseDialog = ref(false);

/*
|--------------------------------------------------------------------------
| Form
|--------------------------------------------------------------------------
*/
const formRef = ref(null);

const form = reactive({
  account: "",
  name: "",
  nickname: "",
  showNickname: false,

  birthday: "",
  showBirthday: false,

  email: "",
  showEmail: false,

  photoUrl: "",
});

/*
|--------------------------------------------------------------------------
| 原始資料
|--------------------------------------------------------------------------
|
| 用來判斷使用者是否修改過資料
|
*/
const originalForm = ref(null);

const isDirty = ref(false);

/*
|--------------------------------------------------------------------------
| Validation Rules
|--------------------------------------------------------------------------
*/

/*
 * 名稱
 * 必填
 */
const nameRules = [
  (value) => {
    if (!value || !value.trim()) {
      return "名稱不能為空";
    }

    return true;
  },
];

/*
 * 暱稱
 * 可以為空
 */
const nicknameRules = [
  (value) => {
    return true;
  },
];

/*
 * 生日
 * 可以為空
 */
const birthdayRules = [
  (value) => {
    return true;
  },
];

/*
 * Email
 * 可以為空
 * 有輸入時才檢查格式
 */
const emailRules = [
  (value) => {
    // Email 可以為空
    if (!value) {
      return true;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    return emailRegex.test(value) || "Email 格式不正確";
  },
];

/*
|--------------------------------------------------------------------------
| 開啟 Dialog
|--------------------------------------------------------------------------
*/

async function openDialog() {
  
console.log(configStore.settings)
  pointsNavigationError.value = "";
  void loadPoints();
  try {
    const user = await userService.getUserDetail(userStore.account);

    console.log("取得使用者資料：", user);

    Object.assign(form, {
      account: user.account ?? "",
      name: user.name ?? "",
      nickname: user.nickname ?? "",
      showNickname: user.showNickname ?? false,

      birthday: user.birthday ?? "",
      showBirthday: user.showBirthday ?? false,

      email: user.email ?? "",
      showEmail: user.showEmail ?? false,

      photoUrl: user.photoUrl ?? "",
    });

    originalForm.value = JSON.parse(JSON.stringify(form));

    isDirty.value = false;
    dialog.value = true;
    nameExistsInDatabase.value = !!user.name?.trim();
  } catch (error) {
    console.error("取得使用者資料失敗：", error);
    router.push("/login");
  }
}

/*
|--------------------------------------------------------------------------
| 標記資料已修改
|--------------------------------------------------------------------------
*/
function markDirty() {
  isDirty.value = true;
}

/*
|--------------------------------------------------------------------------
| 驗證表單
|--------------------------------------------------------------------------
*/
async function validateForm() {
  if (!formRef.value) {
    return false;
  }

  const result = await formRef.value.validate();

  return result.valid;
}

/*
|--------------------------------------------------------------------------
| 儲存
|--------------------------------------------------------------------------
*/
async function save() {
  const valid = await validateForm();

  if (!valid) {
    return;
  }

  try {
    await userService.saveUserDetail(form);

    // 重新查詢，確認後端確實已儲存名稱。
    const user = await userService.getUserDetail(userStore.account);

    nameExistsInDatabase.value = !!user.name?.trim();

    if (!nameExistsInDatabase.value) {
      pointsNavigationError.value =
        "儲存後仍未取得名稱，請確認會員資料 API。";
      return;
    }

    originalForm.value = JSON.parse(JSON.stringify(form));
    isDirty.value = false;
    pointsNavigationError.value = "";

  } catch (error) {
    console.error("儲存或確認會員資料失敗：", error);
    pointsNavigationError.value =
      "無法確認會員資料是否儲存成功，請稍後再試。";
  }
}
/*
|--------------------------------------------------------------------------
| 儲存並關閉
|--------------------------------------------------------------------------
*/
async function saveAndClose() {
  /*
   * 先驗證
   */
  const valid = await validateForm();

  if (!valid) {
    return;
  }

  await userService.saveUserDetail(form);
  emit("saved");

  /*
   * 儲存關閉不需要更新原始資料
   */
  // originalForm.value = JSON.parse(
  //   JSON.stringify(form)
  // )

  isDirty.value = false;

  /*
   * 關閉 Dialog
   */
  dialog.value = false;
}

/*
|--------------------------------------------------------------------------
| 關閉 Dialog
|--------------------------------------------------------------------------
*/
async function requestClose() {
  /*
   * 沒有修改
   * → 直接關閉
   */
  if (!isDirty.value) {
    emit("saved");
    dialog.value = false;

    return;
  }

  /*
   * 有修改
   * → 先驗證資料
   */
  const valid = await validateForm();

  /*
   * 驗證失敗
   * → 不關閉
   */
  if (!valid) {
    return;
  }

  /*
   * 驗證成功
   * → 詢問是否放棄修改
   */
  confirmCloseDialog.value = true;
}

/*
|--------------------------------------------------------------------------
| 確定關閉
|--------------------------------------------------------------------------
*/
function confirmClose() {
  /*
   * 關閉確認 Dialog
   */
  confirmCloseDialog.value = false;

  /*
   * 還原資料
   */
  if (originalForm.value) {
    Object.assign(form, JSON.parse(JSON.stringify(originalForm.value)));
  }

  /*
   * 清除修改狀態
   */
  isDirty.value = false;

  /*
   * 關閉主要 Dialog
   */
  dialog.value = false;
}

/*
|--------------------------------------------------------------------------
| 更改密碼
|--------------------------------------------------------------------------
*/

function changePassword() {
  /*
   * TODO:
   *
   * 之後可以：
   *
   * 1. 開另一個 ChangePasswordDialog
   * 2. 或 router.push('/change-password')
   */

  console.log("開啟更改密碼");
}
</script>

<style scoped>
.field-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 18px;
}

.field-label {
  width: 80px;
  padding-top: 16px;
  font-weight: 500;
}

.field-content {
  flex: 1;
  display: flex;
  align-items: center;
}

.photo-area {
  min-height: 150px;
}

.no-photo {
  width: 150px;
  height: 150px;

  display: flex;
  align-items: center;
  justify-content: center;

  background: #f5f5f5;
  border: 1px solid #ddd;
}

.dialog-actions {
  padding: 16px;
}
.points-actions {
  flex-wrap: wrap;
  gap: 8px;
}

</style>
