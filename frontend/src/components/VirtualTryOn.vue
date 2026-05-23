<template>
  <div class="space-y-6 fade-in p-1">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h2 class="text-2xl font-bold tracking-tight text-slate-900 font-sans font-extrabold">Thử đồ ảo</h2>
        <p class="text-xs text-slate-500">Mặc thử trang phục lên ma-nơ-canh 3D ảo và tự điều chỉnh số đo hình thể của bạn.</p>
      </div>
    </div>

    <!-- Main Workspace -->
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-6">
      
      <!-- Left Panel: Mannequin Canvas (cols: 5) -->
      <div class="lg:col-span-5 flex flex-col items-center">
        <div class="glass-card rounded-3xl p-4 w-full flex flex-col items-center relative">
          <!-- Canvas Container -->
          <div class="relative bg-slate-50 rounded-2xl w-full max-w-[340px] aspect-[3/4] border border-slate-200 overflow-hidden flex items-center justify-center">
            <canvas 
              ref="mannequinCanvas" 
              width="320" 
              height="420"
              class="w-full h-full object-contain"
            ></canvas>
            
            <!-- Canvas Reset/Clear -->
            <button 
              @click="clearCanvasGarments" 
              class="absolute bottom-3 right-3 px-3 py-1.5 bg-slate-900/80 hover:bg-slate-900 border border-slate-700 text-[10px] font-bold rounded-lg text-white"
            >
              ĐẶT LẠI LỚP ĐỒ
            </button>
          </div>
          
          <div class="text-[10px] text-slate-500 mt-2 text-center">
            Ma-nơ-canh tự động co giãn theo số đo của bạn.
          </div>
        </div>
      </div>

      <!-- Center Panel: Measurements & Layer Controls (cols: 4) -->
      <div class="lg:col-span-4 space-y-6">
        <!-- Body Measurements -->
        <div class="glass-card rounded-3xl p-5 space-y-4">
          <div class="flex justify-between items-center">
            <h3 class="text-xs font-extrabold text-slate-900 tracking-wider uppercase">SỐ ĐO MA-NƠ-CANH</h3>
            <button 
              @click="saveMeasurements" 
              :disabled="savingMeas"
              class="px-3 py-1 bg-glowGreen text-white text-[9px] font-bold rounded-lg tracking-wider"
            >
              {{ savingMeas ? 'ĐANG LƯU...' : 'LƯU SỐ ĐO' }}
            </button>
          </div>

          <div class="space-y-3">
            <div>
              <div class="flex justify-between text-xs mb-1">
                <span class="text-slate-500">Chiều cao (cm)</span>
                <span class="text-slate-800 font-bold">{{ measurements.height }}</span>
              </div>
              <input 
                v-model.number="measurements.height" 
                @input="draw" 
                type="range" min="140" max="210" step="1" 
                class="w-full h-1 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-glowGreen"
              />
            </div>

            <div>
              <div class="flex justify-between text-xs mb-1">
                <span class="text-slate-500">Vòng ngực (cm)</span>
                <span class="text-slate-800 font-bold">{{ measurements.bust }}</span>
              </div>
              <input 
                v-model.number="measurements.bust" 
                @input="draw" 
                type="range" min="60" max="130" step="1" 
                class="w-full h-1 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-glowGreen"
              />
            </div>

            <div>
              <div class="flex justify-between text-xs mb-1">
                <span class="text-slate-500">Vòng eo (cm)</span>
                <span class="text-slate-800 font-bold">{{ measurements.waist }}</span>
              </div>
              <input 
                v-model.number="measurements.waist" 
                @input="draw" 
                type="range" min="50" max="120" step="1" 
                class="w-full h-1 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-glowGreen"
              />
            </div>

            <div>
              <div class="flex justify-between text-xs mb-1">
                <span class="text-slate-500">Vòng mông (cm)</span>
                <span class="text-slate-800 font-bold">{{ measurements.hips }}</span>
              </div>
              <input 
                v-model.number="measurements.hips" 
                @input="draw" 
                type="range" min="70" max="140" step="1" 
                class="w-full h-1 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-glowGreen"
              />
            </div>
          </div>
        </div>

        <!-- Layer Alignment Editors (shown only if a garment is active) -->
        <div class="glass-card rounded-3xl p-5 space-y-4">
          <h3 class="text-xs font-extrabold text-slate-900 tracking-wider uppercase">ĐIỀU CHỈNH TRANG PHỤC ĐANG CHỌN</h3>
          
          <div v-if="!activeLayer" class="text-xs text-slate-500 py-4 text-center">
            Chọn một trang phục từ danh sách bên phải để điều chỉnh vị trí mặc thử.
          </div>

          <div v-else class="space-y-3">
            <div class="flex items-center gap-2 mb-2 pb-2 border-b border-slate-100">
              <span class="text-[10px] uppercase font-bold text-glowPurple-dim bg-glowPurple-light px-2 py-0.5 rounded">
                Lớp: {{ activeLayer === 'TOP' ? 'Áo' : activeLayer === 'BOTTOM' ? 'Quần / Váy' : activeLayer === 'OUTERWEAR' ? 'Áo khoác' : activeLayer === 'DRESS' ? 'Váy liền' : activeLayer === 'SHOES' ? 'Giày dép' : activeLayer }}
              </span>
            </div>

            <div>
              <div class="flex justify-between text-xs mb-1">
                <span class="text-slate-500">Tỉ lệ kích thước (Scale)</span>
                <span class="text-slate-800 font-bold">{{ getLayerProp('scale') }}x</span>
              </div>
              <input 
                :value="getLayerProp('scale')" 
                @input="updateLayerProp('scale', $event.target.value)" 
                type="range" min="0.2" max="2.0" step="0.02" 
                class="w-full h-1 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-glowPurple"
              />
            </div>

            <div>
              <div class="flex justify-between text-xs mb-1">
                <span class="text-slate-500">Dịch chuyển ngang (Trục X)</span>
                <span class="text-slate-800 font-bold">{{ getLayerProp('x') }} px</span>
              </div>
              <input 
                :value="getLayerProp('x')" 
                @input="updateLayerProp('x', $event.target.value)" 
                type="range" min="-100" max="100" step="1" 
                class="w-full h-1 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-glowPurple"
              />
            </div>

            <div>
              <div class="flex justify-between text-xs mb-1">
                <span class="text-slate-500">Dịch chuyển dọc (Trục Y)</span>
                <span class="text-slate-800 font-bold">{{ getLayerProp('y') }} px</span>
              </div>
              <input 
                :value="getLayerProp('y')" 
                @input="updateLayerProp('y', $event.target.value)" 
                type="range" min="-150" max="150" step="1" 
                class="w-full h-1 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-glowPurple"
              />
            </div>
            
            <button 
              @click="saveOutfitCombo" 
              class="w-full py-2.5 bg-glowPurple hover:bg-glowPurple-dim text-white text-xs font-bold rounded-xl tracking-wider transition-all mt-4"
            >
              LƯU BỘ PHỐI VÀO TỦ ĐỒ
            </button>
          </div>
        </div>
      </div>

      <!-- Right Panel: Clothes Selector Drawer (cols: 3) -->
      <div class="lg:col-span-3 space-y-4">
        <div class="glass-card rounded-3xl p-5 flex flex-col max-h-[500px]">
          <h3 class="text-xs font-extrabold text-slate-900 tracking-wider uppercase mb-4">DANH SÁCH TRANG PHỤC</h3>
          
          <div class="overflow-y-auto space-y-3 pr-1 flex-1">
            <div v-for="cat in ['TOP', 'BOTTOM', 'OUTERWEAR', 'DRESS', 'SHOES']" :key="cat" class="border-b border-slate-100 pb-2">
              <div class="flex justify-between items-center text-xs font-bold text-slate-700 uppercase py-1">
                <span>{{ cat === 'TOP' ? 'Áo' : cat === 'BOTTOM' ? 'Quần & Váy' : cat === 'OUTERWEAR' ? 'Áo khoác' : cat === 'DRESS' ? 'Váy liền' : cat === 'SHOES' ? 'Giày dép' : cat }}</span>
                <span class="text-[10px] text-slate-400">({{ getItemsByCategory(cat).length }})</span>
              </div>
              
              <div class="grid grid-cols-3 gap-2 mt-2">
                <div 
                  v-for="item in getItemsByCategory(cat)" 
                  :key="item.id"
                  @click="selectGarment(cat, item)"
                  :class="[
                    'aspect-square bg-slate-50 border rounded-xl flex items-center justify-center p-1.5 cursor-pointer relative overflow-hidden transition-all duration-300 hover:bg-slate-100',
                    isItemSelected(cat, item.id) ? 'border-glowGreen shadow-glow-green scale-95 bg-slate-100' : 'border-slate-200'
                  ]"
                >
                  <img 
                    :src="'http://localhost:8081' + item.imageUrl" 
                    class="max-w-[90%] max-h-[90%] object-contain"
                    alt="garment"
                  />
                  <span 
                    class="absolute bottom-1 right-1 w-2 h-2 rounded-full border border-white/20" 
                    :style="{ backgroundColor: item.primaryColor }"
                  ></span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>

    <!-- Alert / Toast -->
    <div 
      v-if="toast" 
      class="fixed bottom-20 right-4 md:right-8 glass-card border border-glowGreen/20 text-slate-800 rounded-xl px-5 py-3 shadow-glow-green text-xs font-bold tracking-wider fade-in z-50 flex items-center gap-2"
    >
      <span class="text-glowGreen-dim">✓</span> {{ toast }}
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import axios from 'axios'

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update-user'])

const mannequinCanvas = ref(null)
const closetItems = ref([])
const activeLayer = ref(null)
const savingMeas = ref(false)
const toast = ref(null)

// Mannequin morph coordinates
const measurements = reactive({
  height: 168,
  bust: 90,
  waist: 70,
  hips: 95
})

// Layers
const layers = reactive({
  TOP: { item: null, x: 0, y: 0, scale: 0.8, image: null },
  BOTTOM: { item: null, x: 0, y: 38, scale: 0.85, image: null },
  OUTERWEAR: { item: null, x: 0, y: 0, scale: 0.85, image: null },
  DRESS: { item: null, x: 0, y: 15, scale: 0.8, image: null },
  SHOES: { item: null, x: 0, y: 110, scale: 0.75, image: null }
})

const getItemsByCategory = (category) => {
  return closetItems.value.filter(item => item.category === category)
}

const isItemSelected = (category, itemId) => {
  return layers[category].item?.id === itemId
}

const fetchWardrobe = async () => {
  try {
    const res = await axios.get(`http://localhost:8081/api/closet/items`, {
      params: { userId: props.user.id }
    })
    closetItems.value = res.data
  } catch (err) {
    console.error('Failed to load wardrobe', err)
  }
}

const fetchMeasurements = () => {
  measurements.height = props.user.height || 168
  measurements.bust = props.user.bust || 90
  measurements.waist = props.user.waist || 70
  measurements.hips = props.user.hips || 95
}

const saveMeasurements = async () => {
  savingMeas.value = true
  try {
    await axios.put(`http://localhost:8081/api/auth/profile/${props.user.id}/measurements`, {
      height: measurements.height,
      bust: measurements.bust,
      waist: measurements.waist,
      hips: measurements.hips
    })
    
    const updatedUser = {
      ...props.user,
      height: measurements.height,
      bust: measurements.bust,
      waist: measurements.waist,
      hips: measurements.hips
    }
    emit('update-user', updatedUser)
    showToast('Đã lưu số đo ma-nơ-canh!')
  } catch (err) {
    console.error('Failed to save measurements', err)
    const updatedUser = {
      ...props.user,
      height: measurements.height,
      bust: measurements.bust,
      waist: measurements.waist,
      hips: measurements.hips
    }
    emit('update-user', updatedUser)
    showToast('Đã lưu số đo cục bộ!')
  } finally {
    savingMeas.value = false
  }
}

const selectGarment = (category, item) => {
  // If clicking on already selected item, deselect it
  if (layers[category].item?.id === item.id) {
    layers[category].item = null
    layers[category].image = null
    activeLayer.value = null
    draw()
    return
  }

  // Mutual exclusion: if Dress is selected, deselect Tops and Bottoms, and vice versa
  if (category === 'DRESS') {
    layers.TOP.item = null; layers.TOP.image = null
    layers.BOTTOM.item = null; layers.BOTTOM.image = null
  } else if (category === 'TOP' || category === 'BOTTOM') {
    layers.DRESS.item = null; layers.DRESS.image = null
  }

  layers[category].item = item
  activeLayer.value = category

  // Default coordinate offsets for each type
  if (category === 'TOP') {
    layers.TOP.x = 0; layers.TOP.y = -5; layers.TOP.scale = 0.8
  } else if (category === 'BOTTOM') {
    layers.BOTTOM.x = 0; layers.BOTTOM.y = 48; layers.BOTTOM.scale = 0.82
  } else if (category === 'OUTERWEAR') {
    layers.OUTERWEAR.x = 0; layers.OUTERWEAR.y = -2; layers.OUTERWEAR.scale = 0.85
  } else if (category === 'DRESS') {
    layers.DRESS.x = 0; layers.DRESS.y = 20; layers.DRESS.scale = 0.82
  } else if (category === 'SHOES') {
    layers.SHOES.x = 0; layers.SHOES.y = 120; layers.SHOES.scale = 0.65
  }

  const img = new Image()
  img.crossOrigin = 'anonymous'
  img.onload = () => {
    layers[category].image = img
    draw()
  }
  img.src = 'http://localhost:8081' + item.imageUrl
}

const getLayerProp = (prop) => {
  if (!activeLayer.value) return 0
  return layers[activeLayer.value][prop]
}

const updateLayerProp = (prop, value) => {
  if (!activeLayer.value) return
  layers[activeLayer.value][prop] = Number(value)
  draw()
}

const clearCanvasGarments = () => {
  for (const cat in layers) {
    layers[cat].item = null
    layers[cat].image = null
  }
  activeLayer.value = null
  draw()
}

const saveOutfitCombo = async () => {
  const activeItems = []
  for (const cat in layers) {
    if (layers[cat].item) {
      activeItems.push(layers[cat].item)
    }
  }

  if (activeItems.length === 0) {
    alert('Vui lòng thử ít nhất một trang phục lên ma-nơ-canh trước khi lưu.')
    return
  }

  try {
    const outfitName = activeItems.map(i => i.subCategory).join(' + ')
    await axios.post(`http://localhost:8081/api/styling/outfits`, {
      userId: props.user.id,
      name: outfitName,
      contextTag: 'Curated Look',
      itemIds: activeItems.map(i => i.id)
    })
    showToast('Đã lưu bộ trang phục phối chọn!')
  } catch (err) {
    console.error('Failed to save outfit combination', err)
  }
}

// Drawing Logic
const draw = () => {
  const canvas = mannequinCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  
  // Clear
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  // Draw premium grid background
  ctx.strokeStyle = 'rgba(0, 0, 0, 0.03)'
  ctx.lineWidth = 1
  for (let x = 0; x < canvas.width; x += 20) {
    ctx.beginPath()
    ctx.moveTo(x, 0)
    ctx.lineTo(x, canvas.height)
    ctx.stroke()
  }
  for (let y = 0; y < canvas.height; y += 20) {
    ctx.beginPath()
    ctx.moveTo(0, y)
    ctx.lineTo(canvas.width, y)
    ctx.stroke()
  }

  // Draw Mannequin
  drawMannequinBody(ctx, canvas)

  // Draw Layers in bottom-up z-index order
  const order = ['BOTTOM', 'DRESS', 'TOP', 'OUTERWEAR', 'SHOES']
  order.forEach(cat => {
    const layer = layers[cat]
    if (layer.image && layer.item) {
      ctx.save()
      const w = layer.image.width * layer.scale
      const h = layer.image.height * layer.scale
      
      // Center position offsets relative to canvas center
      const centerX = canvas.width / 2 + layer.x - w / 2
      const centerY = canvas.height / 2 + layer.y - h / 2
      
      // Draw image
      ctx.drawImage(layer.image, centerX, centerY, w, h)
      
      // Add neon highlight around active editing layer
      if (activeLayer.value === cat) {
        ctx.strokeStyle = '#10B981'
        ctx.lineWidth = 1.5
        ctx.setLineDash([4, 4])
        ctx.strokeRect(centerX - 4, centerY - 4, w + 8, h + 8)
      }
      ctx.restore()
    }
  })
}

const drawMannequinBody = (ctx, canvas) => {
  const midX = canvas.width / 2
  const midY = canvas.height / 2
  
  // Scaling factors based on measurements (normalized around averages)
  const hScale = (measurements.height / 170.0) // height scale
  const bScale = (measurements.bust / 90.0)   // bust width scale
  const wScale = (measurements.waist / 70.0)  // waist width scale
  const hipScale = (measurements.hips / 95.0)  // hip width scale

  ctx.save()
  
  // Gradient for smooth body styling (glowing violet aesthetic)
  const bodyGrad = ctx.createLinearGradient(0, 0, 0, canvas.height)
  bodyGrad.addColorStop(0, 'rgba(124, 58, 237, 0.08)')
  bodyGrad.addColorStop(1, 'rgba(16, 185, 129, 0.03)')
  ctx.fillStyle = bodyGrad

  // Neon violet outline
  ctx.strokeStyle = 'rgba(124, 58, 237, 0.4)'
  ctx.lineWidth = 1.8
  ctx.shadowBlur = 10
  ctx.shadowColor = 'rgba(124, 58, 237, 0.2)'

  ctx.beginPath()
  
  // Head
  const headRadius = 14 * hScale
  const headY = midY - 140 * hScale
  ctx.arc(midX, headY, headRadius, 0, Math.PI * 2)
  ctx.fill()
  ctx.stroke()
  
  // Neck
  ctx.beginPath()
  const neckY = headY + headRadius
  ctx.moveTo(midX - 5, neckY)
  ctx.lineTo(midX - 5, neckY + 10)
  ctx.lineTo(midX + 5, neckY + 10)
  ctx.lineTo(midX + 5, neckY)
  ctx.closePath()
  ctx.fill()
  ctx.stroke()

  // Torso / Body contour
  ctx.beginPath()
  const shY = neckY + 10                 // shoulder Y
  const shW = 34 * bScale                // shoulder half width
  const chY = shY + 28 * hScale          // chest Y
  const chW = 28 * bScale                // chest half width
  const waY = chY + 38 * hScale          // waist Y
  const waW = 18 * wScale                // waist half width
  const hpY = waY + 38 * hScale          // hip Y
  const hpW = 30 * hipScale              // hip half width
  const legY = hpY + 100 * hScale        // ankle Y
  
  // Draw left side contour
  ctx.moveTo(midX, shY)
  ctx.lineTo(midX - shW, shY) // shoulder left
  ctx.bezierCurveTo(midX - chW - 5, chY, midX - waW - 2, waY - 5, midX - waW, waY) // chest to waist left
  ctx.bezierCurveTo(midX - waW, waY + 8, midX - hpW - 2, hpY - 10, midX - hpW, hpY) // waist to hips left
  ctx.lineTo(midX - hpW + 5, hpY + 20) // crotch down left leg inner
  
  // Left leg
  ctx.lineTo(midX - 16, legY) // ankle left
  ctx.lineTo(midX - 4, legY)
  ctx.lineTo(midX - 6, hpY + 28) // inner leg crotch left
  
  // Crotch point
  ctx.lineTo(midX, hpY + 22)
  
  // Right leg
  ctx.lineTo(midX + 6, hpY + 28)
  ctx.lineTo(midX + 4, legY)
  ctx.lineTo(midX + 16, legY) // ankle right
  ctx.lineTo(midX + hpW - 5, hpY + 20)
  
  // Draw right side contour
  ctx.lineTo(midX + hpW, hpY) // hips right
  ctx.bezierCurveTo(midX + hpW + 2, hpY - 10, midX + waW, waY + 8, midX + waW, waY) // hips to waist right
  ctx.bezierCurveTo(midX + waW + 2, waY - 5, midX + chW + 5, chY, midX + shW, shY) // waist to chest right
  ctx.closePath()
  ctx.fill()
  ctx.stroke()
  
  ctx.restore()
}

const showToast = (msg) => {
  toast.value = msg
  setTimeout(() => {
    toast.value = null
  }, 3000)
}

onMounted(() => {
  fetchWardrobe()
  fetchMeasurements()
  nextTick(() => {
    draw()
  })
})
</script>
