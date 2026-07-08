<template>
  <div class="max-w-md mx-auto mt-16">
    <div class="bg-white rounded-lg shadow-md p-8">
      <h2 class="text-2xl font-bold text-center text-[#1a5276] mb-6">用户登录</h2>
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
        <button @click="doLogin" :disabled="loading"
          class="w-full bg-[#1a5276] text-white rounded py-2 text-sm hover:bg-[#154360] transition-colors disabled:opacity-50">
          {{ loading ? '登录中...' : '登录' }}
        </button>
        <p class="text-center text-sm text-gray-500">
          还没有账号？
          <router-link to="/register" class="text-[#1a5276] hover:underline">立即注册</router-link>
        </p>
      </div>
    </div>
    <div v-if="errorMsg" class="mt-4 bg-red-50 text-red-600 text-sm text-center py-2 rounded">
      {{ errorMsg }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const errorMsg = ref('')
const form = ref({ username: '', password: '' })

async function doLogin() {
  if (!form.value.username || !form.value.password) {
    errorMsg.value = '请填写用户名和密码'
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    const res: any = await login(form.value)
    if (res.data?.token) {
      userStore.setToken(res.data.token)
      await userStore.fetchUserInfo()
      router.push('/')
    } else {
      errorMsg.value = res.message || '登录失败'
    }
  } catch (e: any) {
    errorMsg.value = e.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>
