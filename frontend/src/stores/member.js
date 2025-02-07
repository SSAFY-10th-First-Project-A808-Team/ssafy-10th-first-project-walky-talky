import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import router from '@/router'
import { axios } from '@/stores/jwtFilter'
import { useCounterStore } from './counter'

const REST_MEMBER_API = 'https://i10a808.p.ssafy.io'

export const useMemberStore = defineStore(
  'member',
  () => {
    const counterstore = useCounterStore()
    const address_name = ref('')
    const address_code = ref('')
    const token = ref(null)
    const memberId = ref('')
    const nickname = ref('')
    const profileImage = ref('')
    const imgUrl = ref('')
    const isOauth = ref(false)
    const loginType = ref('')

    //회원가입
    const createMember = function (payload) {
      const formData = new FormData()
      if (payload.profileImg) {
        formData.append('multipartFile', payload.profileImg)
      }
      formData.append('id', payload.memberId)
      formData.append('password', payload.password)
      formData.append('birth', payload.birth)
      formData.append('gender', payload.gender)
      formData.append('nickname', payload.nickname)
      formData.append('introduce', payload.introduce)
      formData.append('regionCd', payload.region_cd)
      formData.append('imgUrl', payload.imgUrl)
      formData.append('loginType', payload.loginType)

      axios({
        method: 'POST',
        url: `${REST_MEMBER_API}/api/member/local-signup`,
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        data: formData
      })
        .then((res) => {
          // console.log(res)
          alert('회원가입을 축하드립니다! 로그인을 해주세요')
          router.push({ name: 'Login' })
        })
        .catch((err) => {
          // console.log(err)
          const errmsg = err.response.data.message
          alert(errmsg)
          console.log(errmsg)
        })
    } // 회원가입 end

    // 아이디 중복 체크
    const checkId = function (memberId) {
      axios({
        method: 'POST',
        url: `${REST_MEMBER_API}/api/member/check-id`,
        data: {
          id: memberId
        }
      })
        .then((res) => {
          // console.log(res)
          alert('사용가능한 아이디입니다')
        })
        .catch((err) => {
          // console.log(err)
          const errmsg = err.response.data.message
          console.log(errmsg)
          if (errmsg == 'id is empty') {
            alert('아이디를 입력해주세요')
          } else if (errmsg == '중복된 아이디입니다.') {
            alert('중복된 아이디입니다. 다른 아이디를 입력해주세요')
          }
        })
    } // 아이디 중복 체크 end

    // 닉네임 중복 체크
    const checkNickname = function (nickname) {
      axios({
        method: 'POST',
        url: `${REST_MEMBER_API}/api/member/check-nickname`,
        data: {
          nickname: nickname
        }
      })
        .then((res) => {
          // console.log(res)
          alert('사용가능한 닉네임입니다')
        })
        .catch((err) => {
          // console.log(err)
          const errmsg = err.response.data.message
          console.log(errmsg)
          if (errmsg == 'nickname is empty') {
            alert('닉네임을 입력해주세요')
          } else if (errmsg == '중복된 닉네임입니다.') {
            alert('중복된 닉네임입니다. 다른 닉네임을 입력해주세요')
          }
        })
    } //닉네임 중복 체크 end

    // 로그인
    const login = (payload) => {
      axios({
        method: 'post',
        url: `${REST_MEMBER_API}/api/member/local-login`,
        data: {
          memberId: payload.memberId,
          password: payload.password
        }
      })
        .then((res) => {
          // alert('로그인 성공')
          token.value = res.headers.get('atk')

          counterstore.setCookie('atk', token.value)
          counterstore.setCookie('nickname', res.data.data.nickname)
          counterstore.setCookie('profileImage', res.data.data.profileImage)
          nickname.value = res.data.data.nickname
          profileImage.value = res.data.data.profileImage

          counterstore.setCookie('rtk', res.headers.get('rtk'))

          //router.push({ name: 'home' })
          window.location.href = '/'
        })
        .catch((err) => {
          alert('로그인 실패')
          console.log(err)
        })
    } // 로그인 end

    // 카카오 로그인
    const kakaoLogin = () => {
      const clientId = import.meta.env.VITE_KAKAO_CLIENT_Id
      const redirectUri = import.meta.env.VITE_KAKAO_REDIRECT_URI
      const url = `https://kauth.kakao.com/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=code`
      window.location.href = url
    }

    const isMember = async (code) => {
      await axios({
        method: 'get',
        url: `${REST_MEMBER_API}/api/oauth/kakao?code=${code}`
      }).then((res) => {
        if (res.status === 201) {
          memberId.value = res.data.id
          nickname.value = res.data.nickname
          imgUrl.value = res.data.profileImage
          loginType.value = 'kakao'
          isOauth.value = true
          router.push({ name: 'Signup' })
        } else if (res.status === 200) {
          token.value = res.headers.get('atk')

          counterstore.setCookie('atk', token.value)
          counterstore.setCookie('nickname', res.data.data.nickname)
          counterstore.setCookie('profileImage', res.data.data.profileImage)

          window.location.href = '/'
        }
      })
    } // 카카오 로그인 end

    const isLogin = computed(() => counterstore.getCookie('atk') !== undefined)

    // 스토어의 상태를 초기화하는 함수
    const resetStore = () => {
      address_name.value = ''
      address_code.value = ''
      token.value = null
      memberId.value = ''
      nickname.value = ''
      profileImage.value = ''
      isOauth.value = false
      // 여기에 더 많은 상태를 초기화하십시오.
    }

    // 로그아웃
    const logout = () => {
      axios({
        method: 'post',
        url: `${REST_MEMBER_API}/api/member/logout`,
        headers: {
          Authorization: `Bearer ${counterstore.getCookie('atk')}`
        }
      })
        .then((res) => {
          // alert('로그아웃 성공')

          // 스토어 상태 초기화
          // resetStore()
          nickname.value = null
          profileImage.value = null
          token.value = null
          counterstore.deleteCookie('atk')
          counterstore.deleteCookie('nickname')
          counterstore.deleteCookie('profileImage')
          localStorage.clear()

          window.location.href = '/'
          router.push({ name: 'home' })
        })
        .catch((err) => {
          alert('로그아웃 실패')
          console.log(err)
        })
    } // 로그아웃 end

    // 마이페이지
    const mypage = ref([])
    const getMypage = async () => {
      try {
        const res = await axios({
          method: 'get',
          url: `${REST_MEMBER_API}/api/member/mypage`,
          headers: {
            Authorization: `Bearer ${counterstore.getCookie('atk')}`
          }
        })
        // console.log(res.data.data)
        mypage.value = res.data.data
        // 내 정보 수정 시 네비게이션 바를 즉시 업데이트 하기 위해 쿠키를 이때 변경
        counterstore.deleteCookie('profileImage')
        counterstore.deleteCookie('nickname')
        // console.log(mypage.value)
        counterstore.setCookie('profileImage', mypage.value.profileImage)
        counterstore.setCookie('nickname', mypage.value.nickname)
      } catch (err) {
        console.log(err)
      }
    } // 마이페이지 end

    // 정보 수정
    const modifyInfo = async (payload) => {
      const formData = new FormData()
      if (payload.profileImage) {
        formData.append('profileImage', payload.profileImage)
      }
      formData.append('nickname', payload.nickname)
      formData.append('introduce', payload.introduce)
      formData.append('regionCd', payload.regionCd)

      try {
        const res = await axios({
          method: 'post',
          url: `${REST_MEMBER_API}/api/member/modify-info`,
          headers: {
            Authorization: `Bearer ${counterstore.getCookie('atk')}`
          },
          data: formData
        })
        // console.log(res)
        alert('정보 변경 성공')
        router.push({ name: 'Mypage' })
        setTimeout(() => {
          window.location.reload()
        }, 100)
      } catch (err) {
        console.log(err)
        alert('정보 변경 실패')
      }
    } // 정보 수정 end

    // 비밀번호 수정
    const modifyPassword = (payload) => {
      axios({
        method: 'post',
        url: `${REST_MEMBER_API}/api/member/modify-password`,
        headers: {
          Authorization: `Bearer ${counterstore.getCookie('atk')}`
        },
        data: {
          password: payload.password,
          newPassword: payload.newPassword,
          checkNewPassword: payload.checkNewPassword
        }
      })
        .then((res) => {
          // console.log(res)
          alert('비밀번호 변경 성공')
        })
        .catch((err) => {
          console.log(err)
          alert(err.value)
        })
    } // 비밀번호 수정 end

    // 회원 탈퇴
    const deleteMember = (password) => {
      axios({
        method: 'post',
        url: `${REST_MEMBER_API}/api/member/delete`,
        headers: {
          Authorization: `Bearer ${counterstore.getCookie('atk')}`
        },
        data: {
          password: password
        }
      })
        .then((res) => {
          // console.log(res)
          alert('회원 탈퇴 성공...')
          router.push({ name: 'home' })
        })
        .then((res) => {
          logout()
        })
        .catch((err) => {
          console.log(err)
        })
    }

    // 지역코드 및 주소 가져오기
    const getLocationInfo = () => {
      return [address_name.value, address_code.value]
    }

    return {
      createMember,
      // 로그인
      checkId,
      checkNickname,
      login,
      kakaoLogin,
      isMember,
      // 로그인 정보
      memberId,
      isOauth,
      token,
      nickname,
      profileImage,
      imgUrl,
      loginType,
      isLogin,
      // 로그아웃
      logout,
      // 마이페이지
      mypage,
      getMypage,
      modifyInfo,
      modifyPassword,
      deleteMember,
      // 지역 가져오기 카카오맵
      address_name,
      address_code,
      getLocationInfo,
      resetStore
    }
  }
  // 로컬에 memberstore 정보를 저장하고 싶을 때 사용
  // { persist: true }
)
