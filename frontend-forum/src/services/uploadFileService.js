import httpClient from '@/services/httpClient'

export const fileModule = Object.freeze({
  CONFIG: 'CONFIG',
  USER: 'USER',
  CHAT: 'CHAT',
  DOLL: 'DOLL',
  BOARD: 'BOARD',
  ANY: ''
})

const validModules = Object.values(fileModule)

const uploadFile = async (file, fileModule) => {

    if(file == null){
        return;
    }

    if (!(file instanceof File)) {
    throw new Error('無效的檔案')
    }

    if (!validModules.includes(fileModule)) {
    throw new Error(`無效的檔案模組：${fileModule}`)
    }

    const formData = new FormData()
    formData.append('file', file)
    formData.append('fileModule', fileModule)

    const response = await httpClient({
        method: 'post',
        url: '/api/file/upload',
        data: formData
    })

    return response.data
}

const getFileInfo = async (fileId) => {
  const response = await httpClient({
    method: 'get',
    url: `/api/file/info/${fileId}`
  })

  return response.data
}


export default {
  uploadFile,
  getFileInfo,
  fileModule
}