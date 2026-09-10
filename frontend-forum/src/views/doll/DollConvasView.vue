<template>
  <div style="display: flex; gap: 20px">
    <canvas
      ref="convasRef"
      width="64"
      height="64"
      style="
        border: 1px solid #999;
        image-rendering: pixelated;
        width: 256px;
        height: 256px;
      "
    ></canvas>

    <!-- 試衣間 -->
    <div>
      <h3>試衣間</h3>
      <div v-for="layer in selectLayers" :key="layer.id" class="slot-row">
        <label>{{ layer.name }}</label>
        <select v-model="layer.selected">
            <option
                v-for="opt in layer.options"
                :key="opt.name"
                :value="opt.file"
            >
                {{ opt.name }}
            </option>
        </select>
      </div>
    </div>
  </div>
</template>
<script setup>
import { computed, onMounted, reactive, ref, watch } from "vue";
const convasRef = ref(null);
const frameSize = 64;
const DIRECTION = { UP: 0, LEFT: 1, DOWN: 2, RIGHT: 3 };

const layers = reactive([
  {
    id: "body",
    name: "軀幹",
    zIndex: 10,
    visible: true,
    locked: true,
    selected: "body/bodyidle.png",
  },
  {
    id: "head",
    name: "頭部",
    zIndex: 20,
    visible: true,
    locked: true,
    selected: "head/headidle.png"
  },
  {
    id: "pants",
    name: "褲子",
    zIndex: 30,
    visible: true,
    selected: "clothes/pants/malepantsidle.png",
    options:[
        {name:'預設下裝',file:'clothes/pants/malepantsidle.png'},
        {name:'脫掉下裝',file:null},
    ]
  },
  {
    id: "hair",
    name: "頭髮",
    zIndex: 30,
    visible: true,
    selected: "hair/hairidle.png",
    options:[
        {name:'預設髮型',file:'hair/hairidle.png'},
        {name:'光頭',file:null},
        {name:'辮子頭',file:'hair/braidhairidle.png'},
        {name:'窗簾瀏海',file:'hair/curtainshairidle.png'}
    ]
  },
  {
    id: "shoe",
    name: "鞋子",
    zIndex: 40,
    visible: true,
    selected: "clothes/shoe/maleshoeidle.png",
    options:[
        {name:'預設鞋子',file:'clothes/shoe/maleshoeidle.png'},
        {name:'脫掉鞋子',file:null},
        {name:'綁帶鞋',file:'clothes/shoe/maleghilliesidle.png'},
    ]
  },
  {
    id: "shirt",
    name: "衣服",
    zIndex: 50,
    visible: true,
    selected: "clothes/shirt/maleshirtidle.png",
    options:[
        {name:'預設上衣',file:'clothes/shirt/maleshirtidle.png'},
        {name:'脫掉上衣',file:null},
        {name:'V領襯衫',file:'clothes/shirt/maletshirtvneckidle.png'},
    ]
  },
]);

const selectLayers = computed(()=>{
    return layers.filter(layer => !layer.locked);
})

//load image tool
const imageCache = new Map();
const loadImage = (src) => {
  if (imageCache.has(src)) return Promise.resolve(imageCache.get(src));
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.src = src;
    img.onload = () => {
      console.log(`[成功載入圖片] ${src}`);
      imageCache.set(src, img);
      resolve(img);
    };
    img.onerror = (err) => {
      console.error(`[載入失敗 404/錯誤] ${src}`, err);
      reject(err);
    };
    img.src = src;
  });
};

// 封裝一個通用的單格繪製方法
const drawFrame = (ctx, img, direction = DIRECTION.DOWN, frame = 0) => {
  ctx.drawImage(
    img,
    frame * frameSize,
    direction * frameSize,
    frameSize,
    frameSize, // 從素材圖片切出 64x64
    0,
    0,
    frameSize,
    frameSize,
  );
};

//繪圖引擎
const drawCharacter = async () => {
  console.log("[1] draw 函式開始執行");
  const convas = convasRef.value;
  if (!convas) {
    console.error(
      '[錯誤] 抓不到 Canvas DOM 請檢查 template 中的 ref 是否為 ref="canvasRef"',
    );
    return;
  }
  const ctx = convas.getContext("2d");
  ctx.clearRect(0, 0, convas.width, convas.height);

  //篩選出「啟用中」且「有圖片路徑」的圖層，並按 zIndex 由小到大排序
  const activeLayers = layers
    .filter((layer) => layer.selected !== null)
    .slice()
    .sort((a, b) => a.zIndex - b.zIndex);
  try {
    // 平行載入所有啟用的圖層
    const loadedList = await Promise.all(
      activeLayers.map(async (layer) => ({
        img: await loadImage(`/sprites/${layer.selected}`),
      })),
    );
    // 依順序畫上 Canvas
    for (const { img } of loadedList) {
      drawFrame(ctx, img, DIRECTION.DOWN, 0);
    }
  } catch (err) {
    console.error("load image error!");
  }
};

watch(layers, drawCharacter, { deep: true });
onMounted(drawCharacter);
</script>
<style></style>
