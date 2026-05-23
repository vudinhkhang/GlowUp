<template>
  <div class="space-y-6 fade-in p-1">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h2 class="text-2xl font-bold tracking-tight text-slate-900 font-sans font-extrabold">Nhật ký phối đồ</h2>
        <p class="text-xs text-slate-500">Theo dõi lịch sử mặc và xoay vòng trang phục hàng ngày để tối ưu hóa tủ đồ của bạn.</p>
      </div>
    </div>

    <!-- Calendar Card -->
    <div class="glass-card rounded-3xl p-6 relative overflow-hidden">
      <!-- Calendar Header -->
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-md font-bold text-slate-900 uppercase tracking-wider">
          {{ currentMonthName }} năm {{ currentYear }}
        </h3>
        <div class="flex gap-2">
          <button 
            @click="prevMonth" 
            class="px-3 py-1.5 bg-slate-100 border border-slate-200 hover:bg-slate-200 text-xs font-bold rounded-lg text-slate-600 transition-all"
          >
            ◄ Trước
          </button>
          <button 
            @click="nextMonth" 
            class="px-3 py-1.5 bg-slate-100 border border-slate-200 hover:bg-slate-200 text-xs font-bold rounded-lg text-slate-600 transition-all"
          >
            Sau ►
          </button>
        </div>
      </div>

      <!-- Calendar Grid -->
      <div class="grid grid-cols-7 gap-2 text-center text-xs font-extrabold text-slate-400 uppercase tracking-widest mb-4">
        <span>CN</span>
        <span>T2</span>
        <span>T3</span>
        <span>T4</span>
        <span>T5</span>
        <span>T6</span>
        <span>T7</span>
      </div>

      <div class="grid grid-cols-7 gap-2">
        <!-- Empty spacers for month start offset -->
        <div 
          v-for="blank in blankDays" 
          :key="'blank-' + blank"
          class="aspect-square bg-transparent rounded-2xl"
        ></div>

        <!-- Calendar Days -->
        <div 
          v-for="day in daysInMonth" 
          :key="'day-' + day"
          @click="selectDay(day)"
          :class="[
            'aspect-square bg-slate-50/50 border border-slate-100 rounded-2xl p-2 flex flex-col justify-between cursor-pointer transition-all duration-300 relative overflow-hidden',
            isToday(day) ? 'bg-glowGreen-light border-glowGreen/30' : 'hover:bg-slate-50 hover:border-slate-200',
            hasOutfitWorn(day) ? 'bg-glowPurple-light/50 border-glowPurple/25 shadow-glow-purple' : ''
          ]"
        >
          <!-- Day number -->
          <span 
            :class="[
              'text-[10px] font-bold self-start',
              isToday(day) ? 'text-glowGreen-dim font-extrabold' : 'text-slate-500'
            ]"
          >
            {{ day }}
          </span>

          <!-- Outfit Worn Thumbnails Overlap -->
          <div v-if="getOutfitForDay(day)" class="flex -space-x-3 items-center justify-center translate-y-1">
            <div 
              v-for="item in getOutfitForDay(day).items.slice(0, 3)" 
              :key="item.id"
              class="w-6 h-6 rounded-full bg-slate-100 border border-slate-200 shadow flex items-center justify-center overflow-hidden shrink-0"
              :title="item.subCategory"
            >
              <img 
                :src="'http://localhost:8081' + item.imageUrl" 
                class="w-[85%] h-[85%] object-contain"
              />
            </div>
            <!-- More indicator -->
            <span 
              v-if="getOutfitForDay(day).items.length > 3" 
              class="text-[7px] text-slate-500 font-bold bg-slate-100 w-4 h-4 rounded-full border border-slate-200 flex items-center justify-center shrink-0 -translate-x-1"
            >
              +{{ getOutfitForDay(day).items.length - 3 }}
            </span>
          </div>

          <!-- Small dot indicator -->
          <span 
            v-if="hasOutfitWorn(day)" 
            class="absolute bottom-1 right-2 w-1.5 h-1.5 rounded-full bg-glowPurple"
          ></span>
        </div>
      </div>
    </div>

    <!-- Day Outfit Detail Modal -->
    <div v-if="selectedDayVal" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50 fade-in">
      <div class="w-full max-w-md glass-card rounded-3xl p-6 relative overflow-hidden">
        <!-- Glow -->
        <div class="absolute -top-32 -left-32 w-64 h-64 rounded-full bg-glowPurple/5 blur-3xl"></div>

        <!-- Header -->
        <div class="flex justify-between items-center mb-6 relative">
          <h3 class="text-md font-bold tracking-tight text-slate-900 font-extrabold uppercase">
            NHẬT KÝ NGÀY {{ formatVietnameseDateString(selectedDayVal) }}
          </h3>
          <button @click="selectedDayVal = null" class="text-slate-500 hover:text-slate-700 text-sm font-semibold">✕</button>
        </div>

        <div v-if="getOutfitForDay(selectedDayVal)" class="space-y-4 relative z-10">
          <div class="bg-slate-50 border border-slate-200 rounded-2xl p-4">
            <h4 class="text-xs font-bold text-slate-500 uppercase tracking-wider">BỘ TRANG PHỤC ĐÃ MẶC</h4>
            <div class="text-sm font-extrabold text-slate-900 mt-1">
              {{ getOutfitForDay(selectedDayVal).name }}
            </div>
            <span class="text-[9px] px-2 py-0.5 mt-2 inline-block rounded-md bg-glowPurple-light text-glowPurple-dim font-bold">
              {{ getOutfitForDay(selectedDayVal).contextTag === 'Daily Rotate' ? 'Xoay vòng tủ đồ' : getOutfitForDay(selectedDayVal).contextTag === 'Curated Look' ? 'Tự phối chọn' : getOutfitForDay(selectedDayVal).contextTag }}
            </span>
          </div>

          <!-- Items Row -->
          <div class="grid grid-cols-3 gap-3">
            <div 
              v-for="item in getOutfitForDay(selectedDayVal).items" 
              :key="item.id"
              class="bg-slate-50 border border-slate-100 rounded-xl p-2 flex flex-col items-center"
            >
              <div class="w-12 h-12 flex items-center justify-center mb-1">
                <img 
                  :src="'http://localhost:8081' + item.imageUrl" 
                  class="max-w-full max-h-full object-contain"
                />
              </div>
              <span class="text-[8px] uppercase tracking-wider text-slate-600 text-center font-bold truncate w-full">
                {{ item.subCategory }}
              </span>
            </div>
          </div>

          <!-- Actions -->
          <div class="pt-4 flex gap-2">
            <button 
              @click="deleteLogForDay(selectedDayVal)" 
              class="w-full py-2.5 bg-red-50 hover:bg-red-100 border border-red-200 text-red-600 text-xs font-bold rounded-xl tracking-wider transition-all"
            >
              XÓA NHẬT KÝ NGÀY NÀY
            </button>
          </div>
        </div>

        <div v-else class="text-center py-8 text-slate-500 relative z-10">
          <p class="text-xs mb-4">Bạn chưa lưu lịch sử phối đồ cho ngày này.</p>
          <button 
            @click="selectedDayVal = null; $emit('switch-tab', 'dashboard')" 
            class="px-4 py-2 bg-glowGreen text-white text-xs font-bold rounded-xl tracking-wider glow-btn-green"
          >
            CHỌN BỘ ĐỒ GỢI Ý
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
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
})

defineEmits(['switch-tab'])

const calendarLogs = ref([])
const currentDate = ref(new Date())
const selectedDayVal = ref(null)
const toast = ref(null)

const currentYear = computed(() => currentDate.value.getFullYear())
const currentMonth = computed(() => currentDate.value.getMonth()) // 0-indexed

const months = [
  'Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6',
  'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'
]
const currentMonthName = computed(() => months[currentMonth.value])

const daysInMonth = computed(() => {
  return new Date(currentYear.value, currentMonth.value + 1, 0).getDate()
})

const blankDays = computed(() => {
  const firstDayIndex = new Date(currentYear.value, currentMonth.value, 1).getDay()
  return firstDayIndex
})

const fetchLogs = async () => {
  try {
    const res = await axios.get(`http://localhost:8081/api/calendar/logs`, {
      params: { userId: props.user.id }
    })
    calendarLogs.value = res.data
  } catch (err) {
    console.error('Failed to load calendar logs', err)
  }
}

const prevMonth = () => {
  currentDate.value = new Date(currentYear.value, currentMonth.value - 1, 1)
}

const nextMonth = () => {
  currentDate.value = new Date(currentYear.value, currentMonth.value + 1, 1)
}

const selectDay = (day) => {
  selectedDayVal.value = day
}

const isToday = (day) => {
  const today = new Date()
  return today.getDate() === day &&
         today.getMonth() === currentMonth.value &&
         today.getFullYear() === currentYear.value
}

const getOutfitForDay = (day) => {
  if (!day) return null
  const dayStr = formatDateString(day)
  const log = calendarLogs.value.find(l => l.date === dayStr)
  return log ? log.outfit : null
}

const hasOutfitWorn = (day) => {
  return getOutfitForDay(day) !== null
}

const formatDateString = (day) => {
  const monthPad = String(currentMonth.value + 1).padStart(2, '0')
  const dayPad = String(day).padStart(2, '0')
  return `${currentYear.value}-${monthPad}-${dayPad}`
}

const formatVietnameseDateString = (day) => {
  return `${day}/${currentMonth.value + 1}/${currentYear.value}`
}

const deleteLogForDay = async (day) => {
  if (!confirm('Bạn có muốn xóa lịch sử phối đồ của ngày này khỏi lịch không?')) return
  const dayStr = formatDateString(day)
  try {
    await axios.delete(`http://localhost:8081/api/calendar/log`, {
      params: { userId: props.user.id, date: dayStr }
    })
    showToast('Đã xóa lịch sử phối đồ.')
    fetchLogs()
    selectedDayVal.value = null
  } catch (err) {
    console.error('Failed to delete calendar log', err)
  }
}

const showToast = (msg) => {
  toast.value = msg
  setTimeout(() => {
    toast.value = null
  }, 3000)
}

onMounted(fetchLogs)
</script>
