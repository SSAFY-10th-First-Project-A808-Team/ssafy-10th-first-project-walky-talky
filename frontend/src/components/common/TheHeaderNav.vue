<template>
  <!-- ======= Header ======= -->
  <header id="header" class="d-flex align-items-center">
    <div class="container d-flex align-items-center">
      <!-- <h1 class="logo me-auto">
        <RouterLink :to="{ name: 'home' }">Walky Talky</RouterLink>
      </h1> -->
      <h1 class="logo me-auto">
        <a href="/">Walky Talky</a>
      </h1>
      <!-- Uncomment below if you prefer to use an image logo -->
      <!-- <a href="index.html" class="logo me-auto"><img src="assets/img/logo.png" alt="" class="img-fluid"></a>-->

      <nav id="navbar" class="navbar">
        <ul>
          <li @click="closeNav">
            <RouterLink :to="{ name: 'club' }" class="nav-link scrollto">소모임</RouterLink>
          </li>

          <li @click="closeNav">
            <RouterLink :to="{ name: 'DoWalk' }" class="nav-link scrollto">산책</RouterLink>
          </li>
          <li @click="closeNav">
            <RouterLink :to="{ name: 'share-board' }" class="nav-link scrollto"
              >산책공유</RouterLink
            >
          </li>
          <!-- <li><a class="nav-link scrollto" href="#portfolio">Portfolio</a></li>
          <li><a class="nav-link scrollto" href="#team">Team</a></li> -->
          <!-- <li @click="closeNav">
            <RouterLink :to="{ name: 'club-chat', params: { seq: 1 } }" class="nav-link scrollto"
              >채팅테스트</RouterLink
            >
          </li> -->
          <li class="dropdown" v-if="memberstore.isLogin">
            <a href="#" class="profile-link">
              <!-- 프로필 이미지와 닉네임을 감싸는 flex 컨테이너 -->
              <div class="profile-container">
                <img :src="url" class="profile-image" alt="Profile" />
                <span>{{ nickname }}</span>
              </div>
              <i class="bi bi-chevron-down"></i>
            </a>
            <ul>
              <li @click="closeNav">
                <RouterLink :to="{ name: 'Mypage' }"><a href="#">마이페이지</a></RouterLink>
              </li>
              <!-- <li class="dropdown"><a href="#"><span>Deep Drop Down</span> <i class="bi bi-chevron-right"></i></a>
                <ul>
                  <li><a href="#">Deep Drop Down 1</a></li>
                  <li><a href="#">Deep Drop Down 2</a></li>
                  <li><a href="#">Deep Drop Down 3</a></li>
                  <li><a href="#">Deep Drop Down 4</a></li>
                  <li><a href="#">Deep Drop Down 5</a></li>
                </ul>
              </li> -->
              <li @click="closeNav">
                <RouterLink :to="{ name: 'Logout' }"><a href="#">로그아웃</a></RouterLink>
              </li>
              <!-- <li><a href="#">Drop Down 3</a></li>
              <li><a href="#">Drop Down 4</a></li> -->
            </ul>
          </li>
          <!-- <li><a class="nav-link scrollto" href="#contact">Contact</a></li> -->
          <li v-if="!memberstore.isLogin" @click="closeNav">
            <RouterLink :to="{ name: 'Login' }" class="getstarted scrollto">로그인</RouterLink>
          </li>
        </ul>
        <i class="bi bi-list mobile-nav-toggle" id="ham"></i>
      </nav>
      <!-- .navbar -->
    </div>
  </header>
  <!-- End Header -->
</template>

<script setup>
import { handleError, ref, watch, onMounted } from 'vue'
import { useMemberStore } from '@/stores/member'
import { useCounterStore } from '@/stores/counter'

const memberstore = useMemberStore()
const counterstore = useCounterStore()

const nickname = ref(counterstore.getCookie('nickname'))
const url = ref(counterstore.getCookie('profileImage'))

onMounted(() => {
  nickname.value = counterstore.getCookie('nickname')
  url.value = counterstore.getCookie('profileImage')
})

watch(
  () => counterstore.getCookie('nickname'),
  (newVal) => {
    nickname.value = newVal
  }
)
watch(
  () => counterstore.getCookie('profileImage'),
  (newVal) => {
    url.value = newVal
  }
)
// const closeNav = () => {
//   const navbar = document.querySelector('#navbar')
//   if (navbar.classList.contains('navbar-mobile')) {
//     const mobileNavToggle = document.querySelector('.mobile-nav-toggle')
//     mobileNavToggle.classList.toggle('bi-x')
//     mobileNavToggle.classList.toggle('bi-list')
//     // navbar.classList.remove('navbar-mobile')
//   }
// }
const closeNav = () => {
  const navtoggle = document.querySelector('.bi-x')
  if (navtoggle) {
    navtoggle.click()
  }
}
</script>

<style scoped>
.profile-image {
  width: 40px;
  /* 이미지 크기 설정 */
  height: 40px;
  /* 이미지 크기 설정 */
  border-radius: 50%;
  /* 원형으로 만들기 */
  object-fit: cover;
  /* 이미지 비율을 유지하면서 요소에 맞추기 */
  margin-right: 10px;
  /* 이미지와 닉네임 사이의 여백 설정 */
}
</style>
