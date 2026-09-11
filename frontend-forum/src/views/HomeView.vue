<template>
  <div class="home-page">

    <!-- ========================= -->
    <!-- Header -->
    <!-- ========================= -->
    <header class="header">

      <div class="header-inner">

        <RouterLink
          to="/"
          class="logo"
        >
          iSpanProject
        </RouterLink>

        <nav class="nav">

          <RouterLink
            to="/"
            class="nav-item"
          >
            🏠 首頁
          </RouterLink>

          <RouterLink
            to="/forum"
            class="nav-item"
          >
            💬 討論區
          </RouterLink>

          <RouterLink
            to="/avatar"
            class="nav-item"
          >
            🎭 我的角色
          </RouterLink>

          <RouterLink
            to="/shop"
            class="nav-item"
          >
            🛒 商城
          </RouterLink>

          <RouterLink
            to="/tasks"
            class="nav-item"
          >
            🏆 任務活動
          </RouterLink>

          <RouterLink
            to="/friends"
            class="nav-item"
          >
            👥 好友
          </RouterLink>

        </nav>


        <div class="user-box">

          <div class="user-avatar">
            A
          </div>

          <span>
            哈囉，Admin
          </span>

          <span>
           ⌄
          </span>

        </div>

      </div>

    </header>


    <main class="main">

      <!-- ========================= -->
      <!-- Hero -->
      <!-- ========================= -->

      <section class="hero">

        <div class="hero-overlay">

          <h1>
            歡迎來到
            <span>
              iSpanProject
            </span>
          </h1>

          <p>
            論壇・點數・紙娃娃・商城整合平台
          </p>


          <div class="hero-buttons">

            <RouterLink
              to="/forum"
              class="btn-primary"
            >
              前往討論區 →
            </RouterLink>

            <button class="btn-light">
              ♧ 新手指南
            </button>

          </div>

        </div>

      </section>


      <!-- ========================= -->
      <!-- 功能卡片 -->
      <!-- ========================= -->

      <section class="features">

        <RouterLink
          to="/forum"
          class="feature-card"
        >

          <div class="feature-icon blue">
            💬
          </div>

          <div>
            <h3>
              討論區
            </h3>

            <p>
              瀏覽文章・參與討論<br>
              與大家一起交流學習
            </p>
          </div>

          <span class="arrow">
            →
          </span>

        </RouterLink>


        <RouterLink
          to="/friends"
          class="feature-card"
        >

          <div class="feature-icon cyan">
            👥
          </div>

          <div>
            <h3>
              好友
            </h3>

            <p>
              好友管理・即時聊天<br>
              和夥伴保持聯繫
            </p>
          </div>

          <span class="arrow">
            →
          </span>

        </RouterLink>


        <RouterLink
          to="/avatar"
          class="feature-card"
        >

          <div class="feature-icon purple">
            🎭
          </div>

          <div>
            <h3>
              我的角色
            </h3>

            <p>
              紙娃娃・裝備與背包<br>
              打造專屬你的角色
            </p>
          </div>

          <span class="arrow">
            →
          </span>

        </RouterLink>


        <RouterLink
          to="/shop"
          class="feature-card"
        >

          <div class="feature-icon green">
            🛒
          </div>

          <div>
            <h3>
              商城
            </h3>

            <p>
              使用點數購買商品<br>
              各式道具與裝飾
            </p>
          </div>

          <span class="arrow">
            →
          </span>

        </RouterLink>


        <RouterLink
          to="/tasks"
          class="feature-card"
        >

          <div class="feature-icon pink">
            🏆
          </div>

          <div>
            <h3>
              任務活動
            </h3>

            <p>
              參與任務與活動<br>
              獲得豐富獎勵
            </p>
          </div>

          <span class="arrow">
            →
          </span>

        </RouterLink>

      </section>


      <!-- ========================= -->
      <!-- 下半部 -->
      <!-- ========================= -->

      <section class="content-grid">

        <!-- 熱門討論 -->

        <div class="panel topics-panel">

          <h2>
            🔥 熱門討論
          </h2>


          <div
            v-for="topic in topics"
            :key="topic.title"
            class="topic-row"
          >

            <span
              class="category-tag"
              :class="topic.className"
            >
              {{ topic.category }}
            </span>


            <span class="topic-title">
              {{ topic.title }}
            </span>


            <span class="topic-user">
              {{ topic.user }}
            </span>


            <span class="topic-stat">
              👁 {{ topic.views }}
            </span>


            <span class="topic-stat">
              💬 {{ topic.comments }}
            </span>


            <span class="topic-time">
              {{ topic.time }}
            </span>

          </div>


          <div class="view-more">
            查看全部文章 →
          </div>

        </div>


        <!-- 右側資訊 -->

        <div class="right-column">

          <!-- 個人資訊 -->

          <div class="panel profile-panel">

            <h2>
              👤 個人資訊
            </h2>


            <div class="profile-main">

              <div class="profile-avatar">
                A
              </div>


              <div>

                <strong>
                  Admin
                </strong>

                <div class="level">
                  等級：Lv.15
                </div>


                <div class="progress">

                  <div class="progress-value">
                  </div>

                </div>


                <small>
                  1,250 / 2,000
                </small>

              </div>

            </div>


            <div class="coin-box">

              <div>

                <span class="coin">
                  C
                </span>

                <span>
                  點數餘額
                </span>

                <strong>
                  2,580
                </strong>

              </div>


              <button>
                交易紀錄
              </button>

            </div>

          </div>


          <!-- 公告 -->

          <div class="panel announcement">

            <h2>
              📢 系統公告
            </h2>

            <ul>
              <li>
                9/09 系統維護公告
              </li>

              <li>
                點數儲值活動開跑！
              </li>

              <li>
                新版商城上線囉 🎉
              </li>
            </ul>

            <div class="announcement-more">
              查看全部公告 →
            </div>

          </div>

        </div>

      </section>

    </main>


    <!-- ========================= -->
    <!-- 聊天視窗 -->
    <!-- ========================= -->

    <aside class="chat-box">

      <div class="chat-header">

        <strong>
          好友聊天
        </strong>

        <span>
          —　×
        </span>

      </div>


      <div class="chat-user">

        <div class="chat-avatar">
          K
        </div>

        <div class="chat-message">

          <strong>
            KevinChen
          </strong>

          <p>
            關於 Spring Boot 的問題...
          </p>

        </div>

        <small>
          14:32
        </small>

      </div>


      <div class="chat-user">

        <div class="chat-avatar">
          小
        </div>

        <div class="chat-message">

          <strong>
            小白學習中
          </strong>

          <p>
            謝謝你的解答！
          </p>

        </div>

        <small>
          14:28
        </small>

      </div>


      <div class="chat-input">

        <input
          type="text"
          placeholder="輸入訊息..."
        >

        <button>
          ➤
        </button>

      </div>

    </aside>


    <!-- 聊天圓形按鈕 -->

    <button class="chat-floating">
      💬
      <span>
        2
      </span>
    </button>


    <!-- ========================= -->
    <!-- Footer -->
    <!-- ========================= -->

    <footer class="footer">

      <span>
        © 2026 iSpanProject.
        All rights reserved.
      </span>


      <div>
        關於我們　｜　
        使用條款　｜　
        隱私權政策　｜　
        聯絡我們
      </div>

    </footer>

  </div>
</template>


<script setup>

const topics = [

  {
    category: '程式交流',
    className: 'tag-blue',
    title: 'Spring Boot 啟動失敗問題求解',
    user: 'KevinChen',
    views: '1,245',
    comments: 23,
    time: '2 小時前'
  },

  {
    category: '新手發問',
    className: 'tag-green',
    title: '請問如何在 Java 中連接 SQL Server？',
    user: '小白學習中',
    views: 856,
    comments: 15,
    time: '3 小時前'
  },

  {
    category: '心得分享',
    className: 'tag-purple',
    title: '自學 Java 的心路歷程分享',
    user: '程式狂人',
    views: 632,
    comments: 12,
    time: '5 小時前'
  },

  {
    category: '技術討論',
    className: 'tag-orange',
    title: '關於 Thread 和 Runnable 的疑問',
    user: '多執行緒達人',
    views: 498,
    comments: 8,
    time: '昨天'
  },

  {
    category: '作品展示',
    className: 'tag-red',
    title: '我的紙娃娃角色設計分享！',
    user: '設計小天才',
    views: 367,
    comments: 6,
    time: '昨天'
  }

]

</script>


<style scoped>

* {
  box-sizing: border-box;
}


.home-page {
  min-height: 100vh;

  background:
    #f4f7fb;

  color:
    #15243a;

  font-family:
    "Microsoft JhengHei",
    Arial,
    sans-serif;
}


/* ========================= */
/* Header */
/* ========================= */

.header {
  height: 80px;

  background:
    linear-gradient(
      100deg,
      #091b31,
      #102c4c
    );

  color: white;
}


.header-inner {
  width: 100%;

  height: 100%;

  padding:
    0 38px;

  display: flex;

  align-items: center;
}


.logo {
  width: 245px;

  color: white;

  text-decoration: none;

  font-size: 28px;

  font-weight: 800;
}


.nav {
  flex: 1;

  height: 100%;

  display: flex;

  align-items: center;

  gap: 10px;
}


.nav-item {
  height: 100%;

  padding:
    0 20px;

  display: flex;

  align-items: center;

  color:
    #f4f7fb;

  text-decoration: none;

  font-size: 17px;

  border-bottom:
    3px solid transparent;
}


.nav-item:hover,
.nav-item.router-link-active {
  border-bottom-color:
    #2780ff;

  background:
    rgba(255, 255, 255, 0.03);
}


.user-box {
  display: flex;

  align-items: center;

  gap: 10px;

  font-size: 16px;
}


.user-avatar {
  width: 40px;

  height: 40px;

  border-radius: 50%;

  background:
    #eef3f7;

  color:
    #506174;

  display: flex;

  align-items: center;

  justify-content: center;

  font-weight: bold;
}


/* ========================= */
/* Main */
/* ========================= */

.main {
  padding:
    15px 28px;
}


/* ========================= */
/* Hero */
/* ========================= */

.hero {
  min-height: 320px;

  border-radius: 8px;

  overflow: hidden;

  position: relative;

  background:
    linear-gradient(
      90deg,
      rgba(223, 246, 255, 0.98) 0%,
      rgba(223, 246, 255, 0.90) 27%,
      rgba(223, 246, 255, 0.50) 48%,
      rgba(223, 246, 255, 0.08) 68%,
      rgba(223, 246, 255, 0.00) 100%
    ),
    url('@/assets/home/hero-banner.png');

  background-size: cover;

  background-position: center right;

  background-repeat: no-repeat;
}

.hero-overlay {
  min-height: 270px;

  padding:
    58px 66px;
}


.hero h1 {
  margin:
    0 0 12px;

  font-size:
    41px;
}


.hero h1 span {
  color:
    #1265da;
}


.hero p {
  margin:
    0 0 28px;

  font-size:
    21px;
}


.hero-buttons {
  display: flex;

  gap: 20px;
}


.btn-primary,
.btn-light {
  padding:
    13px 23px;

  border-radius:
    8px;

  border:
    none;

  font-size:
    15px;

  cursor:
    pointer;

  text-decoration:
    none;
}


.btn-primary {
  background:
    #0967dd;

  color:
    white;
}


.btn-light {
  background:
    white;

  color:
    #17253a;

  box-shadow:
    0 3px 10px
    rgba(0,0,0,.08);
}


/* ========================= */
/* Feature */
/* ========================= */

.features {
  display: grid;

  grid-template-columns:
    repeat(5, 1fr);

  gap:
    16px;

  margin-top:
    16px;
}


.feature-card {
  min-height:
    170px;

  padding:
    26px 22px;

  position:
    relative;

  display:
    flex;

  align-items:
    flex-start;

  gap:
    18px;

  background:
    white;

  border:
    1px solid #e1e7ee;

  border-radius:
    7px;

  text-decoration:
    none;

  color:
    inherit;

  box-shadow:
    0 3px 12px
    rgba(0,0,0,.035);
}


.feature-card:hover {
  transform:
    translateY(-3px);

  box-shadow:
    0 9px 25px
    rgba(0,0,0,.08);
}


.feature-icon {
  width:
    64px;

  height:
    64px;

  flex:
    0 0 64px;

  border-radius:
    50%;

  display:
    flex;

  justify-content:
    center;

  align-items:
    center;

  font-size:
    30px;
}


.blue {
  background:
    #e5f3ff;
}


.cyan {
  background:
    #dff8fd;
}


.purple {
  background:
    #f0e9ff;
}


.green {
  background:
    #e9f9e8;
}


.pink {
  background:
    #fdecef;
}


.feature-card h3 {
  margin:
    5px 0 12px;

  font-size:
    19px;
}


.feature-card p {
  margin:
    0;

  color:
    #526176;

  font-size:
    14px;

  line-height:
    1.7;
}


.arrow {
  position:
    absolute;

  right:
    18px;

  bottom:
    17px;

  color:
    #074bb9;

  font-size:
    22px;
}


/* ========================= */
/* Content */
/* ========================= */

.content-grid {
  margin-top:
    16px;

  display:
    grid;

  grid-template-columns:
    minmax(0, 3fr)
    minmax(280px, .9fr);

  gap:
    16px;

  padding-right:
    285px;
}


.panel {
  background:
    white;

  border:
    1px solid #e1e7ee;

  border-radius:
    7px;

  box-shadow:
    0 3px 12px
    rgba(0,0,0,.03);
}


.panel h2 {
  margin:
    0;

  padding:
    17px 20px;

  border-bottom:
    1px solid #e4e9ef;

  font-size:
    19px;
}


/* Topics */

.topic-row {
  min-height:
    52px;

  padding:
    9px 20px;

  display:
    grid;

  grid-template-columns:
    100px
    1fr
    130px
    85px
    70px
    80px;

  align-items:
    center;

  border-bottom:
    1px solid #edf0f3;

  font-size:
    14px;
}


.category-tag {
  width:
    fit-content;

  padding:
    5px 10px;

  border-radius:
    5px;
}


.tag-blue {
  background:
    #e3f2ff;

  color:
    #0871db;
}


.tag-green {
  background:
    #e4f8e3;

  color:
    #208134;
}


.tag-purple {
  background:
    #efebff;

  color:
    #7651d0;
}


.tag-orange {
  background:
    #fff0dc;

  color:
    #d37513;
}


.tag-red {
  background:
    #ffe5e5;

  color:
    #d44040;
}


.topic-title {
  color:
    #122039;
}


.topic-user,
.topic-stat,
.topic-time {
  color:
    #526078;
}


.view-more {
  padding:
    20px;

  text-align:
    center;

  color:
    #0868dc;
}


/* ========================= */
/* Profile */
/* ========================= */

.right-column {
  display:
    flex;

  flex-direction:
    column;

  gap:
    12px;
}


.profile-main {
  padding:
    15px 20px;

  display:
    flex;

  gap:
    18px;

  align-items:
    center;
}


.profile-avatar {
  width:
    66px;

  height:
    66px;

  border-radius:
    50%;

  background:
    #e5eaf0;

  display:
    flex;

  justify-content:
    center;

  align-items:
    center;

  font-size:
    26px;
}


.level {
  margin-top:
    5px;

  color:
    #0768dd;

  font-size:
    13px;
}


.progress {
  width:
    160px;

  height:
    10px;

  margin:
    7px 0;

  border-radius:
    10px;

  background:
    #e8edf3;

  overflow:
    hidden;
}


.progress-value {
  width:
    65%;

  height:
    100%;

  background:
    #1476e8;
}


.coin-box {
  margin:
    0 12px 12px;

  padding:
    12px;

  display:
    flex;

  align-items:
    center;

  justify-content:
    space-between;

  border:
    1px solid #dbe2eb;

  border-radius:
    8px;
}


.coin {
  width:
    37px;

  height:
    37px;

  display:
    inline-flex;

  justify-content:
    center;

  align-items:
    center;

  margin-right:
    7px;

  border-radius:
    50%;

  background:
    #ffb725;

  color:
    white;

  font-weight:
    bold;
}


.coin-box strong {
  display:
    block;

  margin-left:
    47px;

  color:
    #f39800;

  font-size:
    20px;
}


.coin-box button {
  padding:
    8px 11px;

  background:
    white;

  border:
    1px solid #bfcbd9;

  border-radius:
    6px;

  color:
    #284e82;
}


/* ========================= */
/* Announcement */
/* ========================= */

.announcement ul {
  margin:
    8px 0;

  padding:
    0 30px;

  line-height:
    1.9;

  color:
    #213a61;
}


.announcement-more {
  padding:
    7px 15px 14px;

  text-align:
    right;

  color:
    #0868dc;
}


/* ========================= */
/* Chat */
/* ========================= */

.chat-box {
  position:
    fixed;

  right:
    15px;

  bottom:
    105px;

  width:
    300px;

  background:
    white;

  border:
    1px solid #ccd6e1;

  border-radius:
    12px;

  box-shadow:
    0 10px 35px
    rgba(0,0,0,.12);

  overflow:
    hidden;

  z-index:
    50;
}


.chat-header {
  padding:
    18px 20px;

  display:
    flex;

  justify-content:
    space-between;

  border-bottom:
    1px solid #e1e5ea;

  font-size:
    18px;
}


.chat-user {
  display:
    flex;

  gap:
    12px;

  padding:
    15px 18px;
}


.chat-avatar {
  width:
    43px;

  height:
    43px;

  border-radius:
    50%;

  background:
    #e4e8ee;

  display:
    flex;

  align-items:
    center;

  justify-content:
    center;
}


.chat-message {
  flex:
    1;
}


.chat-message p {
  margin:
    5px 0 0;

  color:
    #59677b;

  font-size:
    13px;
}


.chat-input {
  padding:
    14px;

  display:
    flex;

  gap:
    8px;

  border-top:
    1px solid #e5e9ee;
}


.chat-input input {
  min-width:
    0;

  flex:
    1;

  padding:
    10px;

  border:
    1px solid #d6dde7;

  border-radius:
    7px;
}


.chat-input button {
  width:
    44px;

  border:
    none;

  border-radius:
    7px;

  background:
    #0968dc;

  color:
    white;
}


.chat-floating {
  position:
    fixed;

  right:
    30px;

  bottom:
    45px;

  width:
    67px;

  height:
    67px;

  border:
    none;

  border-radius:
    50%;

  background:
    #0867da;

  color:
    white;

  font-size:
    28px;

  box-shadow:
    0 6px 20px
    rgba(0,0,0,.2);
}


.chat-floating span {
  position:
    absolute;

  right:
    -2px;

  top:
    -4px;

  width:
    25px;

  height:
    25px;

  display:
    flex;

  justify-content:
    center;

  align-items:
    center;

  border-radius:
    50%;

  background:
    red;

  color:
    white;

  font-size:
    12px;
}


/* ========================= */
/* Footer */
/* ========================= */

.footer {
  min-height:
    65px;

  margin-top:
    0;

  padding:
    0 320px;

  display:
    flex;

  justify-content:
    space-between;

  align-items:
    center;

  background:
    #0c2745;

  color:
    white;

  font-size:
    13px;
}


/* ========================= */
/* Responsive */
/* ========================= */

@media (max-width: 1200px) {

  .nav-item {
    padding:
      0 10px;
  }


  .content-grid {
    padding-right:
      0;
  }


  .chat-box {
    display:
      none;
  }


  .features {
    grid-template-columns:
      repeat(3, 1fr);
  }


  .footer {
    padding:
      0 30px;
  }

}


@media (max-width: 800px) {

  .nav {
    display:
      none;
  }


  .features {
    grid-template-columns:
      1fr;
  }


  .content-grid {
    grid-template-columns:
      1fr;
  }


  .topic-row {
    grid-template-columns:
      90px 1fr;
  }


  .topic-user,
  .topic-stat,
  .topic-time {
    display:
      none;
  }


  .hero h1 {
    font-size:
      32px;
  }

}

</style>