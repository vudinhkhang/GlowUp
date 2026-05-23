<template>
  <div class="min-h-screen flex flex-col justify-between font-sans selection:bg-glowGreen/30 text-slate-800">
    
    <!-- Onboarding screen -->
    <div v-if="!user" class="flex-1 flex items-center justify-center">
      <Onboarding @onboarding-success="handleAuthSuccess" />
    </div>

    <!-- App view -->
    <div v-else class="flex-1 flex flex-col pb-24 md:pb-6">
      
      <!-- Top Navigation Header -->
      <header class="w-full glass-card border-x-0 border-t-0 py-4 px-6 sticky top-0 z-40 backdrop-blur-md">
        <div class="max-w-7xl mx-auto flex justify-between items-center">
          <!-- Logo -->
          <div class="flex items-center gap-2 cursor-pointer" @click="currentTab = 'dashboard'">
            <span class="text-xl">✨</span>
            <span class="text-xl font-extrabold tracking-wider bg-gradient-to-r from-slate-900 to-glowGreen bg-clip-text text-transparent font-sans">
              GlowUp
            </span>
          </div>

          <!-- Profile / Logout -->
          <div class="flex items-center gap-4">
            <div class="text-right hidden sm:block">
              <div class="text-xs font-bold text-slate-900 uppercase tracking-wider">{{ user.name }}</div>
              <div class="text-[9px] text-glowGreen-dim font-bold uppercase tracking-wider">Chuyên gia thời trang bền vững</div>
            </div>
            
            <button 
              @click="resetApp" 
              class="px-3 py-1.5 bg-slate-100 border border-slate-200 text-slate-700 hover:bg-red-50 hover:text-red-600 hover:border-red-200 rounded-xl text-xs font-bold transition-all"
            >
              Đặt lại ứng dụng
            </button>
          </div>
        </div>
      </header>

      <!-- Main Content Viewport -->
      <main class="max-w-7xl w-full mx-auto p-4 md:p-6 flex-1">
        <transition name="fade" mode="out-in">
          <component 
            :is="activeComponent" 
            :user="user" 
            @switch-tab="switchTab"
            @update-user="updateUser"
            :key="currentTab"
          />
        </transition>
      </main>

      <!-- Bottom Tab Navigation Bar (Mobile / General Layout) -->
      <nav class="fixed bottom-0 inset-x-0 glass-card border-x-0 border-b-0 py-3 px-4 z-40 backdrop-blur-lg flex justify-around items-center">
        <!-- Dashboard Tab -->
        <button 
          @click="currentTab = 'dashboard'" 
          :class="['flex flex-col items-center gap-1 text-[9px] font-bold uppercase tracking-wider transition-colors', currentTab === 'dashboard' ? 'text-glowGreen-dim' : 'text-slate-500 hover:text-slate-800']"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.8" stroke="currentColor" class="w-5 h-5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 12l8.954-8.955c.44-.439 1.152-.439 1.591 0L21.75 12M4.5 9.75v10.125c0 .621.504 1.125 1.125 1.125H9.75v-4.875c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125V21h4.125c.621 0 1.125-.504 1.125-1.125V9.75M8.25 21h8.25" />
          </svg>
          <span>Gợi ý</span>
        </button>

        <!-- Closet Tab -->
        <button 
          @click="currentTab = 'closet'" 
          :class="['flex flex-col items-center gap-1 text-[9px] font-bold uppercase tracking-wider transition-colors', currentTab === 'closet' ? 'text-glowGreen-dim' : 'text-slate-500 hover:text-slate-800']"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.8" stroke="currentColor" class="w-5 h-5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M20.25 7.5l-.625 10.632a2.25 2.25 0 01-2.247 2.118H6.622a2.25 2.25 0 01-2.247-2.118L3.75 7.5M10 11.25h4M3.375 7.5h17.25c.621 0 1.125-.504 1.125-1.125v-1.5c0-.621-.504-1.125-1.125-1.125H3.375c-.621 0-1.125.504-1.125 1.125v1.5c0 .621.504 1.125 1.125 1.125z" />
          </svg>
          <span>Tủ đồ</span>
        </button>

        <!-- Try On Tab -->
        <button 
          @click="currentTab = 'tryon'" 
          :class="['flex flex-col items-center gap-1 text-[9px] font-bold uppercase tracking-wider transition-colors', currentTab === 'tryon' ? 'text-glowGreen-dim' : 'text-slate-500 hover:text-slate-800']"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.8" stroke="currentColor" class="w-5 h-5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
          </svg>
          <span>Thử đồ</span>
        </button>

        <!-- Calendar Tab -->
        <button 
          @click="currentTab = 'calendar'" 
          :class="['flex flex-col items-center gap-1 text-[9px] font-bold uppercase tracking-wider transition-colors', currentTab === 'calendar' ? 'text-glowGreen-dim' : 'text-slate-500 hover:text-slate-800']"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.8" stroke="currentColor" class="w-5 h-5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6.75 3v2.25M17.25 3v2.25M3 18.75V7.5a2.25 2.25 0 012.25-2.25h13.5A2.25 2.25 0 0121 7.5v11.25m-18 0A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75m-18 0v-7.5A2.25 2.25 0 015.25 9h13.5A2.25 2.25 0 0121 11.25v7.5m-9-6h.008v.008H12v-.008zM12 15h.008v.008H12V15zm0 2.25h.008v.008H12v-.008zM9.75 15h.008v.008H9.75V15zm0 2.25h.008v.008H9.75v-.008zM7.5 15h.008v.008H7.5V15zm0 2.25h.008v.008H7.5v-.008zm6.75-4.5h.008v.008h-.008v-.008zm0 2.25h.008v.008h-.008V15zm0 2.25h.008v.008h-.008v-.008zm2.25-4.5h.008v.008H16.5v-.008zm0 2.25h.008v.008H16.5V15z" />
          </svg>
          <span>Nhật ký</span>
        </button>
      </nav>
      
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import Onboarding from './components/Onboarding.vue'
import Dashboard from './components/Dashboard.vue'
import ClosetGrid from './components/ClosetGrid.vue'
import VirtualTryOn from './components/VirtualTryOn.vue'
import OutfitCalendar from './components/OutfitCalendar.vue'

const user = ref(null)
const currentTab = ref('dashboard')

const handleAuthSuccess = (userData) => {
  user.value = userData
  localStorage.setItem('glowup_user', JSON.stringify(userData))
  currentTab.value = 'dashboard'
}

const resetApp = () => {
  if (confirm('Bạn có muốn đặt lại ứng dụng? Thao tác này sẽ xóa toàn bộ số đo cơ thể lưu trên máy của bạn.')) {
    user.value = null
    localStorage.removeItem('glowup_user')
  }
}

const switchTab = (tabName) => {
  currentTab.value = tabName
}

const updateUser = (updatedUser) => {
  user.value = updatedUser
  localStorage.setItem('glowup_user', JSON.stringify(updatedUser))
}

const activeComponent = computed(() => {
  switch (currentTab.value) {
    case 'dashboard':
      return Dashboard
    case 'closet':
      return ClosetGrid
    case 'tryon':
      return VirtualTryOn
    case 'calendar':
      return OutfitCalendar
    default:
      return Dashboard
  }
})

onMounted(() => {
  const cachedUser = localStorage.getItem('glowup_user')
  if (cachedUser) {
    try {
      user.value = JSON.parse(cachedUser)
    } catch (e) {
      localStorage.removeItem('glowup_user')
    }
  }
})
</script>

<style>
/* Tab Component Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
