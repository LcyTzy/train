<template>
  <div class="max-w-md mx-auto mt-16">
    <div class="bg-white rounded-lg shadow-md p-8">
      <h2 class="text-2xl font-bold text-center text-[#1a5276] mb-6">用户注册</h2>
      <div class="space-y-4">
        <div>
          <label class="block text-sm text-gray-600 mb-1">用户名</label>
          <input v-model="form.username" type="text" placeholder="请输入用户名"
            class="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-[#1a5276]" />
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码"
            class="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-[#1a5276]" />
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">手机号</label>
          <input v-model="form.phone" type="text" placeholder="请输入手机号"
            class="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:border-[#1a5276]" />
        </div>
        <button @click="doRegister" :disabled="loading"
          class="w-full bg-[#1a5276] text-white rounded py-2 text-sm hover:bg-[#154360] transition-colors disabled:opacity-50">
          {{ loading ? '注册中...' : '注册' }}
        </button>
        <p class="text-center text-sm text-gray-500">
          已有账号？
          <router-link to="/login" class="text-[#1a5276] hover:underline">立即登录</router-link>
        </p>
      </div>
    </div>
    <div v-if="errorMsg" class="mt-4 bg-red-50 text-red-600 text-sm text-center py-2 rounded">
      {{ errorMsg }}
    </div>
    <div v-if="successMsg" class="mt-4 bg-green-50 text-green-600 text-sm text-center py-2 rounded">
      {{ successMsg }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'

const router = useRouter()
const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const form = ref({ username: '', password: '', phone: '' })

async function doRegister() {
  if (!form.value.username || !form.value.password || !form.value.phone) {
    errorMsg.value = '请填写所有字段'
    return
  }
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const res: any = await register(form.value)
    if (res.code === 200) {
      successMsg.value = '注册成功，请登录'
      setTimeout(() => router.push('/login'), 1500)
    } else {
      errorMsg.value = res.message || '注册失败'
    }
  } catch (e: any) {
    errorMsg.value = e.message || '注册失败'
  } finally {
    loading.value = false
  }
}
</script>
