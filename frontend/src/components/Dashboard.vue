<template>
  <div class="space-y-6 fade-in p-1">
    <!-- Header -->
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
      <div>
        <h2 class="text-2xl font-bold tracking-tight text-slate-900 font-sans">Xin chào, {{ user.name }}</h2>
        <p class="text-xs text-slate-500">Dưới đây là tóm tắt phong cách thời trang bền vững của bạn hôm nay.</p>
      </div>

      <!-- City & Weather status (mini widget) -->
      <div class="flex items-center gap-3 bg-white border border-slate-200 shadow-sm rounded-2xl px-4 py-2 text-sm">
        <span class="font-bold text-slate-800">{{ weather.temp }}°C</span>
        <span class="text-xs px-2 py-0.5 rounded-full bg-glowGreen-light text-glowGreen-dim font-bold uppercase tracking-wider">
          {{ weather.condition === 'Clear' ? 'Trời quang' : weather.condition === 'Clouds' ? 'Nhiều mây' : weather.condition === 'Rain' ? 'Có mưa' : weather.condition === 'Snow' ? 'Tuyết rơi' : weather.condition }}
        </span>
        <input 
          v-model="city" 
          @change="fetchWeather" 
          placeholder="Hà Nội" 
          class="bg-transparent border-none text-slate-700 font-semibold focus:outline-none w-20 text-right"
        />
      </div>
    </div>

    <!-- Weather Simulation Panel (Hidden by default, toggleable) -->
    <div class="glass-card rounded-2xl p-4 border border-slate-200">
      <div class="flex justify-between items-center cursor-pointer" @click="showWeatherSimulation = !showWeatherSimulation">
        <div class="flex items-center gap-2 text-xs font-semibold text-slate-700 tracking-wider uppercase">
          <span>⚙️ Trình giả lập thời tiết (Công cụ nhà phát triển)</span>
        </div>
        <span class="text-xs text-slate-500">{{ showWeatherSimulation ? 'Thu gọn ▲' : 'Mở rộng ▼' }}</span>
      </div>

      <div v-if="showWeatherSimulation" class="mt-4 grid grid-cols-1 md:grid-cols-3 gap-4 pt-4 border-t border-slate-100 slide-up">
        <div>
          <label class="block text-[10px] font-bold text-slate-500 uppercase tracking-widest mb-2">Nhiệt độ giả lập ({{ simTemp }}°C)</label>
          <input 
            v-model.number="simTemp" 
            type="range" 
            min="-10" 
            max="40" 
            step="1" 
            class="w-full h-1 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-glowGreen"
          />
        </div>
        <div>
          <label class="block text-[10px] font-bold text-slate-500 uppercase tracking-widest mb-2">Thời tiết giả lập</label>
          <select v-model="simCondition" class="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs text-slate-800 focus:outline-none focus:border-glowGreen">
            <option value="Clear">Trời quang / Nắng</option>
            <option value="Clouds">Nhiều mây</option>
            <option value="Rain">Mưa</option>
            <option value="Snow">Tuyết rơi</option>
          </select>
        </div>
        <div class="flex items-end">
          <button 
            @click="applySimulatedWeather" 
            class="w-full py-2 bg-glowPurple hover:bg-glowPurple-dim text-white text-xs font-bold rounded-xl tracking-wider transition-all"
          >
            ÁP DỤNG THỜI TIẾT
          </button>
        </div>
      </div>
    </div>

    <!-- Main Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      
      <!-- Recommended Outfit Card -->
      <div class="lg:col-span-2 glass-card rounded-3xl p-6 relative overflow-hidden flex flex-col justify-between min-h-[420px]">
        <!-- Top glow -->
        <div class="absolute -top-32 -left-32 w-64 h-64 rounded-full bg-glowGreen/5 blur-3xl"></div>

        <div>
          <div class="flex justify-between items-center mb-6 relative">
            <h3 class="text-lg font-bold tracking-tight text-slate-900">GỢI Ý PHỐI ĐỒ HÔM NAY</h3>
            <span v-if="recommendation" class="text-xs px-2.5 py-1 rounded-full bg-slate-50 border border-slate-200 text-slate-700 font-bold">
              {{ recommendation.harmonyType === 'Monochromatic' ? 'Đơn Sắc' : recommendation.harmonyType === 'Analogous' ? 'Tương Đồng' : recommendation.harmonyType === 'Complementary' ? 'Bổ Túc' : recommendation.harmonyType === 'Neutral' ? 'Trung Tính' : recommendation.harmonyType }}
            </span>
          </div>

          <!-- Empty Closet Fallback -->
          <div v-if="!recommendation" class="flex flex-col items-center justify-center py-12 text-center relative z-10">
            <div class="w-16 h-16 rounded-full bg-slate-50 flex items-center justify-center mb-4 border border-slate-200">
              <span class="text-2xl">🧥</span>
            </div>
            <h4 class="text-md font-bold text-slate-900 mb-2">Tủ đồ ảo của bạn đang trống</h4>
            <p class="text-xs text-slate-500 max-w-sm mb-6">
              Hãy chụp ảnh và quét trang phục của bạn bằng AI để nhận các gợi ý phối đồ phù hợp với thời tiết và hài hòa màu sắc.
            </p>
            <button 
              @click="$emit('switch-tab', 'closet')" 
              class="px-5 py-2.5 bg-glowGreen text-white text-xs font-bold rounded-xl tracking-wider glow-btn-green"
            >
              QUÉT TRANG PHỤC ĐẦU TIÊN
            </button>
          </div>

          <!-- Outfit Recommendation Display -->
          <div v-else class="grid grid-cols-2 md:grid-cols-4 gap-4 items-center justify-center my-4">
            <div 
              v-for="item in recommendation.items" 
              :key="item.id" 
              class="relative bg-slate-50/50 border border-slate-100 rounded-2xl p-3 flex flex-col items-center group transition-all duration-300 hover:bg-slate-50 hover:border-slate-200"
            >
              <div class="w-24 h-24 flex items-center justify-center relative mb-2">
                <img 
                  :src="'http://localhost:8081' + item.imageUrl" 
                  class="max-w-full max-h-full object-contain floating-artifact"
                  alt="Garment"
                />
              </div>
              <span class="text-[10px] uppercase font-bold text-slate-700 tracking-wider text-center">
                {{ item.subCategory }}
              </span>
              <!-- Color Indicator Dot -->
              <span 
                class="absolute top-2 right-2 w-3.5 h-3.5 rounded-full border border-slate-200 shadow-md"
                :style="{ backgroundColor: item.primaryColor }"
                :title="item.primaryColor"
              ></span>
            </div>
          </div>
        </div>

        <!-- Footer Recommendation Panel -->
        <div v-if="recommendation" class="mt-6 pt-6 border-t border-slate-100 flex flex-col md:flex-row justify-between items-start md:items-center gap-4 relative z-10">
          <div>
            <div class="flex items-center gap-2">
              <span class="text-xs text-slate-500">Điểm xoay vòng bền vững:</span>
              <span class="text-xs font-bold text-glowGreen-dim">{{ Math.round(recommendation.score) }}/100</span>
            </div>
            <p class="text-[10px] text-slate-600 mt-1 max-w-sm">
              {{ recommendation.weatherAdvice === 'Hot day. Wear light, breathable clothes.' ? 'Ngày nắng nóng. Hãy chọn trang phục mỏng nhẹ, thoáng mát.' : recommendation.weatherAdvice === 'Warm weather. Comfortable light layers.' ? 'Thời tiết ấm áp. Phối đồ mỏng nhẹ, thoải mái.' : recommendation.weatherAdvice === 'Cool day. Medium layers recommended.' ? 'Trời se lạnh. Khuyên dùng trang phục nhiều lớp vừa phải.' : recommendation.weatherAdvice === 'Cold day. Bundle up with warm layers.' ? 'Trời lạnh. Hãy mặc ấm áp bằng trang phục dày nhiều lớp.' : recommendation.weatherAdvice }}
            </p>
          </div>

          <div class="flex gap-2 w-full md:w-auto">
            <button 
              @click="wearToday" 
              :disabled="logging"
              class="flex-1 md:flex-none px-5 py-2.5 bg-glowGreen text-white text-xs font-bold rounded-xl tracking-wider glow-btn-green"
            >
              MẶC HÔM NAY
            </button>
            <button 
              @click="regenerateRecommendations" 
              class="px-3 py-2.5 bg-slate-100 border border-slate-200 text-slate-600 rounded-xl text-xs hover:bg-slate-200 font-bold"
            >
              🔄
            </button>
          </div>
        </div>
      </div>

      <!-- Right Column: Rotation statistics & Sustainable tip -->
      <div class="space-y-6">
        
        <!-- Sustainable Tip Box -->
        <div class="glass-card rounded-3xl p-6 relative overflow-hidden bg-gradient-to-br from-glowPurple/5 via-transparent to-transparent">
          <h4 class="text-xs font-extrabold tracking-widest text-glowPurple-dim uppercase mb-3">MẸO THỜI TRANG CHỮA LÀNH</h4>
          <p class="text-sm leading-relaxed text-slate-700 font-sans">
            "Bằng cách mặc trang phục bạn đã sở hữu lâu hơn 9 tháng, bạn có thể giảm lượng khí thải carbon, nước và chất thải của chúng lên đến 20-30%."
          </p>
          <div class="mt-4 flex items-center gap-2 text-xs text-slate-500">
            <span>🌿 GlowUp thúc đẩy thời trang tuần hoàn.</span>
          </div>
        </div>

        <!-- Sustainability Stats Card -->
        <div class="glass-card rounded-3xl p-6 space-y-4">
          <h3 class="text-sm font-extrabold tracking-wider text-slate-900 uppercase">CHỈ SỐ XOAY VÒNG TỦ ĐỒ</h3>
          
          <div class="space-y-3">
            <div>
              <div class="flex justify-between text-xs mb-1">
                <span class="text-slate-500">Tỷ lệ sử dụng trang phục</span>
                <span class="text-glowGreen-dim font-bold">{{ stats.utilization }}%</span>
              </div>
              <div class="w-full bg-slate-100 rounded-full h-1.5 overflow-hidden">
                <div class="bg-glowGreen h-full" :style="{ width: stats.utilization + '%' }"></div>
              </div>
            </div>

            <div>
              <div class="flex justify-between text-xs mb-1">
                <span class="text-slate-500">Lượng khí thải CO2 giảm thiểu</span>
                <span class="text-glowPurple-dim font-bold">{{ stats.co2Saved }} kg</span>
              </div>
              <div class="w-full bg-slate-100 rounded-full h-1.5 overflow-hidden">
                <div class="bg-glowPurple h-full" :style="{ width: Math.min(100, stats.co2Saved * 5) + '%' }"></div>
              </div>
            </div>
          </div>

          <div class="pt-2 grid grid-cols-2 gap-4 text-center border-t border-slate-100">
            <div>
              <div class="text-xl font-bold text-slate-900">{{ stats.totalItems }}</div>
              <div class="text-[9px] text-slate-600 uppercase tracking-wider">Trang phục sở hữu</div>
            </div>
            <div>
              <div class="text-xl font-bold text-slate-900">{{ stats.wornThisMonth }}</div>
              <div class="text-[9px] text-slate-600 uppercase tracking-wider">Đã mặc tháng này</div>
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
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['switch-tab'])

const city = ref('New York')
const showWeatherSimulation = ref(false)
const simTemp = ref(22)
const simCondition = ref('Clear')
const logging = ref(false)
const toast = ref(null)

const weather = reactive({
  temp: 22,
  condition: 'Clear'
})

const stats = reactive({
  utilization: 0,
  co2Saved: 0.0,
  totalItems: 0,
  wornThisMonth: 0
})

const recommendation = ref(null)
const recommendationsList = ref([])
const recommendationIndex = ref(0)

const fetchWeather = async () => {
  try {
    const res = await axios.get(`http://localhost:8081/api/weather/mock`)
    weather.temp = res.data.temp
    weather.condition = res.data.condition
    simTemp.value = Math.round(res.data.temp)
    simCondition.value = res.data.condition
  } catch (err) {
    console.error('Failed to fetch weather', err)
  }
}

const applySimulatedWeather = async () => {
  try {
    const res = await axios.post(`http://localhost:8081/api/weather/mock`, {
      temp: simTemp.value,
      condition: simCondition.value
    })
    weather.temp = res.data.temp
    weather.condition = res.data.condition
    showToast('Đã cập nhật thời tiết giả lập!')
    fetchRecommendations()
  } catch (err) {
    console.error('Failed to set simulated weather', err)
  }
}

const fetchRecommendations = async () => {
  try {
    const res = await axios.get(`http://localhost:8081/api/styling/recommendations`, {
      params: { userId: props.user.id, city: city.value }
    })
    recommendationsList.value = res.data
    recommendationIndex.value = 0
    if (res.data.length > 0) {
      recommendation.value = res.data[0]
    } else {
      recommendation.value = null
    }
  } catch (err) {
    console.error('Failed to load styling recommendations', err)
  }
}

const regenerateRecommendations = () => {
  if (recommendationsList.value.length <= 1) return
  recommendationIndex.value = (recommendationIndex.value + 1) % recommendationsList.value.length
  recommendation.value = recommendationsList.value[recommendationIndex.value]
  showToast('Đã đổi gợi ý phối đồ!')
}

const wearToday = async () => {
  if (!recommendation.value) return
  logging.value = true
  try {
    // 1. Create a curated outfit first
    const items = recommendation.value.items
    const itemIds = items.map(i => i.id)
    const outfitName = items.map(i => i.subCategory).join(' + ')
    
    const outfitRes = await axios.post(`http://localhost:8081/api/styling/outfits`, {
      userId: props.user.id,
      name: outfitName,
      contextTag: 'Daily Rotate',
      itemIds: itemIds
    })

    const outfitId = outfitRes.data.id

    // 2. Log in the calendar
    const todayStr = new Date().toISOString().split('T')[0]
    await axios.post(`http://localhost:8081/api/calendar/log`, {
      userId: props.user.id,
      date: todayStr,
      outfitId: outfitId
    })

    showToast('Đã ghi nhật ký phối đồ hôm nay!')
    fetchStats()
    fetchRecommendations() // refresh recommendations to score wear counts
  } catch (err) {
    console.error('Failed to wear outfit', err)
  } finally {
    logging.value = false
  }
}

const fetchStats = async () => {
  try {
    // Closet items
    const itemsRes = await axios.get(`http://localhost:8081/api/closet/items`, {
      params: { userId: props.user.id }
    })
    stats.totalItems = itemsRes.data.length

    // Calendar logs
    const calendarRes = await axios.get(`http://localhost:8081/api/calendar/logs`, {
      params: { userId: props.user.id }
    })
    
    const uniqueWornItems = new Set()
    let thisMonthCount = 0
    const startOfMonth = new Date()
    startOfMonth.setDate(1)
    
    calendarRes.data.forEach(log => {
      const logDate = new Date(log.date)
      if (logDate >= startOfMonth) {
        thisMonthCount++
      }
      if (log.outfit && log.outfit.items) {
        log.outfit.items.forEach(i => uniqueWornItems.add(i.id))
      }
    })

    stats.wornThisMonth = thisMonthCount
    stats.utilization = stats.totalItems > 0 
      ? Math.round((uniqueWornItems.size / stats.totalItems) * 100) 
      : 0
    
    // Custom carbon offset math: ~0.4 kg saved per wear of an existing clothing item instead of purchasing new
    stats.co2Saved = Math.round((uniqueWornItems.size * 0.45) * 10) / 10
  } catch (err) {
    console.error('Failed to fetch statistics', err)
  }
}

const showToast = (msg) => {
  toast.value = msg
  setTimeout(() => {
    toast.value = null
  }, 3000)
}

onMounted(() => {
  fetchWeather()
  fetchStats()
  fetchRecommendations()
})
</script>
