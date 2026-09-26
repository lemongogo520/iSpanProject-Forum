import httpClient from "@/services/httpClient";

export const forumTemplateService = {
  // 取得全部範本
  getTemplates() {
    return httpClient.get("/api/forumtemplates");
  },

  // 套用指定範本
  applyTemplate(code, selections) {
    return httpClient.post(`/api/forumtemplates/${code}/apply`, {
      categories: selections,
    });
  },
  getStatus() {
    return httpClient.get("/api/forumtemplates/status");
  },

  generateAiTemplate(data) {
  return httpClient.post("/api/forumtemplates/ai-generate",
    data,
  );
},
};
