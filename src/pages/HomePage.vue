<template>
  <div>
    <!-- 搜索区域 -->
    <div class="bg-white rounded-lg shadow-md p-6 mb-6">
      <div class="grid grid-cols-4 gap-4 items-end">
        <div>
          <label class="block text-sm text-gray-600 mb-1">出发站</label>
          <select v-model="searchStore.startStation" class="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-[#1a5276]">
            <option value="">请选择出发站</option>
            <option v-for="s in allStations" :key="s.id" :value="s.name">{{ s.name }}</option>
          </select>
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">到达站</label>
          <select v-model="searchStore.endStation" class="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-[#1a5276]">
            <option value="">请选择到达站</option>
            <option v-for="s in allStations" :key="s.id" :value="s.name">{{ s.name }}</option>
          </select>
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">出发日期</label>
          <input
            v-model="searchStore.trainDate"
            type="date"
            class="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-[#1a5276]"
          />
        </div>
        <button
          @click="doSearch"
          :disabled="loading"
          class="bg-[#1a5276] text-white rounded px-6 py-2 text-sm hover:bg-[#154360] transition-colors disabled:opacity-50"
        >
          {{ loading ? '查询中...' : '查询' }}
        </button>
      </div>
      <!-- 筛选 -->
      <div class="flex gap-4 mt-4 text-sm">
        <label class="flex items-center gap-1">
          <span class="text-gray-600">车次类型：</span>
          <select v-model="searchStore.trainType" class="border border-gray-300 rounded px-2 py-1 text-sm">
            <option :value="undefined">全部</option>
            <option :value="1">高铁(G)</option>
            <option :value="2">动车(D)</option>
            <option :value="3">直达(Z)</option>
            <option :value="4">特快(T)</option>
            <option :value="5">快速(K)</option>
          </select>
        </label>
      </div>
    </div>

    <!-- 结果列表 -->
    <div v-if="searchStore.results.length" class="space-y-3">
      <div
        v-for="train in searchStore.results"
        :key="train.trainId"
        class="bg-white rounded-lg shadow-md p-4 hover:shadow-lg transition-shadow"
      >
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-6">
            <div class="text-center">
              <div class="text-2xl font-bold text-[#1a5276]">{{ train.departTime?.substring(0, 5) }}</div>
              <div class="text-sm text-gray-500">{{ train.startStationName }}</div>
            </div>
            <div class="text-center text-gray-400">
              <div class="text-xs">{{ train.duration }}分钟</div>
              <div class="border-t border-dashed border-gray-300 w-16"></div>
              <div class="text-xs text-[#1a5276] font-bold">{{ train.trainNo }}</div>
            </div>
            <div class="text-center">
              <div class="text-2xl font-bold text-[#1a5276]">{{ train.arriveTime?.substring(0, 5) }}</div>
              <div class="text-sm text-gray-500">{{ train.endStationName }}</div>
            </div>
          </div>
          <div class="flex gap-3">
            <div
              v-for="seat in train.seatPrices"
              :key="seat.seatType"
              class="text-center px-3 py-1 border border-gray-200 rounded"
            >
              <div class="text-xs text-gray-500">{{ seat.seatName }}</div>
              <div class="text-sm font-bold text-[#d4a843]">¥{{ seat.price }}</div>
              <div class="text-xs" :class="seat.availableCount > 0 ? 'text-green-600' : 'text-red-500'">
                {{ seat.availableCount > 0 ? `余${seat.availableCount}张` : '无票' }}
              </div>
            </div>
            <button
              v-if="userStore.token"
              @click="goToOrder(train)"
              class="bg-[#d4a843] text-white rounded px-4 py-2 text-sm hover:bg-[#c49a35] transition-colors self-center"
            >
              预订
            </button>
            <router-link
              v-else
              to="/login"
              class="bg-gray-400 text-white rounded px-4 py-2 text-sm hover:bg-gray-500 transition-colors self-center"
            >
              登录后预订
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!loading" class="text-center py-20 text-gray-400">
      <div class="text-5xl mb-4"></div>
      <p>输入出发站和到达站，开始查询车次</p>
    </div>

    <!-- 加载中 -->
    <div v-else class="text-center py-20">
      <div class="animate-spin text-4xl mb-4">⏳</div>
      <p class="text-gray-400">查询中...</p>
    </div>

    <!-- 错误提示 -->
    <div v-if="errorMsg" class="fixed bottom-6 left-1/2 -translate-x-1/2 bg-red-500 text-white px-6 py-3 rounded-lg shadow-lg text-sm">
      {{ errorMsg }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useSearchStore } from '@/stores/search'
import { useUserStore } from '@/stores/user'
import { getStationList, queryTrains } from '@/api/train'

const searchStore = useSearchStore()
const userStore = useUserStore()
const router = useRouter()

const loading = ref(false)
const errorMsg = ref('')
const allStations = ref<any[]>([])

onMounted(async () => {
  try {
    const res: any = await getStationList()
    allStations.value = res.data || []
  } catch { /* ignore */ }
  // 加载所有车次
  await doSearch()
})

async function doSearch() {
  loading.value = true
  errorMsg.value = ''
  try {
    const params: any = {}
    if (searchStore.startStation) params.startStation = searchStore.startStation
    if (searchStore.endStation) params.endStation = searchStore.endStation
    if (searchStore.trainDate) params.trainDate = searchStore.trainDate
    if (searchStore.trainType !== undefined) params.trainType = searchStore.trainType
    const res: any = await queryTrains(params)
    searchStore.setResults(res.data || [])
  } catch (e: any) {
    errorMsg.value = e.message || '查询失败'
    setTimeout(() => errorMsg.value = '', 3000)
  } finally {
    loading.value = false
  }
}

function goToOrder(train: any) {
  const startSeat = train.seatPrices?.find((s: any) => s.availableCount > 0)
  if (!startSeat) {
    errorMsg.value = '该车次暂无余票'
    setTimeout(() => errorMsg.value = '', 2000)
    return
  }
  router.push({
    path: '/order/confirm',
    query: {
      trainId: train.trainId,
      trainNo: train.trainNo,
      trainDate: searchStore.trainDate,
      startStationName: train.startStationName,
      endStationName: train.endStationName,
      departTime: train.departTime,
      arriveTime: train.arriveTime,
      duration: train.duration,
      seatType: startSeat.seatType,
      seatName: startSeat.seatName,
      price: startSeat.price,
    },
  })
}
</script>
