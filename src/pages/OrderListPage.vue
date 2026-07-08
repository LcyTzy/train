<template>
  <div>
    <div class="bg-white rounded-lg shadow-md p-6">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-bold text-[#1a5276]">我的订单</h2>
        <select v-model="statusFilter" @change="loadOrders" class="border border-gray-300 rounded px-3 py-1 text-sm">
          <option :value="undefined">全部状态</option>
          <option :value="0">待支付</option>
          <option :value="1">已支付</option>
          <option :value="2">已取消</option>
          <option :value="3">已退票</option>
        </select>
      </div>

      <div v-if="orders.length" class="space-y-4">
        <div v-for="order in orders" :key="order.orderNo" class="border border-gray-200 rounded-lg p-4">
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-4 text-sm">
              <span class="font-bold text-[#1a5276]">{{ order.trainNo }}</span>
              <span class="text-gray-500">{{ order.trainDate }}</span>
              <span>{{ order.startStationName }} → {{ order.endStationName }}</span>
            </div>
            <span class="text-xs px-2 py-1 rounded" :class="statusClass(order.status)">
              {{ statusText(order.status) }}
            </span>
          </div>
          <div class="flex items-center justify-between text-sm">
            <div class="text-gray-500">
              订单号：{{ order.orderNo }} | {{ order.seatName }} | {{ order.passengerNames?.join(', ') }}
            </div>
            <div class="flex items-center gap-3">
              <span class="text-[#d4a843] font-bold">¥{{ order.totalPrice }}</span>
              <button v-if="order.status === 0" @click="goPay(order.orderNo)"
                class="bg-[#d4a843] text-white rounded px-3 py-1 text-xs hover:bg-[#c49a35]">
                去支付
              </button>
              <button v-if="order.status === 0" @click="doCancel(order.orderNo)"
                class="border border-gray-300 rounded px-3 py-1 text-xs hover:bg-gray-50">
                取消
              </button>
              <button v-if="order.status === 1" @click="doRefund(order.orderNo)"
                class="border border-red-300 text-red-500 rounded px-3 py-1 text-xs hover:bg-red-50">
                退票
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="!loading" class="text-center py-16 text-gray-400">
        暂无订单
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList, cancelOrder, refundOrder } from '@/api/order'

const router = useRouter()
const orders = ref<any[]>([])
const loading = ref(false)
const statusFilter = ref<number | undefined>(undefined)

const statusTextMap: Record<number, string> = {
  0: '待支付',
  1: '已支付',
  2: '已取消',
  3: '已退票',
}

function statusText(s: number) {
  return statusTextMap[s] || '未知'
}

function statusClass(s: number) {
  const map: Record<number, string> = {
    0: 'bg-yellow-100 text-yellow-700',
    1: 'bg-green-100 text-green-700',
    2: 'bg-gray-100 text-gray-500',
    3: 'bg-red-100 text-red-500',
  }
  return map[s] || 'bg-gray-100 text-gray-500'
}

async function loadOrders() {
  loading.value = true
  try {
    const params: any = { page: 1, size: 20 }
    if (statusFilter.value !== undefined) params.status = statusFilter.value
    const res: any = await getOrderList(params)
    orders.value = res.data?.records || res.data || []
  } catch { /* ignore */ } finally {
    loading.value = false
  }
}

function goPay(orderNo: string) {
  router.push(`/pay/${orderNo}`)
}

async function doCancel(orderNo: string) {
  if (!confirm('确定取消该订单？')) return
  try {
    await cancelOrder(orderNo)
    loadOrders()
  } catch { /* ignore */ }
}

async function doRefund(orderNo: string) {
  if (!confirm('确定退票？退票将原路返回支付金额。')) return
  try {
    await refundOrder(orderNo)
    loadOrders()
  } catch { /* ignore */ }
}

onMounted(loadOrders)
</script>
