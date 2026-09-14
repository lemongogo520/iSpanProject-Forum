<template>
  <section class="hero-section">

    <div class="hero-inner">

      <div class="hero-content">

        <span class="hero-label">
          WELCOME TO ISPANPROJECT
        </span>

        <h1>
          歡迎來到
          <span>iSpanProject</span>
        </h1>

        <p>
          論壇・點數・紙娃娃・商城整合平台
        </p>

        <div class="hero-actions">

          <RouterLink
            to="/forum"
            class="primary-button"
          >
            前往討論區
          </RouterLink>

          <a
            href="#system-functions"
            class="secondary-button"
          >
            探索功能
          </a>

        </div>

      </div>


      <div class="hero-visual">

        <div class="visual-circle">
          ✨
        </div>

      </div>

    </div>

  </section>
</template>


<style scoped>

.hero-section {
  background: linear-gradient(
    135deg,
    #132742,
    #1e3a60
  );

  color: white;
}


.hero-inner {
  max-width: 1200px;
  min-height: 450px;

  margin: 0 auto;
  padding: 60px 24px;

  display: grid;

  grid-template-columns:
    1.2fr 0.8fr;

  align-items: center;

  gap: 40px;
}


.hero-label {
  display: inline-block;

  margin-bottom: 14px;

  color: #93c5fd;

  font-size: 13px;
  font-weight: 700;

  letter-spacing: 1.6px;
}


.hero-content h1 {
  margin: 0 0 18px;

  font-size: 48px;

  line-height: 1.15;
}


.hero-content h1 span {
  color: #77aef8;
}


.hero-content p {
  margin: 0 0 30px;

  font-size: 18px;

  color: #cbd7e8;
}


.hero-actions {
  display: flex;

  gap: 14px;
}


.primary-button,
.secondary-button {
  padding: 12px 22px;

  border-radius: 9px;

  text-decoration: none;

  font-weight: 600;
}


.primary-button {
  background: #3b82f6;

  color: white;
}


.secondary-button {
  border: 1px solid #8294aa;

  color: white;
}


.hero-visual {
  display: flex;

  justify-content: center;
}


.visual-circle {
  width: 260px;
  height: 260px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 50%;

  background:
    rgba(255, 255, 255, 0.08);

  border:
    1px solid rgba(255, 255, 255, 0.15);

  font-size: 90px;
}


@media (max-width: 760px) {

  .hero-inner {
    grid-template-columns: 1fr;

    min-height: auto;
  }


  .hero-visual {
    display: none;
  }


  .hero-content h1 {
    font-size: 38px;
  }


  .hero-actions {
    flex-direction: column;
  }


  .primary-button,
  .secondary-button {
    text-align: center;
  }
}

</style>