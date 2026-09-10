<template>

  <header class="top-header">

    <div class="header-inner">

      <RouterLink
        to="/"
        class="brand"
      >
        iSpanProject
      </RouterLink>


      <nav class="main-nav">

        <RouterLink
          to="/"
          class="nav-link"
        >
          首頁
        </RouterLink>

        <RouterLink
          to="/forum"
          class="nav-link"
        >
          討論區
        </RouterLink>

        <RouterLink
          to="/coin"
          class="nav-link"
        >
          點數
        </RouterLink>

        <RouterLink
          to="/avatar"
          class="nav-link"
        >
          我的角色
        </RouterLink>

        <RouterLink
          to="/shop"
          class="nav-link"
        >
          商城
        </RouterLink>

        <RouterLink
          to="/tasks"
          class="nav-link"
        >
          任務活動
        </RouterLink>

      </nav>


      <div class="user-area">

        <div class="avatar">
          A
        </div>

        <span>
          哈囉，Admin
        </span>

      </div>

    </div>

  </header>

</template>


<style scoped>

.top-header {
  position: sticky;
  top: 0;
  z-index: 100;

  height: 72px;

  background: #0f1f35;

  box-shadow:
    0 2px 8px rgba(0, 0, 0, 0.08);
}


.header-inner {
  max-width: 1200px;
  height: 100%;

  margin: 0 auto;
  padding: 0 24px;

  display: flex;
  align-items: center;
}


.brand {
  margin-right: 42px;

  color: white;

  font-size: 22px;
  font-weight: 700;

  text-decoration: none;
}


.main-nav {
  display: flex;
  align-items: center;
  gap: 26px;

  flex: 1;
}


.nav-link {
  position: relative;

  padding: 25px 0;

  color: #c8d1de;

  text-decoration: none;

  font-size: 15px;
}


.nav-link:hover {
  color: white;
}


.nav-link.router-link-active {
  color: white;
}


.nav-link.router-link-active::after {
  content: '';

  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;

  height: 3px;

  background: #3b82f6;
}


.user-area {
  display: flex;
  align-items: center;
  gap: 10px;

  color: #e5ebf3;

  font-size: 14px;
}


.avatar {
  width: 34px;
  height: 34px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 50%;

  background: #3b82f6;

  color: white;

  font-weight: bold;
}


@media (max-width: 900px) {

  .main-nav {
    display: none;
  }


  .brand {
    flex: 1;
  }

}

</style>