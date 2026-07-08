<template>
  <div>
    <div class="bg-white rounded-lg shadow-md p-6 mb-6">
      <h2 class="text-lg font-bold text-[#1a5276] mb-4">订单确认</h2>
      <div class="grid grid-cols-2 gap-4 text-sm">
        <div>
          <span class="text-gray-500">车次：</span>
          <span class="font-bold">{{ query.trainNo }}</span>
        </div>
        <div>
          <span class="text-gray-500">日期：</span>
          <span>{{ query.trainDate }}</span>
        </div>
        <div>
          <span class="text-gray-500">出发：</span>
          <span>{{ query.startStationName }} {{ query.departTime?.substring(0, 5) }}</span>
        </div>
        <div>
          <span class="text-gray-500">到达：</span>
          <span>{{ query.endStationName }} {{ query.arriveTime?.substring(0, 5) }}</span>
        </div>
        <div>
          <span class="text-gray-500">席别：</span>
          <span>{{ query.seatName }}</span>
        </div>
        <div>
          <span class="text-gray-500">票价：</span>
          <span class="text-[#d4a843] font-bold text-lg">¥{{ query.price }}</span>
        </div>
      </div>
    </div>

    <!-- 选择乘客 -->
    <div class="bg-white rounded-lg shadow-md p-6 mb-6">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-bold text-[#1a5276]">选择乘车人</h2>
        <router-link to="/passengers" class="text-sm text-[#1a5276] hover:underline">管理乘车人</router-link>
      </div>
      <div v-if="passengers.length" class="space-y-2">
        <label v-for="p in passengers" :key="p.id" class="flex items-center gap-3 p-3 border border-gray-200 rounded hover:bg-gray-50 cursor-pointer">
          <input type="checkbox" :value="p.id" v-model="selectedPassengerIds" class="w-4 h-4" />
          <span class="text-sm font-medium">{{ p.name }}</span>
          <span class="text-xs text-gray-400">{{ idTypeMap[p.idType] }} {{ p.idNumber }}</span>
        </label>
      </div>
      <div v-else class="text-center py-8 text-gray-400 text-sm">
        暂无乘车人，请先添加
        <router-link to="/passengers" class="text-[#1a5276] hover:underline">添加乘车人</router-link>
      </div>
    </div>

    <!-- 提交 -->
    <div class="flex items-center justify-between">
      <div class="text-sm">
        <span class="text-gray-500">合计：</span>
        <span class="text-[#d4a843] font-bold text-xl">¥{{ (Number(query.price) || 0) * selectedPassengerIds.length }}</span>
        <span class="text-gray-400 ml-2">× {{ selectedPassengerIds.length }}人</span>
      </div>
      <button @click="submitOrder" :disabled="submitting || !selectedPassengerIds.length"
        class="bg-[#d4a843] text-white rounded px-8 py-2 text-sm hover:bg-[#c49a35] transition-colors disabled:opacity-50">
        {{ submitting ? '提交中...' : '提交订单' }}
      </button>
    </div>

    <!-- 结果弹窗 -->
    <div v-if="showResult" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-8 max-w-sm w-full mx-4 text-center">
        <div class="text-4xl mb-4">{{ resultSuccess ? '✅' : '' }}</div>
        <h3 class="text-lg font-bold mb-2">{{ resultSuccess ? '购票成功' : '购票失败' }}</h3>
        <p class="text-sm text-gray-500 mb-4">{{ resultMessage }}</p>
        <div class="flex gap-3 justify-center">
          <button @click="showResult = false; $router.push('/orders')" class="bg-[#1a5276] text-white rounded px-4 py-2 text-sm">
            查看订单
          </button>
          <button @click="showResult = false; $router.push('/')" class="border border-gray-300 rounded px-4 py-2 text-sm">
            返回首页
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getPassengerList } from '@/api/user'
import { submitTicket, getTicketResult } from '@/api/order'

const route = useRoute()
const query = route.query as any

const passengers = ref<any[]>([])
const selectedPassengerIds = ref<number[]>([])
const submitting = ref(false)
const showResult = ref(false)
const resultSuccess = ref(false)
const resultMessage = ref('')

const idTypeMap: Record<number, string> = {
  1: '身份证',
  2: '护照',
  3: '港澳通行证',
}

onMounted(async () => {
  try {
    const res: any = await getPassengerList()
    passengers.value = res.data || []
  } catch { /* ignore */ }
})

async function submitOrder() {
  if (!selectedPassengerIds.value.length) return
  submitting.value = true
  try {
    const res: any = await submitTicket({
      trainId: Number(query.trainId),
      trainDate: query.trainDate,
      startStationId: Number(query.startStationId) || 0,
      endStationId: Number(query.endStationId) || 0,
      seatType: Number(query.seatType),
      passengerIds: selectedPassengerIds.value,
    })
    const requestNo = res.data?.requestNo
    if (requestNo) {
      // 轮询结果
      let attempts = 0
      const timer = setInterval(async () => {
        attempts++
        try {
          const result: any = await getTicketResult(requestNo)
          if (result.data?.status !== 0) {
            clearInterval(timer)
            resultSuccess.value = result.data?.status === 1
            resultMessage.value = result.data?.message || (resultSuccess.value ? '购票成功' : '购票失败')
            showResult.value = true
          }
        } catch { /* retry */ }
        if (attempts > 20) {
          clearInterval(timer)
          resultSuccess.value = false
          resultMessage.value = '查询超时，请到订单列表查看'
          showResult.value = true
        }
      }, 1000)
    } else {
      resultSuccess.value = false
      resultMessage.value = res.message || '提交失败'
      showResult.value = true
    }
  } catch (e: any) {
    resultSuccess.value = false
    resultMessage.value = e.message || '提交失败'
    showResult.value = true
  } finally {
    submitting.value = false
  }
}
</script>
