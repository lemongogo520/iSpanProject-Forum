<template>

  <RouterLink
    :to="path"
    class="feature-card"
  >

    <div class="feature-icon">
      {{ icon }}
    </div>

    <h3>
      {{ title }}
    </h3>

    <p>
      {{ description }}
    </p>

    <span class="more">
      進入功能 →
    </span>

  </RouterLink>

</template>


<script setup>

defineProps({

  title: {
    type: String,
    required: true
  },

  description: {
    type: String,
    required: true
  },

  icon: {
    type: String,
    required: true
  },

  path: {
    type: String,
    required: true
  }

})

</script>


<style scoped>

.feature-card {
  padding: 26px;

  background: white;

  border: 1px solid #e5eaf1;

  border-radius: 16px;

  text-decoration: none;

  color: inherit;

  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}


.feature-card:hover {
  transform: translateY(-4px);

  box-shadow:
    0 10px 30px
    rgba(31, 45, 70, 0.08);
}


.feature-icon {
  width: 52px;
  height: 52px;

  display: flex;
  align-items: center;
  justify-content: center;

  margin-bottom: 18px;

  border-radius: 13px;

  background: #eef4ff;

  font-size: 25px;
}


.feature-card h3 {
  margin: 0 0 10px;

  font-size: 20px;

  color: #202b3c;
}


.feature-card p {
  min-height: 48px;

  margin: 0 0 18px;

  color: #727d8e;

  font-size: 14px;

  line-height: 1.7;
}


.more {
  color: #3b82f6;

  font-size: 14px;
  font-weight: 600;
}

</style>