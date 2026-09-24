import httpClient from "@/services/httpClient";

export const generateSchoolForumData = () => {
  return httpClient.post("/api/dev/seed/school-forum");
};