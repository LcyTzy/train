<template>
  <div class="max-w-lg mx-auto mt-16">
    <div class="bg-white rounded-lg shadow-md p-8">
      <h2 class="text-xl font-bold text-center text-[#1a5276] mb-6">订单支付</h2>

      <div v-if="orderInfo" class="space-y-4 mb-6">
        <div class="flex justify-between text-sm">
          <span class="text-gray-500">订单号</span>
          <span class="font-mono">{{ orderInfo.orderNo }}</span>
        </div>
        <div class="flex justify-between text-sm">
          <span class="text-gray-500">车次</span>
          <span>{{ orderInfo.trainNo }}</span>
        </div>
        <div class="flex justify-between text-sm">
          <span class="text-gray-500">行程</span>
          <span>{{ orderInfo.startStationName }} → {{ orderInfo.endStationName }}</span>
        </div>
        <div class="flex justify-between text-sm">
          <span class="text-gray-500">日期</span>
          <span>{{ orderInfo.trainDate }}</span>
        </div>
        <div class="border-t pt-4 flex justify-between items-center">
          <span class="text-gray-500">应付金额</span>
          <span class="text-2xl font-bold text-[#d4a843]">¥{{ orderInfo.totalPrice }}</span>
        </div>
      </div>

      <div v-if="payStatus === 0" class="space-y-4">
        <button @click="doPay" :disabled="paying"
          class="w-full bg-[#d4a843] text-white rounded py-3 text-sm hover:bg-[#c49a35] transition-colors disabled:opacity-50">
          {{ paying ? '支付中...' : '确认支付' }}
        </button>
      </div>

      <div v-else-if="payStatus === 1" class="text-center py-8">
        <div class="text-5xl mb-4">✅</div>
        <h3 class="text-lg font-bold text-green-600 mb-2">支付成功</h3>
        <p class="text-sm text-gray-500 mb-4">您的车票已购买成功</p>
        <div class="flex gap-3 justify-center">
          <button @click="$router.push('/orders')" class="bg-[#1a5276] text-white rounded px-4 py-2 text-sm">
            查看订单
          </button>
          <button @click="$router.push('/')" class="border border-gray-300 rounded px-4 py-2 text-sm">
            返回首页
          </button>
        </div>
      </div>

      <div v-else-if="payStatus === -1" class="text-center py-8">
        <div class="text-5xl mb-4">❌</div>
        <h3 class="text-lg font-bold text-red-500 mb-2">支付失败</h3>
        <p class="text-sm text-gray-500 mb-4">{{ payMessage }}</p>
        <button @click="doPay" class="bg-[#d4a843] text-white rounded px-4 py-2 text-sm">
          重新支付
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getOrderDetail } from '@/api/order'
import { createPay, queryPay } from '@/api/pay'

const route = useRoute()
const orderNo = route.params.orderNo as string

const orderInfo = ref<any>(null)
const payStatus = ref(0) // 0待支付 1成功 -1失败
const payMessage = ref('')
const paying = ref(false)

onMounted(async () => {
  try {
    const res: any = await getOrderDetail(orderNo)
    orderInfo.value = res.data
    if (res.data?.payStatus === 1) {
      payStatus.value = 1
    }
  } catch { /* ignore */ }
})

async function doPay() {
  paying.value = true
  payMessage.value = ''
  try {
    await createPay({ orderNo })
    // 模拟支付，轮询查询
    let attempts = 0
    const timer = setInterval(async () => {
      attempts++
      try {
        const res: any = await queryPay(orderNo)
        if (res.data?.payStatus === 1) {
          clearInterval(timer)
          payStatus.value = 1
        }
      } catch { /* retry */ }
      if (attempts > 10) {
        clearInterval(timer)
        payStatus.value = -1
        payMessage.value = '支付超时，请重试'
      }
    }, 1500)
  } catch (e: any) {
    payStatus.value = -1
    payMessage.value = e.message || '支付失败'
  } finally {
    paying.value = false
  }
}
</script>
