<template>
  <div class="space-y-6 fade-in p-1">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h2 class="text-2xl font-bold tracking-tight text-slate-900 font-sans font-extrabold">Tủ đồ của tôi</h2>
        <p class="text-xs text-slate-500">Quản lý và sắp xếp các trang phục đã được xử lý tách nền của bạn.</p>
      </div>
      <button 
        @click="openModal" 
        class="px-5 py-2.5 bg-glowGreen text-white text-xs font-bold rounded-xl tracking-wider glow-btn-green flex items-center gap-1"
      >
        <span>+</span> QUÉT TRANG PHỤC MỚI
      </button>
    </div>

    <!-- Category Filters -->
    <div class="flex gap-2 overflow-x-auto pb-2 border-b border-slate-100 no-scrollbar">
      <button 
        v-for="cat in categories" 
        :key="cat.value"
        @click="selectedCategory = cat.value"
        :class="[
          'px-4 py-2 text-xs font-bold rounded-xl tracking-wider transition-all border shrink-0',
          selectedCategory === cat.value 
            ? 'bg-glowGreen text-white border-glowGreen shadow-sm' 
            : 'bg-white text-slate-500 border-slate-200 hover:bg-slate-100 hover:text-slate-800'
        ]"
      >
        {{ cat.label }}
      </button>
    </div>

    <!-- Empty State -->
    <div v-if="filteredItems.length === 0" class="glass-card rounded-3xl p-12 text-center flex flex-col items-center justify-center min-h-[300px]">
      <div class="w-16 h-16 rounded-full bg-slate-50 flex items-center justify-center mb-4 border border-slate-200">
        <span class="text-2xl">👗</span>
      </div>
      <h3 class="text-md font-bold text-slate-900 mb-2">Không có trang phục nào trong danh mục này</h3>
      <p class="text-xs text-slate-500 max-w-sm mb-6">
        Hãy quét thêm quần áo hoặc thay đổi bộ lọc phân loại để hoàn thiện tủ đồ số của bạn.
      </p>
    </div>

    <!-- Clothes Grid -->
    <div v-else class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-6">
      <div 
        v-for="item in filteredItems" 
        :key="item.id"
        class="glass-card rounded-2xl p-4 flex flex-col justify-between group glass-card-hover relative"
      >
        <!-- Floating Garment Artifact -->
        <div class="w-full aspect-square bg-slate-50/30 border border-slate-100 rounded-xl flex items-center justify-center mb-3 relative overflow-hidden">
          <img 
            :src="'http://localhost:8081' + item.imageUrl" 
            class="max-w-[85%] max-h-[85%] object-contain floating-artifact" 
            alt="Clothing item"
          />
          
          <!-- Delete Hover Button -->
          <button 
            @click="deleteItem(item.id)" 
            class="absolute top-2 right-2 p-1.5 bg-red-50 hover:bg-red-100 border border-red-200 text-red-600 rounded-lg text-[10px] font-bold opacity-0 group-hover:opacity-100 transition-opacity"
            title="Xóa trang phục"
          >
            ✕
          </button>
        </div>

        <div>
          <!-- Tags and Details -->
          <div class="flex justify-between items-start gap-1">
            <span class="text-xs font-bold text-slate-800 uppercase tracking-wider truncate">
              {{ item.subCategory || 'Trang phục' }}
            </span>
            <!-- Primary color indicator -->
            <span 
              class="w-3.5 h-3.5 rounded-full border border-slate-200 shadow-sm shrink-0"
              :style="{ backgroundColor: item.primaryColor }"
              :title="item.primaryColor"
            ></span>
          </div>

          <div class="flex flex-wrap gap-1 mt-2">
            <span class="text-[9px] px-2 py-0.5 rounded-md bg-slate-100 border border-slate-200 text-slate-600 font-medium">
              {{ item.category === 'TOP' ? 'Áo' : item.category === 'BOTTOM' ? 'Quần/Váy' : item.category === 'OUTERWEAR' ? 'Áo khoác' : item.category === 'DRESS' ? 'Váy liền' : item.category === 'SHOES' ? 'Giày dép' : item.category }}
            </span>
            <span 
              v-for="season in item.seasonTags" 
              :key="season"
              class="text-[9px] px-2 py-0.5 rounded-md bg-glowPurple-light border border-glowPurple/10 text-glowPurple-dim font-bold"
            >
              {{ season === 'Spring' ? 'Xuân' : season === 'Summer' ? 'Hạ' : season === 'Fall' ? 'Thu' : season === 'Winter' ? 'Đông' : season }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Upload & Scan Modal -->
    <div v-if="modalOpen" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50 fade-in">
      <div class="w-full max-w-lg glass-card rounded-3xl p-6 relative overflow-hidden flex flex-col max-h-[90vh]">
        <!-- Glows -->
        <div class="absolute -top-32 -left-32 w-64 h-64 rounded-full bg-glowPurple/5 blur-3xl"></div>
        <div class="absolute -bottom-32 -right-32 w-64 h-64 rounded-full bg-glowGreen/5 blur-3xl"></div>

        <!-- Title -->
        <div class="flex justify-between items-center mb-6 relative">
          <h3 class="text-lg font-bold tracking-tight text-slate-900 font-extrabold uppercase">QUÉT TRANG PHỤC BẰNG AI</h3>
          <button @click="closeModal" class="text-slate-500 hover:text-slate-700 text-sm font-semibold">Đóng ✕</button>
        </div>

        <div class="overflow-y-auto space-y-4 pr-1 relative z-10 flex-1">
          <!-- Image Upload Box -->
          <div v-if="!scannedResult && !scanning" class="border-2 border-dashed border-slate-200 hover:border-glowGreen/40 rounded-2xl p-8 text-center cursor-pointer transition-all bg-slate-50/50" @click="triggerFileSelect">
            <input 
              type="file" 
              ref="fileInput" 
              class="hidden" 
              accept="image/*" 
              @change="handleFileUpload"
            />
            <div class="text-3xl mb-3">📸</div>
            <p class="text-xs text-slate-800 font-bold mb-1">Nhấp để chụp ảnh hoặc tải lên hình ảnh trang phục</p>
            <p class="text-[10px] text-slate-500">Hỗ trợ PNG, JPG, JPEG. Kích thước tối đa: 10MB.</p>
          </div>

          <!-- Scanning Overlay / Pulse animation -->
          <div v-if="scanning" class="relative border border-slate-100 rounded-2xl p-6 flex flex-col items-center justify-center bg-slate-50/30 overflow-hidden min-h-[220px]">
            <div class="absolute inset-x-0 h-4 scanning-line"></div>
            <div class="text-3xl animate-bounce mb-3">🔮</div>
            <h4 class="text-xs font-extrabold text-glowGreen-dim tracking-wider uppercase mb-1">AI ĐANG PHÂN TÍCH & TÁCH NỀN...</h4>
            <p class="text-[10px] text-slate-500">Đang tự động xóa phông nền và nhận dạng mã màu sắc.</p>
          </div>

          <!-- Scanned Results / Editor Form -->
          <div v-if="scannedResult && !scanning" class="space-y-4 slide-up">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 items-center">
              
              <!-- Result Image Preview -->
              <div class="bg-slate-50 border border-slate-100 rounded-2xl aspect-square flex items-center justify-center p-4 relative overflow-hidden">
                <img 
                  :src="'http://localhost:8081' + scannedResult.imageUrl" 
                  class="max-w-full max-h-full object-contain floating-artifact" 
                  alt="Scanned garment"
                />
                <span class="absolute bottom-2 left-2 text-[8px] bg-slate-900/80 px-2 py-0.5 rounded text-glowGreen font-bold tracking-widest uppercase">
                  ĐÃ TÁCH NỀN
                </span>
              </div>

              <!-- Details Editor -->
              <div class="space-y-3">
                <!-- Color Extract Info -->
                <div class="flex items-center gap-3 bg-slate-50 border border-slate-100 rounded-xl p-3">
                  <span 
                    class="w-6 h-6 rounded-full border border-slate-200 shadow-sm shrink-0" 
                    :style="{ backgroundColor: scannedResult.primaryColor }"
                  ></span>
                  <div>
                    <div class="text-[10px] text-slate-500 font-bold uppercase tracking-wider">MÀU SẮC CHỦ ĐẠO</div>
                    <div class="text-xs font-bold text-slate-800 uppercase">{{ scannedResult.primaryColor }}</div>
                  </div>
                </div>

                <!-- Category selection -->
                <div>
                  <label class="block text-[10px] font-bold text-slate-500 uppercase tracking-wider mb-1">Danh mục</label>
                  <select 
                    v-model="editForm.category" 
                    class="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs text-slate-800 focus:outline-none focus:border-glowGreen"
                  >
                    <option value="TOP">ÁO (TOP)</option>
                    <option value="BOTTOM">QUẦN / VÁY (BOTTOM)</option>
                    <option value="OUTERWEAR">ÁO KHOÁC (OUTERWEAR)</option>
                    <option value="DRESS">VÁY LIỀN (DRESS)</option>
                    <option value="SHOES">GIÀY DÉP (SHOES)</option>
                  </select>
                </div>

                <!-- Sub-Category input -->
                <div>
                  <label class="block text-[10px] font-bold text-slate-500 uppercase tracking-wider mb-1">Tên loại đồ</label>
                  <input 
                    v-model="editForm.subCategory" 
                    type="text" 
                    placeholder="ví dụ: Áo thun trắng, Quần jeans xanh" 
                    class="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs text-slate-800 focus:outline-none focus:border-glowGreen"
                  />
                </div>
              </div>

            </div>

            <!-- Season tags checklist -->
            <div>
              <label class="block text-[10px] font-bold text-slate-500 uppercase tracking-wider mb-2">Mùa phù hợp</label>
              <div class="grid grid-cols-4 gap-2">
                <button 
                  v-for="s in [
                    { key: 'Spring', label: 'XUÂN' },
                    { key: 'Summer', label: 'HẠ' },
                    { key: 'Fall', label: 'THU' },
                    { key: 'Winter', label: 'ĐÔNG' }
                  ]" 
                  :key="s.key"
                  type="button"
                  @click="toggleSeason(s.key)"
                  :class="[
                    'py-2 text-[10px] font-bold rounded-xl tracking-wider border transition-all',
                    editForm.seasonTags.includes(s.key) 
                      ? 'bg-glowPurple/10 border-glowPurple text-glowPurple-dim shadow-glow-purple' 
                      : 'bg-slate-50 border-slate-200 text-slate-500 hover:bg-slate-100'
                  ]"
                >
                  {{ s.label }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Modal Footer Actions -->
        <div v-if="scannedResult && !scanning" class="mt-6 pt-4 border-t border-slate-100 flex gap-2 relative z-10">
          <button 
            @click="resetScan" 
            class="w-1/3 py-2.5 bg-slate-50 border border-slate-200 text-slate-600 font-bold rounded-xl text-xs tracking-wider hover:bg-slate-100"
          >
            QUÉT LẠI
          </button>
          <button 
            @click="saveItem" 
            :disabled="saving"
            class="w-2/3 py-2.5 bg-glowGreen text-white font-bold rounded-xl text-xs tracking-wider glow-btn-green"
          >
            <span v-if="saving">ĐANG LƯU...</span>
            <span v-else>THÊM VÀO TỦ ĐỒ</span>
          </button>
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
import { ref, reactive, computed, onMounted } from 'vue'
import axios from 'axios'

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
})

const items = ref([])
const selectedCategory = ref('ALL')
const modalOpen = ref(false)
const scanning = ref(false)
const saving = ref(false)
const scannedResult = ref(null)
const fileInput = ref(null)
const toast = ref(null)

const categories = [
  { label: 'TẤT CẢ', value: 'ALL' },
  { label: 'ÁO', value: 'TOP' },
  { label: 'QUẦN & VÁY', value: 'BOTTOM' },
  { label: 'ÁO KHOÁC', value: 'OUTERWEAR' },
  { label: 'VÁY LIỀN', value: 'DRESS' },
  { label: 'GIÀY DÉP', value: 'SHOES' }
]

const editForm = reactive({
  category: 'TOP',
  subCategory: '',
  seasonTags: ['Spring', 'Summer']
})

const filteredItems = computed(() => {
  if (selectedCategory.value === 'ALL') {
    return items.value
  }
  return items.value.filter(item => item.category === selectedCategory.value)
})

const fetchItems = async () => {
  try {
    const res = await axios.get(`http://localhost:8081/api/closet/items`, {
      params: { userId: props.user.id }
    })
    items.value = res.data
  } catch (err) {
    console.error('Failed to load closet items', err)
  }
}

const openModal = () => {
  modalOpen.value = true
  resetScan()
}

const closeModal = () => {
  modalOpen.value = false
  resetScan()
}

const resetScan = () => {
  scannedResult.value = null
  scanning.value = false
  saving.value = false
  editForm.category = 'TOP'
  editForm.subCategory = ''
  editForm.seasonTags = ['Spring', 'Summer']
}

const triggerFileSelect = () => {
  fileInput.value.click()
}

const handleFileUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  scanning.value = true
  const formData = new FormData()
  formData.append('file', file)

  try {
    const res = await axios.post(`http://localhost:8081/api/closet/scan`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    scannedResult.value = res.data
    
    // Autofill initial guess values
    editForm.subCategory = file.name.split('.')[0].replace(/[-_]/g, ' ')
  } catch (err) {
    console.error('Failed scanning garment', err)
    alert('Dịch vụ tách nền AI thất bại. Vui lòng xác minh rằng dịch vụ Python FastAPI đang hoạt động trên cổng 8000.')
    resetScan()
  } finally {
    scanning.value = false
  }
}

const toggleSeason = (season) => {
  const index = editForm.seasonTags.indexOf(season)
  if (index === -1) {
    editForm.seasonTags.push(season)
  } else {
    editForm.seasonTags.splice(index, 1)
  }
}

const saveItem = async () => {
  saving.value = true
  try {
    await axios.post(`http://localhost:8081/api/closet/items`, {
      userId: props.user.id,
      category: editForm.category,
      subCategory: editForm.subCategory || 'Trang phục',
      primaryColor: scannedResult.value.primaryColor,
      hslHue: scannedResult.value.hslHue,
      hslSaturation: scannedResult.value.hslSaturation,
      hslLightness: scannedResult.value.hslLightness,
      imageUrl: scannedResult.value.imageUrl,
      seasonTags: editForm.seasonTags
    })
    
    showToast('Đã lưu trang phục vào tủ đồ!')
    fetchItems()
    closeModal()
  } catch (err) {
    console.error('Failed to save item', err)
  } finally {
    saving.value = false
  }
}

const deleteItem = async (itemId) => {
  if (!confirm('Bạn có chắc chắn muốn xóa trang phục này khỏi tủ đồ không?')) return
  try {
    await axios.delete(`http://localhost:8081/api/closet/items/${itemId}`)
    showToast('Đã xóa trang phục.')
    fetchItems()
  } catch (err) {
    console.error('Failed to delete item', err)
  }
}

const showToast = (msg) => {
  toast.value = msg
  setTimeout(() => {
    toast.value = null
  }, 3000)
}

onMounted(fetchItems)
</script>
