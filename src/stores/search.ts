import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useSearchStore = defineStore('search', () => {
  const startStation = ref('')
  const endStation = ref('')
  const trainDate = ref(new Date().toISOString().split('T')[0])
  const trainType = ref<number | undefined>(undefined)
  const departTimeStart = ref('')
  const departTimeEnd = ref('')
  const results = ref<any[]>([])

  function setResults(data: any[]) {
    results.value = data
  }

  return {
    startStation,
    endStation,
    trainDate,
    trainType,
    departTimeStart,
    departTimeEnd,
    results,
    setResults,
  }
})
