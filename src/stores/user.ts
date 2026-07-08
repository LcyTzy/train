import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<any>(null)

  function setToken(t: string) {
    token.value = t
    localStorage.setItem('token', t)
  }

  function clearToken() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  async function fetchUserInfo() {
    try {
      const res: any = await getUserInfo()
      userInfo.value = res.data
    } catch {
      clearToken()
    }
  }

  return { token, userInfo, setToken, clearToken, fetchUserInfo }
})
