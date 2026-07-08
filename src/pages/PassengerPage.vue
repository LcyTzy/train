<template>
  <div>
    <div class="bg-white rounded-lg shadow-md p-6">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-bold text-[#1a5276]">乘车人管理</h2>
        <button @click="showAddForm = !showAddForm" class="bg-[#1a5276] text-white rounded px-4 py-2 text-sm hover:bg-[#154360]">
          {{ showAddForm ? '取消添加' : '+ 添加乘车人' }}
        </button>
      </div>

      <!-- 添加表单 -->
      <div v-if="showAddForm" class="border border-gray-200 rounded-lg p-4 mb-4 bg-gray-50">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm text-gray-600 mb-1">姓名</label>
            <input v-model="newPassenger.name" class="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">证件类型</label>
            <select v-model="newPassenger.idType" class="w-full border border-gray-300 rounded px-3 py-2 text-sm">
              <option :value="1">身份证</option>
              <option :value="2">护照</option>
              <option :value="3">港澳通行证</option>
            </select>
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">证件号码</label>
            <input v-model="newPassenger.idNumber" class="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">旅客类型</label>
            <select v-model="newPassenger.passengerType" class="w-full border border-gray-300 rounded px-3 py-2 text-sm">
              <option :value="1">成人</option>
              <option :value="2">儿童</option>
              <option :value="3">学生</option>
            </select>
          </div>
          <div class="col-span-2">
            <label class="block text-sm text-gray-600 mb-1">手机号</label>
            <input v-model="newPassenger.phone" class="w-full border border-gray-300 rounded px-3 py-2 text-sm" />
          </div>
        </div>
        <button @click="doAdd" :disabled="adding" class="mt-4 bg-[#d4a843] text-white rounded px-4 py-2 text-sm hover:bg-[#c49a35]">
          {{ adding ? '添加中...' : '确认添加' }}
        </button>
      </div>

      <!-- 列表 -->
      <div v-if="passengers.length" class="space-y-2">
        <div v-for="p in passengers" :key="p.id" class="flex items-center justify-between p-4 border border-gray-200 rounded-lg">
          <div>
            <div class="font-medium text-sm">{{ p.name }} <span class="text-xs text-gray-400 ml-2">{{ typeMap[p.passengerType] }}</span></div>
            <div class="text-xs text-gray-400 mt-1">{{ idTypeMap[p.idType] }} {{ p.idNumber }} | {{ p.phone }}</div>
          </div>
          <button @click="doDelete(p.id)" class="text-red-400 text-sm hover:text-red-600">删除</button>
        </div>
      </div>
      <div v-else class="text-center py-16 text-gray-400 text-sm">暂无乘车人</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPassengerList, addPassenger, deletePassenger } from '@/api/user'

const passengers = ref<any[]>([])
const showAddForm = ref(false)
const adding = ref(false)
const newPassenger = ref({ name: '', idType: 1, idNumber: '', passengerType: 1, phone: '' })

const idTypeMap: Record<number, string> = { 1: '身份证', 2: '护照', 3: '港澳通行证' }
const typeMap: Record<number, string> = { 1: '成人', 2: '儿童', 3: '学生' }

async function loadPassengers() {
  try {
    const res: any = await getPassengerList()
    passengers.value = res.data || []
  } catch { /* ignore */ }
}

async function doAdd() {
  const p = newPassenger.value
  if (!p.name || !p.idNumber || !p.phone) return
  adding.value = true
  try {
    await addPassenger(p)
    newPassenger.value = { name: '', idType: 1, idNumber: '', passengerType: 1, phone: '' }
    showAddForm.value = false
    loadPassengers()
  } catch { /* ignore */ } finally {
    adding.value = false
  }
}

async function doDelete(id: number) {
  if (!confirm('确定删除该乘车人？')) return
  try {
    await deletePassenger(id)
    loadPassengers()
  } catch { /* ignore */ }
}

onMounted(loadPassengers)
</script>
