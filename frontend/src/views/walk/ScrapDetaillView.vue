<template>
  <WalkHeaderNav />
  <div class="map-container">
    <div
      :id="'map-' + uniqueId"
      class="map"
      :style="{ width: '100%', height: '500px', justifyContent: 'center' }"
      @click="setDraggable(true)"
    ></div>

    <div class="record-container">
      <div class="header">
        <div class="left-section">
          <h2>{{ title }}</h2>
        </div>
        <div class="right-section star-section">
          <StarRating :starRating="parseInt(starRating)" />
        </div>
      </div>

      <div class="mid-container">
        <div class="right-section">
          <p>{{ startTime }}</p>
          <p>
            소요 시간 : {{ convertTime(parseInt(duration)) }} | 총 거리 :
            {{ Math.round(distance * 10) / 10 }} km
          </p>
        </div>
      </div>

      <div class="comment-section">
        <p>{{ comment }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useWalkStore } from '@/stores/walk'
import { useRoute } from 'vue-router'
import WalkHeaderNav from '@/components/common/WalkHeaderNav.vue'
import StarRating from '@/components/walk/StarRating.vue'

const walkStore = useWalkStore()
const route = useRoute()

const API_KEY = import.meta.env.VITE_KAKAO_API_KEY
let map = null // map is not defined Reference Error 방지
const uniqueId = ref(Date.now()) // 각 컴포넌트에 고유한 ID를 부여하기 위한 ref

const record = ref(null)
const title = ref(null)
const duration = ref(null)
const distance = ref(null)
const points = ref(null)
const starRating = ref(null)
const comment = ref(null)
const startTime = ref(null)

onMounted(async () => {
  await walkStore.getScrapDetail(route.params.seq)
  record.value = walkStore.scrapDetail

  title.value = record.value.title
  duration.value = record.value.duration
  distance.value = record.value.distance
  points.value = record.value.points
  starRating.value = record.value.starRating
  comment.value = record.value.comment
  startTime.value = record.value.startTime.substr(0, 10)

  if (window.kakao && window.kakao.maps) {
    initMap()
  } else {
    const script = document.createElement('script')
    script.onload = () => {
      /* global kakao*/
      kakao.maps.load(initMap)
    }
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${API_KEY}&libraries=services&autoload=false`
    document.head.appendChild(script)
  }
})

const initMap = () => {
  const container = document.getElementById(`map-${uniqueId.value}`)

  const mid = parseInt(points.value.length / 2)

  if (points.value && points.value.length > 0 && mid >= 0 && mid < points.value.length) {
    const options = {
      center: new kakao.maps.LatLng(points.value[mid].latitude, points.value[mid].longitude),
      level: 5,
      draggable: false
    }

    map = new kakao.maps.Map(container, options)

    ///// 시작, 끝 점 /////
    var startEndPositions = [
      {
        title: '산책 시작',
        img: 'start.png',
        latlng: new kakao.maps.LatLng(points.value[0].latitude, points.value[0].longitude)
      },
      {
        title: '산책 끝',
        img: 'end.png',
        latlng: new kakao.maps.LatLng(
          points.value[points.value.length - 1].latitude,
          points.value[points.value.length - 1].longitude
        )
      }
    ]

    for (var i = 0; i < startEndPositions.length; i++) {
      var imageSize = new kakao.maps.Size(20, 30)

      var markerImage = new kakao.maps.MarkerImage(
        import.meta.env.VITE_MARKER_IMAGE_ROUTE + startEndPositions[i].img,
        imageSize
      )

      var marker = new kakao.maps.Marker({
        map: map,
        position: startEndPositions[i].latlng,
        title: startEndPositions[i].title,
        image: markerImage
      })
    }

    ///// 마커찍기/////
    for (const point of points.value) {
      if (!point.url && !point.pointComment) {
        continue
      }

      const latlng = new kakao.maps.LatLng(point.latitude, point.longitude)

      const marker = new kakao.maps.Marker({
        map: map,
        position: latlng
      })

      let content = `<div class="wrap">` + `    <div class="info">` + `        <div class="body">`

      if (point.url && point.pointComment) {
        content +=
          `            <div class="img">` +
          `                <img src=${point.url} width="100" height="100">` +
          `            </div>` +
          `            <div class="desc">` +
          `                <div class="ellipsis">${point.pointComment}</div>` +
          `            </div>`
      } else if (point.url) {
        content +=
          `            <div class="img">` +
          `                <img src=${point.url} width="100" height="100">` +
          `            </div>`
      } else if (point.pointComment) {
        content +=
          `            <div class="desc">` +
          `                <div class="ellipsis">${point.pointComment}</div>` +
          `            </div>`
      }
      content += `        </div>` + `    </div>` + `</div>`

      const contentElement = document.createElement('div')
      contentElement.innerHTML = content

      const overlay = new kakao.maps.CustomOverlay({
        content: contentElement,
        map: map,
        position: marker.getPosition()
      })

      let isClose = true
      overlay.setMap(null)

      kakao.maps.event.addListener(marker, 'click', function () {
        if (isClose) {
          overlay.setMap(map)
          isClose = false
        } else {
          overlay.setMap(null)
          isClose = true
        }
      })
    }

    // 경로 폴리라인
    var polyline = new kakao.maps.Polyline({
      map: map,
      path: points.value.map((point) => new kakao.maps.LatLng(point.latitude, point.longitude)),
      strokeWeight: 5,
      strokeColor: '#FF0000'
    })

    polyline.setMap(map)
  } else {
    const options = {
      center: new kakao.maps.LatLng(37.501289692413124, 127.03961880220784),
      level: 5
    }

    map = new kakao.maps.Map(container, options)
  }
}

function setDraggable(draggable) {
  map.setZoomable(draggable)
  map.setDraggable(draggable)
}

function convertTime(seconds) {
  if (typeof seconds !== 'number' || seconds < 0) {
    return 'Invalid input'
  }

  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60

  const minutesString = minutes > 0 ? `${minutes}분` : ''
  const secondsString = remainingSeconds > 0 ? `${remainingSeconds}초` : ''

  return `${minutesString} ${secondsString}`.trim() || '0초'
}
</script>

<style scoped>
.map-container {
  display: flex;
  flex-direction: column;
}

.map {
  margin: 20px;
}

.record-container {
  width: 100%;
  max-width: 600px;
  flex-direction: column;
}

.header {
  display: flex;
  margin: 10px;
}

.mid-container {
  display: flex;
  align-items: center;
  margin: 10px;
}

.left-section {
  flex-grow: 1;
}

.right-section {
  flex-grow: 2;
  align-items: center;
}

.comment-section {
  margin: 10px;
}
</style>

<style>
.wrap {
  position: absolute;
  left: 50%;
  bottom: 40px;
  transform: translateX(-50%);
  width: auto;
  max-width: 288px;
  height: auto;
  padding: 10px;
  text-align: left;
  font-size: 14px;
  font-family: 'Malgun Gothic', dotum, '돋움', sans-serif;
  line-height: 1.5;
}

.wrap .info {
  display: flex;
  border-radius: 5px;
  border: 1px solid #ccc;
  background: #fff;
  box-shadow: 0px 1px 2px #888;
}

.info .body {
  padding: 10px;
  display: flex;
  flex-direction: column;
}

.desc {
  margin-left: 10px;
  font-size: 17px;
  flex-grow: 1;
}

.desc .ellipsis {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.info .img {
  border: 1px solid #ddd;
}
</style>
