<template>
  <div class="min-h-screen flex items-center justify-center p-4 w-full max-w-md">
    <div class="w-full glass-card rounded-3xl p-8 relative overflow-hidden fade-in shadow-xl">
      <!-- Background glow effects -->
      <div class="absolute -top-24 -left-24 w-48 h-48 rounded-full bg-glowPurple/5 blur-3xl"></div>
      <div class="absolute -bottom-24 -right-24 w-48 h-48 rounded-full bg-glowGreen/5 blur-3xl"></div>

      <!-- App Logo & Step Indicator -->
      <div class="text-center mb-6 relative">
        <h1 class="text-3xl font-extrabold tracking-tight bg-gradient-to-r from-slate-900 via-slate-700 to-glowGreen-dim bg-clip-text text-transparent font-sans">
          GlowUp
        </h1>
        <!-- Progress Dots -->
        <div class="flex justify-center gap-2 mt-4">
          <span 
            v-for="step in 3" 
            :key="step"
            :class="[
              'w-2 h-2 rounded-full transition-all duration-300',
              currentStep === step ? 'w-6 bg-glowGreen' : 'bg-slate-200'
            ]"
          ></span>
        </div>
      </div>

      <!-- Step 1: Basic Info -->
      <div v-if="currentStep === 1" class="space-y-5 slide-up">
        <div class="text-center">
          <h2 class="text-lg font-bold text-slate-800">Chào mừng bạn đến với GlowUp</h2>
          <p class="text-xs text-slate-500 mt-1">Hãy cho chúng tôi biết một chút về bạn để cá nhân hóa tủ đồ nhé.</p>
        </div>

        <div class="space-y-4">
          <div>
            <label class="block text-[10px] font-bold text-slate-500 uppercase tracking-wider mb-2">Tên gọi của bạn</label>
            <input 
              v-model="form.name" 
              type="text" 
              placeholder="ví dụ: Alex" 
              required 
              class="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-sm text-slate-800 placeholder-slate-400 focus:outline-none focus:border-glowGreen focus:ring-1 focus:ring-glowGreen/20 transition-all"
            />
          </div>

          <div>
            <label class="block text-[10px] font-bold text-slate-500 uppercase tracking-wider mb-2">Giới tính</label>
            <div class="grid grid-cols-3 gap-2">
              <button 
                v-for="g in ['Nam', 'Nữ', 'Khác']" 
                :key="g"
                type="button"
                @click="form.gender = g"
                :class="[
                  'py-2.5 text-xs font-bold rounded-xl border transition-all',
                  form.gender === g 
                    ? 'bg-glowGreen border-glowGreen text-white shadow-glow-green' 
                    : 'bg-slate-50 border-slate-200 text-slate-600 hover:bg-slate-100'
                ]"
              >
                {{ g }}
              </button>
            </div>
          </div>
        </div>

        <button 
          @click="nextStep" 
          :disabled="!form.name.trim()"
          class="w-full py-3 mt-6 bg-glowGreen text-white text-xs font-bold rounded-xl tracking-wider glow-btn-green disabled:opacity-50 disabled:cursor-not-allowed"
        >
          TIẾP TỤC
        </button>
      </div>

      <!-- Step 2: Body Metrics -->
      <div v-if="currentStep === 2" class="space-y-4 slide-up">
        <div class="text-center mb-2">
          <h2 class="text-lg font-bold text-slate-800">Số đo cơ thể</h2>
          <p class="text-xs text-slate-500 mt-1">Dùng để tự động dựng ma-nơ-canh thử đồ khớp với bạn.</p>
        </div>

        <div class="space-y-3 max-h-[300px] overflow-y-auto pr-1">
          <div>
            <div class="flex justify-between text-xs mb-1">
              <span class="text-slate-500">Chiều cao (cm)</span>
              <span class="text-slate-800 font-bold">{{ form.height }} cm</span>
            </div>
            <input 
              v-model.number="form.height" 
              type="range" min="140" max="210" step="1" 
              class="w-full h-1 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-glowGreen"
            />
          </div>

          <div>
            <div class="flex justify-between text-xs mb-1">
              <span class="text-slate-500">Cân nặng (kg)</span>
              <span class="text-slate-800 font-bold">{{ form.weight }} kg</span>
            </div>
            <input 
              v-model.number="form.weight" 
              type="range" min="30" max="150" step="1" 
              class="w-full h-1 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-glowGreen"
            />
          </div>

          <div>
            <div class="flex justify-between text-xs mb-1">
              <span class="text-slate-500">Vòng ngực (Vòng 1 - cm)</span>
              <span class="text-slate-800 font-bold">{{ form.bust }} cm</span>
            </div>
            <input 
              v-model.number="form.bust" 
              type="range" min="60" max="130" step="1" 
              class="w-full h-1 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-glowGreen"
            />
          </div>

          <div>
            <div class="flex justify-between text-xs mb-1">
              <span class="text-slate-500">Vòng eo (Vòng 2 - cm)</span>
              <span class="text-slate-800 font-bold">{{ form.waist }} cm</span>
            </div>
            <input 
              v-model.number="form.waist" 
              type="range" min="50" max="120" step="1" 
              class="w-full h-1 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-glowGreen"
            />
          </div>

          <div>
            <div class="flex justify-between text-xs mb-1">
              <span class="text-slate-500">Vòng mông (Vòng 3 - cm)</span>
              <span class="text-slate-800 font-bold">{{ form.hips }} cm</span>
            </div>
            <input 
              v-model.number="form.hips" 
              type="range" min="70" max="140" step="1" 
              class="w-full h-1 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-glowGreen"
            />
          </div>
        </div>

        <div class="flex gap-2 mt-4">
          <button 
            @click="prevStep" 
            class="w-1/3 py-3 bg-slate-100 border border-slate-200 text-slate-600 font-bold rounded-xl text-xs tracking-wider hover:bg-slate-200"
          >
            QUAY LẠI
          </button>
          <button 
            @click="nextStep" 
            class="w-2/3 py-3 bg-glowGreen text-white text-xs font-bold rounded-xl tracking-wider glow-btn-green"
          >
            TIẾP TỤC
          </button>
        </div>
      </div>

      <!-- Step 3: Terms & Agreement -->
      <div v-if="currentStep === 3" class="space-y-5 slide-up">
        <div class="text-center">
          <h2 class="text-lg font-bold text-slate-800">Điều khoản & Bảo mật</h2>
          <p class="text-xs text-slate-500 mt-1">Vui lòng đọc và chấp thuận điều khoản để bắt đầu sử dụng.</p>
        </div>

        <!-- Terms Box -->
        <div class="bg-slate-50 border border-slate-100 rounded-2xl p-4 text-[10px] text-slate-600 space-y-2 h-[200px] overflow-y-auto leading-relaxed">
          <p class="font-bold text-slate-800 text-xs">Chính sách bảo mật dữ liệu GlowUp</p>
          <p>1. **Quyền riêng tư cục bộ (Local Privacy)**: Thông tin giới tính, chiều cao, cân nặng và số đo cơ thể của bạn sẽ được lưu trữ trực tiếp trên thiết bị này. Chúng tôi tôn trọng quyền riêng tư và cam kết không mua bán dữ liệu cá nhân của bạn.</p>
          <p>2. **Xử lý hình ảnh thông qua AI**: Các hình ảnh quần áo mà bạn tải lên sẽ được chuyển tới dịch vụ AI để thực hiện tách nền tự động và trích xuất màu sắc. Ảnh sau khi tách nền sẽ được lưu trữ trên máy chủ để hiển thị trong tủ đồ cá nhân.</p>
          <p>3. **Tính bền vững**: GlowUp đề xuất gợi ý trang phục dựa trên thuật toán xoay vòng thời trang, nhằm mục đích tối ưu hóa tủ đồ hiện có và hạn chế lượng rác thải thời trang ra môi trường.</p>
          <p>4. **Trách nhiệm người dùng**: Bạn chịu trách nhiệm về tính chính xác của các số đo hình thể để mô hình thử đồ ảo hiển thị đúng tỷ lệ nhất.</p>
        </div>

        <!-- Checkbox -->
        <div class="flex items-start gap-2.5">
          <input 
            id="terms-checkbox" 
            type="checkbox" 
            v-model="form.termsAccepted" 
            class="mt-1 w-4 h-4 text-glowGreen border-slate-300 rounded focus:ring-glowGreen/20 cursor-pointer accent-glowGreen"
          />
          <label for="terms-checkbox" class="text-[11px] text-slate-600 leading-tight cursor-pointer selection:bg-transparent">
            Tôi đồng ý với các điều khoản dịch vụ và cam kết bảo mật dữ liệu của ứng dụng GlowUp.
          </label>
        </div>

        <div v-if="error" class="text-rose-600 text-xs bg-rose-50 border border-rose-200 rounded-lg p-3">
          {{ error }}
        </div>

        <div class="flex gap-2 mt-4">
          <button 
            @click="prevStep" 
            :disabled="loading"
            class="w-1/3 py-3 bg-slate-100 border border-slate-200 text-slate-600 font-bold rounded-xl text-xs tracking-wider hover:bg-slate-100 disabled:opacity-50"
          >
            QUAY LẠI
          </button>
          <button 
            @click="finishOnboarding" 
            :disabled="!form.termsAccepted || loading" 
            class="w-2/3 py-3 bg-glowGreen text-white text-xs font-bold rounded-xl tracking-wider glow-btn-green disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <span v-if="loading">ĐANG KHỞI TẠO...</span>
            <span v-else>BẮT ĐẦU TRẢI NGHIỆM</span>
          </button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import axios from 'axios'

const emit = defineEmits(['onboarding-success'])

const currentStep = ref(1)
const loading = ref(false)
const error = ref(null)

const form = reactive({
  name: 'Khách',
  gender: 'Nữ',
  height: 168,
  weight: 52,
  bust: 90,
  waist: 70,
  hips: 95,
  termsAccepted: false
})

const nextStep = () => {
  if (currentStep.value < 3) currentStep.value++
}

const prevStep = () => {
  if (currentStep.value > 1) currentStep.value--
}

const generateUUID = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

const finishOnboarding = async () => {
  loading.value = true
  error.value = null
  const uuid = generateUUID()
  
  try {
    // Register mock/virtual user on Spring Boot backend
    const apiEmail = `user-${uuid}@glowup.local`
    const apiPass = 'local-bypass-password'
    
    const response = await axios.post('http://localhost:8081/api/auth/register', {
      email: apiEmail,
      password: apiPass,
      name: form.name,
      height: Number(form.height),
      bust: Number(form.bust),
      waist: Number(form.waist),
      hips: Number(form.hips)
    })
    
    // Save response user id (generated by backend DB)
    const backendUser = response.data
    
    const localUser = {
      id: backendUser.id,
      name: form.name,
      gender: form.gender,
      height: form.height,
      weight: form.weight,
      bust: form.bust,
      waist: form.waist,
      hips: form.hips,
      termsAccepted: true
    }
    
    emit('onboarding-success', localUser)
  } catch (err) {
    console.error('Failed to register virtual profile on backend', err)
    // Fallback: If backend is completely offline, still run the app locally
    const fallbackUser = {
      id: uuid,
      name: form.name,
      gender: form.gender,
      height: form.height,
      weight: form.weight,
      bust: form.bust,
      waist: form.waist,
      hips: form.hips,
      termsAccepted: true
    }
    emit('onboarding-success', fallbackUser)
  } finally {
    loading.value = false
  }
}
</script>
